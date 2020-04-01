package com.gmail.inland;

import com.sun.mail.util.MailSSLSocketFactory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailInfo {

    /** 发送邮件的服务器的IP */
    private String mailHost;
    /** 发送邮件的服务器的端口 */
    private String mailPort = "465";
    /** 发送邮件的用户名（邮箱全名称） */
    private String username;
    /** 发送邮件的密码 */
    private String password;


    /** 信息发送地址（多个邮件地址以";"分隔） */
    private List<String> toList;

    /** 邮件主题 */
    private String subject;
    /** 邮件内容 */
    private String content;
    /** 邮件附件的文件名 */
    private List<String> attachFileNames;

    /**
     * 获取邮件参数
     *
     * @return
     * @throws GeneralSecurityException
     */
    public Properties getProperties() throws GeneralSecurityException {
        Properties props = new Properties();
        MailSSLSocketFactory sf = null;
        sf = new MailSSLSocketFactory();

        props.put("mail.debug", "true");
        props.put("mail.smtp.host", getMailHost());
        props.put("mail.smtp.port", getMailPort());
        props.put("mail.smtp.auth", "true");
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.put("mail.smtp.socketFactory.port", "465");
       /* //props.put("mail.smtp.starttls.enable", "true");

        MailSSLSocketFactory sslSF = new MailSSLSocketFactory();
        sslSF.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sslSF);
        props.put("mail.transport.protocol", "smtp");*/

        props.put("mail.user", getUsername());
        props.put("mail.password", getPassword());
        return props;
    }


}
