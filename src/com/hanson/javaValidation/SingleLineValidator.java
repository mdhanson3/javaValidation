package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2/10/16.
 */
public class SingleLineValidator {
    private List<String> fileContents;
    private List<KeywordInstance> keywords;
    private List<SingleLineError> singleLineErrors;

    SingleLineValidator(List<String> content, List<KeywordInstance> listOfKeywords) {
        fileContents = new ArrayList<>();
        singleLineErrors = new ArrayList<>();
        keywords = new ArrayList<>();

        keywords = listOfKeywords;
        fileContents = content;
    }

    public void runSingleLineValidation() {
        // Check every line for these errors
        checkEachLine();

        checkLineInformation();
        // Check each occurrence of found keywords for specific errors
        // Test that each keyword is followed by a single space
        // Test that each function opening line open paren is not preceded by a space
        // Test that each constant name is all uppercase and underscores
        // Test that each variable name starts with a lowercase and is all letters
        // Test that each function name is starts with a lowercase and is all letters
        // Test that each class name starts with Uppercase and is all letters

    }

    private void checkLineInformation() {
        for(KeywordInstance key : keywords) {
            //System.out.println(key.getKeyword() + " " + key.getLineNumber());

            switch(key.getKeyword()) {
                case "public" :
                    System.out.println("SWITCH found public: " + key.getLineNumber() + ". Keyword: " + key.getKeyword());
                    //createPublicError(key);
                    break;

                case "final" :
                    System.out.println("SWITCH found final: " + key.getLineNumber() + ". Keyword: " + key.getKeyword());
                    break;

                case "variable" :
                    System.out.println("SWITCH found variable: " + key.getLineNumber() + ". Keyword: " + key.getKeyword());
                    break;

                default :
                    System.out.println("SWITCH found keyword: " + key.getLineNumber() + ". Keyword: " + key.getKeyword());

            }
        }
    }
    private void checkEachLine() {
        for(int lineNumber = 0; lineNumber < fileContents.size(); lineNumber++) {
            checkLineLength(lineNumber, fileContents.get(lineNumber));
        }
    }

    private void checkLineLength(int lineNumber, String lineText) {
        if (lineText.length() > 80) {
            singleLineErrors.add(new SingleLineError(lineNumber, 0, lineText.length(), "Line longer than 80 characters", "LineLength"));
        }
    }

    public void outputErrors() {
        for (SingleLineError error : singleLineErrors) {
            System.out.println("Line " + error.getLineNumber() + " " + error.getErrorMessage());
        }
    }
}
