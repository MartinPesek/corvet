package co.orbu.controller;

import co.orbu.parser.DetectFileType;
import co.orbu.utils.MimeTypeExtension;
import co.orbu.utils.StringGenerator;
import com.dropbox.core.v2.DbxClientV2;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Controller
public class UploadController {

    private static final String CHARSET = "UTF-8";
    private static final Logger LOG = LogManager.getLogger(UploadController.class);

    @Value("${ofs.dirUploadPath}")
    private String dirUploadPath;

    @Value("${ofs.maxFileDownloadSize}")
    private long maxFileDownloadSize;

    private final ExecutorService executorService;
    private final DbxClientV2 dropboxClient;
    private final CloudBlobContainer storageService;

    @Autowired
    public UploadController(ExecutorService executorService, DbxClientV2 dropboxClient, CloudBlobContainer storageService) {
        this.executorService = executorService;
        this.dropboxClient = dropboxClient;
        this.storageService = storageService;
    }

    /**
     * Saves Base64 string as binary file.
     *
     * @param data Base64 string.
     * @return File name of saved file or null if no file was saved.
     */
    private String saveBase64ToFile(String data) {
        String[] mimeTypeBase64 = data.split(";");

        String base64Data = null;
        String extension = null;
        String mimeType = null;

        for (String s : mimeTypeBase64) {
            int colonIndex = s.indexOf(':');
            if (colonIndex == -1) {
                if (s.toLowerCase().startsWith("base64,")) {
                    base64Data = s.substring(6).replace(' ', '+');
                }

                continue;
            }

            String[] header = s.split(":");
            if (!"data".equalsIgnoreCase(header[0])) {
                continue;
            }

            mimeType = header[1];
            extension = MimeTypeExtension.getExtensionFromMimeType(mimeType);
        }

        // unable to parse data, do nothing
        if (base64Data == null || base64Data.isEmpty() || extension == null || extension.isEmpty()) {
            LOG.error("Unable to parse base64 data.");
            return null;
        }

        // TODO: error/exception handling
        // TODO: this code sucks... Proof of concept though -- improve!

        byte[] rawData = DatatypeConverter.parseBase64Binary(base64Data);
        String filename = StringGenerator.getRandomString() + extension;

        String resultUrl = null;
        boolean failedUpload = false;

        try {
            CloudBlockBlob blob = storageService.getBlockBlobReference(filename);

            BlobProperties properties = blob.getProperties();
            properties.setContentType(mimeType);
            blob.uploadProperties();

            resultUrl = blob.getUri().toASCIIString();
        } catch (URISyntaxException | StorageException e) {
            LOG.error("Unable to upload file to Azure.", e);
            failedUpload = true;
        }

        String failedFlag = failedUpload ? "_failed_" : "_";

        executorService.submit(() -> {
            try {
                String dropboxFilename = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + failedFlag + filename;
                dropboxClient.files().uploadBuilder("/ius/" + dropboxFilename).uploadAndFinish(new ByteArrayInputStream(rawData));
            } catch (Exception e) {
                LOG.error("Unable to upload file to Dropbox.", e);
            }
        });

        return resultUrl;
    }

    /**
     * Downloads file from web and saves it to file.
     *
     * @param url URL of a file to download.
     * @return File name of saved file or null if no file was saved.
     */
    private String saveURLToFile(String url) {
        File file = new File(new File(dirUploadPath), StringGenerator.getRandomString());

        try {
            URL fileUrl = new URL(url);

            try (ReadableByteChannel rbc = Channels.newChannel(fileUrl.openStream())) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.getChannel().transferFrom(rbc, 0, maxFileDownloadSize);
                }
            }

            String extension = null;

            DetectFileType dft = new DetectFileType(file);
            if (dft.isKnownType()) {
                extension = MimeTypeExtension.getExtensionFromMimeType(dft.getMimeType());
            }

            if (extension != null) {
                // rename file with proper extension
                File newFilename = new File(file.getPath() + extension);
                Files.move(file.toPath(), newFilename.toPath(), StandardCopyOption.REPLACE_EXISTING);
                file = newFilename;
            } else {
                // unknown file, delete it
                LOG.error("Unknown file type.");
                Files.delete(file.toPath());
                return null;
            }
        } catch (Exception e) {
            LOG.error("URL: {}; Exception: {}", url, e);
            return null;
        }

        return file.getName();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String saveImage(@ModelAttribute(value = "data") String data, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (data == null || data.isEmpty()) {
            LOG.error("Data is empty, nothing to save.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "No data received.";
        }

        String resultUrl;
        if (data.startsWith("http://")) {
            String urlParameters = getEncodedUrlParameters(request.getParameterMap());
            resultUrl = saveURLToFile(data + urlParameters);
        } else {
            resultUrl = saveBase64ToFile(data);
        }

        if (resultUrl == null) {
            LOG.error("No file saved.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Unable to parse data.";
        }

        return resultUrl;
    }

    private String getEncodedUrlParameters(Map<String, String[]> parameters) throws UnsupportedEncodingException {
        String urlParameters = "";

        if (parameters != null && parameters.size() > 1) {
            for (Map.Entry<String, String[]> p : parameters.entrySet()) {
                String key = p.getKey();
                String[] values = p.getValue();

                if (key.startsWith("data") || values.length == 0) {
                    continue;
                }

                urlParameters += "&" + URLEncoder.encode(p.getKey(), CHARSET) + "=" + URLEncoder.encode(p.getValue()[0], CHARSET);
            }
        }

        return urlParameters;
    }
}
