package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by student on 2/10/16.
 */
public class SingleLineValidator {
    private FileInformation fileInformation;
    private List<String> fileContents;
    private List<KeywordInstance> keywords;
    private List<SingleLineError> singleLineErrors;

    SingleLineValidator(FileInformation information) {
        singleLineErrors = new ArrayList<>();
        fileInformation = information;

        keywords = fileInformation.getLineInformation();
        fileContents = fileInformation.getFileContents();
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

    /**
     * This method runs a switch on the entire line information list and runs the appropriate validation method on
     * each one.
     */
    private void checkLineInformation() {
        for(KeywordInstance key : keywords) {

            switch(key.getKeyword()) {
                case "public" :
                    createErrorWithKeywordIndices(key, "Public variable found.", "public", "public");
                    break;

                case "constant" :
                    checkConstantSyntax(key);
                    break;

                case "variable" :
                    checkVariableSyntax(key);
                    break;

                default :
                    System.out.println("SWITCH found keyword: " + key.getLineNumber() + ". Keyword: " + key.getKeyword());

            }
        }
    }

    private boolean constantHasCorrectSyntax(String constantName) {
        boolean correctSyntax = Pattern.matches("^[A-Z_]*", constantName);
        return correctSyntax;
    }

    private boolean variableHasCorrectSyntax(String variableName) {
        boolean correctSyntax = false;
        if (Pattern.matches("^[a-z]+[a-zA-Z]*", variableName)) {
            System.out.println("-------matching regex-------");
            correctSyntax = true;
        }
        return correctSyntax;
    }
    private String getVariableNameByKey(KeywordInstance key) {
        // Get the substring from index 0 to the first index of "="
        String stringBeforeEqualsSign = fileContents.get(key.getLineNumber() - 1).substring(0, fileContents.get(key.getLineNumber() - 1).indexOf("="));

        // Split the string with any amount of spaces as delimiter
        String[] splitString = stringBeforeEqualsSign.split("\\s+");

        //Return the last string in the array (should be the last word before the "=", or the constant name
        return splitString[splitString.length - 1];
    }

    private void checkVariableSyntax(KeywordInstance key) {
        System.out.println("IN CHECK VARIABLE SYNTAX");
        String variableName = getVariableNameByKey(key);
        if (!variableHasCorrectSyntax(variableName)) {
            createErrorWithKeywordIndices(key, "Variable contains illegal characters.", "variable", variableName);
        }
    }
    private void checkConstantSyntax(KeywordInstance key) {
        String constantName = getVariableNameByKey(key);
        if (!constantHasCorrectSyntax(constantName)) {
            createErrorWithKeywordIndices(key, "Constant contains illegal characters.", "constant", constantName);
        }

    }
    private void createErrorWithKeywordIndices(KeywordInstance key, String errorMessage, String errorType, String underlineString) {
        int[] indices = getUnderlineIndicesBySubstring(key.getLineNumber(), underlineString);
        singleLineErrors.add(new SingleLineError(key.getLineNumber(), indices[0], indices[1] + 5, errorMessage, errorType));
    }

    private void checkEachLine() {
        for(int lineNumber = 0; lineNumber < fileContents.size(); lineNumber++) {
            checkLineLength(lineNumber, fileContents.get(lineNumber));
        }
    }

    private void checkLineLength(int lineNumber, String lineText) {
        if (lineText.length() > 80) {
            singleLineErrors.add(new SingleLineError(lineNumber + 1, 0, lineText.length(), "Line longer than 80 characters", "LineLength"));
        }
    }

    public void outputErrors() {
        for (SingleLineError error : singleLineErrors) {
            System.out.println("Line " + error.getLineNumber() + " " + error.getErrorMessage());
        }
    }

    // Find the start and end of the specified keyword in the line
    private int[] getUnderlineIndicesBySubstring(int lineNumber, String substring) {
        int openingIndex = fileContents.get(lineNumber - 1).indexOf("public");
        int closingIndex = openingIndex + substring.length();

        int[] indices = new int[]{openingIndex, closingIndex};

        return indices;
    }
}
