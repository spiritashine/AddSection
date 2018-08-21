package com.huj.addsection.mail.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.InputStream;

public class Attach implements Parcelable {

    public String name;
    public String path;
    public String size;
    public InputStream in;
    public boolean isLoading=false;


    public Attach() {

    }

    public Attach(String name, String path, String size) {
        this.name = name;
        this.path = path;
        this.size = size;
    }

    public Attach(String name, String path, InputStream in, String size) {
        this.name = name;
        this.path = path;
        this.size = size;
        this.in = in;
    }

    @Override
    public String toString() {
        return "Attach{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", size='" + size + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.path);
        dest.writeString(this.size);
    }

    protected Attach(Parcel in) {
        this.name = in.readString();
        this.path = in.readString();
        this.size = in.readString();
    }

    public static final Parcelable.Creator<Attach> CREATOR = new Parcelable.Creator<Attach>() {
        @Override
        public Attach createFromParcel(Parcel source) {
            return new Attach(source);
        }

        @Override
        public Attach[] newArray(int size) {
            return new Attach[size];
        }
    };
}
