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
    private QuoteAndCommentReplacer quoteAndTextReplacer;
    private ClassAndFunctionBoundsFinder classAndFunctionBoundsFinder;

    private List<String> keywords;
    private FileParser fileParser;

    private List<int[]> lineInformation;
    private List<String> fileContents;
    private List<String> sanitizedFileContents;

    FileInformation(FileParser parser) {
        lineInformation = new ArrayList<>();
        keywords = new ArrayList<>();

        //**********************************************************************************************
        // TODO: Figure out pass by reference issues, how to maintain original file contents?
        sanitizedFileContents = new ArrayList<>();
        fileContents = parser.getFileContents();
        fileParser = parser;
        quoteAndTextReplacer = new QuoteAndCommentReplacer(fileParser.getFileContents());
        //**********************************************************************************************

        classAndFunctionBoundsFinder = new ClassAndFunctionBoundsFinder();
    }

    public List getLineINformation() {
        return lineInformation;
    }
    public List<String> getFileContents() {return sanitizedFileContents;};
    public List<String> getOriginalFileContents() {return fileContents;};

    /**
     * Stores information about the file in a list.
     */
    public void runFileInformation() {
        quoteAndTextReplacer.replaceQuotesAndComments();
        sanitizedFileContents = quoteAndTextReplacer.getFileContents();
        classAndFunctionBoundsFinder.findClassAndFunctionBounds(sanitizedFileContents);

        generateKeywordList();

        System.out.println(sanitizedFileContents.size());
        for (int index = 0; index < sanitizedFileContents.size(); index ++) {
            runInfoGatherers(index + 1, sanitizedFileContents.get(index));
        }
    }

    private void runInfoGatherers(int lineNumber, String lineText) {
        for (String keyword : keywords) {
            checkKeyword(lineNumber, lineText, keyword);
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

    // Enum of keywords to search for
    private enum Keyword {
        FOR(" for"),
        IF(" if"),
        ELSE(" else"),
        ELSE_IF(" else if"),
        WHILE(" while"),
        DO(" do"),
        TRY(" try"),
        CATCH(" catch"),
        DO_WHILE(" do while");

        private String value;

        Keyword(String value) {
            this.value = value;
        }

        public String getKeywordString() {
            return value;
        }

    }

    private void generateKeywordList() {
        for (Keyword key : Keyword.values()) {
            keywords.add(key.getKeywordString());
        }
    }

}
