package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 3/1/16.
 */
public class FileParser {
    private String filePath;
    private List<String> fileContents;

    FileParser(String path) {
        filePath = path;
        fileContents = new ArrayList();
    }

    public List getFileContents() {
        return fileContents;
    }

}
