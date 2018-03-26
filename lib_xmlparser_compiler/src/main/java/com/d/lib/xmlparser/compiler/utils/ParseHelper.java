package com.d.lib.xmlparser.compiler.utils;

import com.d.lib.xmlparser.compiler.BindLink;
import com.d.lib.xmlparser.compiler.BindingNode;
import com.d.lib.xmlparser.compiler.BindingSet;
import com.d.lib.xmlparser.compiler.bind.ElementBinding;
import com.d.lib.xmlparser.compiler.bind.ElementListBinding;
import com.d.lib.xmlparser.compiler.bind.RootBinding;

import java.util.Map;
import java.util.Set;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * ParseHelper
 * Created by D on 2018/3/24.
 */
public class ParseHelper {
    public static final String BINDING_CLASS_SUFFIX = "$$XmlBinder";
    private ProcessingEnvironment processingEnv;
    private Elements elementUtils;

    public ParseHelper(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
        this.elementUtils = processingEnv.getElementUtils();
    }

    public void parseRoot(TypeElement element, Map<TypeElement, BindingNode> targetClassMap, Set<TypeElement> erasedTargetNames) {
        BindingSet bindingSet = (BindingSet) _getOrCreateTargetClass(element, targetClassMap);
        bindingSet.addRootBinding(new RootBinding(element));

        erasedTargetNames.add(element);
    }

    public void parseElement(Element element, Map<TypeElement, BindingNode> targetClassMap, Set<TypeElement> erasedTargetNames) {
        BindingNode bindingClass = _getOrCreateTargetClass(element, targetClassMap);
        bindingClass.addElementBinding(new ElementBinding(element));

        erasedTargetNames.add((TypeElement) element.getEnclosingElement());
    }

    public void parseElementList(Element element, Map<TypeElement, BindingNode> targetClassMap, Set<TypeElement> erasedTargetNames) {
        BindingNode bindingClass = _getOrCreateTargetClass(element, targetClassMap);
        bindingClass.addElementListBinding(new ElementListBinding(element));

        erasedTargetNames.add((TypeElement) element.getEnclosingElement());
    }

    private BindingNode _getOrCreateTargetClass(Element element, Map<TypeElement, BindingNode> targetClassMap) {
        TypeElement enclosingElement;
        if (element.getKind() == ElementKind.CLASS) {
            enclosingElement = (TypeElement) element;
        } else {
            enclosingElement = (TypeElement) element.getEnclosingElement();
        }
        BindingNode bindingClass = targetClassMap.get(enclosingElement);
        if (bindingClass == null) {
            String targetQualify = enclosingElement.getQualifiedName().toString();
            String targetPackageName = elementUtils.getPackageOf(enclosingElement).getQualifiedName().toString();
            String targetSimpleName = targetQualify.substring(targetPackageName.length() + 1);
            String bindingSimpleName = targetSimpleName + BINDING_CLASS_SUFFIX;
            if (element.getKind() == ElementKind.CLASS) {
                bindingClass = new BindingSet(processingEnv, new BindLink(targetPackageName, targetSimpleName, bindingSimpleName));
            } else {
                bindingClass = new BindingNode(processingEnv, new BindLink(targetPackageName, targetSimpleName, bindingSimpleName));
            }
            targetClassMap.put(enclosingElement, bindingClass);
        }
        return bindingClass;
    }
}
