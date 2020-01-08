package com.gmail.test;


import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

public class SendGmailToQQ {

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
        props.put("mail.smtp.connectiontimeout",1000 * 60 *3);//套接字连接超时值毫秒。 默认值为无限超时
        props.put("mail.smtp.timeout", 1000 * 60 *3); //套接字 I/O 超时值以毫秒为单位

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
        final String password = "";//gmail密码
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
       // String proxyHost = "155.138.231.16";
       // String proxyPort = "8288";
        String proxyHost = "127.0.0.1";
        String proxyPort = "10808";

        System.setProperty("socksProxyHost", proxyHost);
        System.setProperty("socksProxyPort", proxyPort);
        //Authenticator.setDefault(new MyAuthenticator("root","aabbcc123!"));
        SslUtils.ignoreSsl();
        gmailSender("1004974993@qq.com");  //目的邮箱账号

        System.out.println("已发送到QQ邮箱。请查收！");

    }

    //javax.mail.MessagingException: Could not connect to SMTP host: smtp.gmail.com, port: 465;
    //看看代理服务器有没有开通465端口
   /* public static class MyAuthenticator extends Authenticator{

        private String user;
        private String password;

        public MyAuthenticator() {
        }

        public MyAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }
        protected PasswordAuthentication getPasswordAuthentication(){

            PasswordAuthentication passwordAuthentication;
            passwordAuthentication = new PasswordAuthentication(user, password.toCharArray());
            return passwordAuthentication;
        }
    }*/
}

