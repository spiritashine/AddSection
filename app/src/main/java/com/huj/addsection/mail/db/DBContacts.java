package com.huj.addsection.mail.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.huj.addsection.BaseApplication;
import com.huj.addsection.mail.bean.Contacts;

import java.util.ArrayList;
import java.util.Collections;

public class DBContacts {

    private DBHelper dbHelper;
    private static DBContacts dBContacts;

    private DBContacts(Context context) {
        super();
        dbHelper = DBHelper.getInstance(context);
    }

    public static DBContacts getInstance() {
        if (dBContacts == null) {
            dBContacts = new DBContacts(BaseApplication.getApplication());
        }
        return dBContacts;
    }

    /**
     * 增加Contacts
     */
    public Contacts addContacts(Contacts c) {
        SQLiteDatabase db = null;
        try {
            c.contactId = getMaxContactId() + 1;
            db = dbHelper.getWritableDatabase();
            String str_sql = "insert into contacts_table values (null,?,?,?,?,?,?,?,?,?,?,?);";
            db.execSQL(str_sql, new Object[]{c.contactId, c.textColor, c.name, c.company, c.department, c.post, c.mail, c.phone, c.address, c.remark, c.sortLetters});
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return c;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "添加失败 e =" + e);
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return null;
        }
    }

    /**
     * 删除
     */
    public void deleteContacts(Contacts c) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "delete from contacts_table where contactId = ? ;";
            db.execSQL(str_sql, new Object[]{c.contactId});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "删除失败" + e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 更新数据
     */
    public void updateContacts(Contacts c) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "update contacts_table set textColor=?, name=? , company=?, department=?, post=?," +
                    "  mail=?, phone=? ,address=?, remark=?, sortLetters=? where contactId =?;";
            db.execSQL(str_sql, new Object[]{c.textColor, c.name, c.company, c.department, c.post, c.mail, c.phone, c.address,
                    c.remark, c.sortLetters, c.contactId});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "修改失败" + e);
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
    public ArrayList<Contacts> select(String str_sql) {
        ArrayList<Contacts> list = new ArrayList<Contacts>();
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(str_sql, null);
            while (cursor.moveToNext()) {
                Contacts c = new Contacts(cursor.getString(cursor.getColumnIndex("name")));
                c.contactId = cursor.getInt(cursor.getColumnIndex("contactId"));
                c.textColor = cursor.getInt(cursor.getColumnIndex("textColor"));
                c.company = cursor.getString(cursor.getColumnIndex("company"));
                c.department = cursor.getString(cursor.getColumnIndex("department"));
                c.post = cursor.getString(cursor.getColumnIndex("post"));
                c.mail = cursor.getString(cursor.getColumnIndex("mail"));
                c.phone = cursor.getString(cursor.getColumnIndex("phone"));
                c.address = cursor.getString(cursor.getColumnIndex("address"));
                c.remark = cursor.getString(cursor.getColumnIndex("remark"));
                c.sortLetters = cursor.getString(cursor.getColumnIndex("sortLetters"));
                list.add(c);
                if (db != null && db.isOpen()) {
                    db.close();
                    db = null;
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "查询失败" + e);
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return list;
        }

    }

//    /**
//     * 查询所有联系人
//     */
//    public ArrayList<Contacts> selectAllContacts() {
//        String str_sql = "select * from contacts_table;";
//        ArrayList<Contacts> list = select(str_sql);
//        Collections.sort(list, new PinyinComparator());
//        return list;
//    }
//
//    /**
//     * 查询所有联系人中邮箱地址有效的联系人
//     */
//    public ArrayList<Contacts> selectAllContactsValidMail() {
//        String str_sql = "select * from contacts_table;";
//        ArrayList<Contacts> list = select(str_sql);
//        for (int i = list.size()-1;i>=0;i--){
//            if (TextUtils.isEmpty(list.get(i).mail)){
//                list.remove(i);
//            }
//        }
//        Collections.sort(list, new PinyinComparator());
//        return list;
//    }

    /**
     * 模糊查询,姓名，电话，邮件
     */
    public ArrayList<Contacts> selectFuzzy(String key) {
        if (TextUtils.isEmpty(key)) {
            return new ArrayList<Contacts>();
        }

        String str_sql = "select * from contacts_table where name like '%" + key + "%' or phone like '%" + key + "%' or mail like '%" + key + "%' ;";
        return select(str_sql);
    }

//    /**
//     * 模糊查询,姓名，邮件,且要求邮箱地址不为空，且包含于receivers中
//     */
//    public ArrayList<Contacts> selectFuzzyNameMail(String key, ArrayList<Contacts> receivers) {
//        if (TextUtils.isEmpty(key)) {
//            return new ArrayList<Contacts>();
//        }
//        String str_sql = "select * from contacts_table where name like '%" + key + "%' or mail like '%" + key + "%' ;";
//         ArrayList<Contacts> list = select(str_sql);
//        for (int i = list.size()-1;i>=0;i--){
//            if (TextUtils.isEmpty(list.get(i).mail)|| WriteMailUtils.contains(receivers,list.get(i))){
//                list.remove(i);
//            }
//        }
//        Collections.sort(list, new PinyinComparator());
//
//        return list;
//    }

    /**
     * 获得最大的ContactId
     */
    public int getMaxContactId() {
        String str_sql = "select max(contactId) from contacts_table;";
        int contactIdMax = 0;
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.rawQuery(str_sql, null);
            cursor.moveToFirst();
            contactIdMax = cursor.getInt(0);
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return contactIdMax;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("gao", "查询失败" + e);
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
            return contactIdMax;
        }
    }

    /**
     * 通过邮件查询联系人
     */
    public Contacts selectContactsByMail(String mail) {
        String str_sql = "select * from contacts_table where mail = '" + mail + "' ;";
        ArrayList<Contacts> list = select(str_sql);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }

    /**
     * 通过邮件名字查询联系人
     */
    public Contacts selectContactsByMailName(String key) {
        String str_sql = "select * from contacts_table where mail = '" + key + "' or name = '"+key+"' ;";
        ArrayList<Contacts> list = select(str_sql);
        if (list.size() > 0) {
            return list.get(0);
        } else {
            return null;
        }
    }




}
