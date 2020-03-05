package com.gmail.mailGun;

import com.alibaba.fastjson.JSONObject;
import com.gmail.common.CommonUtils;
import com.gmail.common.ResponseData;
import kong.unirest.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MailGunToGmail {

    // ...
    static final String YOUR_DOMAIN_NAME = "mailguntt.info";
    static final String API_KEY = "9c3d638e1e11ebb675ea2cd8aacc8190-0a4b0c40-e834b8b7";
    private static final Logger logger = LoggerFactory.getLogger(MailGunToGmail.class);

    /**
     * 发送简单的纯文本邮件
     *
     * @param from
     * @param to
     * @param subject
     * @param text
     * @return
     * @throws UnirestException
     */
    public static HttpRequestWithBody sendSimpleMail(String from, String to, String subject, String text, String attachmentName) throws UnirestException {
        HttpRequestWithBody requestWithBody = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages");
        MultipartBody multipartBody = requestWithBody
                .basicAuth("api", API_KEY)
                .field("from", from)   //"mailguntt <mail@mailguntt.info.COM>"
                .field("subject", subject)
                .field("text", text)
                .field("o:tracking", "yes"); //开启追踪
        if(to.contains(",")){
            String[] targets = to.split(",");
            List<String> list= new ArrayList<>();
           for(int i=0 ; i<targets.length;i++){
                list.add(targets[i]);
           }
            multipartBody.field("to",list);
        }else{
            multipartBody.field("to",to);
        }

        if (StringUtils.isNotEmpty(attachmentName)) {
            File attFile = new File("C:" + File.separator + "tmp" + File.separator + "attachment" + File.separator , attachmentName);
            multipartBody.field("attachment", attFile);

        }

        HttpResponse<JsonNode> response = multipartBody.asJson();
        System.out.println("返回数据：" + response.getBody());
        return requestWithBody;
    }

    /**
     * 利用线程发送大量邮件
     *
     * @param total 邮件发送数量
     * @param type  邮件发送类型（普通文本/html邮件）
     */
    public static void sendMailsOfThread(String from, String to, String cc, String bcc, String subject, String text, String htmlFileName, String attachmentFileName, Integer total) {
        Thread thread = new Thread(new Runnable() {
            LocalDateTime startDate = LocalDateTime.now();
            int sendNum = 0;
            boolean flag=true;
            // 735 + 318  18min +8
            //这里判断是否多用户发送，如果发送数量多出现异常就从异常用户开始继续发送

            @Override
            public void run() {
                try {
                    if (StringUtils.isNotEmpty(htmlFileName)) {  //html邮件
                        for (int i = 0; i < total; i++) {
                            //ResponseData data = sendComplexMessage(from, to, cc, bcc, subject, text, htmlFileName, attachmentFileName);
                            JsonNode jsonNode = sendComplexMessage(from, to, cc, bcc, subject, text, htmlFileName, attachmentFileName);
                            Map<String, String> map = (Map<String, String>) com.alibaba.fastjson.JSONObject.parse(jsonNode.toString());
                            System.out.println("map:" +map);
                            if (map.get("id") != null) {
                                //成功接受到请求
                               // System.out.println("id :"+map.get("id") );
                                sendNum += 1;

                            }else {
                                System.out.println("message：" + map.get("message"));
                                flag=false;
                                throw new RuntimeException(map.get("message"));
                            }

                        }
                    } else {   //文本邮件
                        for (int i = 0; i < total; i++) {
                            sendSimpleMail(from, to, subject, text, attachmentFileName);
                            sendNum += 1;
                        }
                    }
                } catch (Exception e) {  // SocketException  UnirestException
                    if (sendNum == 0) {
                        System.out.println(e.getMessage());
                        logger.info("配置异常：" + e.getMessage());

                    } else {
                        try {
                            Thread.sleep(1000 * 60 * 2);
                        } catch (Exception e1) {
                            if (e1 instanceof SocketException) {
                                logger.info("线程休眠两分钟：SocketException ！！！！");
                            }
                            if (e1 instanceof InterruptedException) {

                                logger.info("线程中断异常：" + e.getMessage());
                            }
                        }
                        //System.out.println("线程继续开始运行！！！！");
                        logger.info("线程继续开始运行！！！！");
                        Thread.currentThread().run();
                    }

                }
                if(flag){

                    System.out.println("邮件发送成功");
                    logger.info("邮件发送完成！");
                }

            }
        });
        thread.start();
    }

    // 发送Html和TXT文本

    public static JsonNode sendComplexMessage(String from, String to, String cc, String bcc, String subject, String text, String html, String attachmentName) throws UnirestException, IOException {

        String htmlContent = CommonUtils.readAndWriteHtml(html);
        MultipartBody multipartBody = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", from)   //"MailGun <USER@YOURDOMAIN.COM>"
                .field("to", to)
                .field("o:tracking", "yes") //开启追踪
                .field("subject", subject)
                .field("text", text)
                .field("html", htmlContent);
        if(to.contains(",")){
            String[] targets = to.split(",");
            List<String> list= new ArrayList<>();
            for(int i=0 ; i<targets.length;i++){
                list.add(targets[i]);
            }
            multipartBody.field("to",list);
        }else{
            multipartBody.field("to",to);
        }
        if (StringUtils.isNotEmpty(attachmentName)) {
            File attFile = new File("C:" + File.separator + "tmp" + File.separator + "attachment" + File.separator , attachmentName);
            multipartBody.field("attachment", attFile);

        }
        if (StringUtils.isNotEmpty(cc)) {
            multipartBody.field("cc", cc);  //抄送

        }
        if (StringUtils.isNotEmpty(bcc)) {
            multipartBody.field("bcc", bcc); //暗抄送
        }

        JsonNode jsonNode = multipartBody.asJson().getBody();
        //Map<String, String> map = (Map<String, String>) com.alibaba.fastjson.JSONObject.parse(jsonNode.toString());
       /* if (map.get("id") != null) {

           return ResponseData.returnData(true,"发送成功");
        }

        return ResponseData.returnData(false,map.get("message"));*/
        return jsonNode;
    }

    /**
     * 发送邮件
     *
     * @param targetCount 目标账户（多个账号用）
     */
    public static void sendToManyPeople(String targetCount) {

        if (!StringUtils.contains(targetCount, ",")) {  // 单个账号

        }
        if (StringUtils.isNoneBlank(targetCount)) {
            String[] split = StringUtils.split(targetCount, ",");
        }
    }

    /**
     * 查询邮件发送数
     *
     * @return
     * @throws UnirestException
     */
    public static int getSendTotal() throws UnirestException {
        GetRequest request = Unirest.get("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/events");
        HttpResponse<JsonNode> response = request.basicAuth("api", API_KEY).queryString("event", "accepted").asJson();
        List list = CommonUtils.JsonArrayParseList(response);
        int sendTotal = CommonUtils.selectTotalByType(list, "accepted");
        System.out.println("邮件发送数:" + sendTotal);
        getDeliveredTotal();//13
        getOpentTotal();//5
        return sendTotal;

    }

    /**
     * 查询邮件已递送成功数
     *
     * @return
     * @throws UnirestException
     */

    public static int getDeliveredTotal() throws UnirestException {

        HttpResponse<JsonNode> response = Unirest.get("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/events")
                .basicAuth("api", API_KEY)
                .queryString("event", "delivered") //查询已递送成功的邮件
                .asJson();

        List list = CommonUtils.JsonArrayParseList(response);

        int deliveredTotal = CommonUtils.selectTotalByType(list, "delivered");

        System.out.println("邮件发送成功数:" + deliveredTotal);

        return deliveredTotal;
    }


    /**
     * 查询邮件回执数（打开邮件数）
     * 打开几次就会计算几次，会造成打开数比发件数还多的情况。只有通过状态来判断数量
     * @return
     * @throws UnirestException
     */

    public static int getOpentTotal() throws UnirestException {

        HttpResponse<JsonNode> response = Unirest.get("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/events")
                .basicAuth("api", API_KEY)
                .queryString("event", "opened")
                .asJson();
        List list = CommonUtils.JsonArrayParseList(response);
        int openTotal = CommonUtils.selectTotalByType(list, "opened");
        System.out.println("邮件打开数:" + openTotal);

        return openTotal;
    }

}
