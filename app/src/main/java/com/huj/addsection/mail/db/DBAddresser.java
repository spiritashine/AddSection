package com.huj.addsection.mail.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.huj.addsection.BaseApplication;
import com.huj.addsection.mail.bean.Addresser;
import com.huj.addsection.mail.bean.Protocol;

import java.util.ArrayList;

public class DBAddresser {

    private DBHelper dbHelper;
    private static DBAddresser dBAddresser;

    private DBAddresser(Context context) {
        super();
        dbHelper = DBHelper.getInstance(context);
    }

    public static DBAddresser getInstance() {
        if (dBAddresser == null) {
            dBAddresser = new DBAddresser(BaseApplication.getApplication());
        }
        return dBAddresser;
    }

    /**
     * 增加Addresser
     */
    public void addAddresser(Addresser a) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "replace   into addresser_table " + " values (null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            if (a.getSendProtocol()==null){
                db.execSQL(str_sql, new Object[]{a.account, a.password, a.name, a.hasSign, a.sign, a.isDeleteFromServer ? 1 : 0, a.autoDeleteEmailTiem,
                        a.receiveProtocol.name, a.receiveProtocol.host, a.receiveProtocol.port, a.receiveProtocol.ssl ? 1 : 0,
                        "", "", "", 0});
            }else {
                db.execSQL(str_sql, new Object[]{a.account, a.password, a.name, a.hasSign, a.sign, a.isDeleteFromServer ? 1 : 0, a.autoDeleteEmailTiem,
                        a.receiveProtocol.name, a.receiveProtocol.host, a.receiveProtocol.port, a.receiveProtocol.ssl ? 1 : 0,
                        a.sendProtocol.name, a.sendProtocol.host, a.sendProtocol.port, a.sendProtocol.ssl ? 1 : 0});
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("db", "添加失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }


    /**
     * 删除
     */
    public boolean deleteAddresser(Addresser a) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "delete from addresser_table where account = ? ;";

            db.execSQL(str_sql, new Object[]{a.account});
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
     * 更新服务器数据
     * update addresser_table set receiveProtocolName='imap', receiveProtocolHost='mail.qq.com',receiveProtocolPort='25',
     * receiveProtocolSsl=1 ,sendProtocolName='imap', sendProtocolHost='mail.qq.com',sendProtocolPort='110',
     * sendProtocolSsl=1 where account ='123458';
     */
    public void updateAddresserProtocol(Addresser addresser) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "update addresser_table set receiveProtocolName=? , receiveProtocolHost=? ,receiveProtocolPort=? ,receiveProtocolSsl=? , " +
                    " sendProtocolName=? ,sendProtocolHost=? ,sendProtocolPort=? , sendProtocolSsl = ? where account =?;" ;
            db.execSQL(str_sql, new Object[]{addresser.receiveProtocol.name, addresser.receiveProtocol.host, addresser.receiveProtocol.port,
                    addresser.receiveProtocol.ssl ? 1 : 0, addresser.sendProtocol.name, addresser.sendProtocol.host,
                    addresser.sendProtocol.port, addresser.sendProtocol.ssl ? 1 : 0, addresser.account});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "修改失败 e="+e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 更新账号的签名
     */
    public void updateAddresserSign(Addresser addresser) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "update addresser_table set hasSign=?, sign=?  where account =? ;";
            db.execSQL(str_sql, new Object[]{addresser.hasSign ? 1 : 0, addresser.sign, addresser.account});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "修改失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }

    /**
     * 更新账号的名字
     */
    public void updateAddresserName(Addresser addresser) {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "update addresser_table set name=?  where account =? ;";
            db.execSQL(str_sql, new Object[]{addresser.name, addresser.account});
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "修改失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
    }


    /**
     * 查询所有
     */
    public ArrayList<Addresser> selectAllAddresser() {
        ArrayList<Addresser> list = new ArrayList<Addresser>();
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "select * from addresser_table;";
            Cursor cursor = db.rawQuery(str_sql, null);
            while (cursor.moveToNext()) {
                Addresser a = new Addresser();
                a.account = cursor.getString(cursor.getColumnIndex("account"));
                a.password = cursor.getString(cursor.getColumnIndex("password"));
                a.name = cursor.getString(cursor.getColumnIndex("name"));
                a.hasSign = cursor.getInt(cursor.getColumnIndex("hasSign")) == 1;
                a.sign = cursor.getString(cursor.getColumnIndex("sign"));
                a.isDeleteFromServer = cursor.getInt(cursor.getColumnIndex("isDeleteFromServer")) == 1;
                a.autoDeleteEmailTiem = cursor.getInt(cursor.getColumnIndex("autoDeleteEmailTiem"));
                a.receiveProtocol = new Protocol();
                a.receiveProtocol.name = cursor.getString(cursor.getColumnIndex("receiveProtocolName"));
                a.receiveProtocol.host = cursor.getString(cursor.getColumnIndex("receiveProtocolHost"));
                a.receiveProtocol.port = cursor.getString(cursor.getColumnIndex("receiveProtocolPort"));
                a.receiveProtocol.ssl = cursor.getInt(cursor.getColumnIndex("receiveProtocolSsl")) == 1;
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("sendProtocolName")))){
                    a.sendProtocol = new Protocol();
                    a.sendProtocol.name = cursor.getString(cursor.getColumnIndex("sendProtocolName"));
                    a.sendProtocol.host = cursor.getString(cursor.getColumnIndex("sendProtocolHost"));
                    a.sendProtocol.port = cursor.getString(cursor.getColumnIndex("sendProtocolPort"));
                    a.sendProtocol.ssl = cursor.getInt(cursor.getColumnIndex("sendProtocolSsl")) == 1;
                }
                list.add(a);
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
     * 查询
     */
    public Addresser selectAddresser(String account) {

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String str_sql = "select * from addresser_table where account = '" + account + "' ;";
            Cursor cursor = db.rawQuery(str_sql, null);
            while (cursor.moveToNext()) {
                Addresser a = new Addresser();
                a.account = cursor.getString(cursor.getColumnIndex("account"));
                a.password = cursor.getString(cursor.getColumnIndex("password"));
                a.name = cursor.getString(cursor.getColumnIndex("name"));
                a.hasSign = cursor.getInt(cursor.getColumnIndex("hasSign")) == 1;
                a.sign = cursor.getString(cursor.getColumnIndex("sign"));
                a.isDeleteFromServer = cursor.getInt(cursor.getColumnIndex("isDeleteFromServer")) == 1;
                a.autoDeleteEmailTiem = cursor.getInt(cursor.getColumnIndex("autoDeleteEmailTiem"));
                a.receiveProtocol = new Protocol();
                a.receiveProtocol.name = cursor.getString(cursor.getColumnIndex("receiveProtocolName"));
                a.receiveProtocol.host = cursor.getString(cursor.getColumnIndex("receiveProtocolHost"));
                a.receiveProtocol.port = cursor.getString(cursor.getColumnIndex("receiveProtocolPort"));
                a.receiveProtocol.ssl = cursor.getInt(cursor.getColumnIndex("receiveProtocolSsl")) == 1;
                if (!TextUtils.isEmpty(cursor.getString(cursor.getColumnIndex("sendProtocolName")))){
                    a.sendProtocol = new Protocol();
                    a.sendProtocol.name = cursor.getString(cursor.getColumnIndex("sendProtocolName"));
                    a.sendProtocol.host = cursor.getString(cursor.getColumnIndex("sendProtocolHost"));
                    a.sendProtocol.port = cursor.getString(cursor.getColumnIndex("sendProtocolPort"));
                    a.sendProtocol.ssl = cursor.getInt(cursor.getColumnIndex("sendProtocolSsl")) == 1;
                }
                return a;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("db", "查询失败");
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
        return null;
    }

}
