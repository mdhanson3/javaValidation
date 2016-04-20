package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Created by student on 2/10/16.
 */
public class SingleLineValidator {
    private FileInformation fileInformation;
    private List<String> fileContents;
    private List<KeywordInstance> keywords;
    private List<FunctionBounds> functionBoundsList;
    private FunctionBounds classBounds;
    private List<SingleLineError> singleLineErrors;     //Holds each single-line error found

    SingleLineValidator(FileInformation information) {
        singleLineErrors = new ArrayList<>();
        fileInformation = information;

        keywords = fileInformation.getLineInformation();
        fileContents = fileInformation.getFileContents();
        classBounds = fileInformation.getClassAndFunctionBoundsFinder().getClassBounds();
        functionBoundsList = fileInformation.getClassAndFunctionBoundsFinder().getFunctionBoundsList();
    }

    public void runSingleLineValidation() {
        // Check every line for these errors
        checkEachLine();

        // Check each occurrence of found keywords for specific errors
        // Test that each keyword is followed by a single space
        // Test that each constant name is all uppercase and underscores
        // Test that each variable name starts with a lowercase and is all letters
        checkLineInformation();

        // Test that each function opening line open paren is not preceded by a space
        verifyClassSyntax();

        // Test that each function name is starts with a lowercase and is all letters
        // Test that each class name starts with Uppercase and is all letters
        checkFunctions();
    }

    /**
     * This method runs a switch on the entire line information list and runs the appropriate validation method on
     * each one.
     */
    private void checkLineInformation() {
        for(KeywordInstance key : keywords) {
            switch(key.getKeyword()) {
                case "public" :
                    createErrorWithKeyword(key, "Public variable found.", "public", "public");
                    break;

                case "constant" :
                    checkConstantSyntax(key);
                    break;

                case "variable" :
                    checkVariableSyntax(key);
                    break;

                default :
                    checkKeywordSpacing(key);

            }
        }
    }

    private void processFunction(FunctionBounds bounds) {
        //get line text
        String lineText = fileContents.get(bounds.getOpeningLine() - 1);

        //get bounds of function name
        int[] functionNameBounds = getFunctionNameBounds(lineText);

        // isolate function name and check space
        String functionName = getFunctionName(lineText, functionNameBounds);

        // Check naming convention
        checkFunctionSyntax(functionName, functionNameBounds, bounds.getOpeningLine());

        // Check for space after
        checkFunctionSpacing(lineText, functionNameBounds, bounds.getOpeningLine());
    }

    private void checkFunctionSpacing(String lineText, int[] functionNameBounds, int lineNumber) {
        if (lineText.length() >= functionNameBounds[1]) {
            if(lineText.charAt(functionNameBounds[1]) != '(') {
                System.out.println("space between stuff: " + lineNumber);
                createErrorWithSpecifiedIndices(lineNumber, "Space between function name and parameters.", "functionSpace", functionNameBounds[0], functionNameBounds[1]);
            }
        }
    }

    private void checkFunctionSyntax(String functionName, int[] functionNameBounds, int lineNumber) {
        if (!Pattern.matches("^[a-z]+[a-zA-Z]*", functionName)) {
            System.out.println("function name sucks: " + functionName);
            createErrorWithSpecifiedIndices(lineNumber, "Function name does not match standards.", "functionName", functionNameBounds[0], functionNameBounds[1]);
        }
    }

    private int[] getFunctionNameBounds(String lineText) {
        int parenIndex = lineText.indexOf('(');
        int closingIndex = nextCharacterThatIsNotASpace(lineText, parenIndex) + 1;
        int openingIndex = lineText.lastIndexOf(' ', closingIndex - 1) + 1;

        return new int[]{openingIndex, closingIndex};
    }
    private String getFunctionName(String lineText, int[] functionNameBounds) {
        String functionName = lineText.substring(functionNameBounds[0], functionNameBounds[1]);
        return functionName;
    }

    private int nextCharacterThatIsNotASpace(String string, int startIndex) {
        for(int characterIndex = startIndex - 1; characterIndex >= 0; characterIndex --) {
            char currentChar = string.charAt(characterIndex);
            if(currentChar != ' ') {
                return characterIndex;
            }
        }
        return -1;
    }

