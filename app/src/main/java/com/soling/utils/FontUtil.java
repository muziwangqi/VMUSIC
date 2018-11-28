package com.soling.utils;

import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

public class FontUtil {

    private static final String TAG = "FontUtil";

    public static void setDefaultFont(Context context, String fieldName, String asset) {
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), asset);
        replaceFont(typeface, fieldName);
    }

    public static void replaceFont(Typeface typeface, String fieldName) {
        try {
            Field field = Typeface.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, typeface);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
