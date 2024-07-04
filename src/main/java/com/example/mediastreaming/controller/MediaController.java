package com.example.mediastreaming.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class MediaController {

    private static final String FILE_PATH = "D:\\Skate Into Love\\demo.mp4";

    @GetMapping("/video")
    public ResponseEntity<InputStreamResource> streamVideo(@RequestHeader(value = "Range", required = false) String rangeHeader) throws IOException {
        File videoFile = new File(FILE_PATH);
        long fileLength = videoFile.length();
        long rangeStart = 0;
        long rangeEnd = fileLength - 1;

        if (rangeHeader != null && rangeHeader.startsWith("bytes=")) {
            String[] ranges = rangeHeader.substring(6).split("-");
            try {
                rangeStart = Long.parseLong(ranges[0]);
                if (ranges.length > 1) {
                    rangeEnd = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                rangeStart = 0;
                rangeEnd = fileLength - 1;
            }
        }

        if (rangeEnd > fileLength - 1) {
            rangeEnd = fileLength - 1;
        }

        long contentLength = rangeEnd - rangeStart + 1;
        InputStream inputStream = new FileInputStream(videoFile);
        inputStream.skip(rangeStart);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentLength(contentLength);
        headers.setContentType(MediaType.parseMediaType("video/mp4"));
        headers.set("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);

        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.PARTIAL_CONTENT);
    }
}
