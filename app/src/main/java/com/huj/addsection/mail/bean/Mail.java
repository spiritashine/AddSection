package com.huj.addsection.mail.bean;

import android.os.Environment;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.Formatter;


import com.huj.addsection.App;
import com.huj.addsection.mail.db.DBContacts;
import com.huj.addsection.mail.utils.FileUtils;
import com.huj.addsection.mail.manager.L;
import com.huj.addsection.mail.manager.TimeManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeUtility;

/**
 * 邮件的实体类
 */
public class Mail implements Parcelable {
    public final static int PAGE_COUNT = 30;
    public Message message;

    public final static int MAIL_TYPE_INBOX = 0;//收件箱
    public final static int MAIL_TYPE_DRAFT = 1;//草稿箱
    public final static int MAIL_TYPE_SEND = 2;//发件箱
    public final static int MAIL_TYPE_OUT = 3;//已发送邮件
    public final static int MAIL_TYPE_DELETED = 4;//已删除邮件
    public final static int MAIL_TYPE_SPAM = 5;//垃圾邮件
    public final static int MAIL_TYPE_DELETE_MIN = 6;//删除邮件最小值
    public final static int MAIL_TYPE_DELETE_MAX = 7;//删除邮件最大值

    public String uid;
    public String froms;
    public String subject;
    public String contentNoImage;
    public String contentHint;
    public boolean isSeen;
    public String sendDate;
    public int type;//1,发件箱   2，收件箱  3草稿箱

    public String tos;
    public String ccs;
    public String bccs;
    public String contentWithImage;
    public boolean isContainAttch;//是否有附件
    public String attchs;//附件本地路径
    public int textColor;//mail展示的时候最前面的变色的圆圈颜色。1-8的随机数，随机生成
    public Contacts fromContacts;
    public ArrayList<Contacts> tosList;
    public ArrayList<Contacts> ccsList;
    public ArrayList<Contacts> bccsList;
    public ArrayList<Attach> attchsList = new ArrayList<>();
    public ArrayList<Image> imagesList = new ArrayList<>();

    public boolean isSelected = false;

    public String getFroms() {
        return froms;
    }

    public void setFroms(String froms) {
        this.froms = froms;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContentNoImage() {
        return contentNoImage;
    }

    public void setContentNoImage(String contentNoImage) {
        this.contentNoImage = contentNoImage;
    }

    public String getContentHint() {
        return contentHint;
    }

    public void setContentHint(String contentHint) {
        this.contentHint = contentHint;
    }

    public String getContentWithImage() {
        return contentWithImage;
    }

    public void setContentWithImage(String contentWithImage) {
        this.contentWithImage = contentWithImage;
    }

    public Mail() {
    }

    /**
     * 实例化收件箱未读邮件
     */
    public Mail(Message message, String uid) {
//        this();
        this.message = message;
        this.uid = uid;
        init();
    }

    /**
     * 转换发件人,收件人，抄送人，密送人
     */
    public static Contacts conversionToContacts(String string) {
        if (!TextUtils.isEmpty(string)) {
            String mail = string.substring(string.indexOf("<") + 1, string.length() - 1);
            Contacts contacts = DBContacts.getInstance().selectContactsByMail(mail);
            if (contacts == null) {
                String name = string.substring(0, string.indexOf("<"));
                contacts = new Contacts(name, mail);
            }
//            if (contacts.mail.equals(App.addresser.account)) {
//                contacts.name = "我";
//            }
            return contacts;
        } else {
            return null;
        }
    }

    /**
     * 转换发件人,收件人，抄送人，密送人
     */
    public static ArrayList<Contacts> conversionToContactses(String string) {
        ArrayList<Contacts> list = new ArrayList<>();
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split(";");
            for (int i = 0; i < split.length; i++) {
                list.add(conversionToContacts(split[i]));
            }
        }
        return list;
    }

