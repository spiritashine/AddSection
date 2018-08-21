package com.huj.addsection.mail.db;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper dbHelper;
    private DBHelper(Context context) {
        super(context, "xiniuyun.db", null, 1);
        // TODO Auto-generated constructor stub
    }

    //单例模式
    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建邮箱接收发送所用协议表格
        String stringCreateProtocol = "create table if not exists protocol_table("
                + "id integer primary key autoincrement,"
                + "name varchar(20) ,"
                + "host varchar(20) ,"
                + "port varchar(20),"
                + "ssl integer ); ";
        db.execSQL(stringCreateProtocol);
        //初始化协议表格中的数据
        String INSERT_SQL = "insert into protocol_table " + " values (null,?,?,?,?);";
        db.execSQL(INSERT_SQL, new Object[]{"smtp", "smtp.mxhichina.com", "25", "0"});//eims sll=false
        db.execSQL(INSERT_SQL, new Object[]{"pop3", "pop3.mxhichina.com", "110", "0"});//eims sll=false
//        db.execSQL(INSERT_SQL, new Object[]{"smtp", "mail.eims.com.cn", "465", "1"});//eims sll=true
//        db.execSQL(INSERT_SQL, new Object[]{"pop3", "mail.eims.com.cn", "995", "1"});//eims sll=true
        db.execSQL(INSERT_SQL, new Object[]{"smtp", "smtp.qq.com", "25", "0"});//qq sll=false
        db.execSQL(INSERT_SQL, new Object[]{"imap", "imap.qq.com", "143", "0"});//qq sll=false
        db.execSQL(INSERT_SQL, new Object[]{"smtp", "smtp.qq.com", "465", "1"});//qq sll=true
        db.execSQL(INSERT_SQL, new Object[]{"imap", "imap.qq.com", "993", "1"});//qq sll=true
        db.execSQL(INSERT_SQL, new Object[]{"smtp", "smtp.163.com", "25", "0"});//163 sll=false
        db.execSQL(INSERT_SQL, new Object[]{"pop3", "pop.163.com", "110", "0"});//163 sll=false
//        db.execSQL(INSERT_SQL, new Object[]{"smtp", "smtp.163.com", "465", "1"});//163 sll=true
//        db.execSQL(INSERT_SQL, new Object[]{"pop3", "pop.163.com", "995", "1"});//163 sll=true
        db.execSQL(INSERT_SQL, new Object[]{"smtp", "smtp.126.com", "25", "0"});//163 sll=false
        db.execSQL(INSERT_SQL, new Object[]{"pop3", "pop.126.com", "110", "0"});//163 sll=false
//        db.execSQL(INSERT_SQL, new Object[]{"smtp", "smtp.126.com", "465", "1"});//163 sll=true
//        db.execSQL(INSERT_SQL, new Object[]{"pop3", "pop.126.com", "995", "1"});//163 sll=true


        //创建发件人，邮箱账号的表格
        String stringCreateAddresser = "create table if not exists addresser_table("
                + "id integer primary key autoincrement,"
                + "account varchar(20) unique,"
                + "password varchar(20) ,"
                + "name varchar(20) ,"
                + "hasSign integer ,"
                + "sign varchar(20) ,"
                + "isDeleteFromServer integer ,"
                + "autoDeleteEmailTiem integer ,"
                + "receiveProtocolName varchar(20) ,"
                + "receiveProtocolHost varchar(20) ,"
                + "receiveProtocolPort varchar(20) ,"
                + "receiveProtocolSsl integer ,"
                + "sendProtocolName varchar(20) ,"
                + "sendProtocolHost varchar(20) ,"
                + "sendProtocolPort varchar(20) ,"
                + "sendProtocolSsl integer );";
        db.execSQL(stringCreateAddresser);

        //创建通讯录联系人表格
        String stringCreateContacts = "create table if not exists contacts_table ("
                + "id integer primary key autoincrement,"
                + "contactId integer unique ,"
                + "textColor integer  ,"
                + "name varchar(20) ,"
                + "company varchar(20) ,"
                + "department varchar(20) ,"
                + "post varchar(20) ,"
                + "mail varchar(20) ,"
                + "phone varchar(20) ,"
                + "address varchar(20) ,"
                + "remark varchar(20) , "
                + "sortLetters varchar(20) ); ";
        db.execSQL(stringCreateContacts);
        //初始化协议表格中的数据
//		String str_sql = "insert into contacts_table values (null,?,?,?,?,?,?,?,?,?,?,?);";
//		db.execSQL(str_sql, new Object[]{0,2, "xiaoming", "", "", "","", "","","","X"});
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }

}
