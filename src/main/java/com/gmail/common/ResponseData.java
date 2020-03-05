package com.gmail.common;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
@Data
@AllArgsConstructor
public class ResponseData implements Serializable {

    private boolean flag;
    private String msg;
    private Object data;


    public static ResponseData returnData(boolean flag, String msg){
        return returnData(flag, msg, null);
    }
    public static ResponseData returnData(boolean flag,String msg,Object data){
        return new ResponseData(flag, msg, data);
    }
}
