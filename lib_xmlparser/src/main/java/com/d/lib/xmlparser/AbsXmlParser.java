package com.d.lib.xmlparser;

/**
 * AbsXmlParser
 * Created by D on 2018/3/24.
 */
public abstract class AbsXmlParser<T> {

    protected abstract T parserXml(String source);
}
