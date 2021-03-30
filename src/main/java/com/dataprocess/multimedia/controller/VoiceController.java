package com.dataprocess.multimedia.controller;

import com.dataprocess.multimedia.dao.VoiceDao;
import com.dataprocess.multimedia.result.MsgResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/voice")
public class VoiceController {

    private final VoiceDao voiceDao = new VoiceDao();
    private String curFilePath = "";

    @GetMapping("/index")
    public String index() {
        return "voice";
    }

    @PostMapping("/upload")
    @ResponseBody
    public MsgResult upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException, InterruptedException {
        this.curFilePath = this.voiceDao.upload(file);
        byte[] bytes = this.voiceDao.transformPicToImage(this.curFilePath);
        return MsgResult.imageTransformSuccess(bytes);
    }

//    @RequestMapping("/topic")
//    @ResponseBody
//    public MsgResult toRed(HttpServletRequest request, HttpServletResponse response) throws IOException, InterruptedException {
//        byte[] bytes = this.voiceDao.transformPicToImage(this.curFilePath);
//        return MsgResult.imageTransformSuccess(bytes);
//    }
}
