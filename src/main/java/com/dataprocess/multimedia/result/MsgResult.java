package com.dataprocess.multimedia.result;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class MsgResult implements Serializable {

    private int code;
    private String msg;
    private Object data;

    public static MsgResult success(Object data) {
        MsgResult result = new MsgResult();
        result.setCode(200);
        result.setData(data);
        return result;
    }

    public static MsgResult fail(Object data) {
        MsgResult result = new MsgResult();
        result.setCode(400);
        result.setData(data);
        return result;
    }

    public static MsgResult uploadSuccess(String path) {
        MsgResult result = new MsgResult();
        result.setCode(0);
        result.setMsg("");
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put("src", path);
        result.setData(dataMap);
        return result;
    }

    public static MsgResult imageTransformSuccess(Object data) {
        MsgResult result = new MsgResult();
        result.setCode(0);
        result.setMsg("");
        result.setData(data);
        return result;
    }
}
