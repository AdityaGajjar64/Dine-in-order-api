package com.example.dine_in_order_api.controller;

import com.example.dine_in_order_api.service.QRcodeGaneratorService;
import com.google.zxing.WriterException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("${app.base-url}")
@AllArgsConstructor
public class QRcodeController {

    private final QRcodeGaneratorService qRcodeGaneratorService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(value = "/qr" ,produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> ganerateQRcode(@RequestParam("url") String url){
        try {
            byte[] qrImage = qRcodeGaneratorService.ganerateQR(url);
            return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(qrImage);
        } catch (IOException | WriterException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
