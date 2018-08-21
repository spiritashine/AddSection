package com.huj.addsection.mail.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 收件人的各种信息
 */
public class Addresser implements Parcelable {
    public String account;
    public String password;
    public String name;
    public boolean hasSign;//是否保存签名
    public String sign;
    public int autoDeleteEmailTiem;//自动删除邮件时间 0代表“永不”，1代表“1天”，2代表“7天”，3代表“30天”
    public boolean isDeleteFromServer;//删除邮件的时候是否从服务器上删除
    public Protocol receiveProtocol;//接收协议
    public Protocol sendProtocol;//发送协议

    public Addresser() {
        this.hasSign=true;
        this.sign = "发自青鸟助手";
        this.autoDeleteEmailTiem=1;
    }

    public Addresser(String account, String password) {
        this();
        this.account = account;
        this.password = password;
        this.name = account;

    }

    public Addresser(String account, String password, String name, String sign,
                     boolean isDeleteFromServer, Protocol receiveProtocol, Protocol sendProtocol) {
        this(account, password);
        this.name = name;
        this.sign = sign;

        this.isDeleteFromServer = isDeleteFromServer;
        this.receiveProtocol = receiveProtocol;
        this.sendProtocol = sendProtocol;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    public boolean isDeleteFromServer() {
        return isDeleteFromServer;
    }

    public void setDeleteFromServer(boolean deleteFromServer) {
        isDeleteFromServer = deleteFromServer;
    }

    public Protocol getReceiveProtocol() {
        return receiveProtocol;
    }

    public void setReceiveProtocol(Protocol receiveProtocol) {
        this.receiveProtocol = receiveProtocol;
    }

    public Protocol getSendProtocol() {
        return sendProtocol;
    }

    public void setSendProtocol(Protocol sendProtocol) {
        this.sendProtocol = sendProtocol;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.account);
        dest.writeString(this.password);
        dest.writeString(this.name);
        dest.writeString(this.sign);
        dest.writeInt(this.autoDeleteEmailTiem);
        dest.writeByte(isDeleteFromServer ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.receiveProtocol, flags);
        dest.writeParcelable(this.sendProtocol, flags);
    }

    protected Addresser(Parcel in) {
        this.account = in.readString();
        this.password = in.readString();
        this.name = in.readString();
        this.sign = in.readString();
        this.autoDeleteEmailTiem = in.readInt();
        this.isDeleteFromServer = in.readByte() != 0;
        this.receiveProtocol = in.readParcelable(Protocol.class.getClassLoader());
        this.sendProtocol = in.readParcelable(Protocol.class.getClassLoader());
    }

    public static final Parcelable.Creator<Addresser> CREATOR = new Parcelable.Creator<Addresser>() {
        @Override
        public Addresser createFromParcel(Parcel source) {
            return new Addresser(source);
        }

        @Override
        public Addresser[] newArray(int size) {
            return new Addresser[size];
        }
    };

    @Override
    public String toString() {
        return "Addresser{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", sign='" + sign + '\'' +
                ", autoDeleteEmailTiem=" + autoDeleteEmailTiem +
                ", isDeleteFromServer=" + isDeleteFromServer +
                ", receiveProtocol=" + receiveProtocol +
                ", sendProtocol=" + sendProtocol +
                '}';
    }
}
