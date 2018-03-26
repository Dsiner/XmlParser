package com.d.lib.xmlparser.compiler;

import com.squareup.javapoet.ClassName;

/**
 * BindInterface
 * Created by D on 2018/3/24.
 */
public class BindLink {
    public ClassName targetClassName;
    public ClassName bindingClassName;

    public BindLink(String targetPackageName, String targetSimpleName, String bindingSimpleName) {
        targetClassName = ClassName.get(targetPackageName, targetSimpleName);
        bindingClassName = ClassName.get(targetPackageName, bindingSimpleName);
    }
}
