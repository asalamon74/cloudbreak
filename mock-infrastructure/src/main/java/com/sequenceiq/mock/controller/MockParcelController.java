package com.sequenceiq.mock.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MockParcelController {

    @GetMapping("/mock-parcel/**")
    public ResponseEntity<HttpStatus> getMockParcel() {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_LENGTH, "1024");
        return new ResponseEntity<>(responseHeaders, HttpStatus.OK);
    }

}
