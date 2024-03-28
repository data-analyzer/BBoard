package com.se.board.common.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.web.multipart.MultipartFile;

public class FileToBase64Encoder {

    public static String encodeFileToBase64(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];
            fileInputStream.read(fileBytes);
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

//    public static void main(String[] args) {
//        File file = new File("path/to/your/file.txt");
//        String base64String = encodeFileToBase64(file);
//        System.out.println(base64String);
//    }


 // 사용하는 패키지 import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
 // 해당 패키지는 jasypt-{버전}.jar 에 있다. Base64 encoding 은 다양한 방법으로 생성할 수 있다
 // 여기서는 기본 사용
    public static String encodeMutipartFileToBase64(MultipartFile file) {
    	String binaryString = null;
    	if(file != null && !file.isEmpty()) {
	        try {
	        	byte[] encodeBase64 = Base64.getEncoder().encode(file.getBytes());
	            binaryString = new String(encodeBase64, "UTF-8"); // "data:image/png;base64," + new String(encodeBase64, "UTF-8"); // 실제 data url 생성!
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
    	}
        return binaryString;
    }
}
