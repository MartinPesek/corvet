package co.orbu.controller;

import co.orbu.utils.StringGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Controller
public class UploadController {

    @Value("${ofs.dirUploadPath}")
    private String dirUploadPath;

    @Value("${ofs.baseImageUrl}")
    private String baseImageUrl;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public String saveImage(@ModelAttribute(value = "data") String data, HttpServletResponse response) throws IOException {
        if (data == null || data.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "No data received.";
        }

        String[] mimeTypeBase64 = data.split(";");

        String base64Data = null;
        String extension = null;

        for (String s : mimeTypeBase64) {
            int colonIndex = s.indexOf(':');
            if (colonIndex == -1) {
                if (s.toLowerCase().startsWith("base64,"))
                    base64Data = s.substring(6).replace(' ', '+');

                continue;
            }

            String[] header = s.split(":");
            if (!header[0].toLowerCase().equals("data"))
                continue;

            switch (header[1]) {
                case "image/png":
                    extension = ".png";
                    break;

                case "image/jpg":
                    extension = ".jpg";
                    break;

                case "image/gif":
                    extension = ".gif";
                    break;
            }
        }

        // unable to parse data, do nothing
        if (base64Data == null || base64Data.isEmpty() || extension == null || extension.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Unable to parse data.";
        }

        // TODO: error/exception handling
        // TODO: this code sucks... Proof of concept though -- improve!

        byte[] rawData = DatatypeConverter.parseBase64Binary(base64Data);
        File file = new File(new File(dirUploadPath), StringGenerator.getRandomString() + extension);

        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file))) {
            dos.write(rawData, 0, rawData.length);
        }

        return baseImageUrl + file.getName();
    }
}
