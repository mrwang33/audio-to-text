package com.wh.att.util;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;
import com.wh.att.entity.Text;
import org.apache.log4j.PropertyConfigurator;

import java.util.HashMap;
import java.util.List;

public class CoreUtil {
    /*
     * 转写类型选择：标准版和电话版分别为：
     * LfasrType.LFASR_STANDARD_RECORDED_AUDIO 和 LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO
     * */
    private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
    // 等待时长（秒）
    private static int sleepSecond = 20;

    public static Message audioToText(String filePath) {
        // 初始化LFASR实例
        LfasrClientImp lc = null;
        Message uploadMsg = null;
        try {
            lc = LfasrClientImp.initLfasrClient();
        } catch (LfasrException e) {
            // 初始化异常，解析异常描述信息
            Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + initMsg.getErr_no());
            System.out.println("failed=" + initMsg.getFailed());
        }

        // 获取上传任务ID
        String task_id = "";
        HashMap<String, String> params = new HashMap<>();
        params.put("has_participle", "true");
        try {
            // 上传音频文件
            uploadMsg = lc.lfasrUpload(filePath, type, params);

            // 判断返回值
            int ok = uploadMsg.getOk();
            if (ok == 0) {
                // 创建任务成功
                task_id = uploadMsg.getData();
                System.out.println("task_id=" + task_id);
            } else {
                // 创建任务失败-服务端异常
                System.out.println("ecode=" + uploadMsg.getErr_no());
                System.out.println("failed=" + uploadMsg.getFailed());
            }
        } catch (LfasrException e) {
            // 上传异常，解析异常描述信息
            uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + uploadMsg.getErr_no());
            System.out.println("failed=" + uploadMsg.getFailed());
        }


        return uploadMsg;

    }


    public static String getResult(String id) {
        String data = "";
        // 初始化LFASR实例
        LfasrClientImp lc = null;
        try {
            lc = LfasrClientImp.initLfasrClient();
        } catch (LfasrException e) {
            // 初始化异常，解析异常描述信息
            Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + initMsg.getErr_no());
            System.out.println("failed=" + initMsg.getFailed());
        }
        // 获取任务结果
        try {
            Message resultMsg = lc.lfasrGetResult(id);
            System.out.println(resultMsg.getData());
            // 如果返回状态等于0，则任务处理成功
            if (resultMsg.getOk() == 0) {
                // 打印转写结果
                data = dealJson(resultMsg.getData());
            } else {
                // 转写失败，根据失败信息进行处理
                System.out.println("ecode=" + resultMsg.getErr_no());
                System.out.println("failed=" + resultMsg.getFailed());
                data = "ecode="+resultMsg.getErr_no()+"\nfailed="+resultMsg.getFailed();
            }
        } catch (LfasrException e) {
            // 获取结果异常处理，解析异常描述信息
            Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + resultMsg.getErr_no());
            System.out.println("failed=" + resultMsg.getFailed());
            data = "ecode="+resultMsg.getErr_no()+"\nfailed="+resultMsg.getFailed();
        }

        return data;
    }

    public static String dealJson(String jsonData) {
        List<Text> texts = JSON.parseArray(jsonData, Text.class);

        return texts.toString();
    }



}

