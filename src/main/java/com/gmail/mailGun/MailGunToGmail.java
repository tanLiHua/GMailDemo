package com.gmail.mailGun;

import kong.unirest.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;

public class MailGunToGmail {

        // ...
    static final String YOUR_DOMAIN_NAME = "sandbox85c45374dda441e5bde45ec3ff1f839e.mailgun.org";
    static final String API_KEY = "9c3d638e1e11ebb675ea2cd8aacc8190-0a4b0c40-e834b8b7";

    public static JsonNode sendSimpleMessage(String targetMail) throws UnirestException {
        HttpRequestWithBody httpRequestWithBody = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages");
        if(StringUtils.isNotEmpty(targetMail)){
            String[] mails = targetMail.split(",");
            for (int i=0;i<mails.length;i++){
                if(StringUtils.isNotEmpty(mails[i])){
                    String mail = "\"" + mails[i] + "\"";
                    httpRequestWithBody.field("to", mail);
                    System.out.println("mails:"+mail);
                }
            }

        }
        HttpResponse<JsonNode> request = httpRequestWithBody
                .basicAuth("api", API_KEY)
                .field("from", "Excited User <USER@YOURDOMAIN.COM>")
                .field("subject", "helloGmailFromMailGun")
                .field("text", "mailGun send success!!!")
                .asJson();

        return request.getBody();
    }
    //多人批量发送
    public static JsonNode batchSending(String targetMail){
        return sendSimpleMessage(targetMail);

    }

    public static void main(String[] args) {
       // sendSimpleMessage("1004974993qq@gmail.com");
        System.out.println(batchSending("1004974993qq@gmail.com,1004974993@qq.com"));
    }

    // 发送Html和TXT文本

    public static JsonNode sendComplexMessage() throws UnirestException {

        HttpResponse<JsonNode> request = Unirest.post("https://api.mailgun.net/v3/" + YOUR_DOMAIN_NAME + "/messages")
                .basicAuth("api", API_KEY)
                .field("from", "MailGun <USER@YOURDOMAIN.COM>")
                .field("to", "alice@example.com")
              //  .field("cc", "bob@example.com")
              //  .field("bcc", "joe@example.com")
                .field("subject", "Hello")
                .field("text", "Testing out some Mailgun awesomeness!")
                .field("html", "<html>HTML version </html>")
                .field("attachment", new File("/temp/folder/test.txt"))
                .asJson();

        return request.getBody();
    }
}
