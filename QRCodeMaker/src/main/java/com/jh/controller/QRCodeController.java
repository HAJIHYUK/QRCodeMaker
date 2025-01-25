package com.jh.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Controller
public class QRCodeController {

    // 이미지 저장 위치 (static/uploads)
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping("/")
    public String home() {
        return "index"; // index.jsp 로드
    }

    @PostMapping("/qr/link")
    public String generateQRCodeLink(@RequestParam("text") String text, Model model) {
        try {
            
            QRCodeWriter qrCodeWriter = new QRCodeWriter(); //QR코드 생성기 객체 생성
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, 250, 250); //text를 QR코드로 변환 크기는 250, 250 , 이미지가 아닌 단순히 qr코드 정보를 담은 2차원 배열형태

            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); //QR 코드 이미지를 바이트 배열로 변환하기 위한 ByteArrayOutputStream
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream); //bitMatrix를 PNG 형식의 이미지로 전환하여 outputStream에 저장, 실질적 png 변환단계

            // Base64로 인코딩
            String qrCodeImage = Base64.getEncoder().encodeToString(outputStream.toByteArray()); 

            // 모델에 QR 코드 이미지 추가
            model.addAttribute("qrCode", "data:image/png;base64," + qrCodeImage); //"data:image/png;base64,"는 Base64로 인코딩된 이미지 데이터를 HTML에서 사용할 수 있도록 하기 위해 필요한 data URI 형식
            model.addAttribute("text", text);

        } catch (WriterException | IOException e) {
            model.addAttribute("error", "QR 코드 생성에 실패했습니다: " + e.getMessage());
        }
        return "index"; // 결과를 같은 JSP에 렌더링
    }
    
    
    

    // 이미지 업로드 후 URL을 QR 코드로 생성
    @PostMapping("/qr/image")
    public String generateQRCodeFromImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            // 파일이 비어 있으면 오류 처리
            if (file.isEmpty()) {
                model.addAttribute("error", "파일을 선택해주세요.");
                return "index";
            }

            // 파일 저장
            String originalFilename = file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + originalFilename);
            Files.createDirectories(path.getParent()); // 디렉토리 생성
            file.transferTo(path); // 파일 저장

            // 이미지 URL (로컬 IP 주소 사용)
            String imageUrl = "http://192.168.219.187:8080/uploads/" + originalFilename;

            // URL을 QR 코드로 생성
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(imageUrl, BarcodeFormat.QR_CODE, 250, 250);

            // QR 코드를 PNG 이미지로 변환
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);

            // Base64로 인코딩
            String qrCodeImage = Base64.getEncoder().encodeToString(outputStream.toByteArray());

            // 모델에 QR 코드 이미지 추가
            model.addAttribute("qrCode", "data:image/png;base64," + qrCodeImage);
            model.addAttribute("imageUrl", imageUrl);

        } catch (WriterException | IOException e) {
            model.addAttribute("error", "QR 코드 생성에 실패했습니다: " + e.getMessage());
        }

        return "index"; // 결과를 같은 JSP에 렌더링
    }

}
