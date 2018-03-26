package com.d.lib.xmlparser.compiler;

import com.d.lib.xmlparser.compiler.utils.ParseHelper;
import com.d.lib.xmlparser.compiler.utils.Util;
import com.d.lib.xmlparser.compiler.utils.VerifyHelper;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@AutoService(Processor.class)
public class XmlParserProcessor extends AbstractProcessor {
    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;
    private ParseHelper parseHelper;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.typeUtils = processingEnv.getTypeUtils();
        this.elementUtils = processingEnv.getElementUtils();
        this.filer = processingEnv.getFiler();
        this.messager = processingEnv.getMessager();
        this.parseHelper = new ParseHelper(processingEnv);
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(com.d.lib.xmlparser.annotations.Root.class.getCanonicalName());
        annotations.add(com.d.lib.xmlparser.annotations.Element.class.getCanonicalName());
        annotations.add(com.d.lib.xmlparser.annotations.ElementList.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 保存包含注解元素的目标类，注意是使用注解的外围类，主要用来处理父类继承
        Set<TypeElement> erasedTargetNames = new LinkedHashSet<>();
        // TypeElement 使用注解的外围类，BindingNode 对应一个要生成的类
        Map<TypeElement, BindingNode> targetClassMap = new LinkedHashMap<>();

        // 处理Root
        Set<? extends Element> roots = roundEnv.getElementsAnnotatedWith(com.d.lib.xmlparser.annotations.Root.class);
        if (roots != null) {
            for (Element element : roots) {
                if (VerifyHelper.verifyRoot(element, messager)) {
                    parseHelper.parseRoot((TypeElement) element, targetClassMap, erasedTargetNames);
                }
            }
        }

        // 处理Element
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(com.d.lib.xmlparser.annotations.Element.class);
        if (elements != null) {
            for (Element element : elements) {
                if (VerifyHelper.verifyElement(element, messager)) {
                    parseHelper.parseElement(element, targetClassMap, erasedTargetNames);
                }
            }
        }

        // 处理ElementList
        Set<? extends Element> elementLists = roundEnv.getElementsAnnotatedWith(com.d.lib.xmlparser.annotations.ElementList.class);
        if (elementLists != null) {
            for (Element element : elementLists) {
                if (VerifyHelper.verifyElementList(element, messager)) {
                    parseHelper.parseElementList(element, targetClassMap, erasedTargetNames);
                }
            }
        }

        for (Map.Entry<TypeElement, BindingNode> entry : targetClassMap.entrySet()) {
            TypeElement typeElement = entry.getKey();
            BindingNode bindingNode = entry.getValue();
            if (typeElement.getKind() != ElementKind.CLASS) {
                continue;
            }
            try {
                // 生成Java文件
                bindingNode.brewJava().writeTo(filer);
            } catch (IOException e) {
                Util._error(messager, typeElement, "Unable to write xml binder for type %s: %s", typeElement,
                        e.getMessage());
            }
        }
        return true;
    }
}
