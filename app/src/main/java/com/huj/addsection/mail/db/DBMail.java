package com.huj.addsection.mail.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;


import com.huj.addsection.BaseApplication;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Attach;
import com.huj.addsection.mail.bean.Contacts;
import com.huj.addsection.mail.bean.Mail;

import java.io.File;
import java.util.ArrayList;


public class DBMail {

    private DBHelper dbHelper;
    private static DBMail dBMail;

    private DBMail(Context context) {
        super();
        dbHelper = DBHelper.getInstance(context);
    }

    public static DBMail getInstance() {
        synchronized (DBMail.class) {
            if (dBMail == null) {
                dBMail = new DBMail(BaseApplication.getApplication());
            }
        }
        return dBMail;
    }

    /**
     * 创建邮件表
     */
    public void createTable(Addresser addresser) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String stringCreatemail = "create table if not exists " + tableName + " ("
                    + "id integer primary key autoincrement,"
                    + "uid varchar(20) unique,"
                    + "froms varchar(20) ,"
                    + "subject varchar(20) ,"
                    + "contentHint varchar(20) ,"
                    + "contentWithImage varchar(20) ,"
                    + "isSeen integer ,"
                    + "sendDate varchar(20) ,"
                    + "type  integer ,"
                    + "tos varchar(20) ,"
                    + "ccs varchar(20) ,"
                    + "bccs varchar(20) ,"
                    + "attchs varchar(20) ,"
                    + "isContainAttch integer ,"
                    + "textColor integer );";
            db.execSQL(stringCreatemail);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "添加失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 删除邮件表，
     */
    public boolean deleteTable(Addresser addresser) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String stringCreatemail = "  drop table if  exists " + tableName + " ;";

