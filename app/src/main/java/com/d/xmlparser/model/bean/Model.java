package com.d.xmlparser.model.bean;

import com.d.lib.xmlparser.annotations.Element;

/**
 * Model
 * Created by D on 2018/3/24.
 */
public class Model {

    @Element(name = "bra", required = false)
    public boolean b;

    @Element(name = "car", required = false)
    public char c;

    @Element(name = "intell", required = false)
    public int i;

    @Element(name = "lemon", required = false)
    public long l;

    @Element(name = "far", required = false)
    public float f;

    @Element(name = "dior", required = false)
    public double d;

    @Element(name = "content", required = false)
    public String content;
}
