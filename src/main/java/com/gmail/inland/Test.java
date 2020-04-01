package com.gmail.inland;


import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Test {
    /**
     * 以下两种失败率太高  都会出现邮件拒绝发送  准备使用Springboot
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        sslBy163();
     /*  MailSender mailSender = MailSender.getInstance();
        MailInfo mailInfo = mailSender.getMailInfo();
         mailSender.sendHtmlMail(mailInfo);*/
        //mailSender.sendTextMail(mailInfo);

    }


    /**
     *  已测试发送成功
     * @throws IOException
     */
    public static void sslBy163() throws IOException {
        Properties props = new Properties();
        //选择ssl方式
       //final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.debug", "true");
        props.put("mail.smtp.host", "smtp.163.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.smtp.auth", "true");
       // props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.transport.protocol", "smtp");
        // SSL加密 3/16
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        // 设置信任所有的主机
        sf.setTrustAllHosts(true);
        // end
        props.put("mail.smtp.ssl.socketFactory", sf);
        props.put("mail.smtp.connectiontimeout",1000 * 60 *3);//套接字连接超时值毫秒。 默认值为无限超时
        props.put("mail.smtp.timeout", 1000 * 60 *3); //套接字 I/O 超时值以毫秒为单位

        final String username = "tlh1004974993@163.com";//163邮箱
        final String password = "tlh1004974993";//密码

        Session session = Session.getDefaultInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
        // -- Create a new message --
        //Message msg = new MimeMessage(session);

        // -- Set the FROM and TO fields --
        try {
          /*  msg.setFrom(new InternetAddress(username));
            msg.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse("853908786qq@gmail.com,1004974993@qq.com"));//InternetAddress.parse("1004974993qq@gmail.com")
            msg.setSubject("163 send mail");
            msg.setText("googleMailSend and close proxy163邮箱发送");
            msg.setSentDate(new Date());
            Transport.send(msg);*/
            MailInfo mailInfo = MailSender.getMailInfo();
            MailSender.sendHtmlMail(mailInfo,session);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Message sent.");

    }

}
