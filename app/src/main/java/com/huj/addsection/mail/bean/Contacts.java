package com.huj.addsection.mail.bean;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 通讯录的实体类
 */
public class Contacts implements Parcelable {
    public int contactId;//联系人id不可以重复。
    public int textColor;//mail展示的时候最前面的变色的圆圈颜色。1-8的随机数，随机生成
    public String name;
    public String company;
    public String department;//部门
    public String post;//职位
    public String mail;
    public String phone;
    public String address;
    public String remark;
    public String sortLetters;  //显示数据拼音的首字母
    public boolean isSelected;//在SelectContactsActivity中用到的属性，其他地方不用，不存数据库

    public Contacts(String name) {
        this.textColor = (int)(Math.random()*5);
        this.name = name;

//        String sortString = CharacterParser.getInstance().getSelling(name).substring(0, 1).toUpperCase();
//        // 正则表达式，判断首字母是否是英文字母
//        if (sortString.matches("[A-Z]")) {
//            sortLetters=sortString.toUpperCase();
//        } else {
//            sortLetters  ="#";
//        }
    }

    public Contacts(String name, String mail) {
        this(name);
        this.mail = mail;
    }

    public Contacts(String name, String company, String department, String post, String mail, String phone, String address, String remark) {
        this(name);
        this.company = company;
        this.department = department;
        this.post = post;
        this.mail = mail;
        this.phone = phone;
        this.address = address;
        this.remark = remark;
    }





    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }




    @Override
    public String toString() {
        return "Contacts{" +
                "name='" + name + '\'' +
                ", company='" + company + '\'' +
                ", department='" + department + '\'' +
                ", post='" + post + '\'' +
                ", mail='" + mail + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.contactId);
        dest.writeInt(this.textColor);
        dest.writeString(this.name);
        dest.writeString(this.company);
        dest.writeString(this.department);
        dest.writeString(this.post);
        dest.writeString(this.mail);
        dest.writeString(this.phone);
        dest.writeString(this.address);
        dest.writeString(this.remark);
        dest.writeString(this.sortLetters);
    }

    protected Contacts(Parcel in) {
        this.contactId = in.readInt();
        this.textColor = in.readInt();
        this.name = in.readString();
        this.company = in.readString();
        this.department = in.readString();
        this.post = in.readString();
        this.mail = in.readString();
        this.phone = in.readString();
        this.address = in.readString();
        this.remark = in.readString();
        this.sortLetters = in.readString();
    }

    public static final Parcelable.Creator<Contacts> CREATOR = new Parcelable.Creator<Contacts>() {
        @Override
        public Contacts createFromParcel(Parcel source) {
            return new Contacts(source);
        }

        @Override
        public Contacts[] newArray(int size) {
            return new Contacts[size];
        }
    };
}
