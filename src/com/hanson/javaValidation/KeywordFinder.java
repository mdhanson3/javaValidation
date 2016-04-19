package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Finds instances of the keywords defined within this class.
 *
 *
 * Created on 4/19/16.
 * @author Mitchell Hanson
 */
public class KeywordFinder {

    List<KeywordInstance> keywords;

    /**
     * Searches the passed file contents for information contained in the file.  Returns a list of KeywordInstances
     * representing each item found in the file.
     *
     * @return list of keywords found in the file, including line numbers
     */
    public void generateSingleLineInformation(List<String> contents, ClassAndFunctionBoundsFinder boundsFinder) {
        //List<String> keys = new ArrayList<>();
        keywords = new ArrayList<>();
        List<String> fileContents = new ArrayList<>();
        fileContents = contents;

        // Search between opening class and first function for variables and constants
        findInstanceVariablesAndConstants(boundsFinder, fileContents);

        // Search within each function for keywords
        findKeywords(fileContents);

    }

    public List<KeywordInstance> getKeywords() {
        return keywords;
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
        CATCH(" catch");

        private String value;

        Keyword(String value) {
            this.value = value;
        }

        public String getKeywordString() {
            return value;
        }

    }

    private void findKeywords(List<String> content) {
        for(int lineNumber = 0; lineNumber < content.size(); lineNumber ++ ) {
            findKeywordsInLine(lineNumber, content.get(lineNumber));
        }
    }
    private void findKeywordsInLine(int lineNumber, String lineText) {
        if (checkKeyword(lineNumber, lineText, " else if")) {

        } else {
            for (Keyword key : Keyword.values()) {
                String keyString = key.getKeywordString();
                checkKeyword(lineNumber, lineText, keyString);
            }
        }

    }

    private boolean checkKeyword(int lineNumber, String lineText, String keyword) {
        if (lineText.contains(keyword)) {
            System.out.println("found code: " + keywordDecode.decodeKeyword(keyword) + ". On line " + lineNumber + ".  Keyword: " + keyword);
            keywords.add(new KeywordInstance(lineNumber + 1, keyword));
            return true;
        }

        return false;
    }

    private void findInstanceVariablesAndConstants(ClassAndFunctionBoundsFinder boundsFinder, List<String> contents) {
        FunctionBounds classBounds =  boundsFinder.getClassBounds();
        FunctionBounds firstFunctionBounds = boundsFinder.getFirstFunctionBounds();

        if (firstFunctionBounds != null) {
            for (int lineNumber = classBounds.getOpeningLine(); lineNumber < firstFunctionBounds.getOpeningLine() - 1; lineNumber ++) {
                // Check if variable or const
                variableOrConstantCheck(lineNumber, contents.get(lineNumber));

                // Check if there are any public instances
                publicCheck(lineNumber, contents.get(lineNumber));
            }
        }
    }

    private void variableOrConstantCheck(int lineNumber, String lineContent) {
        if (lineContent.contains(" final ")) {
            keywords.add(new KeywordInstance(lineNumber + 1, "constant"));
            System.out.println("CONSTANT FOUND: " + lineContent);
        } else if (lineContent.contains(" = ")) {
            keywords.add(new KeywordInstance(lineNumber + 1, "variable"));
            System.out.println("VARIABLE FOUND: " + lineContent);
        }
    }

    // TODO:  Will catch var names with 'public' in them.  Originally was looking for ' public' but that missed
    // any public variables that started as the first character in the line
    private void publicCheck(int lineNumber, String lineContent) {
        if (lineContent.contains("public ")) {
            keywords.add(new KeywordInstance(lineNumber + 1, "public"));
            System.out.println("PUBLIC FOUND: " + lineContent);
        }
    }
}
