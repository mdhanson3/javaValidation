package com.hanson.javaValidation;

import java.util.*;

/**
 * Looks for various details within the given file and stores the information in a list of arrays, which contains a
 * line number and what was found.
 *
 * @author Mitchell Hanson
 * Created by student on 3/1/16.
 */
public class FileInformation {
    private final int JAVADOC_CODE = 1;
    private final int FOR_CODE = 2;
    private final String[] keywords = {"for", "if", "else", "else if", "while", "do"};
    private FileParser fileParser;
    private List<int[]> lineInformation;
    private List<Integer> javadocComments;

    private boolean previousLineJavadocComment = false;
    private boolean javadocCommentOpen = false;

    FileInformation(FileParser parser) {
        lineInformation = new ArrayList<>();
        javadocComments = new ArrayList<>();
        fileParser = parser;
    }

    public List getLineINformation() {
        return lineInformation;
    }

    /**
     * Stores information about its file in a list.
     */
    public void runFileInformation() {
        previousLineJavadocComment = false;
        javadocCommentOpen = false;

        List<String> fileContents = fileParser.getFileContents();
        System.out.println(fileContents.size());
        for (int index = 0; index < fileContents.size(); index ++) {
            runInfoGatherers(index + 1, fileContents.get(index));
        }

    }

    private void runInfoGatherers(int lineNumber, String lineText) {
        checkJavadocComment(lineNumber, lineText);
        checkKeyword(lineNumber, lineText, FOR_CODE, "for");
    }

    private void checkJavadocComment(int lineNumber, String lineText) {
        if (lineText.contains("/*")) {
            addLineInformation(lineNumber, JAVADOC_CODE);
        }
    }

    private void checkKeyword(int lineNumber, String lineText, int code, String keyword) {
        if (lineText.contains(keyword)) {
            System.out.println("found: " + keyword + ". On line ");
            addLineInformation(lineNumber, code);
        }
    }

    private void addLineInformation(int lineNumber, int lineCode) {
        int[] tempArray = {lineNumber, lineCode};
        lineInformation.add(tempArray);
        System.out.println(tempArray[0]);
    }

}
