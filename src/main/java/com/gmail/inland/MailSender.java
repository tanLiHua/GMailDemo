package com.gmail.inland;

import com.gmail.common.CommonUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MailSender {

    private static MailSender sender = null;


    public static MailSender getInstance() {
        if(sender == null){
            sender = new MailSender();
        }
        return sender;
    }

/*    *//**
     * 以文本格式发送邮件
     *
     * @param mailInfo
     *            邮件信息
     * @param mailType
     *            邮件类型 1-error；2-warning；3-notify；
     * @return
     * @throws Exception
     */
    public boolean sendTextMail(MailInfo mailInfo) throws Exception {

        // 需要身份认证，创建一个密码验证器
        MailAuthenticator authenticator = new MailAuthenticator(mailInfo.getUsername(), mailInfo.getPassword());

        Properties prop = mailInfo.getProperties();
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(prop, authenticator);

        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getUsername());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);

            Address[] maillToArr = getMailToAddress(mailInfo);

            // 设置邮件消息的接收者，发送
            if (maillToArr !=null && maillToArr.length>0) {//InternetAddress.parse("853908786@qq.com")
                mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse("853908786qq@gmail.com"));
            }

            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(Calendar.getInstance().getTime());
            // 设置邮件消息的主要内容
            mailMessage.setText(mailInfo.getContent());

            // 发送邮件
            Transport.send(mailMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    /**
     * 发送邮件
     *
     * @param mailInfo
     *            邮件信息
     * @param
     *
     * @return
     * @throws Exception
     */
    public static boolean sendHtmlMail(MailInfo mailInfo,Session sendMailSession) throws Exception {

        // 需要身份认证，创建一个密码验证器
       // MailAuthenticator authenticator = new MailAuthenticator(mailInfo.getUsername(), mailInfo.getPassword());

       // Properties prop = mailInfo.getProperties();
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
       // Session sendMailSession = Session.getDefaultInstance(prop, authenticator);

        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址 InternetAddress(发件人邮箱地址, "名称/昵称", "UTF-8")
            Address from = new InternetAddress(mailInfo.getUsername());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);

            // 创建邮件的接收者地址 to：发送
            Address[] maillToArr = getMailToAddress(mailInfo);

            //mailMessage.addRecipients(Message.RecipientType.CC, InternetAddress.parse("tlh1004974993@163.com"));
            // 设置邮件消息的接收者，发送
            if (maillToArr !=null && maillToArr.length>0) {  //InternetAddress(receiveMail, "XX用户", "UTF-8")
                mailMessage.addRecipients(Message.RecipientType.TO, InternetAddress.parse("853908786qq@gmail.com"));
            }

            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(Calendar.getInstance().getTime());

            // MimeMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart multiPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart bodyPart = null;

            List<String> fileList = mailInfo.getAttachFileNames();
            String htmlStr="";
            //添加附件
            if(fileList.size() != 0){
                for(String attachFile : fileList){
                    if(attachFile.endsWith(".html")){
                        htmlStr=attachFile;
                    }else {
                        bodyPart=new MimeBodyPart();
                        FileDataSource fds=new FileDataSource(attachFile); //得到数据源
                        bodyPart.setDataHandler(new DataHandler(fds)); //得到附件本身并放入BodyPart
                        bodyPart.setFileName(MimeUtility.encodeText(fds.getName()));  //得到文件名并编码（防止中文文件名乱码）同样放入BodyPart
                        //image.setContentID("image_fairy_tail");	    // 为“节点”设置一个唯一编号（在文本“节点”将引用该ID）
                        multiPart.addBodyPart(bodyPart);
                    }
                }
            }
           // bodyPart=new MimeBodyPart();
            if(StringUtils.isNotEmpty(htmlStr)){
                htmlStr = CommonUtils.readAndWriteHtml(htmlStr);
                // 设置html邮件消息内容
                bodyPart=new MimeBodyPart();
                bodyPart.setContent(htmlStr,"text/html; charset=utf-8");
                multiPart.addBodyPart(bodyPart);
            }else {
                // 设置邮件消息的主要内容
                bodyPart=new MimeBodyPart();
                bodyPart.setContent("带附件的文本邮件","text/html; charset=utf-8");
                multiPart.addBodyPart(bodyPart);
            }
            //multiPart.addBodyPart(bodyPart);

            // 设置邮件消息的主要内容
            mailMessage.setContent(multiPart);
            // 发送邮件
            Transport.send(mailMessage);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 创建发送邮件列表地址对象
     *
     * @param mailInfo
     *            邮件信息
     * @return Address[0]：发送地址数组
     */
    public static Address[] getMailToAddress(MailInfo mailInfo) throws AddressException {
        Address[] toAdds = null;

        List<String> toMails = mailInfo.getToList();
        toAdds = new InternetAddress[toMails.size()];
        for (int index = 0; index < toMails.size(); index++) {
            toAdds[index] = new InternetAddress(toMails.get(index));
        }

        return toAdds;
    }

    /**
     * 构建MailInfo对象
     * @return
     */
    public static MailInfo getMailInfo() {
        MailInfo info = new MailInfo();
        info.setMailHost("smtp.163.com");
        info.setMailPort("465");
        info.setUsername("tlh1004974993@163.com");
        info.setPassword("tlh1004974993");
        //info.setNotifyTo("邮件接收人");
        List<String> tolist= new ArrayList<>();
        tolist.add("1004974993qq@gmail.com");
        //tolist.add("853908786@qq.com");
        info.setToList(tolist);
        info.setSubject("163 send mail");
        info.setContent("googleMailSend ");
        List<String> fileList= new ArrayList<>();
        fileList.add("C:\\tmp\\attachment\\1.jpg");
        fileList.add("C:\\tmp\\attachment\\2.jpg");
        //fileList.add("C:\\Users\\Admin\\Desktop\\数据文档说明.zip");
        fileList.add("C:\\tmp\\html\\index1.html");
        info.setAttachFileNames(fileList);//添加附件
        return info;

    }
}