package com.qr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.qr.service.QRCodeService;

@Controller
public class QRCodeController {
    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @PostMapping("/qr/link")
    public String generateQRCodeLink(@RequestParam("text") String text, Model model) {
        try {
            String qrCodeImage = qrCodeService.generateQRCodeFromText(text);
            model.addAttribute("qrCode", "data:image/png;base64," + qrCodeImage);
            model.addAttribute("text", text);
        } catch (Exception e) {
            model.addAttribute("error", "QR 코드 생성에 실패했습니다: " + e.getMessage());
        }
        return "index";
    }

    @PostMapping("/qr/image")
    public String generateQRCodeFromImage(@RequestParam("file") MultipartFile file, Model model) {
        try {
            String qrCodeImage = qrCodeService.generateQRCodeFromImage(file);
            model.addAttribute("qrCode", "data:image/png;base64," + qrCodeImage);
            model.addAttribute("imageUrl", "http://192.168.219.187:8080/uploads/" + file.getOriginalFilename());
        } catch (Exception e) {
            model.addAttribute("error", "QR 코드 생성에 실패했습니다: " + e.getMessage());
        }
        return "index";
    }
}