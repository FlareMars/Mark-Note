package com.flaremars.markandnote.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.flaremars.markandnote.common.ComponentHolder;
import com.flaremars.markandnote.event.JumpIntent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlareMars on 2016/11/4.
 */
public class MarkdownView extends WebView {

    private static final String TAG = "MarkdownView";

    public static final String DEFAULT_MARKDOWN_CSS_FILE = "file:///android_asset/css/markdown.css";

    public static final List<String> BASE_MARKDOWN_JS_FILES = new ArrayList<>();

    private int backListCount = 0;

    static {
        BASE_MARKDOWN_JS_FILES.add("file:///android_asset/js/zepto.min.js");
        BASE_MARKDOWN_JS_FILES.add("file:///android_asset/js/markdown.js");
    }

    public MarkdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MarkdownView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWebViewClient(new OverrideUrlWebViewClient());
    }

    private class OverrideUrlWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("internal:")) {
                ComponentHolder.getAppComponent().getEventBus().post(JumpIntent.fromUrl(url));
                return true;
            } else {
                backListCount++;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void goBack() {
        backListCount--;
        super.goBack();
    }

    public int getBackListCount() {
        return backListCount;
    }

    public void loadMarkdown(File mdFile) throws IOException {
        loadMarkdown(mdFile, DEFAULT_MARKDOWN_CSS_FILE);
    }

    public void loadMarkdown(File mdFile, String cssFileUrl) throws IOException {
        loadMarkdown(new FileInputStream(mdFile), cssFileUrl);
    }

    public void loadMarkdown(InputStream inputStream) {
        loadMarkdown(inputStream, DEFAULT_MARKDOWN_CSS_FILE);
    }

    public void loadMarkdown(InputStream inputStream, String cssFileUrl) {
        try {
            InputStreamReader inputReader = new InputStreamReader(inputStream);
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line = "";
            String result = "";
            while((line = bufReader.readLine()) != null) {
                result += line + "\n";
            }
            loadMarkdown(result, cssFileUrl, true);
        } catch (Exception e) {
            e.printStackTrace();
            loadMarkdown(exceptionReportStr(e), cssFileUrl, false);
        }
    }

    public void loadMarkdown(String str) {
        loadMarkdown(str, DEFAULT_MARKDOWN_CSS_FILE, true);
    }

    public void loadMarkdown(String str, String cssFileUrl, boolean isMarkdownStr) {
        loadMarkdown(str, cssFileUrl, null, isMarkdownStr);
    }

    public void loadMarkdown(String str, String cssFileUrl, List<String> jsFileUrls, boolean isMarkdownStr) {
        String jsImport = "";
        if (jsFileUrls != null) {
            for (String jsFileUrl: jsFileUrls) {
                jsImport = jsImport + "\n<script src=\"" + jsFileUrl + "\"></script>\n";
            }
        }

        String cssImport = "";
        if (cssFileUrl != null) {
            cssImport = "<link rel='stylesheet' type='text/css' href='" + cssFileUrl + "' />\n";
        }

        MarkdownProcessor processor = new MarkdownProcessor();
        String html;
        if (isMarkdownStr) {
            try {
                html = processor.markdownHtml(str);
            } catch (IOException e) {
                e.printStackTrace();
                html = exceptionReportStr(e);
            }
        } else {
            html = str;
        }
        loadDataWithBaseURL("fake://", cssImport + html + jsImport, "text/html", "UTF-8", null);
    }

    private String exceptionReportStr(Exception e) {
        return MarkdownProcessor.wrapMarkdownBodyTag("<h2>Error: 解析错误\n<pre><code>" + e.getMessage() + "</code></pre>");
    }

}
