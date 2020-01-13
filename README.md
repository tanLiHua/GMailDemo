

javax.mail.MessagingException: Unknown SMTP host: smtp.gmail.com 465;
1.代理服务器是否能ping smtp.gmail.com ;telnet smtp.gmail.com 465
2.代理服务器防火墙是否对外开放了465端口 （个人感觉无所谓...）
3.检查本地能ping smtp.gmail.com ;telnet smtp.gmail.com 465（DNS解析问题）
4.检查代码中设置的代理地址、端口、协议是否正确。

javax.mail.AuthenticationFailedException: 原因：gmail没有对设备授权
1.打开开关：允许不够安全的应用
链接：https://myaccount.google.com/lesssecureapps
2.访问这个链接：https://accounts.google.com/DisplayUnlockCaptcha允许任何设备登陆

如果要实现用gmail接收邮件，要在gmail设置POP或IMAP
