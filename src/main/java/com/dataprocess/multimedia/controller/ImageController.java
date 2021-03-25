package com.dataprocess.multimedia.controller;

import com.dataprocess.multimedia.dao.ImageDao;
import com.dataprocess.multimedia.result.MsgResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;

@Controller
@RequestMapping("/image")
public class ImageController {

    private final ImageDao imageDao = new ImageDao();
    private String curFilePath = "";

    @GetMapping("/index")
    public String index() {
        return "image";
    }

    @PostMapping("/upload")
    @ResponseBody
    public MsgResult upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        this.curFilePath = this.imageDao.upload(file);
        return MsgResult.uploadSuccess("");
    }

    @RequestMapping("/togray")
    @ResponseBody
    public MsgResult toGray(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        byte[] bytes = this.imageDao.toGray(this.curFilePath);
        return MsgResult.imageTransformSuccess(bytes);
    }

    @RequestMapping("/togreen")
    @ResponseBody
    public MsgResult toGreen(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        byte[] bytes = this.imageDao.toGreen(this.curFilePath);
        return MsgResult.imageTransformSuccess(bytes);
    }

    @RequestMapping("/toblue")
    @ResponseBody
    public MsgResult toBlue(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        byte[] bytes = this.imageDao.toBlue(this.curFilePath);
        return MsgResult.imageTransformSuccess(bytes);
    }

    @RequestMapping("/tored")
    @ResponseBody
    public MsgResult toRed(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        byte[] bytes = this.imageDao.toRed(this.curFilePath);
        return MsgResult.imageTransformSuccess(bytes);
    }

    @GetMapping("/randomconfig")
    public String toRandomConfig() {
        return "randomSubmit";
    }

    @RequestMapping("/random")
    @ResponseBody
    public MsgResult toRandom(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
        int xBlocks = Integer.parseInt(request.getParameter("x_blocks"));
        int yBlocks = Integer.parseInt(request.getParameter("y_blocks"));
        byte[] bytes = this.imageDao.toRandom(this.curFilePath, xBlocks, yBlocks);
        return MsgResult.imageTransformSuccess(bytes);
    }



//    @GetMapping(value = "/catch-image", produces = MediaType.IMAGE_JPEG_VALUE)
//    @ResponseBody
//    public byte[] catchImage(@RequestParam("filename") String filename) throws IOException {
//        File img = new File(FileProgress.imagePathResult + File.separator + filename);
//        FileInputStream inputStream = new FileInputStream(img);
//        byte[] bytes = new byte[inputStream.available()];
//        inputStream.read(bytes, 0, inputStream.available());
//        return bytes;
//    }

}
