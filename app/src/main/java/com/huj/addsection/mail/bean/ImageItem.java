package com.huj.addsection.mail.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 一个图片对象
 * 
 * @author Administrator
 * 
 */
public class ImageItem implements Parcelable {
	public String imageId;
	public String thumbnailPath;
	public String imagePath;
	public boolean isSelected = false;

	@Override
	public String toString() {
		return "ImageItem [imageId=" + imageId + ", thumbnailPath="
				+ thumbnailPath + ", imagePath=" + imagePath + ", isSelected="
				+ isSelected + "]";
	}


	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.imageId);
		dest.writeString(this.thumbnailPath);
		dest.writeString(this.imagePath);
		dest.writeByte(isSelected ? (byte) 1 : (byte) 0);
	}

	public ImageItem() {
	}

	protected ImageItem(Parcel in) {
		this.imageId = in.readString();
		this.thumbnailPath = in.readString();
		this.imagePath = in.readString();
		this.isSelected = in.readByte() != 0;
	}

	public static final Parcelable.Creator<ImageItem> CREATOR = new Parcelable.Creator<ImageItem>() {
		@Override
		public ImageItem createFromParcel(Parcel source) {
			return new ImageItem(source);
		}

		@Override
		public ImageItem[] newArray(int size) {
			return new ImageItem[size];
		}
	};
}
