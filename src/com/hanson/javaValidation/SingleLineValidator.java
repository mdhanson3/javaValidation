package com.hanson.javaValidation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2/10/16.
 */
public class SingleLineValidator {
    private List<String> fileContents;

    private List<String[]> singleLineErrors;

    SingleLineValidator(List<String> content) {
        fileContents = new ArrayList<>();
        singleLineErrors = new ArrayList<>();

        fileContents = content;
    }

    public void runSingleLineValidation() {
        // Check every line for these errors


        // Test line length > 80
        // Test that each keyword is followed by a single space
        // Test that each function opening line open paren is not preceded by a space
        // Test that each constant name is all uppercase and underscores
        // Test that each variable name starts with a lowercase and is all letters
        // Test that each function name is starts with a lowercase and is all letters
        // Test that each class name starts with Uppercase and is all letters

    }

    private void runErrorTests(int lineNumber, String lineText) {
        // These test check all text regardless of quotes or comments
        characterCountPerLine(lineNumber, lineText);

        /*
        TODO: Strip unnecessary text (comments, multi-line comments, java-doc comments, text in quotes, text in double-quotes)
        Consider:  How will this affect line numbers, character indices for producing output
        */

        // The tests below should not check text between quotes, comments, and after comments.
        testKeywordSpacing(lineNumber, lineText);

    }

    private void characterCountPerLine(int lineNumber, String lineText) {
        if (lineText.length() > 80) {
            createError(lineNumber, "Line over 80 characters.");
        }
    }

    private void testKeywordSpacing(int lineNumber, String lineText) {
        // Ignore characters in between '', "", and /* */, and after //
        // look for while
        // If next character is not a space, produce while error
    }

    private void createError(int lineNumber, String error) {
        String[] tempArray = new String[2];
        tempArray[0] = String.valueOf(lineNumber);
        tempArray[1] = error;
        singleLineErrors.add(tempArray);
    }

    public void outputErrors() {
        for (String[] tempArray :
                singleLineErrors) {
            System.out.println("Line " + tempArray[0] + " " + tempArray[1]);
        }
    }
}
