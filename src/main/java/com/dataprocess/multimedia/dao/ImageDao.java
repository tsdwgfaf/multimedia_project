package com.dataprocess.multimedia.dao;

import com.dataprocess.multimedia.result.FileProgress;
import com.dataprocess.multimedia.result.MsgResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ImageDao {

    public String upload(MultipartFile file) {
        return FileProgress.saveImage(file);
    }

    private static final int GRAY = 0;
    private static final int GREEN = 1;
    private static final int BLUE = 2;
    private static final int RED = 3;
    private static final int RANDOM = 4;

    /**
     * 执行py脚本，返回处理后的图片(base64)
     * @param filepath
     * @param type
     * @return
     * @throws InterruptedException
     * @throws IOException
     */
    private byte[] execPy(String filepath, int type) throws InterruptedException, IOException {
        String suffix = "";
        String pyFile = "";
        switch (type) {
            case GRAY:
                suffix = "_gray";
                pyFile = "togray.py";
                break;
            case GREEN:
                suffix = "_green";
                pyFile = "togreen.py";
                break;
            case BLUE:
                suffix = "_blue";
                pyFile = "toblue.py";
                break;
            case RED:
                suffix = "_red";
                pyFile = "tored.py";
                break;
            default:
                assert false;
        }
        // 由于反斜杠和正斜杠的原因，此行代码在linux中可能会无法按预期执行，暂时未想到好的解决办法
        String[] fileNameSplitArray = filepath.split("\\\\");
        fileNameSplitArray = fileNameSplitArray[fileNameSplitArray.length - 1].split("\\.");
        String resultFileName = fileNameSplitArray[0] + suffix + ".png";
        String resultFilePath = FileProgress.imagePathResult + File.separator + resultFileName;
        String pyPath = FileProgress.pyPath + File.separator + pyFile;
        String[] args = new String[] {
                "python",
                pyPath,
                filepath,
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

    /**
     * 将图片转为灰度图，返回转换后图片保存的路径
     * @param filepath 原图路径
     * @return 灰度图文件名(不带路径)
     * @throws IOException
     * @throws InterruptedException
     */
    public byte[] toGray(String filepath) throws IOException, InterruptedException {
        return execPy(filepath, GRAY);
    }

    public byte[] toGreen(String filepath) throws IOException, InterruptedException {
        return execPy(filepath, GREEN);
    }
    public byte[] toRed(String filepath) throws IOException, InterruptedException {
        return execPy(filepath, RED);
    }
    public byte[] toBlue(String filepath) throws IOException, InterruptedException {
        return execPy(filepath, BLUE);
    }
    public byte[] toRandom(String filepath, int x_blocks, int y_blocks) throws IOException, InterruptedException {
        String[] fileNameSplitArray = filepath.split("\\\\");
        fileNameSplitArray = fileNameSplitArray[fileNameSplitArray.length - 1].split("\\.");
        String resultFileName = fileNameSplitArray[0] + "_random" + ".png";
        String resultFilePath = FileProgress.imagePathResult + File.separator + resultFileName;
        String pyPath = FileProgress.pyPath + File.separator + "divide_and_random.py";
        String[] args = new String[] {
                "python",
                pyPath,
                filepath,
                resultFilePath,
                "" + x_blocks,
                "" + y_blocks
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
