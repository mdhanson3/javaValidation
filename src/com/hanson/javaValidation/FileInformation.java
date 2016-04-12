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

    // private final int JAVADOC_CODE = -1;
    // private final int MULTI_LINE_COMMENT_CODE = 0;
    //private final keywordDecode keywordDecoder = new keywordDecode();
    //private List<Integer> javadocComments;

    //private boolean previousLineJavadocComment = false;
    //private boolean javadocCommentOpen = false;
    //private boolean multiLineCommentOpen = false;


    FileInformation(FileParser parser) {
        lineInformation = new ArrayList<>();
        //javadocComments = new ArrayList<>();
        keywords = new ArrayList<>();
        fileParser = parser;
        quoteAndTextReplacer = new QuoteAndCommentReplacer(parser.getFileContents());
        classAndFunctionBoundsFinder = new ClassAndFunctionBoundsFinder();
    }

    public List getLineINformation() {
        return lineInformation;
    }
    public List getFileContents() {return fileContents;};

    /**
     * Stores information about the file in a list.
     */
    public void runFileInformation() {
        quoteAndTextReplacer.replaceQuotesAndComments();
        fileContents = quoteAndTextReplacer.getFileContents();
        classAndFunctionBoundsFinder.findClassAndFunctionBounds(fileContents);

        generateKeywordList();

        System.out.println(fileContents.size());
        for (int index = 0; index < fileContents.size(); index ++) {
            runInfoGatherers(index + 1, fileContents.get(index));
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

    //********************Probably new classes*************************************
    //*****************************************************************************



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

        private Keyword(String value) {
            this.value = value;
        }

        public String getKeywordString() {
            return value;
        }

    }
    //************************************************************************


    private void generateKeywordList() {
        for (Keyword key : Keyword.values()) {
            keywords.add(key.getKeywordString());
        }
    }

}
