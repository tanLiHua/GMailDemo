package com.gmail.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonUtils {

    /**
     *  JsonArray解析成List<Map<String,String>>
     * @param response
     * @return 元素是map的list集合
     */
    public static List JsonArrayParseList(HttpResponse<JsonNode> response){
        JSONObject json = (JSONObject)JSONObject.parse(response.getBody().toString());

        List list = (List) json.get("items");

        return list;
    }

    /**
     * 根据事件类型查询邮件数量
     * @param list
     * @param eventType 查询条件
     * @return 邮件数量
     */
    public static int selectTotalByType(List list,String  eventType){
        /**存储messageId,eventType(追踪事件的类型) **/
        Map<String,String> map= new HashMap<>();
        int total=0;
        if("opened".equals(eventType) || "clicked".equals(eventType)){

            for(int i=0 ;i<list.size();i++){
                JSONObject jsonObject =(JSONObject) list.get(i);
                JSONObject headJson =(JSONObject) jsonObject.get("message");
                Map<String,String> msgMap = (Map<String,String>)headJson.get("headers");

                if(!map.containsKey(msgMap.get("message-id"))){
                    map.put(msgMap.get("message-id"),"message-id");
                    total++;
                }

            }

        }else {
           total=list.size();
        }

        return total;
    }

    /**
     * 利用流读取文件内容
     * @param html html文件名
     * @throws IOException
     */
    public static String readAndWriteHtml(String html) throws IOException {
        File htmlFile = new File("C:" + File.separator + "tmp" + File.separator + "html" + File.separator, html);
        InputStreamReader isReader = null;
        BufferedReader bufReader = null;
        StringBuffer buf = new StringBuffer();
        try {
            isReader = new InputStreamReader(new FileInputStream(htmlFile), "utf-8");
            bufReader = new BufferedReader(isReader, 1);
            String data;
            while((data = bufReader.readLine())!= null) {

                buf.append(data);
            }

        } catch (Exception e) {
            //TODO 处理异常
        } finally {
            //TODO 关闭流
            isReader.close();

            bufReader.close();

        }
        System.out.print(buf.toString());
        return buf.toString();
    }

    /**
     * 计算百分率(会出现打开数比总数多的情况：日志只保存五天，发送)
     * @param targetNum  目标数（分子）
     * @param total 总数（分母）
     * @return
     */
    public static String getPercentResult(int targetNum,int total){
        if(targetNum!=0 && total!=0){
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMaximumFractionDigits(2);
            String result = numberFormat.format((float) targetNum / (float) total * 100);
            return result+"%";
        }
        return "0%";
    }


    static final String YOUR_DOMAIN_NAME = "mailguntt.info";
    static final String API_KEY = "9c3d638e1e11ebb675ea2cd8aacc8190-0a4b0c40-e834b8b7";
    public static void main(String[] args) {
      /*  Map<String,String> map= new HashMap<>();
        int total=0;
        HttpResponse<JsonNode> response = Unirest.get("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/events")
                .basicAuth("api", API_KEY)
                .queryString("event", "opened")
                .asJson();
        JsonNode node = response.getBody();
        JSONObject o = JSONObject.parseObject(node.toString());
       // System.out.println("node:" + node.toString());
        List items = (List) o.get("items");
       for(int i=0 ;i<items.size();i++){
           JSONObject jsonObject =(JSONObject) items.get(i);
           JSONObject headJson =(JSONObject) jsonObject.get("message");
           Map<String,String> msgMap = (Map<String,String>)headJson.get("headers");

           if(!map.containsKey(msgMap.get("message-id"))){
               map.put(msgMap.get("message-id"),"message-id");
               total++;
           }

       }
        System.out.println("total:" +total);*/
    }
}
