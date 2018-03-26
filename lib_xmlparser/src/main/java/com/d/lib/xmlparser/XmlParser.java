package com.d.lib.xmlparser;

import android.support.annotation.CheckResult;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * XmlParser
 * Created by D on 2018/3/24.
 */
public class XmlParser {
    private static boolean debug = false;
    private static final String TAG = "XmlParser";

    public static <T> T parserInvoke(Class<T> targetClass, String xml) {
        T result = null;
        if (debug) Log.d("XmlParser", "Looking up binding for " + targetClass.getName());
        String clsName = targetClass.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return null;
        }
        try {
            Class<?> bindingClass = targetClass.getClassLoader().loadClass(clsName + "$$XmlBinder");
            Method method = bindingClass.getMethod("parserXml", String.class);
            result = (T) method.invoke(null, xml);
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T parser(Class<T> targetClass, String xml) {
        if (debug) Log.d("XmlParser", "Looking up binding for " + targetClass.getName());
        Constructor<?> constructor = findBindingConstructorForClass(targetClass);
        if (constructor == null) {
            return null;
        }
        //noinspection TryWithIdenticalCatches Resolves to API 19+ only type.
        try {
            Object obj = constructor.newInstance();
            if (obj instanceof AbsXmlParser) {
                return (T) ((AbsXmlParser) obj).parserXml(xml);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to invoke " + constructor, e);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new RuntimeException("Unable to create binding instance.", cause);
        }
        return null;
    }

    @Nullable
    @CheckResult
    @UiThread
    private static Constructor<?> findBindingConstructorForClass(Class<?> cls) {
        Constructor<?> bindingCtor;
        String clsName = cls.getName();
        if (clsName.startsWith("android.") || clsName.startsWith("java.")) {
            if (debug) Log.d(TAG, "MISS: Reached framework class. Abandoning search.");
            return null;
        }
        try {
            Class<?> bindingClass = cls.getClassLoader().loadClass(clsName + "$$XmlBinder");
            //noinspection unchecked
            bindingCtor = (Constructor<?>) bindingClass.getConstructor();
            if (debug) Log.d(TAG, "HIT: Loaded binding class and constructor.");
        } catch (ClassNotFoundException e) {
            if (debug) Log.d(TAG, "Not found. Trying superclass " + cls.getSuperclass().getName());
            bindingCtor = findBindingConstructorForClass(cls.getSuperclass());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find binding constructor for " + clsName, e);
        }
        return bindingCtor;
    }
}
