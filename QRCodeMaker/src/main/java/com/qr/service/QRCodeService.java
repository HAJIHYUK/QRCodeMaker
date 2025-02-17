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
    // QR 코드 생성 후 파일을 저장할 디렉토리 경로
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    // 텍스트로 QR 코드를 생성하는 메서드
    public String generateQRCodeFromText(String text) throws WriterException, IOException {
        // QR 코드 생성 객체 생성
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        // 텍스트를 기반으로 QR 코드의 비트맵을 생성 (250x250 크기)
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250);
        
        // 비트맵 데이터를 PNG 이미지로 변환할 출력 스트림 생성
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // 비트맵을 PNG 이미지 형식으로 출력 스트림에 작성
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        
        // 이미지를 Base64로 인코딩하여 반환 (이렇게 하면 이미지 데이터를 HTML로 쉽게 표시 가능)
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    // 이미지를 기반으로 QR 코드를 생성하는 메서드
    public String generateQRCodeFromImage(MultipartFile file) throws IOException, WriterException {
        // 파일이 비어있는지 확인, 비어 있다면 예외를 발생
        if (file.isEmpty()) {
            throw new IOException("파일을 선택해주세요.");
        }

        // 원본 파일 이름을 가져옴
        String originalFilename = file.getOriginalFilename();
        
        // 파일을 저장할 경로 설정 (uploads 폴더에 파일 저장)
        Path path = Paths.get(UPLOAD_DIR + originalFilename);
        
        // 파일을 저장할 디렉토리가 없다면 디렉토리를 생성
        Files.createDirectories(path.getParent());
        
        // MultipartFile 객체에서 실제 파일을 지정한 경로에 저장
        file.transferTo(path);

        // 파일을 업로드한 후, 해당 이미지의 URL을 생성
        String imageUrl = "http://192.168.219.187:8080/uploads/" + originalFilename;

        // QR 코드 생성 객체 생성
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        
        // 이미지 URL을 기반으로 QR 코드 비트맵을 생성 (250x250 크기)
        BitMatrix bitMatrix = qrCodeWriter.encode(imageUrl, BarcodeFormat.QR_CODE, 250, 250);

        // 비트맵 데이터를 PNG 이미지로 변환할 출력 스트림 생성
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        // 비트맵을 PNG 이미지 형식으로 출력 스트림에 작성
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

        // 이미지를 Base64로 인코딩하여 반환 (이미지 URL을 기반으로 생성한 QR 코드를 Base64 형식으로 반환)
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }
}
