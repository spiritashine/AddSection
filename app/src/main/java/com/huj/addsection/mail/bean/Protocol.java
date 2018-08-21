package com.huj.addsection.mail.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Protocol 通信协议
 */
public class Protocol implements Parcelable {
	public	String  name;
	public	String  host;
	public	String  port;
	public	 boolean ssl;

	public Protocol() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Protocol(String name, String host, String port, boolean ssl) {
		super();
		this.name = name;
		this.host = host;
		this.port = port;
		this.ssl = ssl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}

	@Override
	public String toString() {
		return "Protocol [name=" + name + ", host=" + host + ", port=" + port
				+ ", ssl=" + ssl + "]";
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.name);
		dest.writeString(this.host);
		dest.writeString(this.port);
		dest.writeByte(ssl ? (byte) 1 : (byte) 0);
	}

	protected Protocol(Parcel in) {
		this.name = in.readString();
		this.host = in.readString();
		this.port = in.readString();
		this.ssl = in.readByte() != 0;
	}

	public static final Parcelable.Creator<Protocol> CREATOR = new Parcelable.Creator<Protocol>() {
		@Override
		public Protocol createFromParcel(Parcel source) {
			return new Protocol(source);
		}

		@Override
		public Protocol[] newArray(int size) {
			return new Protocol[size];
		}
	};
}
