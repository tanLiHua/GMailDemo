package com.gmail.mailGun;

import com.gmail.common.ResponseData;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
@NoArgsConstructor
public class MailSendCallable implements Callable<ResponseData> {

    private static final Logger logger = LoggerFactory.getLogger(MailSendCallable.class);
    private MailInfo mailInfo;

    /**
     * 邮件发送总数
     */


    @Override
    public ResponseData call() throws Exception {
        int count = mailInfo.getToAddr().size();
        ResponseData responseData = null;
        if (StringUtils.isNotEmpty(mailInfo.getHtmlFile())) {  //html邮件
            responseData = MailGunToGmail.sendComplexMessage(mailInfo);
        } else {   //文本邮件
            try {
                responseData = MailGunToGmail.sendSimpleMail(mailInfo);
            } catch (Exception e) {
                if (e instanceof SocketException || e instanceof InterruptedException) {
                    logger.info("发送频率过快,线程进入休眠:" + e.getMessage());
                    try {
                        Thread.sleep(1000 * 60 * 2);
                    } catch (InterruptedException e1) {
                        logger.error("线程休眠异常" + e1.getMessage());
                    }
                    System.out.println("线程休眠结束,开始发送邮件");
                    logger.info("线程休眠结束,开始发送邮件");
                }


            }
        }
        if (responseData.isFlag()) {
            System.out.println(count + "邮件发送成功");
            logger.info("邮件发送完成！");
            return ResponseData.returnData(true, "邮件发送成功", count);

        }
        return responseData;
    }

}
