package com.d.lib.xmlparser.compiler;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * BindNode
 * Created by D on 2018/3/24.
 */
public class BindingSet extends BindingNode {

    public BindingSet(ProcessingEnvironment processingEnv, BindLink bindLink) {
        super(processingEnv, bindLink);
    }
}