    private void checkFunctions() {
        for(FunctionBounds bounds : functionBoundsList) {
            processFunction(bounds);
        }
    }
    private void verifyClassSyntax() {
        int[] openingAndClosingIndices = getClassNameIndeces();
        int lineNumber = classBounds.getOpeningLine();

        // isolate class name
        String className = getClassName(openingAndClosingIndices[0], openingAndClosingIndices[1]);

        // process class name
        if (!checkClassSyntax(className)){
            createErrorWithSpecifiedIndices(lineNumber, "Class name does not follow conventions.", "classError", openingAndClosingIndices[0], openingAndClosingIndices[1]);
        }
    }

    private boolean checkClassSyntax(String className) {
        boolean matchesPattern = true;
        if (!Pattern.matches("^[A-Z]+[a-zA-Z]*", className)) {
            System.out.println("class name does not start with capitol letter");
            matchesPattern = false;
        }
        return matchesPattern;
    }

    private String getClassName(int openingIndex, int closingIndex) {

        String lineText = fileContents.get(classBounds.getOpeningLine() - 1);

        String className = lineText.substring(openingIndex, closingIndex);

        return className;
    }

    private int[] getClassNameIndeces() {
        String lineText = fileContents.get(classBounds.getOpeningLine() - 1);
        int closingIndex;
        int openingIndex = lineText.indexOf(" class") + 7;  //First letter of the class name
        int nextSpace = lineText.indexOf(' ', openingIndex);
        int nextBracket = lineText.indexOf('{', openingIndex);

        // If you find a space and it is closer than the bracket, use it as the closing index. else use the bracket index.
        if(nextSpace == -1 ) {
            closingIndex = nextBracket ;
        } else if (nextSpace >= nextBracket){
            closingIndex = nextBracket;
        } else {
            closingIndex = nextSpace;
        }

        return new int[]{openingIndex,closingIndex};
    }

    private void checkKeywordSpacing(KeywordInstance key) {
        // Get string from linenumber
        String lineText = fileContents.get(key.getLineNumber() - 1);
        String keyword = key.getKeyword();

        // Get index of keyword
        int openingIndex = lineText.indexOf(keyword);
        int closingKeywordIndex = openingIndex + keyword.length();

        // Make sure there is a character after the keyword to check (no index out of bounds errors, please)
        // If the keyword is the last thing in the line, just skip it.  Bleh.
        if(closingKeywordIndex >= (lineText.length() )) {
        }

        // Check that the character immediately next to the key word is not a space
        else if(lineText.charAt(closingKeywordIndex ) != ' ') {

            // If it is not a space, get the index of the next space after the keyword start index, set this as closing index
            int closingIndex = lineText.indexOf(" ", closingKeywordIndex);

            // If no space, stringlength - 1 is the closing index
            if (closingIndex == -1) {
                closingIndex = lineText.length() - 1;
            }
            // Create error with opening underline keyword start, and calculated ending index
            createErrorWithSpecifiedIndices(key.getLineNumber(), "Missing space after keyword" + keyword, keyword, openingIndex, closingIndex );
        }
    }

    private boolean constantHasCorrectSyntax(String constantName) {
        boolean correctSyntax = Pattern.matches("^[A-Z_]*", constantName);
        return correctSyntax;
    }

    private boolean variableHasCorrectSyntax(String variableName) {
        boolean correctSyntax = false;
        if (Pattern.matches("^[a-z]+[a-zA-Z]*", variableName)) {
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
        String variableName = getVariableNameByKey(key);
        if (!variableHasCorrectSyntax(variableName)) {
            createErrorWithKeyword(key, "Variable contains illegal characters.", "variable", variableName);
        }
    }
    private void checkConstantSyntax(KeywordInstance key) {
        String constantName = getVariableNameByKey(key);
        if (!constantHasCorrectSyntax(constantName)) {
            createErrorWithKeyword(key, "Constant contains illegal characters.", "constant", constantName);
        }

    }
    private void createErrorWithKeyword(KeywordInstance key, String errorMessage, String errorType, String underlineString) {
        int[] indices = getUnderlineIndicesBySubstring(key.getLineNumber(), underlineString);
        singleLineErrors.add(new SingleLineError(key.getLineNumber(), indices[0], indices[1], errorMessage, errorType));
    }

    private void createErrorWithSpecifiedIndices(int lineNumber , String errorMessage, String errorType, int openingIndex, int closingIndex) {
        singleLineErrors.add(new SingleLineError(lineNumber, openingIndex, closingIndex, errorMessage, errorType));
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
