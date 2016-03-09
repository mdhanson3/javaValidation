package com.hanson.javaValidation;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.*;

/**
 * Looks for various details within the given file and stores the information in a list of arrays, which contains a
 * line number and what was found.
 *
 * @author Mitchell Hanson
 * Created by student on 3/1/16.
 */
public class FileInformation {
    private final int JAVADOC_CODE = -1;
    private final int MULTI_LINE_COMMENT_CODE = 0;
    private final String[] keywords = {" for", " if", " else", " else if", " while", " do", " try", " catch", " do while"};
    //private final keywordDecode keywordDecoder = new keywordDecode();
    private FileParser fileParser;
    private List<int[]> lineInformation;
    private List<Integer> javadocComments;

    //************************************/
    // These variables are for finding class bounds.  Should be refactored into its own class most likely.
    //************************************
    private Boolean foundClassBounds = false;
    private Boolean foundOpeningClassLine = false;
    private int openBrackets;
    private int openingClassLine = 0;
    private int closingClassLine = 0;

    // These variables are for finding the function bounds
    private Boolean functionIsOpen = false;
    private int openFunctionBrackets;
    private List<int[]> functionBounds = new ArrayList<>();
    private int openingFunctionLine = 0;
    private int closingFunctionLine = 0;
     /************************************************/

    //private boolean previousLineJavadocComment = false;
    private boolean javadocCommentOpen = false;
    private boolean multiLineCommentOpen = false;

    FileInformation(FileParser parser) {
        lineInformation = new ArrayList<>();
        javadocComments = new ArrayList<>();
        fileParser = parser;
    }

    public List getLineINformation() {
        return lineInformation;
    }

    /**
     * Stores information about the file in a list.
     */
    public void runFileInformation() {
        //previousLineJavadocComment = false;
        javadocCommentOpen = false;
        multiLineCommentOpen = false;

        List<String> fileContents = fileParser.getFileContents();
        System.out.println(fileContents.size());
        for (int index = 0; index < fileContents.size(); index ++) {
            runInfoGatherers(index + 1, fileContents.get(index));
        }
        System.out.println("Class Opening:  " + openingClassLine + ". Class closing: " + closingClassLine);
        findFunctionBounds();
        printFunctionBounds();
    }

    private void runInfoGatherers(int lineNumber, String lineText) {
        checkJavadocComment(lineNumber, lineText);
        findClassBounds(lineNumber, lineText);
        for (String keyword : keywords) {
            checkKeyword(lineNumber, lineText, keyword);
        }
    }

    private void checkJavadocComment(int lineNumber, String lineText) {
        if (javadocCommentOpen) {
            if (lineText.contains("*/")) {
                javadocCommentOpen = false;
            }
            addLineInformation(lineNumber, JAVADOC_CODE);
        }
        else if (lineText.contains("/**")) {
            javadocCommentOpen = true;
            addLineInformation(lineNumber, JAVADOC_CODE);
        }

        else {
            checkMultiLineComment(lineNumber, lineText);
        }
    }

    private void checkMultiLineComment(int lineNumber, String lineText) {
        if (multiLineCommentOpen) {
            if (lineText.contains("*/")) {
                multiLineCommentOpen = false;
            }
            addLineInformation(lineNumber, MULTI_LINE_COMMENT_CODE);
        }
        else if (lineText.contains("/*")) {
            multiLineCommentOpen = true;
            addLineInformation(lineNumber, MULTI_LINE_COMMENT_CODE);
        }
    }

    private void checkKeyword(int lineNumber, String lineText, String keyword) {
        if (lineText.contains(keyword)) {
            System.out.println("found code: " + keywordDecode.decodeKeyword(keyword) + ". On line " + lineNumber + ".  Keyword: " + keyword);
            addLineInformation(lineNumber, keywordDecode.decodeKeyword(keyword));
        }
    }

    private void addLineInformation(int lineNumber, int lineCode) {
        int[] tempArray = {lineNumber, lineCode};
        lineInformation.add(tempArray);
    }

    public void debugTerminalOutput () {
        for (int i = 0; i < lineInformation.size(); i++) {
            System.out.println("Line type: " + lineInformation.get(i)[1] + ". On line: " + lineInformation.get(i)[0]);
        }
    }

    public void findClassBounds(int lineNumber, String lineText) {
        if (!foundClassBounds) {
            if (foundOpeningClassLine) {
                for (char c : lineText.toCharArray()) {
                    if (c == '{') {
                        openBrackets ++;
                    } else if (c == '}') {
                        openBrackets --;
                    }
                }
                /*  Code does not account for multiple brackets on one line
                if (lineText.contains("{")) {
                    openBrackets++;
                } else if (lineText.contains("}")) {
                    openBrackets--;
                }
                */
                if (openBrackets == 0) {
                    closingClassLine = lineNumber;
                    foundClassBounds = true;
                }
            } else {
                if (lineText.contains("{")) {
                    foundOpeningClassLine = true;
                    openBrackets = 1;
                    openingClassLine = lineNumber;
                }
            }
        }
    }

    private void findFunctionBounds() {
        // Check lines within class bounds for function bounds
        // It seems like the index should start at openingClassLine + 1, but line numbers are one greater than the index of the array
        // so we must actually remove one from the closing line
        List<String> fileContents = fileParser.getFileContents();
        String lineText = "";
        for (int index = openingClassLine; index < closingClassLine - 1; index ++) {
            lineText = fileContents.get(index);
            // If there is an open function, search the text until the bracket count is zero.  Record the function closing line number
            // and add the bounds to the functionBounds list. Reset the appropriate booleans.
            if (functionIsOpen) {
                for (char c : lineText.toCharArray()) {
                    if (c == '{') {
                        openFunctionBrackets ++;
                    } else if (c == '}') {
                        openFunctionBrackets --;
                    }
                }
                if (openFunctionBrackets == 0) {
                    closingFunctionLine = index + 1;
                    functionIsOpen = false;
                    int[] tempArray = {openingFunctionLine, closingFunctionLine};
                    functionBounds.add(tempArray);
                }
            }
            // If there is not an open function, search for an opening bracket, record the function opening, and set the appropriate booleans.
            else {
                if (lineText.contains("{")) {
                    functionIsOpen = true;
                    openingFunctionLine = index + 1;
                    openFunctionBrackets = 1;

                }
            }
        }
    }

    private void printFunctionBounds() {
        for(int[] functionBoundArray : functionBounds) {
            System.out.println("Opening function line: " + functionBoundArray[0] + ". Closing function line: " + functionBoundArray[1]);
        }
    }


}
