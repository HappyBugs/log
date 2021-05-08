package org.example.logs;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class TestController3 {

    @ShowParam
    @PostMapping(value = "test3")
    public void nihao(MultipartFile multipartFile, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        InputStream inputStream = multipartFile.getInputStream();
        byte[] buf = new byte[8192];
        int n;
        while ((n = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, n);
        }
        outputStream.flush();
        outputStream.close();
    }

}
