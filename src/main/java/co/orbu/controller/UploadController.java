package co.orbu.controller;

import co.orbu.utils.MimeTypeExtension;
import co.orbu.utils.StringGenerator;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobProperties;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@Controller
public class UploadController {

    private static final String CHARSET = "UTF-8";
    private static final Logger LOG = LogManager.getLogger(UploadController.class);

    private final CloudBlobContainer storageService;

    @Autowired
    public UploadController(CloudBlobContainer storageService) {
        this.storageService = Objects.requireNonNull(storageService);
    }

    @RequestMapping("/")
    public String index() {
        return "index";
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
                    base64Data = s.substring(7).replace(' ', '+');
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

        byte[] rawData = Base64.getDecoder().decode(base64Data);
        return saveFile(rawData, mimeType);
    }

    /**
     * Downloads file from web and saves it to file.
     *
     * @param url URL of a file to download.
     * @return File name of saved file or null if no file was saved.
     */
    private String saveURLToFile(String url) {
        byte[] rawData;
        String mimeType;

        try {
            URL fileUrl = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) fileUrl.openConnection();
            connection.connect();

            mimeType = connection.getContentType();

            try (InputStream is = connection.getInputStream(); ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                byte[] chunk = new byte[8192];
                int bytesRead;

                while ((bytesRead = is.read(chunk)) > 0) {
                    outputStream.write(chunk, 0, bytesRead);
                }

                // TODO: check if downloading exceeds max. file size

                rawData = outputStream.toByteArray();
            }
        } catch (Exception e) {
            LOG.error("Unable to download image: " + url, e);
            return null;
        }

        if (rawData == null || rawData.length == 0) {
            return null;
        }

        return saveFile(rawData, mimeType);
    }

    private String saveFile(byte[] rawData, String mimeType) {
        String extension = MimeTypeExtension.getExtensionFromMimeType(mimeType);
        String filename = StringGenerator.getRandomString() + extension;

        try {
            CloudBlockBlob blob = storageService.getBlockBlobReference(filename);
            blob.uploadFromByteArray(rawData, 0, rawData.length);

            BlobProperties properties = blob.getProperties();
            properties.setContentType(mimeType);
            blob.uploadProperties();

            return blob.getUri().toASCIIString();
        } catch (URISyntaxException | StorageException | IOException e) {
            LOG.error("Unable to upload file to Azure.", e);
        }

        return null;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public String saveImage(@ModelAttribute(value = "data") String data, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (data == null || data.isEmpty()) {
            LOG.error("Data is empty, nothing to save.");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "No data received.";
        }

        String resultUrl;
        if (data.startsWith("http")) {
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