    /**
     * 去掉html中的标签，提取文字
     */
    public static String removeHtmlTag(String inputString) {
        if (inputString == null)
            return null;
        String htmlStr = inputString; // 含html标签的字符串
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;
        java.util.regex.Pattern p_special;
        java.util.regex.Matcher m_special;
        try {
            //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
            String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
            //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
            String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
            // 定义HTML标签的正则表达式
            String regEx_html = "<[^>]+>";
            // 定义一些特殊字符的正则表达式 如：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            String regEx_special = "\\&[a-zA-Z]{1,10};";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签
            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签
            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签
            p_special = Pattern.compile(regEx_special, Pattern.CASE_INSENSITIVE);
            m_special = p_special.matcher(htmlStr);
            htmlStr = m_special.replaceAll(""); // 过滤特殊标签
            textStr = htmlStr.replaceAll("</?[^>]+>|\r|\n| ", "").trim();
            textStr = textStr.length() <= 30 ? textStr : textStr.substring(0, 30);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return textStr;// 返回文本字符串
    }


    public void init() {
        try {
            this.froms = initfroms();
            this.fromContacts = conversionToContacts(this.froms);
            this.subject = initSubject();
            this.contentNoImage = getHTMLContentNoImage(message, new StringBuffer());
            this.contentHint = removeHtmlTag(this.contentNoImage);
            this.isSeen = false;
            this.sendDate = message.getSentDate() != null ? Long.toString(message.getSentDate().getTime()) : "";
            this.type = MAIL_TYPE_INBOX;
            this.textColor = (int) (Math.random() * 5);
            this.tos = getMailAddress(message, Message.RecipientType.TO);
            this.ccs = getMailAddress(message, Message.RecipientType.CC);
            this.bccs = getMailAddress(message, Message.RecipientType.BCC);
            this.tosList = conversionToContactses(this.tos);
            this.ccsList = conversionToContactses(this.ccs);
            this.bccsList = conversionToContactses(this.bccs);
            this.isContainAttch = isContainAttch(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    /**
     * 判断是否有附件
     */
    public boolean isContainAttch(Part part) {
        boolean flag = false;
        try {
            if (part.isMimeType("multipart/mixed")) {
                Multipart multipart = (Multipart) part.getContent();
                int count = multipart.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart bodypart = multipart.getBodyPart(i);
                    String dispostion = bodypart.getDisposition();
                    if (dispostion != null && dispostion.equals(Part.ATTACHMENT)) {
                        return true;
                    } else if (bodypart.isMimeType("multipart/mixed")) {
                        flag = isContainAttch(bodypart);
                    }
                }
            } else if (part.isMimeType("message/rfc822")) {
                flag = isContainAttch((Part) part.getContent());
            }
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * 保存附件
     */
    public void getAttachMent(Part part) throws Exception {
        String fileName = "";
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart mpart = mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                if (disposition != null && disposition.equals(Part.ATTACHMENT)) {
                    fileName = MimeUtility.decodeText(mpart.getFileName());
                    String path = Environment.getExternalStorageDirectory() + "/Xiniuyun/attach/" + fileName;
                    Attach attach = new Attach(fileName, path, mpart.getInputStream(), Formatter.formatFileSize(App.getApplication(), mpart.getSize()));
                    attchsList.add(attach);
                } else if (mpart.isMimeType("multipart/*")) {
                    getAttachMent(mpart);
                }
            }
        } else if (part.isMimeType("message/rfc822")) {
            getAttachMent((Part) part.getContent());
        }
    }


    /**
     * 实例化发件人
     */
    public String initfroms() {
        String froms = "";
        try {
            InternetAddress[] address = (InternetAddress[]) message.getFrom();
            String from = address[0].getAddress();
            if (from == null) {
                return "";
            }
            Contacts contacts = DBContacts.getInstance().selectContactsByMail(froms);
            if (contacts != null) {//如果本地已经保存了，就换成本地的名字
                froms = contacts.name + "<" + contacts.mail + ">";
                return froms;
            }
            String personal = address[0].getPersonal();
            if (personal == null) {
                personal = from;
            }
            froms = personal + "<" + from + ">";
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return froms;
    }

    /**
     * 获得标题
     */
    public String initSubject() {
        String subject = "";
        try {
            subject = message.getSubject();
            subject = subject == null ? "" : MimeUtility.decodeText(subject);
        } catch (MessagingException e) {
            // TODO: handle exception
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return subject;
    }

    /**
     * 获得邮件html内容
     */
    public String getHTMLContentWithImage(Part message, StringBuffer bodytext) {
        try {
            if (message.isMimeType("message/rfc822")) {
                getHTMLContentWithImage(message, bodytext);
            } else if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart m = multipart.getBodyPart(i);
                    if (m.isMimeType("multipart/*")) {
                        getHTMLContentWithImage(m, bodytext);
                    }
                    if (m.isMimeType("text/html")) {
                        bodytext.append((String) m.getContent());
                    }
                    if (m.isMimeType("image/*")) {
                        // 得到HTML文件中所有图片的Content-ID,里面包含图片的cid名称
                        String[] strs = m.getHeader("Content-ID");
                        if (strs != null && strs.length != 0) {
                            for (int j = 0; j < strs.length; j++) {
                                // cid名称规范化
                                if (strs[0].startsWith("<") && strs[0].endsWith(">")) {
                                    strs[0] = "cid:" + strs[0].substring(1, strs[0].length() - 1);
                                } else {
                                    strs[0] = "\"cid:" + strs[0] + "\"";
                                }
                                String path = Environment.getExternalStorageDirectory() + "/Xiniuyun/image";
                                File file = FileUtils.makeFilePath(path, MimeUtility.decodeText(m.getFileName()));
                                BufferedInputStream bis = new BufferedInputStream(m.getInputStream());
                                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), 3 * 1024 * 1024);
                                int len = -1;
                                while ((len = bis.read()) != -1) {
                                    bos.write(len);
                                    bos.flush();
                                }
                                bos.close();
                                bis.close();
                                if (strs[0].startsWith("\"")) {
                                    strs[0] = strs[0].substring(1, strs[0].length());
                                }
                                if (strs[0].endsWith("\"")) {
                                    strs[0] = strs[0].substring(0, strs[0].length() - 1);
                                }
                                int index = bodytext.indexOf(strs[0]);
                                // 交换图片内嵌地址
                                if (index != -1) {
                                    bodytext.replace(index, index + strs[0].length(), "file://" + file.getAbsolutePath());
                                    imagesList.add(new Image(file.getAbsolutePath(), FileUtils.getMd5(file)));
                                }
                            }
                        }
                    }
                }
            }
        } catch (MessagingException e) {
            L.i("MessagingException e = " + e);
            e.printStackTrace();
        } catch (IOException e) {
            L.i("IOException e = " + e);
            e.printStackTrace();
        }

        return bodytext.toString();
    }


    /**
     * 获得邮件text内容
     */
    public String getHTMLContentNoImage(Part message, StringBuffer bodytext) {
        try {
            if (message.isMimeType("text/html")) {
                bodytext.append((String) message.getContent());
            } else if (message.isMimeType("message/rfc822")) {
                getHTMLContentNoImage((Part) message.getContent(), bodytext);
            } else if (message.isMimeType("multipart/*")) {
                String disposition = message.getDisposition();
                if (disposition == null || (!disposition.equals(Part.ATTACHMENT) || !disposition.equals(Part.INLINE))) {
                    Multipart multipart = (Multipart) message.getContent();
                    if (multipart.getCount() > 0) {
                        for (int i = 0; i < multipart.getCount(); i++) {
                            getHTMLContentNoImage(multipart.getBodyPart(i), bodytext);
                        }
                    }
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bodytext.toString();
    }


    /**
     * 获得抄送，发送，密送人
     */
    public String getMailAddress(Message msg, Message.RecipientType type) {
        StringBuilder mailaddr = new StringBuilder();
        try {
            InternetAddress[] address = (InternetAddress[]) msg.getRecipients(type);
            if (address != null) {
                for (int i = 0; i < address.length; i++) {
                    String mail = address[i].getAddress();
                    if (mail == null) {
                        continue;
                    } else {
                        mail = MimeUtility.decodeText(mail);
                    }
                    String personal = address[i].getPersonal();
                    if (personal == null) {
                        personal = mail;
                    } else {
                        personal = MimeUtility.decodeText(personal);
                    }
                    String compositeto = personal + "<" + mail + ">;";
                    mailaddr.append(compositeto);
                }
                return mailaddr.substring(0, mailaddr.length() - 1);
            }

        } catch (UnsupportedEncodingException e) {
            // TODO: handle exception
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }


    /**
     * 邮件是否读过
     */
    public boolean isSeen() {
        boolean isSeen = false;
        try {
            Flags flags = ((Message) message).getFlags();
            Flags.Flag[] flag = flags.getSystemFlags();

            for (int i = 0; i < flag.length; i++) {
                if (flag[i] == Flags.Flag.SEEN) {
                    isSeen = true;
                    break;
                }
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return isSeen;
    }

    @Override
    public String toString() {
        return "Mail{" +
                "subject='" + subject + '\'' +

                ", sendDate='" + (!TextUtils.isEmpty(sendDate) ? TimeManager.getMailTime(Long.parseLong(sendDate)) : "") + '\'' +

                '}';
    }

//    @Override
//    public String toString() {
//        return "Mail{" +
//                ", uid='" + uid + '\'' +
//                ", froms='" + froms + '\'' +
//                ", subject='" + subject + '\'' +
//                ", contentNoImage='" + contentNoImage + '\'' +
//                ", contentHint='" + contentHint + '\'' +
//                ", isSeen=" + isSeen +
//                ", sendDate='" + sendDate + '\'' +
//                ", type=" + type +
//                ", tos='" + tos + '\'' +
//                ", ccs='" + ccs + '\'' +
//                ", bccs='" + bccs + '\'' +
//                ", contentWithImage='" + contentWithImage + '\'' +
//                ", isContainAttch=" + isContainAttch +
//                ", attchs='" + attchs + '\'' +
//                ", textColor=" + textColor +
//                ", fromContacts=" + fromContacts +
//                ", tosList=" + tosList +
//                ", ccsList=" + ccsList +
//                ", bccsList=" + bccsList +
//                ", attchsList=" + attchsList +
//                '}';
//    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uid);
        dest.writeString(this.froms);
        dest.writeString(this.subject);
        dest.writeString(this.contentNoImage);
        dest.writeString(this.contentHint);
        dest.writeByte(isSeen ? (byte) 1 : (byte) 0);
        dest.writeString(this.sendDate);
        dest.writeInt(this.type);
        dest.writeString(this.tos);
        dest.writeString(this.ccs);
        dest.writeString(this.bccs);
        dest.writeString(this.contentWithImage);
        dest.writeByte(isContainAttch ? (byte) 1 : (byte) 0);
        dest.writeString(this.attchs);
        dest.writeInt(this.textColor);
        dest.writeParcelable(this.fromContacts, flags);
        dest.writeTypedList(tosList);
        dest.writeTypedList(ccsList);
        dest.writeTypedList(bccsList);
        dest.writeTypedList(attchsList);
        dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
    }

    protected Mail(Parcel in) {
        this.uid = in.readString();
        this.froms = in.readString();
        this.subject = in.readString();
        this.contentNoImage = in.readString();
        this.contentHint = in.readString();
        this.isSeen = in.readByte() != 0;
        this.sendDate = in.readString();
        this.type = in.readInt();
        this.tos = in.readString();
        this.ccs = in.readString();
        this.bccs = in.readString();
        this.contentWithImage = in.readString();
        this.isContainAttch = in.readByte() != 0;
        this.attchs = in.readString();
        this.textColor = in.readInt();
        this.fromContacts = in.readParcelable(Contacts.class.getClassLoader());
        this.tosList = in.createTypedArrayList(Contacts.CREATOR);
        this.ccsList = in.createTypedArrayList(Contacts.CREATOR);
        this.bccsList = in.createTypedArrayList(Contacts.CREATOR);
        this.attchsList = in.createTypedArrayList(Attach.CREATOR);
        this.isSelected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<Mail> CREATOR = new Parcelable.Creator<Mail>() {
        @Override
        public Mail createFromParcel(Parcel source) {
            return new Mail(source);
        }

        @Override
        public Mail[] newArray(int size) {
            return new Mail[size];
        }
    };
}
