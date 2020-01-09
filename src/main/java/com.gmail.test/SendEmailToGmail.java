package com.gmail.test;

import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.util.MailSSLSocketFactory;
import sun.awt.windows.ThemeReader;

import java.security.GeneralSecurityException;

public class SendEmailToGmail {

    public static void sendEmail() throws Exception {
        // 收件人电子邮箱
        String to = "853908786qq@gmail.com";

        // 发件人电子邮箱
      //  String from = "853908786@qq.com";
        String from = "q1004974993@163.com";
        // 指定发送邮件的主机为 smtp.qq.com
        //String host = "smtp.qq.com";  //QQ 邮件服务器
        String host = "smtp.163.com";  //网易邮件服务器

        // 获取系统属性
        Properties properties = System.getProperties();

        // 设置邮件服务器
        properties.setProperty("mail.smtp.host", host);

        properties.put("mail.smtp.auth", "true");
        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        properties.put("mail.smtp.ssl.enable", "true");  //加密
        properties.put("mail.smtp.ssl.socketFactory", sf);
        // 获取默认session对象
        Session session = Session.getDefaultInstance(properties, new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
                /*温馨提醒：为了你的帐户安全，更改QQ密码以及独立密码会触发授权码过期，需要重新获取新的授权码登录。*/
                return new PasswordAuthentication(from, "aabbcc123"); //发件人邮件用户名、密码
            }
        });

        try {
            // 创建默认的 MimeMessage 对象
            MimeMessage message = new MimeMessage(session);

            // Set From: 头部头字段
            message.setFrom(new InternetAddress(from));

            // Set To: 头部头字段
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

            // Set Subject: 头部头字段
            message.setSubject("This is the Subject Line!");

            // 设置消息体
            message.setText("This is actual message");

            // 发送消息
            Transport.send(message);
            System.out.println("Sent message successfully....from runoob.com");
        } catch (MessagingException mex) {
            mex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) {

        // localThread thread = new localThread();
        // new Thread(thread).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int count = 501;
                int num = 0;  //200+240
                try {
                    for (int i = 0; i <= count; i++) {
                        sendEmail();
                        num += 1;
                        System.out.println("i=---" + i + "当前成功发送完成第--" + num + "--封邮件");
                        Thread.sleep(5000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public static class localThread implements Runnable {

        @Override
        public void run() {
            int count = 501;
            int num = 0;
            try {
                for (int i = 1; i <= count; i++) {
                    sendEmail();
                    num += 1;
                    System.out.println("i=---" + i + "当前成功发送完成第--" + num + "--封邮件");
                    Thread.sleep(5000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
