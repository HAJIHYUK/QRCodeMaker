package com.qr.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QRCodeService {
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public String generateQRCodeFromText(String text) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    public String generateQRCodeFromImage(MultipartFile file) throws IOException, WriterException {
        if (file.isEmpty()) {
            throw new IOException("파일을 선택해주세요.");
        }

        String originalFilename = file.getOriginalFilename();
        Path path = Paths.get(UPLOAD_DIR + originalFilename);
        Files.createDirectories(path.getParent());
        file.transferTo(path);

        String imageUrl = "http://192.168.219.187:8080/uploads/" + originalFilename;

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(imageUrl, BarcodeFormat.QR_CODE, 250, 250);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}