package com.flaremars.markandnote;

import org.junit.Assert;
import org.junit.Test;
import org.markdown4j.Markdown4jProcessor;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlareMars on 2016/11/9.
 */
public class TestMDProcessor {

    @Test
    public void testMD() {
        Markdown4jProcessor processor = new Markdown4jProcessor();
        try {
            System.out.println(processor.process("# wakaka"));
            Assert.assertEquals(processor.process("# wakaka").trim(), "<h1>wakaka</h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testFindString() throws IOException {
        InputStreamReader inputReader = new InputStreamReader(new FileInputStream("D:\\BugsRepository\\mdprocessor\\src\\main\\assets\\CheckTheTransform.md"));
        BufferedReader bufReader = new BufferedReader(inputReader);
        List<String> contentArray = new ArrayList<>();
        String line;
        while ((line = bufReader.readLine()) != null) {
            contentArray.add(line + "\n");
        }
        bufReader.close();

        String targetStr = "I have a 苹果";
        int position = -1;
        for (int i = 0, size = contentArray.size();i < size;i++) {
            String content = contentArray.get(i);
            if (content.contains(targetStr)) {
                int spaceIndex = content.indexOf(' ');
                if (spaceIndex > 0) {
                    if (content.substring(spaceIndex+1).trim().equals(targetStr)) {
                        position = i;
                        break;
                    }
                }
            }
        }
        org.junit.Assert.assertEquals(30, position);
    }
}
