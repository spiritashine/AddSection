package com.huj.addsection.mail.manager;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.style.CharacterStyle;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.huj.addsection.App;
import com.huj.addsection.R;
import com.huj.addsection.mail.bean.Attach;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.bean.Image;
import com.huj.addsection.mail.db.DBContacts;
import com.huj.addsection.mail.utils.FileUtils;
import com.huj.addsection.mail.view.TextCircleView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 */
public class StringManager {


    /**
     * 验证邮箱地址是否正确
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证邮箱地址是否正确
     */
    public static boolean checkEmail(TextView tv) {

        return checkEmail(getStringByTv(tv));
    }


    /**
     * 验证手机号码
     */
    public static boolean isMobileNO(String mobiles) {
        boolean flag = false;
        try {
            Pattern p = Pattern
                    .compile("^((13[0-9])|(15[^4,\\D])|(17[0-9])|(18[0-9]))\\d{8}$");
            Matcher m = p.matcher(mobiles);
            flag = m.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


    /**
     * 将TextView中中文本获取到
     */
    public static String getStringByTv(TextView tv) {
        return tv.getText().toString().trim();
    }

    /**
     * 判断textview是否没有文字
     */
    public static boolean isEmpty(TextView textView) {
        return TextUtils.isEmpty(textView.getText().toString().trim());
    }

    /**
     * 像素和sp 相互转换
     */
    public static int spToPix(Context context, int sp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (sp * scale + 0.5f);
    }

    /**
     * 根据文件绝对路径获取文件名
     *
     * @param filePath
     * @return
     */
    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath))
            return "";
        return filePath.substring(filePath.lastIndexOf(File.separator) + 1);
    }

    /**
     * 获取字符串资源
     */
    public static String getString(int id) {
        return App.getApplication().getResources().getString(id);
    }

    /**
     * 判断用户输入姓名信息是否合格
     */
    public static boolean checkNameMessage(String userName) {
        // 用户名只能是中文或者英文
        if (isChineseChar(userName) || isEnglishChar(userName)) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 判断用户名是否为中文
     */
    public static String getCountStr(int count) {
        String string = "";
        if (count != 0) {
            string = String.valueOf(count);
        }
        return string;
    }

    /**
     * 判断用户名是否为中文
     */
    public static boolean isChineseChar(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断用户名是否为英文
     */
    public static boolean isEnglishChar(String str) {
        Pattern p = Pattern.compile("^[a-zA-Z]+$");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否为数字，英文，汉语中的一种
     */
    public static boolean isStringOk(String str) {
        return isEnglishChar(str) || isChineseChar(str) || isNumeric(str);
    }

    /**
     * 给TextCircleView设置颜色
     */
    public static void setTextCircleViewColor(TextCircleView ctv, int textColor) {
        ctv.setTextColor(Color.parseColor(App.getApplication().getResources().getStringArray(R.array.color)[textColor]));
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

    /**
     * 发件人前面的小圆圈里的字
     */
    public static String getCtv(String from) {
        String ctv = "";
        if (!TextUtils.isEmpty(from)) {
            ctv = extractionChinese(from);
            if (!TextUtils.isEmpty(ctv)) {
                ctv = ctv.substring(ctv.length() - 1, ctv.length());
            } else {
                ctv = from.substring(0, 1);
            }
            if (StringManager.isEnglishChar(ctv)) {
                ctv = ctv.toUpperCase();
            }
        }
        return ctv;
    }

    /**
     * 提取字符串中的汉字
     */
    public static String extractionChinese(String str) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find())
            for (int i = 0; i <= m.groupCount(); i++) {
                sb.append(m.group());
            }
        return sb.toString();
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
            if (contacts.mail.equals(App.addresser.account)) {
                contacts.name = "我";
            }
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
     * 转换发件人,收件人，抄送人，密送人
     */
    public static String conversionToContactsString(ArrayList<Contacts> list) {
        if (list.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < list.size(); i++) {
                Contacts contacts = list.get(i);
                sb.append(contacts.name + "<" + contacts.mail + ">;");
            }
            String string = sb.toString();
            return string.substring(0, string.length() - 1);
        }
        return "";
    }

    /**
     * 邮件相同认为是同一个
     */
    public static boolean containsContacts(Contacts c, List<Contacts> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).mail.equals(c.mail)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换附件
     */
    public static String conversionToAttachsString(ArrayList<Attach> attachs) {
        if (attachs.size() > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < attachs.size(); i++) {
                Attach attach = attachs.get(i);
                sb.append(attach.path + ";");
            }
            String string = sb.toString();
            return string.substring(0, string.length() - 1);
        }
        return "";
    }

    /**
     * 转换附件
     */
    public static ArrayList<Attach> conversionToAttachs(String string) {
        ArrayList<Attach> list = new ArrayList<>();
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split(";");
            for (int i = 0; i < split.length; i++) {
                File file = new File(split[i]);
                list.add(new Attach(file.getName(), file.getAbsolutePath(), Formatter.formatFileSize(App.getApplication(), file.length())));
            }
        }
        return list;
    }


    /**
     * 如果tv的结尾没有“、”则给他加上“、”
     */
    public static void addComma(TextView tv) {
        String string = getStringByTv(tv);
        string = string.endsWith(StringManager.getString(R.string.comma)) ? string : string + StringManager.getString(R.string.comma);
        tv.setText(string);
    }

    /**
     * 如果tv的结尾有“、”则去掉“、”
     */
    public static void removeComma(TextView tv) {
        String string = getStringByTv(tv);
        string = string.endsWith("、") ? string.substring(0, string.length() - 1) : string;
        tv.setText(string);
    }

    /**
     * spanned 转化成HTML
     */
    public static String convertSpannedToRichText(Spanned spanned, boolean isWithImage) {
        List<CharacterStyle> spanList = Arrays.asList(spanned.getSpans(0, spanned.length(), CharacterStyle.class));
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(spanned);
        for (CharacterStyle characterStyle : spanList) {
            int start = stringBuilder.getSpanStart(characterStyle);
            int end = stringBuilder.getSpanEnd(characterStyle);
            if (start >= 0) {
                String htmlStyle = handleCharacterStyle(characterStyle, isWithImage);

                if (htmlStyle != null) {
                    stringBuilder.replace(start, end, htmlStyle);
                }
            }
        }
        return stringBuilder.toString().replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;").replaceAll("✺", " ");
    }

    private static String handleCharacterStyle(CharacterStyle characterStyle, boolean isWithImage) {
        if (characterStyle instanceof ImageSpan) {
            ImageSpan span = (ImageSpan) characterStyle;
            if (isWithImage) {//带本地路径
                return String.format("<img✺src=\"%s\">", "file://" + span.getSource());
            } else {//带cid
                return String.format("<img✺src=\"cid:%s\">", FileUtils.getMd5(new File(span.getSource())));
            }
        }
        return null;
    }

//    /**
//     *回复邮件的时候，把无照片的内容转换成有图片的内容，并把图片保存到images中
//     */
    public static String withImage2NoImage(String withImage, List<Image> list, List<Image> images) {
        String noImageOld = "" + withImage;
        for (Image key : list) {
            if (noImageOld.contains("file://"+key.path)) {
                noImageOld  = noImageOld.replaceFirst("file://"+key.path, "cid:"+key.cid);
                    images.add(key);
            }
        }
        return noImageOld;
    }
}
