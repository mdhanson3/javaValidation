package com.hanson.javaValidation;

/**
 * Holds a line number and keyword.
 *
 * Created by student on 4/19/16.
 */
public class KeywordInstance {
    private int lineNumber;
    private String keyword;

    public KeywordInstance(int line, String word) {
        lineNumber = line;
        keyword = word;
    }
    public int getLineNumber() {
        return lineNumber;
    }

    public String getKeyword() {
        return keyword;
    }

}
