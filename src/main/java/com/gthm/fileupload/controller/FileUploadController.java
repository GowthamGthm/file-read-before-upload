package com.gthm.fileupload.controller;


import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@RestController
public class FileUploadController {

    @PostMapping("/upload")
    public Mono<Void> uploadFile(@RequestPart("file") FilePart filePart) {
        return filePart.content()
                .doOnNext(dataBuffer -> {
                    // Log or perform an action to indicate processing has started
                    System.out.println("------------------------ Processing started ----------------------------");
                })
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .flatMap(content -> {
                    // Split content by lines and process each line
                    String[] lines = content.split("\n");
                    for (String line : lines) {
                        // Process each line (e.g., parse CSV fields)
                        System.out.println(line);
                    }
                    return Mono.empty();
                })
                .then();
    }


    @PostMapping("/upload2")
    public Mono<Void> uploadFile2(@RequestPart("file") FilePart filePart) {
        StringBuilder accumulator = new StringBuilder();

        return filePart.content()
                .doOnNext(dataBuffer -> {
                    // Log or perform an action to indicate processing has started
                    System.out.println("------------------------ Processing started ----------------------------");
                })
                .map(dataBuffer -> {
                    byte[] bytes = new byte[dataBuffer.readableByteCount()];
                    dataBuffer.read(bytes);
                    return new String(bytes, StandardCharsets.UTF_8);
                })
                .flatMap(content -> {
                    accumulator.append(content);
                    String accumulatedContent = accumulator.toString();
                    String[] lines = accumulatedContent.split("\n");

                    // Process all complete lines
                    for (int i = 0; i <= lines.length - 1; i++) {
                        System.out.println("Processing line: " + lines[i]);
                    }

                    // Check if the last character of the accumulated content is a newline
                    if (accumulatedContent.charAt(accumulatedContent.length() - 1) == '\n') {
                        // The last line is complete
//                        System.out.println("Processing line: " + lines[lines.length - 1]);
                        accumulator.setLength(0); // Clear the accumulator
                    } else {
                        // The last line is incomplete, keep it in the accumulator
                        accumulator.setLength(0);
                        accumulator.append(lines[lines.length - 1]);
                        System.out.println("last line incomplete - " + accumulator);
                    }

                    return Mono.empty();
                })
                .then(Mono.defer(() -> {
                    // Process any remaining content in the accumulator
                    if (accumulator.length() > 0) {
                        System.out.println("Processing last line: " + accumulator.toString());
                    }
                    return Mono.empty();
                }));
    }

}