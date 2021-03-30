package com.dataprocess.multimedia.result;

import org.springframework.util.ClassUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileProgress {

    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
//    private static final String staticPath = Objects.requireNonNull(ClassUtils.getDefaultClassLoader()).getResource("static").getPath();
    public static final String staticPath = System.getProperty("user.dir")
                                            + File.separator + "src"
                                            + File.separator + "main"
                                            + File.separator + "resources"
                                            + File.separator + "static";
    public static final String imagePathUploaded = staticPath + File.separator + "image" + File.separator + "uploaded";
    public static final String imagePathResult = staticPath + File.separator + "image" + File.separator + "transformed";
    public static final String voicePathUploaded = staticPath + File.separator + "voice" + File.separator + "uploaded";
    public static final String voicePathTransformed = staticPath + File.separator + "voice" + File.separator + "transformed";
    public static final String pyPath = System.getProperty("user.dir")
                                        + File.separator + "src"
                                        + File.separator + "main"
                                        + File.separator + "java"
                                        + File.separator + "com"
                                        + File.separator + "dataprocess"
                                        + File.separator + "multimedia"
                                        + File.separator + "python";


    public static String rename(String fileName) {
        LocalDateTime time = LocalDateTime.now();
        String[] stringArray = fileName.split("\\.");
        return time.format(format) + "." + stringArray[stringArray.length - 1];
    }

    /**
     * 保存上传的图片，返回文件存放地址
     * @param file 文件
     * @return 返回文件存放地址
     */
    public static String saveImage(MultipartFile file) {
        String fileName = rename(Objects.requireNonNull(file.getOriginalFilename()));
        String savePath = imagePathUploaded + File.separator + fileName;
        File saveFile = new File(savePath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savePath;
    }

    public static String saveVoice(MultipartFile file) {
        String fileName = rename(Objects.requireNonNull(file.getOriginalFilename()));
        String savePath = voicePathUploaded + File.separator + fileName;
        File saveFile = new File(savePath);
        if (!saveFile.exists()) {
            saveFile.mkdirs();
        }
        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return savePath;
    }

}
