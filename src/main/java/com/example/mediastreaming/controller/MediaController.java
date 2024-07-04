package com.example.mediastreaming.controller;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
@RequestMapping("/media")
public class MediaController {

    // Update this to the absolute path of your video file on your local disk
    private static final String FILE_PATH = "D:\\Skate Into Love/demo.mp4";

    @GetMapping("/video")
    @ResponseBody
    public ResponseEntity<InputStreamResource> streamVideo(@RequestHeader HttpHeaders headers) throws IOException {
        File file = new File(FILE_PATH);
        InputStream inputStream = new FileInputStream(file);
        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

        long fileLength = file.length();
        long rangeStart = 0;
        long rangeEnd = fileLength - 1;

        String rangeHeader = headers.getFirst(HttpHeaders.RANGE);
        if (rangeHeader != null) {
            String[] ranges = rangeHeader.split("=")[1].split("-");
            rangeStart = Long.parseLong(ranges[0]);
            if (ranges.length > 1) {
                rangeEnd = Long.parseLong(ranges[1]);
            }
        }

        long contentLength = rangeEnd - rangeStart + 1;
        inputStream.skip(rangeStart);

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + fileLength);
        responseHeaders.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        responseHeaders.add(HttpHeaders.ACCEPT_RANGES, "bytes");

        return new ResponseEntity<>(inputStreamResource, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    }
}
