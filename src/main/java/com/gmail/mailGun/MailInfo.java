package com.gmail.mailGun;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailInfo implements Serializable {

    /**
     * 邮件发送方
     */
    private String fromAddr;
    /**
     * 邮件接收方
     */
    private List<String> toAddr;
    /**
     * 主题
     */
    private String subject;
    /**
     * 文本内容
     */
    private String text;
    /**
     * 抄送
     */
    private String cc;
    /**
     * 暗抄送
     */
    private String bcc;
    /**
     * html文件
     */
    private String htmlFile;
    /**
     * 附件
     */
    private List<String> attachFileNames;
}
