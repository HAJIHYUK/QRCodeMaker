<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>QR 코드 생성기</title>
</head>
<body>
    <h1>QR 코드 생성기</h1>

    <!-- 텍스트 입력 후 QR 코드 생성 -->
    <form action="/qr/link" method="post">
        <label for="text">텍스트 입력: </label>
        <input type="text" name="text" required>
        <button type="submit">QR 코드 생성</button>
    </form>

    <!-- 이미지 파일 업로드 후 QR 코드 생성 -->
    <form action="/qr/image" method="post" enctype="multipart/form-data">
        <label for="file">이미지 파일 업로드: </label>
        <input type="file" name="file" accept="image/*" required>
        <button type="submit">QR 코드 생성</button>
    </form>

    <!-- QR 코드 표시 -->
    <div>
        <h2>QR 코드</h2>
        <img src="${qrCode}" alt="QR Code">
    </div>

    <!-- 오류 메시지 표시 -->
    <div>
        <p style="color: red;">${error}</p>
    </div>
</body>
</html>
