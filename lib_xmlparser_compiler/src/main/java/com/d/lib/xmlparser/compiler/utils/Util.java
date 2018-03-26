package com.d.lib.xmlparser.compiler.utils;

import java.util.List;
import java.util.Set;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

/**
 * Util
 * Created by D on 2018/3/24.
 */
public class Util {

    /**
     * 判断该类型是否为 otherType 的子类型
     *
     * @param typeMirror 元素类型
     * @param otherType  比对类型
     */
    private static boolean _isSubtypeOfType(TypeMirror typeMirror, String otherType) {
        if (otherType.equals(typeMirror.toString())) {
            return true;
        }
        if (typeMirror.getKind() != TypeKind.DECLARED) {
            return false;
        }
        DeclaredType declaredType = (DeclaredType) typeMirror;
        // 判断泛型参数列表
        List<? extends TypeMirror> typeArguments = declaredType.getTypeArguments();
        if (typeArguments.size() > 0) {
            StringBuilder typeString = new StringBuilder(declaredType.asElement().toString());
            typeString.append('<');
            for (int i = 0; i < typeArguments.size(); i++) {
                if (i > 0) {
                    typeString.append(',');
                }
                typeString.append('?');
            }
            typeString.append('>');
            if (typeString.toString().equals(otherType)) {
                return true;
            }
        }
        // 判断是否为类或接口类型
        Element element = declaredType.asElement();
        if (!(element instanceof TypeElement)) {
            return false;
        }
        // 判断父类
        TypeElement typeElement = (TypeElement) element;
        TypeMirror superType = typeElement.getSuperclass();
        if (_isSubtypeOfType(superType, otherType)) {
            return true;
        }
        // 判断接口
        for (TypeMirror interfaceType : typeElement.getInterfaces()) {
            if (_isSubtypeOfType(interfaceType, otherType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否为接口
     */
    private static boolean _isInterface(TypeMirror typeMirror) {
        return typeMirror instanceof DeclaredType
                && ((DeclaredType) typeMirror).asElement().getKind() == ElementKind.INTERFACE;
    }

    /**
     * 查找父类型
     *
     * @param typeElement       类元素
     * @param erasedTargetNames 存在的类元素
     */
    public static TypeElement _findParentType(TypeElement typeElement, Set<TypeElement> erasedTargetNames) {
        TypeMirror typeMirror;
        while (true) {
            // 父类型要通过 TypeMirror 来获取
            typeMirror = typeElement.getSuperclass();
            if (typeMirror.getKind() == TypeKind.NONE) {
                return null;
            }
            // 获取父类元素
            typeElement = (TypeElement) ((DeclaredType) typeMirror).asElement();
            if (erasedTargetNames.contains(typeElement)) {
                // 如果父类元素存在则返回
                return typeElement;
            }
        }
    }

    /**
     * 输出错误信息
     */
    public static void _error(Messager messager, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(Diagnostic.Kind.ERROR, message);
    }

    /**
     * 输出错误信息
     */
    public static void _error(Messager messager, Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
