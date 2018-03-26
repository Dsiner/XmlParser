package com.d.lib.xmlparser;

import android.text.TextUtils;

public class XmlHelper {

    public static boolean converBoolean(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        if (TextUtils.equals(value, "1")) {
            return true;
        } else if (TextUtils.equals(value, "0")) {
            return false;
        }
        try {
            return Boolean.parseBoolean(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int converInt(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static long converLong(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static float converFloat(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static double converDouble(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
