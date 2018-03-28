package com.wh.att.controller;


import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.wh.att.io.IdToFile;
import com.wh.att.util.CoreUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
public class MainController {

    @RequestMapping("/index")
    public String index() {
        return "forward:/index.html";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public String uploadAudio(@RequestParam("file") MultipartFile file) {

        String result = "";

        if (file.isEmpty()) {
            result = "请选择上传文件";
            return result;
        }

        try {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get("F://upload/audio" + file.getOriginalFilename());

            File localFile = new File("F://upload/audio" + file.getOriginalFilename());
            // 判断文件是否存在
            if (localFile.exists()) {
                return "文件已经存在 请勿重复上传";
            }

            Files.write(path, bytes);

            // 创建音频转文字任务
            Message message = CoreUtil.audioToText("F://upload/audio" + file.getOriginalFilename());
            if (message.getOk() == 0) {
                result = "文件上传成功！请使用"+message.getData()+"进入首页查看状态!";
                // 保存id
                IdToFile.saveId(message.getData());
            } else {
                result = "音频处理失败!";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    @PostMapping("/getText")
    @ResponseBody
    public String getText(@RequestParam("taskId") String id) {
        return CoreUtil.getResult(id);
    }


}
