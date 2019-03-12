package com;

import android.util.Log;

/**
 */

public class LogUtil {

    private static String Tag = null;
    private static final String SL = "yzx";
    private final static int MaxNewLine = 3000;

    public static boolean isIsOpen() {
        return isOpen;
    }

    public static void setIsOpen(boolean isOpen) {
        LogUtil.isOpen = isOpen;
    }

    private static boolean isOpen = true;

    public static void clearTag() {
        Tag = null;
    }

    public static void setTag(String tag) {
        if (tag == null || tag.equals("") || tag.isEmpty()) {
            System.out.print("tag can't null");
            return;
        }
        Tag = tag;
    }

    public static void v(Object... objects) {
        if (!isOpen) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(callMethodAndLine() + ":");
        for (int i = 0; i < objects.length; i++) {
            if (i != objects.length - 1) {
                stringBuffer.append(objects[i] + "" + "_");
            } else {
                stringBuffer.append(objects[i] + "");
            }
        }
        handlertolengthe(stringBuffer + "");
    }

    public static void asl(Object... objects) {
        if (!isOpen) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(callMethodAndLine() + ":");
        for (int i = 0; i < objects.length; i++) {
            if (i != objects.length - 1) {
                stringBuffer.append(objects[i] + "" + "_");
            } else {
                stringBuffer.append(objects[i] + "");
            }
        }
        handlertolength_sl(stringBuffer + "");
    }

    public static void e(Object... objects) {
        if (!isOpen) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(callMethodAndLine() + ":");
        for (int i = 0; i < objects.length; i++) {
            if (i != objects.length - 1) {
                stringBuffer.append(objects[i] + "" + "_");
            } else {
                stringBuffer.append(objects[i] + "");
            }
        }
        handlertolengthe(stringBuffer + "");

    }


    public static void d(Object... objects) {
        if (!isOpen) {
            return;
        }
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(callMethodAndLine() + ":");
        for (int i = 0; i < objects.length; i++) {
            if (i != objects.length - 1) {
                stringBuffer.append(objects[i] + "" + "_");
            } else {
                stringBuffer.append(objects[i] + "");
            }
        }
        handlertolengthd(stringBuffer + "");

    }

    public static void t() {
        if (!isOpen) {
            return;
        }
        Log.e(getClassName(), "", new Throwable());
    }

    private static String callMethodAndLine() {
        String result = "";
        StackTraceElement ss = getStackTrace();
        result += handlerString(ss.getClassName()) + ".";
        result += ss.getMethodName();
        result += "(" + ss.getFileName();
        result += ":" + ss.getLineNumber() + ")  ";
        return result;
    }

    private static String getClassName() {
        String classname = getStackTrace().getClassName();
        return handlerString(classname);
    }

    private static StackTraceElement getStackTrace() {
        StackTraceElement[] s = Thread.currentThread().getStackTrace();
        return s[5];
    }

    private static String handlerString(String values) {
        if (values.contains(".")) {
            values = values.substring(values.lastIndexOf(".") + 1);
        }
        return values;
    }

    private static void handlertolengthe(String values) {

        if (values.length() > MaxNewLine) {
            String temp = values.substring(0, MaxNewLine - 1);
            Log.e(Tag == null ? getClassName() : Tag, temp);
            values = values.substring(MaxNewLine - 1);
            handlertolengthe(values);
        } else {
            Log.e(SL, values);
        }

    }


    private static void handlertolengthd(String values) {

        if (values.length() > MaxNewLine) {
            String temp = values.substring(0, MaxNewLine - 1);
            Log.d(Tag == null ? getClassName() : Tag, temp);
            values = values.substring(MaxNewLine - 1);
            handlertolengthd(values);
        } else {
            Log.d(SL, values);
        }

    }

    private static void handlertolength_sl(String values) {

        if (values.length() > MaxNewLine) {
            String temp = values.substring(0, MaxNewLine - 1);
            Log.d(SL, temp);
            values = values.substring(MaxNewLine - 1);
            handlertolength_sl(values);
        } else {
            Log.d(SL, values);
        }

    }

}
