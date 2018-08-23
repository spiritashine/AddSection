package com.huj.addsection.mail.utils;


import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.bean.Image;
import com.huj.addsection.mail.bean.Mail;
import com.huj.addsection.mail.db.DBAddresser;
import com.huj.addsection.mail.manager.L;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class SendUtils {
    public static final int CONNECT_FAILED = -1;//连接失败
    ArrayList<Image> images;//一个有规则的ArrayList，用作嵌入图片
    Addresser addresser;
    Mail mail;

    public SendUtils(Addresser addresser, Mail mail, ArrayList<Image> images) {
        this.images = images;
        this.addresser = addresser;
        this.mail = mail;
    }

    public int send() {
        try {
            if (addresser.sendProtocol == null) {
                if (LoginUtils.loginSMTP( addresser.account, addresser.password,addresser.sendProtocol, addresser)) {
                    DBAddresser.getInstance().updateAddresserProtocol(addresser);
                }else {
                    return Mail.MAIL_TYPE_SEND;  //连接失败
                }
            }
            Session session = LoginUtils.getSessionSMTP(addresser.sendProtocol,addresser.account, addresser.password);
            MimeMessage msg = createMessage(session);
            Transport transport = session.getTransport(addresser.sendProtocol.name);//设置通讯协议
            transport.connect(addresser.account, addresser.password);//连接
            transport.sendMessage(msg, msg.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            e.printStackTrace();
            L.i("e=" + e);
            return Mail.MAIL_TYPE_SEND;//发送失败
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            L.i("e=" + e);
            return Mail.MAIL_TYPE_SEND;//发送失败
        } catch (IOException e) {
            e.printStackTrace();
            L.i("e=" + e);
            return Mail.MAIL_TYPE_SEND;//发送失败
        }
        return Mail.MAIL_TYPE_OUT;//发送成功
    }

    public MimeMessage createMessage(Session session) throws IOException, MessagingException {
        MimeMessage message = new MimeMessage(session);
        //设置发件人
        message.setFrom(new InternetAddress(mail.fromContacts.mail, mail.fromContacts.name));
        //设置收件人，抄送人，密送人
        setReceiver(message, Message.RecipientType.TO, mail.tosList);
        setReceiver(message, Message.RecipientType.CC, mail.ccsList);
        setReceiver(message, Message.RecipientType.BCC, mail.bccsList);
        message.setSubject(mail.subject);//设置主题
        message.setSentDate(new Date()); // 发送日期
        //创建代表邮件正文和附件的各个MimeBodyPart对象
        MimeMultipart allMultipart = new MimeMultipart();
        MimeBodyPart contentpart = createContent(mail.contentNoImage);
        allMultipart.addBodyPart(contentpart);
        for (int i = 0; i < mail.attchsList.size(); i++) {//创建用于组合邮件正文和附件的MimeMultipart对象
            allMultipart.addBodyPart(createAttachment(mail.attchsList.get(i).path));
        }
        message.setContent(allMultipart);//设置整个邮件内容为最终组合出的MimeMultipart对象
        message.saveChanges();
        return message;
    }

    /**
     * 设置收件人，抄送人，密送人
     */
    private void setReceiver(MimeMessage message, Message.RecipientType type, ArrayList<Contacts> list) throws UnsupportedEncodingException, MessagingException {
        if (list.size() > 0) {
            InternetAddress[] tos = new InternetAddress[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Contacts contacts = list.get(i);
                tos[i] = new InternetAddress(contacts.mail, contacts.name);
            }
            message.setRecipients(type, tos);
        }
    }

    public MimeBodyPart createContent(String body) throws MessagingException, IOException {
        //创建代表组合Mime消息的MimeMultipart对象，将该MimeMultipart对象保存到MimeBodyPart对象
        MimeBodyPart contentPart = new MimeBodyPart();
        MimeMultipart contentMultipart = new MimeMultipart("related");

        //创建用于保存HTML正文的MimeBodyPart对象，并将它保存到MimeMultipart中
        MimeBodyPart htmlbodypart = new MimeBodyPart();
        htmlbodypart.setContent(body, "text/html;charset=UTF-8");
        contentMultipart.addBodyPart(htmlbodypart);
        for (int i = 0; i < images.size(); i++) {
            //创建用于保存图片的MimeBodyPart对象，并将它保存到MimeMultipart中
            Image image  = images.get(i);
            MimeBodyPart gifBodyPart = new MimeBodyPart();
            String path = ImageUtils.revitionImageSize(image.path);//压缩图片
             FileDataSource fileDataSource = new FileDataSource(path);
            gifBodyPart.setDataHandler(new DataHandler(fileDataSource));//图片所在的目录的绝对路径
            gifBodyPart.setContentID(image.cid);   //cid的值

            gifBodyPart.setFileName(MimeUtility.encodeText(fileDataSource.getName()));
            gifBodyPart.setDisposition(Part.INLINE);
            contentMultipart.addBodyPart(gifBodyPart);
        }
        //将MimeMultipart对象保存到MimeBodyPart对象
        contentPart.setContent(contentMultipart);
        return contentPart;
    }

    public MimeBodyPart createAttachment(String filePath) throws MessagingException, UnsupportedEncodingException {
        //创建保存附件的MimeBodyPart对象，并加入附件内容和相应的信息
        InputStream isBm = null;
        MimeBodyPart attachPart = new MimeBodyPart();
        FileDataSource fsd = new FileDataSource(filePath);
        attachPart.setDataHandler(new DataHandler(fsd));
        attachPart.setDisposition(Part.ATTACHMENT);
        attachPart.setFileName(MimeUtility.encodeText(fsd.getName()));
        return attachPart;
    }
}



