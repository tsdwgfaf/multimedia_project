package com.dataprocess.multimedia.dao;


import com.dataprocess.multimedia.result.FileProgress;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class VoiceDao {

    public String upload(MultipartFile file) {
        return FileProgress.saveVoice(file);
    }

    /**
     * 解析音频文件
     * @param filePath
     * @return
     */
    public byte[] transformPicToImage(String filePath) throws IOException, InterruptedException {
        String[] fileNameSplitArray = filePath.split("\\\\");
        fileNameSplitArray = fileNameSplitArray[fileNameSplitArray.length - 1].split("\\.");
        String resultFileName = fileNameSplitArray[0] + ".png";
        String resultFilePath = FileProgress.voicePathTransformed + File.separator + resultFileName;
        String pyPath = FileProgress.pyPath + File.separator + "wav_reader.py";
        String[] args = new String[] {
                "python",
                pyPath,
                filePath,
                resultFilePath
        };
        Process process = Runtime.getRuntime().exec(args);
        System.out.println(process.waitFor());
        File img = new File(resultFilePath);
        FileInputStream inputStream = new FileInputStream(img);
        byte[] bytes = new byte[inputStream.available()];
        inputStream.read(bytes, 0, inputStream.available());
        return bytes;
    }
}
