package com.d.lib.xmlparser.compiler.utils;

import android.support.annotation.NonNull;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;

/**
 * XmlHelper
 * Created by D on 2018/3/24.
 */
public class XmlHelper {
    private static final ClassName TEXT_UTILS = ClassName.get("android.text", "TextUtils");

    private static TypeName bestGuess(String type) {
        switch (type) {
            case "void":
                return TypeName.VOID;
            case "boolean":
                return TypeName.BOOLEAN;
            case "byte":
                return TypeName.BYTE;
            case "char":
                return TypeName.CHAR;
            case "double":
                return TypeName.DOUBLE;
            case "float":
                return TypeName.FLOAT;
            case "int":
                return TypeName.INT;
            case "long":
                return TypeName.LONG;
            case "short":
                return TypeName.SHORT;
            default:
                int left = type.indexOf('<');
                if (left != -1) {
                    ClassName typeClassName = ClassName.bestGuess(type.substring(0, left));
                    List<TypeName> typeArguments = new ArrayList<>();
                    do {
                        typeArguments.add(WildcardTypeName.subtypeOf(Object.class));
                        left = type.indexOf('<', left + 1);
                    } while (left != -1);
                    return ParameterizedTypeName.get(typeClassName,
                            typeArguments.toArray(new TypeName[typeArguments.size()]));
                }
                return ClassName.bestGuess(type);
        }
    }

    public static JavaFile brewJava() {

        TypeSpec xmlHelper = TypeSpec.classBuilder("XmlHelper")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(getBooleanMethod())
                .addMethod(getIntMethod())
                .addMethod(getLongMethod())
                .build();

        return JavaFile.builder("com.d.lib.xmlparser", xmlHelper)
                .addFileComment("Generated code from XmlParser. Do not modify!")
                .build();
    }

    @NonNull
    private static MethodSpec getBooleanMethod() {
        return MethodSpec.methodBuilder("converBoolean")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(boolean.class)
                .addParameter(String.class, "value")
                .beginControlFlow("if ($T.isEmpty(value))", TEXT_UTILS)
                .addStatement("return false")
                .endControlFlow()
                .beginControlFlow("if ($T.equals(value, \"1\"))", TEXT_UTILS)
                .addStatement("return true")
                .nextControlFlow("else if ($T.equals(value, \"0\"))", TEXT_UTILS)
                .addStatement("return false")
                .endControlFlow()
                // try catch ---->
                .beginControlFlow("try")
                .addStatement("return $T.parseBoolean(value)", Boolean.class)
                .nextControlFlow("catch ($T e)", NumberFormatException.class)
                .addStatement("e.printStackTrace()")
                .addStatement("return false")
                .endControlFlow()
                // try catch <----
                .build();
    }


    @NonNull
    private static MethodSpec getIntMethod() {
        return MethodSpec.methodBuilder("converInt")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(int.class)
                .addParameter(String.class, "value")
                .beginControlFlow("if ($T.isEmpty(value))", TEXT_UTILS)
                .addStatement("return 0")
                .endControlFlow()

                // try catch ---->
                .beginControlFlow("try")
                .addStatement("return $T.parseInt(value)", Integer.class)
                .nextControlFlow("catch ($T e)", NumberFormatException.class)
                .addStatement("e.printStackTrace()")
                .addStatement("return 0")
                .endControlFlow()
                // try catch <----
                .build();
    }

    @NonNull
    private static MethodSpec getLongMethod() {
        return MethodSpec.methodBuilder("converLong")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(long.class)
                .addParameter(String.class, "value")
                .beginControlFlow("if ($T.isEmpty(value))", TEXT_UTILS)
                .addStatement("return 0")
                .endControlFlow()

                // try catch ---->
                .beginControlFlow("try")
                .addStatement("return $T.parseLong(value)", Long.class)
                .nextControlFlow("catch ($T e)", NumberFormatException.class)
                .addStatement("e.printStackTrace()")
                .addStatement("return 0")
                .endControlFlow()
                // try catch <----
                .build();
    }
}
