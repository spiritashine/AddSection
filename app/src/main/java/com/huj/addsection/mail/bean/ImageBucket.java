package com.huj.addsection.mail.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * 一个目录的相册对象
 * 
 * @author Administrator
 * 
 */
public class ImageBucket implements Parcelable {
	public int count = 0;
	public int selectedCount = 0;
	public String bucketName;
	public ArrayList<ImageItem> imageList;

	@Override
	public String toString() {
		return "ImageBucket{" +
				"count=" + count +
				", selectedCount=" + selectedCount +
				", bucketName='" + bucketName + '\'' +
				", imageList=" + imageList +
				'}';
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.count);
		dest.writeInt(this.selectedCount);
		dest.writeString(this.bucketName);
		dest.writeTypedList(imageList);
	}

	public ImageBucket() {
	}

	protected ImageBucket(Parcel in) {
		this.count = in.readInt();
		this.selectedCount = in.readInt();
		this.bucketName = in.readString();
		this.imageList = in.createTypedArrayList(ImageItem.CREATOR);
	}

	public static final Parcelable.Creator<ImageBucket> CREATOR = new Parcelable.Creator<ImageBucket>() {
		@Override
		public ImageBucket createFromParcel(Parcel source) {
			return new ImageBucket(source);
		}

		@Override
		public ImageBucket[] newArray(int size) {
			return new ImageBucket[size];
		}
	};
}