            db.execSQL(stringCreatemail);
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "删除失败");
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return false;
        }
    }

    /**
     * 增加mail
     */
    public void addMail(Addresser addresser, Mail m) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = " replace into  " + tableName + " values (null,?,?,?,?,?,?,?,?,?,?,?,?,?,?); ";//
            db.execSQL(str_sql, new Object[]{m.uid, m.froms, m.subject, m.contentHint, m.contentWithImage, m.isSeen ? 1 : 0, m.sendDate, m.type
                    , m.tos, m.ccs, m.bccs, m.attchs, m.isContainAttch ? 1 : 0, m.textColor});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "添加失败" + e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 删除邮件
     */
    public void deleteMail(Addresser addresser, Mail m) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "delete from " + tableName + " where uid = ? ;";
            db.execSQL(str_sql, new Object[]{m.uid});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "删除失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 邮件更新
     */
    public void updateMail(Addresser addresser, Mail m) {

        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "update " + tableName + " set subject = ? , contentHint = ? , contentWithImage = ? ,isSeen = ? ," +
                    "sendDate = ? , type = ? , tos = ? , ccs = ? , bccs = ? , attchs = ? , isContainAttch = ?  where uid = ? ;";
            db.execSQL(str_sql, new Object[]{m.subject, m.contentHint, m.contentWithImage, m.isSeen ? 1 : 0, m.sendDate, m.type, m.tos,
                    m.ccs, m.bccs, m.attchs, m.isContainAttch ? 1 : 0, m.uid});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("dbmail", "邮件更新失败");
        } finally {
            Log.e("dbmail", "邮件更新成功");
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 邮件设为已删除
     */
    public void updateMailType(Addresser addresser, Mail m) {

        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "update " + tableName + " set type = " + m.type + " where uid =? ;";
            db.execSQL(str_sql, new Object[]{m.uid});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("dbmail", "邮件设置为已删除失败");
        } finally {
            Log.e("dbmail", "邮件设置为已删除成功");
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 邮件设置为已读或者未读
     */
    public void updateMailRead(Addresser addresser, Mail m) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "update " + tableName + " set isSeen = ? where uid = ? ;";
            db.execSQL(str_sql, new Object[]{m.isSeen ? 1 : 0, m.uid});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "标记已读或者未读失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 查询
     */
    public ArrayList<Mail> select(String str_sql) {
        ArrayList<Mail> list = new ArrayList<>();
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(str_sql, null);
            while (cursor.moveToNext()) {
                list.add(getMail(cursor));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "查询失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
        return list;
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
            if (contacts.mail.equals(BaseApplication.addresser.account)) {
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
     * 转换附件
     */
    public static ArrayList<Attach> conversionToAttachs(String string) {
        ArrayList<Attach> list = new ArrayList<>();
        if (!TextUtils.isEmpty(string)) {
            String[] split = string.split(";");
            for (int i = 0; i < split.length; i++) {
                File file = new File(split[i]);
                list.add(new Attach(file.getName(), file.getAbsolutePath(), Formatter.formatFileSize(BaseApplication.getApplication(), file.length())));
            }
        }
        return list;
    }



    /**
     * 实例化mail
     */
    private Mail getMail(Cursor cursor) {
        Mail m = new Mail();
        m.froms = cursor.getString(cursor.getColumnIndex("froms"));
        m.fromContacts = conversionToContacts(m.froms);
        m.tos = cursor.getString(cursor.getColumnIndex("tos"));
        m.ccs = cursor.getString(cursor.getColumnIndex("ccs"));
        m.bccs = cursor.getString(cursor.getColumnIndex("bccs"));
        m.tosList = conversionToContactses(m.tos);
        m.ccsList = conversionToContactses(m.ccs);
        m.bccsList = conversionToContactses(m.bccs);
        m.subject = cursor.getString(cursor.getColumnIndex("subject"));
        m.sendDate = cursor.getString(cursor.getColumnIndex("sendDate"));
        m.contentHint = cursor.getString(cursor.getColumnIndex("contentHint"));
        m.contentWithImage = cursor.getString(cursor.getColumnIndex("contentWithImage"));
        m.attchs = cursor.getString(cursor.getColumnIndex("attchs"));
        m.attchsList = conversionToAttachs(m.attchs);
        m.isSeen = cursor.getInt(cursor.getColumnIndex("isSeen")) == 1;
        m.uid = cursor.getString(cursor.getColumnIndex("uid"));
        m.isContainAttch = cursor.getInt(cursor.getColumnIndex("isContainAttch")) == 1;
        m.type = cursor.getInt(cursor.getColumnIndex("type"));
        m.textColor = cursor.getInt(cursor.getColumnIndex("textColor"));
        return m;
    }

    /**
     * 查询所有
     */
    public ArrayList<Mail> selectAllMail(Addresser addresser) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        String str_sql = "select * from " + tableName + " order by sendDate desc ;";
        return select(str_sql);
    }

    /**
     * 从收件箱中第id个开始取，取一页数据id-(id+total)
     * select * from users order by id limit 10 offset 0
     *
     * @param addresser 账号
     * @param m         从这个开始mail的下一个开始
     * @param total     要取多少个mail
     */
    public ArrayList<Mail> getPageMailFromDbIndex(Addresser addresser, Mail m, int total) {
        ArrayList<Mail> list = new ArrayList<>();
        if (m == null) {
            return list;
        }
        int startId = selectId(addresser, m);
        if (startId == -1) {
            return list;
        }
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        String str_sql = "select  * from " + tableName + " where" + "  type = " + Mail.MAIL_TYPE_INBOX +
                "  order by sendDate desc limit " + Mail.PAGE_COUNT + " offset " + startId + " ;";
        ArrayList<Mail> select = select(str_sql);
        return select;
    }

    /**
     * 从数据库获得数据库中最新的mail
     */
    public Mail getMaxSendDateMailIndex(Addresser addresser) {

        return getExtremeSendDateMailIndex(addresser, "max");
    }

    /**
     * 从数据库获得数据库中时间最久的mail
     */
    public Mail getMinSendDateMailIndex(Addresser addresser) {

        return getExtremeSendDateMailIndex(addresser, "min");
    }

    /**
     * 从收件箱获得数据库中时间最久或者最新的mail
     */
    public Mail getExtremeSendDateMailIndex(Addresser addresser, String extreme) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        String str_sql = "select * from " + tableName + " where sendDate = (select " + extreme + "(sendDate) from " + tableName
                + " where  type = " + Mail.MAIL_TYPE_INBOX + " or type = " + Mail.MAIL_TYPE_DELETED + " or type = "
                + Mail.MAIL_TYPE_DELETE_MAX + " or type = " + Mail.MAIL_TYPE_DELETE_MIN + " or type = " + Mail.MAIL_TYPE_SPAM + "  )  ;";
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(str_sql, null);
            if (cursor.moveToFirst()) {
                Mail m = getMail(cursor);
                return m;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("gao", "查询失败 getExtremeSendDateMailIndex " + e);
//            if (db != null && db.isOpen()) {
//                db.close();
//                db = null;
//            }
            return null;
        }

    }


    /**
     * 查询邮件的id
     */
    public int selectId(Addresser addresser, Mail m) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        int id = -1;
        String str_sql = "select * from " + tableName + " where uid = '" + m.uid + "' ; ";
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(str_sql, null);
            cursor.moveToFirst();
            id = cursor.getInt(cursor.getColumnIndex("id"));
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("gao", "查询失败" + e);
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return id;
        }
    }

    /**
     * 查询收件箱
     */
    public ArrayList<Mail> selectInbox(Addresser addresser) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        String str_sql = "select * from " + tableName + " where type = " + Mail.MAIL_TYPE_INBOX + " order by sendDate desc; ";
        ArrayList<Mail> select = select(str_sql);
        return select;
    }

    /**
     * 根据uid查询邮件
     */
    public Mail selectMail(Addresser addresser, String uid) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        String str_sql = "select * from " + tableName + " where uid = '" + uid + "' ; ";
        ArrayList<Mail> select = select(str_sql);
        return select.get(0);
    }

    /**
     * 根据uid查询邮件
     */
    public Mail getExtremeDeleteMail(Addresser addresser, int type) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        String str_sql = "select * from " + tableName + " where type = " + type + " ; ";
        ArrayList<Mail> select = select(str_sql);
        if (select.isEmpty()) {
            return null;
        } else {
            return select.get(0);
        }
    }

    /**
     * 更新删除邮件的最大，最小值
     */
    public void updataExtremeDeleteMail(Addresser addresser, Mail m, int type) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "delete from " + tableName + " where type =  " + type + "  ;";
            db.execSQL(str_sql);
            String str_sql2 = "update " + tableName + " set type = " + type + " where uid = ? ;";
            db.execSQL(str_sql2, new Object[]{m.uid});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("gao", "updataExtremeDeleteMail   失败" + e);
        }

    }


    /**
     * 查询 各个文件夹的mail
     */
    public ArrayList<Mail> getFolderMail(String account, int type) {
        String tableName = "table_" + account.substring(0, account.indexOf("@"));
        String str_sql = "select * from " + tableName + " where type = " + type + " order by sendDate desc  ; ";
        ArrayList<Mail> select = select(str_sql);
        return select;
    }

    /**
     * 查询未读
     */
    public ArrayList<Mail> getUnread(String account) {
        String tableName = "table_" + account.substring(0, account.indexOf("@"));//
        String str_sql = "select * from " + tableName + " where type = " + Mail.MAIL_TYPE_INBOX + " and  isSeen = 0  order by sendDate desc ; ";
        return select(str_sql);
    }

    /**
     * 查询收件箱里有附件的
     */
    public ArrayList<Mail> selectAttach(String account) {
        String tableName = "table_" + account.substring(0, account.indexOf("@"));
        String str_sql = "select * from " + tableName + " where type = " + Mail.MAIL_TYPE_INBOX + " and  isContainAttch = 1 ; ";
        return select(str_sql);
    }

    /**
     * 查询邮件总数
     */
    public int getCount(String str_sql) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(str_sql, null);
            cursor.moveToFirst();
            int count = cursor.getInt(0);
//            if (db != null && db.isOpen()) {
//                db.close();
//                db = null;
//            }
            return count;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "更新失败e=" + e);
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return 0;
        }
    }

    /**
     * 查询未读邮件的总数
     */
    public int getInboxCountUnread(String account) {
        String tableName = "table_" + account.substring(0, account.indexOf("@"));
        String str_sql = "select count(*) from " + tableName + " where type = " + Mail.MAIL_TYPE_INBOX + " and  isSeen = 0 ; ";
        return getCount(str_sql);
    }

    /**
     * 查询收件箱里的总数
     */
    public int getCount(String account, int tpye) {
        String tableName = "table_" + account.substring(0, account.indexOf("@"));
        String str_sql = "select count(*) from " + tableName + " where type = " + tpye + "  ;";
        return getCount(str_sql);
    }

    /**
     * 根据uid判断数据库中是否存在该邮件
     */
    public boolean exist(Addresser addresser, String uid) {
        String tableName = "table_" + addresser.account.substring(0, addresser.account.indexOf("@"));
        String str_sql = "select count(*) from " + tableName + " where uid = " + uid + "  ;";
        return getCount(str_sql) > 0;
    }

}
