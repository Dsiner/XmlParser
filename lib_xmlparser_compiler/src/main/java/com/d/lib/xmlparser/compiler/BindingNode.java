package com.d.lib.xmlparser.compiler;

import com.d.lib.xmlparser.compiler.bind.ElementBinding;
import com.d.lib.xmlparser.compiler.bind.ElementListBinding;
import com.d.lib.xmlparser.compiler.bind.RootBinding;
import com.d.lib.xmlparser.compiler.utils.ParseHelper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * BindingClass
 * Created by D on 2018/3/24.
 */
public class BindingNode {
    private static final ClassName TEXT_UTILS = ClassName.get("android.text", "TextUtils");
    private static final ClassName XML = ClassName.get("android.util", "Xml");
    private static final ClassName XML_PULL_PARSER = ClassName.get("org.xmlpull.v1", "XmlPullParser");
    private static final ClassName XML_HELPER = ClassName.get("com.d.lib.xmlparser", "XmlHelper");
    private static final ClassName ABS_XML_PARSER = ClassName.get("com.d.lib.xmlparser", "AbsXmlParser");

    private static final String RESP = "resp";

    private ProcessingEnvironment processingEnv;
    private Types typeUtils;
    private Elements elementUtils;

    private ClassName list = ClassName.get("java.util", "List");
    private ClassName arrayList = ClassName.get("java.util", "ArrayList");

    private TypeName targetTypeName;
    private ClassName targetClassName;
    private ClassName bindingClassName;

    private List<RootBinding> rootBindings = new ArrayList<>();
    private List<ElementBinding> elementBindings = new ArrayList<>();
    private List<ElementListBinding> elementListBindings = new ArrayList<>();
    private BindingNode parentBinding;

    public BindingNode(ProcessingEnvironment processingEnv, BindLink bindLink) {
        this.processingEnv = processingEnv;
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.targetClassName = bindLink.targetClassName;
        this.bindingClassName = bindLink.bindingClassName;
    }

