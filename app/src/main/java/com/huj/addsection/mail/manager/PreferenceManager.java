package com.huj.addsection.mail.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

public class PreferenceManager {
    private static Editor editor;
    private static SharedPreferences sp;

    private static SharedPreferences getPreferencesInstance(Context context) {
        if (sp == null && context != null) {
            sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static Editor getEditorInstance(Context context) {

        if (editor == null) {
            editor = getPreferencesInstance(context).edit();
        }
        return editor;
    }


    public static void setString(Context context, String key, String value) {
        getEditorInstance(context).putString(key, value).commit();
    }

    public static void setBoolean(Context context, String key, boolean value) {
        getEditorInstance(context).putBoolean(key, value).commit();
    }

    public static void setInt(Context context, String key, int value) {
        getEditorInstance(context).putInt(key, value).commit();
    }

    public static void setLong(Context context, String key, long value) {
        getEditorInstance(context).putLong(key, value).commit();
    }

    public static void setFloat(Context context, String key, float value) {
        getEditorInstance(context).putFloat(key, value).commit();
    }


    public static String getString(Context context, String key) {
        return getString(context, key, "");
    }

    ;

    public static String getString(Context context, String key, String defValue) {
        return getPreferencesInstance(context).getString(key, defValue);
    }

    ;

    public static boolean getBoolean(Context context, String key) {
        return getPreferencesInstance(context).getBoolean(key, false);
    }

    public static boolean getBoolean(Context context, String key, Boolean defValue) {
        return getPreferencesInstance(context).getBoolean(key, defValue);
    }

    public static int getInt(Context context, String key) {
        return getPreferencesInstance(context).getInt(key, 0);
    }

    public static int getInt(Context context, String key, int defValue) {
        return getPreferencesInstance(context).getInt(key, defValue);
    }

    public static long getLong(Context context, String key) {
        return getPreferencesInstance(context).getLong(key, 0);
    }

    public static float getFloat(Context context, String key) {
        return getPreferencesInstance(context).getFloat(key, 0.0F);
    }

    /*
     * 清空SharedPreferences
     * */
    public static void clear(Context context) {
        getEditorInstance(context).clear();
        editor.commit();
    }

    /**
     * 缓存的时候存储输入的内容
     * （搜索栏缓存）
     *
     * @param key   存储是的键
     * @param value 存储是的值
     * @param size  存储的条数
     */
    public static void saveKeys(Context context, String key, String value, int size) {
        if (!TextUtils.isEmpty(value)) {
            String[] keys = getKeys(context, key);
            StringBuilder sb = new StringBuilder(value);
            size--;
            int count = size < keys.length ? size : keys.length;
            for (int i = 0; i < count; i++) {
                sb.append(":" + keys[i]);
            }
            setString(context, key, sb.toString());
        }
    }

    /**
     * 默认保存5条数据
     */
    public static void saveKeys(Context context, String key, String value) {
        saveKeys(context, key, value, 5);
    }

    /**
     * 取出存储的keys
     */
    public static String[] getKeys(Context context, String key) {
        String phones = getString(context, key);
        if (!TextUtils.isEmpty(phones)) {
            return phones.split(":");
        }
        return new String[]{};
    }

    /**
     * 清空存储的keys
     */
    public static void clearKeys(Context context, String key) {
        setString(context, key, "");
    }
}
