package com.flaremars.markandnote.widget;

import org.markdown4j.Markdown4jProcessor;

import java.io.File;
import java.io.IOException;

/**
 * Created by FlareMars on 2016/11/5.
 */
public class MarkdownProcessor {

    public static final String FORMAT_LINK = "[%s](%s)";
    public static final String FORMAT_PICTURE = "\n![%s](%s)\n";
    public static final String FORMAT_PICTURE_LOCAL = "\n![%s](file://%s)\n";
    public static final String HOLDER_TODOLIST = "%%% todoList\n+ 待完成项目\n- 已完成项目\n%%%\n";

    public String markdownHtml(File mdFile) throws IOException {
        Markdown4jProcessor markdown4jProcessor = new Markdown4jProcessor();
        String result = markdown4jProcessor.process(mdFile);
        return wrapMarkdownBodyTag(result);
    }

    public String markdownHtml(String markdownStr) throws IOException {
        Markdown4jProcessor markdown4jProcessor = new Markdown4jProcessor();
        String result = markdown4jProcessor.process(markdownStr);
        return wrapMarkdownBodyTag(result);
    }

    public static String wrapMarkdownBodyTag(String src) {
        return "<body><article class=\"markdown-body\">" + src + "</article></body>";
    }
}
