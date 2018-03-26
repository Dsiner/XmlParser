package com.d.lib.xmlparser.compiler.bind;

import com.squareup.javapoet.TypeName;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;

/**
 * RootBinding
 * Created by D on 2018/3/24.
 */
public class RootBinding {
    public Element element;
    public String name;
    public String field;
    public TypeName typeName;
    public TypeKind typeKind;

    public RootBinding(Element element) {
        this.element = element;
        this.name = element.getAnnotation(com.d.lib.xmlparser.annotations.Root.class).name();
        this.field = element.getSimpleName().toString();
        this.typeName = TypeName.get(element.asType());
        this.typeKind = element.asType().getKind();
    }
}
