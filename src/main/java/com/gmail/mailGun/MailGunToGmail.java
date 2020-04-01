package com.gmail.mailGun;

import com.alibaba.fastjson.JSONObject;
import com.gmail.common.CommonUtils;
import com.gmail.common.ResponseData;
import kong.unirest.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public static ResponseData sendSimpleMail(MailInfo mailInfo)  {
        HttpRequestWithBody requestWithBody = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages");
        MultipartBody multipartBody = requestWithBody
                .basicAuth("api", API_KEY)
                   .field("from", mailInfo.getFromAddr())   //"mailguntt <mail@mailguntt.info.COM>"
                .field("subject", mailInfo.getSubject())
                .field("text", mailInfo.getText())
                .field("o:tracking", "yes") //开启追踪
                .field("to",mailInfo.getToAddr());
       /* if(to.contains(",")){
            String[] targets = to.split(",");
            List<String> list= new ArrayList<>();
           for(int i=0 ; i<targets.length;i++){
                list.add(targets[i]);
           }
            multipartBody.field("to",list);
        }else{
            multipartBody.field("to",to);
        }

        if (StringUtils.isNotEmpty(mailInfo.getAttachFileNames())) {
            File attFile = new File("C:" + File.separator + "tmp" + File.separator + "attachment" + File.separator , attachmentName);
            multipartBody.field("attachment", attFile);

        }*/
        /**
         * 附件
         */
        if(CollectionUtils.isNotEmpty(mailInfo.getAttachFileNames())){
            multipartBody.field("attachment", mailInfo.getAttachFileNames());
        }
        JsonNode jsonNode = multipartBody.asJson().getBody();
        System.out.println("返回数据：" + jsonNode);
        Map<String, String> map = (Map<String, String>) com.alibaba.fastjson.JSONObject.parse(jsonNode.toString());
        if (map.get("id") != null) {
            return ResponseData.returnData(true,"发送成功");
        }
        return ResponseData.returnData(false,map.get("message"));

    }

    /**
     * 发送邮件
     *
     * @param
     * @param type  邮件发送类型（普通文本/html邮件）
     */
    public static ResponseData sendMail(MailInfo mailInfo){
        ResponseData responseData = null;
        int count = mailInfo.getToAddr().size();
        try{


            if (StringUtils.isNotEmpty(mailInfo.getHtmlFile())) {  //html邮件
                responseData = MailGunToGmail.sendComplexMessage(mailInfo);
            } else {   //文本邮件
                    responseData = MailGunToGmail.sendSimpleMail(mailInfo);
            }

        } catch (Exception e){
            if (e instanceof SocketException || e instanceof InterruptedException) {
                logger.info("发送频率过快,线程进入休眠:" + e.getMessage());
                try {
                    Thread.sleep(1000 * 30 );

                } catch (InterruptedException e1) {
                    logger.error("线程休眠异常" + e1.getMessage());
                }
                System.out.println("线程休眠结束,开始发送邮件");
                logger.info("线程休眠结束,开始发送邮件");
                sendMail(mailInfo);
            }

        }
        if (responseData.isFlag()) {
            System.out.println( count + "邮件发送成功");
            logger.info("邮件发送完成！");
            return ResponseData.returnData(true, "邮件发送成功", count);

        }
        return responseData;
    }

    // 发送Html和TXT文本

    public static ResponseData sendComplexMessage(MailInfo mailInfo) throws  IOException {
        /** 用缓冲流读取html字符串（html内容超出String长度） */
        String htmlContent = CommonUtils.readAndWriteHtml(mailInfo.getHtmlFile());
        MultipartBody multipartBody = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", mailInfo.getFromAddr())   //"MailGun <USER@YOURDOMAIN.COM>"
                .field("to", mailInfo.getToAddr())
                .field("o:tracking", "yes") //开启追踪
                .field("subject", mailInfo.getSubject())
                .field("text", mailInfo.getText())
                .field("html", htmlContent);
       /* if(to.contains(",")){
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

        }*/
       if(CollectionUtils.isNotEmpty(mailInfo.getAttachFileNames())){
           multipartBody.field("attachment", mailInfo.getAttachFileNames());
       }
        if (StringUtils.isNotEmpty(mailInfo.getCc())) {
            multipartBody.field("cc", mailInfo.getCc());  //抄送

        }
        if (StringUtils.isNotEmpty(mailInfo.getBcc())) {
            multipartBody.field("bcc", mailInfo.getBcc()); //暗抄送
        }

        JsonNode jsonNode = multipartBody.asJson().getBody();
        Map<String, String> map = (Map<String, String>) com.alibaba.fastjson.JSONObject.parse(jsonNode.toString());
        if (map.get("id") != null) {

           return ResponseData.returnData(true,"发送成功");
        }

        return ResponseData.returnData(false,map.get("message"));

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
    public static int getSendTotal() throws ParseException {
        GetRequest request = Unirest.get("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/stats/total");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = simpleDateFormat.parse("2020-03-01");
        ZoneId zoneId = ZoneId.of("America/Los_Angeles");
        ZonedDateTime dateTime = date.toInstant().atZone(zoneId);
        LocalDate localDate = dateTime.toLocalDate();
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
        Calendar calendar = Calendar.getInstance(timeZone);
        //获得时区和 GMT-0 的时间差,偏移量
        int offset = calendar.get(Calendar.ZONE_OFFSET);
        //获得夏令时  时差
        int dstoff = calendar.get(Calendar.DST_OFFSET);
        //calendar.add(, - (offset + dstoff));
        String s1 = formatDate(date);
        HttpResponse<JsonNode> response = request.basicAuth("api", API_KEY).queryString("event", "accepted")
                .queryString("start",s1)
                .asJson();
        String s = response.getBody().toString();
        JSONObject jsonObject =(JSONObject) JSONObject.parse(s);
        Object start = jsonObject.get("start");
        System.out.println("startDate:  "+ start);
        //List list = CommonUtils.JsonArrayParseList(response);
        //int sendTotal = CommonUtils.selectTotalByType(list, "accepted");
       // System.out.println("邮件发送数:" + sendTotal);
        //getDeliveredTotal();//13
        //getOpentTotal();//5
        //return sendTotal;
        return 0;
    }
    public static String formatDate(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String format = simpleDateFormat.format(date);
        System.out.println(format);
        return format;
    }

    public static void main(String[] args) throws ParseException {
        getSendTotal();
    }

    /**
     * 查询邮件已递送成功数
     *
     * @return
     * @throws UnirestException
     */

    public static int getDeliveredTotal()  {

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
