package com.gmail.test;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class SendGmail {

    /*
     * gmail邮箱SSL方式
     */
    private static void gmailssl(Properties props) {
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.debug", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.connectiontimeout",30000);//套接字连接超时值毫秒。 默认值为无限超时
        props.put("mail.smtp.timeout", 50000); //套接字 I/O 超时值以毫秒为单位

    }


    //gmail邮箱的TLS方式
    private static void gmailtls(Properties props) {
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
    }

    /*
     * 通过gmail邮箱发送邮件
     */
    public static void gmailSender(String email) {

        // Get a Properties object
        Properties props = new Properties();
        //选择ssl方式
        gmailssl(props);

        final String username = "1004974993qq@gmail.com";//gmail邮箱
        final String password = "xiaohua1314.";//密码
        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        // -- Create a new message --
        Message msg = new MimeMessage(session);


        // -- Set the FROM and TO fields --
        try {
            msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            msg.setSubject("googleMailSend");
            msg.setText("googleMailSend");
            msg.setSentDate(new Date());
            Transport.send(msg);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }


        System.out.println("Message sent.");
    }

    public static void main(String[] args) throws Exception {
        String proxyHost = "127.0.0.1";
        String proxyPort = "10809";

        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);

        // 对https也开启代理
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);
        SslUtils.ignoreSsl();
        gmailSender("1004974993@qq.com");

        System.out.println("已发送到QQ邮箱。请查收！");

    }

    //javax.mail.MessagingException: Could not connect to SMTP host: smtp.gmail.com, port: 465;
    //看看代理服务器有没有开通465端口
}