    public JavaFile brewJava() {
        TypeName listOfTargetClassName = ParameterizedTypeName.get(list, targetClassName);

        MethodSpec.Builder builder = MethodSpec.methodBuilder("parserXml")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(targetClassName)
                .addParameter(String.class, "source")
                .addStatement("$T " + RESP + " = null", targetClassName)

                // try catch ---->
                .beginControlFlow("try")
                .addStatement("$T parser = $T.newPullParser()", XML_PULL_PARSER, XML)
                .addStatement("parser.setInput(new $T(source))", StringReader.class)
                .addCode("\n")

                .addStatement("$T eventType = parser.getEventType()", int.class)
                // while ---->
                .beginControlFlow("while (eventType != $T.END_DOCUMENT)", XML_PULL_PARSER)
                .addStatement("$T name", String.class)

                // switch ---->
                .beginControlFlow("if (eventType == $T.START_DOCUMENT)", XML_PULL_PARSER)
                .addStatement("resp = new $T()", targetClassName)

                .nextControlFlow("else if (eventType == $T.START_TAG)", XML_PULL_PARSER)
                .addStatement("name = parser.getName()");

        //****************************** if... else if... else *****************************

        getLoop(builder);

        //****************************** if... else if... else *****************************

        builder.nextControlFlow("else if (eventType == $T.END_TAG)", XML_PULL_PARSER)
                .endControlFlow()
                // switch <----

                .addStatement("eventType = parser.next()")

                .endControlFlow()
                // while <----

                .nextControlFlow("catch ($T e)", Throwable.class)
                .addStatement("e.printStackTrace()")
                .addStatement("return null")
                .endControlFlow()
                .addCode("\n")
                // try catch <----

                .addStatement("return resp");

        MethodSpec parserXml = builder.build();

        TypeSpec helloWorld = TypeSpec.classBuilder(bindingClassName.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .superclass(ABS_XML_PARSER)
                .addMethod(parserXml)
                .addMethod(parserXml())
                .build();

        return JavaFile.builder(bindingClassName.packageName(), helloWorld)
                .addFileComment("Generated code from XmlParser. Do not modify!")
                .build();
    }

    private void getLoop(MethodSpec.Builder builder) {
        boolean isStart = false;
        for (int i = 0; i < elementBindings.size(); i++) {
            ElementBinding binding = elementBindings.get(i);
            if (!isStart) {
                isStart = true;
                builder.beginControlFlow("if ($T.equals(name, $S))", TEXT_UTILS, binding.name);
            } else {
                builder.nextControlFlow("else if ($T.equals(name, $S))", TEXT_UTILS, binding.name);
            }
            if (binding.typeKind == TypeKind.BOOLEAN) {
                builder.addStatement(RESP + "." + binding.field + " = $T.converBoolean(parser.nextText())", XML_HELPER);
            } else if (binding.typeKind == TypeKind.INT) {
                builder.addStatement(RESP + "." + binding.field + " = $T.converInt(parser.nextText())", XML_HELPER);
            } else if (binding.typeKind == TypeKind.LONG) {
                builder.addStatement(RESP + "." + binding.field + " = $T.converLong(parser.nextText())", XML_HELPER);
            } else if (binding.typeKind == TypeKind.FLOAT) {
                builder.addStatement(RESP + "." + binding.field + " = $T.converFloat(parser.nextText())", XML_HELPER);
            } else if (binding.typeKind == TypeKind.DOUBLE) {
                builder.addStatement(RESP + "." + binding.field + " = $T.converDouble(parser.nextText())", XML_HELPER);
            } else if (binding.element.asType().toString().equals("java.lang.String")) {
                builder.addStatement(RESP + "." + binding.field + " = parser.nextText()");
            } else if (binding.typeKind == TypeKind.BYTE) {
                //BYTE
            } else if (binding.typeKind == TypeKind.SHORT) {
                //SHORT
            } else if (binding.typeKind == TypeKind.CHAR) {
                //CHAR
            } else {
                //Object
                String tName = binding.element.asType().toString();
                builder.addStatement(RESP + "." + binding.field + " = new $T()", ClassName.bestGuess(tName));
                String field = RESP + "." + binding.field;
                builder.addCode("$T.parserXml(" + field + ", parser, name);", ClassName.bestGuess(tName + ParseHelper.BINDING_CLASS_SUFFIX));
            }
        }

        for (int i = 0; i < elementListBindings.size(); i++) {
            ElementListBinding binding = elementListBindings.get(i);
            if (!isStart) {
                isStart = true;
                builder.beginControlFlow("if ($T.equals(name, $S))", TEXT_UTILS, binding.name);
            } else {
                builder.nextControlFlow("else if ($T.equals(name, $S))", TEXT_UTILS, binding.name);
            }

            TypeMirror typeMirror = typeUtils.getArrayType(binding.element.asType()).getComponentType();
            String classNameString = typeMirror.toString();
            classNameString = classNameString.substring(classNameString.indexOf("<") + 1, classNameString.length() - 1);
            ClassName clz = ClassName.bestGuess(classNameString);

            builder.addStatement("$T data = new $T()", clz, clz);

            builder.beginControlFlow("if (" + RESP + "." + binding.field + " == null)", TEXT_UTILS, binding.name)
                    .addStatement(RESP + "." + binding.field + " = new $T<>()", arrayList)
                    .endControlFlow();
            builder.addStatement("$T.parserXml(data, parser, name)",
                    ClassName.bestGuess(classNameString + ParseHelper.BINDING_CLASS_SUFFIX));
            builder.addStatement(RESP + "." + binding.field + ".add(data)");
        }
        if (isStart) {
            builder.endControlFlow();
        }
    }

    private MethodSpec parserXml() {
        ParameterSpec resp = ParameterSpec.builder(targetClassName, RESP)
                .addModifiers(Modifier.FINAL)
                .build();

        ParameterSpec parser = ParameterSpec.builder(XML_PULL_PARSER, "parser")
                .addModifiers(Modifier.FINAL)
                .build();

        ParameterSpec tag = ParameterSpec.builder(String.class, "tag")
                .addModifiers(Modifier.FINAL)
                .build();

        MethodSpec.Builder builder = MethodSpec.methodBuilder("parserXml")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(void.class)
                .addParameter(resp)
                .addParameter(parser)
                .addParameter(tag)
                .addException(Throwable.class)
                .addStatement("$T eventType = parser.getEventType()", int.class)
                // while ---->
                .addStatement("$T keepParsing = true", boolean.class)
                .beginControlFlow("while (keepParsing)")
                .addStatement("$T name", String.class)

                // switch ---->
                .beginControlFlow("if (eventType == $T.START_TAG)", XML_PULL_PARSER)
                .addStatement("name = parser.getName()");

        //****************************** if... else if... else *****************************

        getLoop(builder);

        //****************************** if... else if... else *****************************

        builder.nextControlFlow("else if (eventType == $T.END_TAG)", XML_PULL_PARSER)
                .addStatement("name = parser.getName()")
                .beginControlFlow("if ($T.equals(name,tag))", TEXT_UTILS)
                .addStatement("keepParsing = false")
                .endControlFlow()
                .endControlFlow()
                // switch <----

                .beginControlFlow("if (keepParsing)")
                .addStatement("eventType = parser.next()")
                .endControlFlow()

                .endControlFlow();
        // while <----

        return builder.build();
    }

    public void addRootBinding(RootBinding binding) {
        rootBindings.add(binding);
    }

    public void addElementBinding(ElementBinding binding) {
        elementBindings.add(binding);
    }

    public void addElementListBinding(ElementListBinding binding) {
        elementListBindings.add(binding);
    }

    private boolean hasRootBindings() {
        return !rootBindings.isEmpty();
    }
}
