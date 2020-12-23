package com.d.xmlparser.model;

import com.d.lib.xmlparser.annotations.Element;
import com.d.lib.xmlparser.annotations.ElementList;
import com.d.lib.xmlparser.annotations.Root;
import com.d.xmlparser.model.Model;

import java.util.List;

/**
 * Resp
 * Created by D on 2018/3/24.
 */
@Root(name = "resp")
public class Resp {
    @Element(name = "code", required = false)
    public int code;

    @Element(name = "desc", required = false)
    public String desc;

    @Element(name = "top", required = false)
    public Model top;

    @ElementList(name = "list", required = false)
    public List<Model> list;
}
