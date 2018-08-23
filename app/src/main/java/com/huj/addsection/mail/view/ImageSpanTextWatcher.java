package com.huj.addsection.mail.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.widget.EditText;

import com.huj.addsection.App;
import com.huj.addsection.mail.bean.Image;
import com.huj.addsection.mail.utils.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ImageSpanTextWatcher implements TextWatcher {
    private final EditText mEditor;
    ArrayList<Image> images;
    private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<>();

    public ImageSpanTextWatcher(EditText editor, ArrayList<Image> images) {
        this.mEditor = editor;
        this.images = images;
        mEditor.addTextChangedListener(this);
    }

    /**
     * @param path  图片路径
     * @param index 如果等于-1，则在光标选定位置插入图片，如果不等于-1则在index位置加入图片
     */
    public void insert(String path, int index) {
        String emoticon = "☠";
        Drawable drawable = new BitmapDrawable(App.getApplication().getResources(), revitionImageSize(path));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        ImageSpan span = new ImageSpan(drawable, path, ImageSpan.ALIGN_BASELINE);
        Editable message = mEditor.getEditableText();
        int start = index;
        int end = index;
        if (index == -1) {
            start = mEditor.getSelectionStart();
            end = mEditor.getSelectionEnd();
        }
        message.replace(start, end, emoticon);
        message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        Image image = new Image(path, FileUtils.getMd5(new File(path)));
            images.add(image);
    }


    public Bitmap revitionImageSize(String path) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(new File(path)));
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();
            int i = 0;
            while (true) {
                if ((options.outWidth >> i <= 1000) && (options.outHeight >> i <= 1000)) {
                    in = new BufferedInputStream(new FileInputStream(new File(path)));
                    options.inSampleSize = (int) Math.pow(2.0D, i);
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeStream(in, null, options);
                    break;
                }
                i += 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void beforeTextChanged(CharSequence text, int start, int count, int after) {
        if (count > 0) {
            int end = start + count;
            Editable message = mEditor.getEditableText();
            ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

            for (ImageSpan span : list) {
                int spanStart = message.getSpanStart(span);
                int spanEnd = message.getSpanEnd(span);
                if ((spanStart < end) && (spanEnd > start)) {
                    mEmoticonsToRemove.add(span);
                    removeCid(span);//删除图片时也删除对应的cid
                }
            }
        }
    }

    /**
     * 删除图片时也删除对应的cid
     */
    private void removeCid(ImageSpan span) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).path.equals(span.getSource())) {
                images.remove(i);
                return;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable text) {
        Editable message = mEditor.getEditableText();
        for (ImageSpan span : mEmoticonsToRemove) {
            int start = message.getSpanStart(span);
            int end = message.getSpanEnd(span);
            message.removeSpan(span);
            if (start != end) {
                message.delete(start, end);
            }
        }
        mEmoticonsToRemove.clear();
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
    }

}

