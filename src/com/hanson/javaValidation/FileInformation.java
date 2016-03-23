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
    private final int JAVADOC_CODE = -1;
    private final int MULTI_LINE_COMMENT_CODE = 0;
    private List<String> keywords;
    //private final keywordDecode keywordDecoder = new keywordDecode();
    private FileParser fileParser;
    private List<int[]> lineInformation;
    private List<Integer> javadocComments;

    private List<String> fileContents;

    //private boolean previousLineJavadocComment = false;
    private boolean javadocCommentOpen = false;
    private boolean multiLineCommentOpen = false;

    //******************************************************************************************************/
    // These variables are for finding class bounds.  Should be refactored into its own class most likely.
    //******************************************************************************************************
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
    //******************************************************************************************************
    //******************************************************************************************************

    //******************************************************************************************************/
    // These variables are for stripping comment and quoted text.  Should be refactored into its own class most likely.
    //******************************************************************************************************
    private Boolean openQuote = false;
    private int quoteOpenIndex;
    private int multiLineOpenCommentIndex;
    private int multiLineOpenLine;
    private List<String> quotedTextArray = new ArrayList<>();
    private Boolean openMultiLineComment = false;
    private Boolean openJavadocComment = false;
    private Boolean afterSingleLineComment = false;
    private Boolean previousCharacterIsEscape = false;
    private List<int[]> charactersToRemove = new ArrayList<>();  // Three values, line number, opening quote, closing quote
    private List<Integer> linesToReplace = new ArrayList<>();    // Replace the entire line for comments?


    //******************************************************************************************************
    //******************************************************************************************************


    FileInformation(FileParser parser) {
        lineInformation = new ArrayList<>();
        javadocComments = new ArrayList<>();
        keywords = new ArrayList<>();
        fileParser = parser;
    }

    public List getLineINformation() {
        return lineInformation;
    }
    public List getFileContents() {return fileContents;};

    /**
     * Stores information about the file in a list.
     */
    public void runFileInformation() {
        //previousLineJavadocComment = false;
        javadocCommentOpen = false;
        multiLineCommentOpen = false;

        fileContents = fileParser.getFileContents();
        generateKeywordList();

        System.out.println(fileContents.size());
        for (int index = 0; index < fileContents.size(); index ++) {
            runInfoGatherers(index + 1, fileContents.get(index));
        }
        System.out.println("Class Opening:  " + openingClassLine + ". Class closing: " + closingClassLine);
        findFunctionBounds();
        replaceQuotedText();
        printFunctionBounds();
        printTextToRemove();
    }

    private void runInfoGatherers(int lineNumber, String lineText) {
        checkJavadocComment(lineNumber, lineText);
        findClassBounds(lineNumber, lineText);
        removeQuotedAndCommentedText(lineNumber, lineText);
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

    //********************Probably new classes*************************************
    //*****************************************************************************
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

    private void removeQuotedAndCommentedText(int lineNumber, String lineText) {
        /*
        private Boolean openQuote = false;
        private int quoteOpenIndex;
        private int multiLineOpenComment;
        private int multiLineOpenLine;
        private Boolean openMultiLineComment = false;
        private Boolean openJavadocComment = false;
        private Boolean afterSingleLineComment = false;
        private Boolean previousCharacterIsEscape = false;
        private List<int[]> charactersToRemove = new ArrayList<>();  // Three values, line number, opening quote, closing quote
        private List<Integer> linesToReplace = new ArrayList<>();    // Replace the entire line for comments?
        */

        /*
         *  This loop looks at all characters in the passed string.  It will create int arrays and add them to the
         *  charactersToRemove for all text between quotes, multi-line comments, and javadoc comments, as well as any
         *  text after single line comments.  It tracks whether it is processing a text within a quote, comment, ..., with
         *  boolean variables and ignore certain characters if it's within various areas of text (for example:  quotations
         *  are not logged to be removed if found inside of a multi-line comment).
         */
        for (int index = 0; index < lineText.length(); index ++) {

            /*
              * Search for a quotation mark not preceded by an escape character.  If found add bounds of quoted text to
              * an int array and add that array to the charactersToRemoveList and set openQuote to false;
             */
            if (openQuote) {
                if (lineText.charAt(index) == '\"') {
                    if (previousCharacterIsEscape) {
                        previousCharacterIsEscape = false;
                    } else {
                        int[] tempArray = {lineNumber, quoteOpenIndex, lineNumber, index};
                        charactersToRemove.add(tempArray);
                        quotedTextArray.add(lineText.substring(quoteOpenIndex + 1, index));
                        openQuote = false;
                    }

                } else if (lineText.charAt(index) == '\\') {
                    previousCharacterIsEscape = true;
                }
            /*
             * Search for a closing javadoc symbol.
             */
            }  else if (openJavadocComment) {
                // Check for first character of closing comment
                if (lineText.charAt(index) == '*' && (index < lineText.length() - 1)) {
                    // If first character is found, check it isn't the last character in the string, and that the next
                    // character is the second character of the closing comment
                    if (lineText.charAt(index + 1) == '/') {
                        System.out.println("Found closing javadoc");
                        int[] tempArray = {multiLineOpenLine, multiLineOpenCommentIndex, lineNumber, index};
                        charactersToRemove.add(tempArray);
                        openJavadocComment = false;
                    }
                }
            }
            else if (openMultiLineComment) {
                // Check for first character of closing comment
                if (lineText.charAt(index) == '*' && (index < lineText.length() - 1)) {
                    // If first character is found, check it isn't the last character in the string, and that the next
                    // character is the second character of the closing comment
                    if (lineText.charAt(index + 1) == '/') {
                        System.out.println("Found closing multi-line comment");
                        int[] tempArray = {multiLineOpenLine, multiLineOpenCommentIndex, lineNumber, index};
                        charactersToRemove.add(tempArray);
                        openMultiLineComment = false;
                    }
                }
            }
            else {
                // Set index and booleans if quote found
                if (lineText.charAt(index) == '\"') {
                    openQuote = true;
                    quoteOpenIndex = index;
                }

                // Look for multi-line comments and javadocs, if there are at least two characters following the /
                else if (lineText.charAt(index) == '/' ) {
                    if (index < lineText.length() - 2) {

                        // If followed by two * then it's a javadoc
                        if (lineText.charAt(index + 1) == '*' && lineText.charAt(index + 2) == '*') {
                            System.out.println("FOUND JAVADOC COMMENT ONE LINE: " + lineNumber);
                            openJavadocComment = true;
                            multiLineOpenCommentIndex = index + 2;
                            multiLineOpenLine = lineNumber;
                            index += 2;
                        }

                        // If followed by one * then it's a multi-line comment
                        else if (lineText.charAt(index + 1) == '*') {
                            System.out.println("FOUND MULTI-LINE COMMENT ONE LINE: " + index);
                            openMultiLineComment = true;
                            multiLineOpenCommentIndex = index + 1;
                            multiLineOpenLine = lineNumber;
                            index += 1;
                        } else if (lineText.charAt(index + 1) == '/') {
                            System.out.println("FOUND SINGLE LINE COMMENT");
                            int[] tempArray = {lineNumber, index + 2, lineNumber, lineText.length() - 1};
                            charactersToRemove.add(tempArray);
                            return;
                        }
                    }

                    // Check only for multi-line comment if there is only one character following the /
                    else if (index < lineText.length() - 1) {
                        if (lineText.charAt(index + 1) == '*') {
                            System.out.println("FOUND MULTI-LINE COMMENT ONE LINE: " + index);
                            openMultiLineComment = true;
                            multiLineOpenCommentIndex = index + 1;
                            multiLineOpenLine = lineNumber;
                            index += 1;
                        } else if (lineText.charAt(index + 1) == '/') {
                            System.out.println("FOUND SINGLE LINE COMMENT");
                            int[] tempArray = {lineNumber, index, lineNumber, lineText.length() - 1};
                            charactersToRemove.add(tempArray);
                            return;
                        }


                    }
                }
            }
        }
    }
    private void replaceQuotedText() {
        for(int index = 0; index < charactersToRemove.size(); index ++) {

            int[] tmpArray = charactersToRemove.get(index);

            // If the text to replace spans only one line, use this simple code.  Else, remove text from each line.
            if (tmpArray[0] == tmpArray[2]) {
                String replacement = buildDots(tmpArray[1], tmpArray[3]);

                String lineText = fileContents.get(tmpArray[0] - 1);
                String newText = lineText.substring(0, tmpArray[1]) + replacement + lineText.substring(tmpArray[3] + 1);

                fileContents.set(tmpArray[0] - 1, newText);

                System.out.println(newText);
            } else {

                // Replace all text between the first line number first index and second line number second index
                for (int currentLineNumber = tmpArray[0]; currentLineNumber <= tmpArray[2]; currentLineNumber ++) {

                    // If this is the last line of text to replace, replace from index 0 to the closing index of the comment
                    if (currentLineNumber == tmpArray[2]) {
                        String replacement = buildDots(0, tmpArray[3]);
                        String lineText = fileContents.get(currentLineNumber - 1);

                        String newText = replacement + lineText.substring(tmpArray[3] );

                        fileContents.set(tmpArray[2] - 1, newText);

                        System.out.println("Line Number: " + currentLineNumber + ". New text: " + newText);
                    }

                    // If this is the first line of text to replace, replace from the opening index to the end of the string
                    else if (currentLineNumber == tmpArray[0]) {
                        String lineText = fileContents.get(currentLineNumber - 1);
                        String replacement = buildDots(tmpArray[1], lineText.length() - 1);

                        String newText = lineText.substring(0, tmpArray[1]) + replacement;

                        fileContents.set(tmpArray[0] -1, newText);
                        System.out.println("Line Number: " + currentLineNumber + ". New text: " + newText);
                    }

                    // This is a middle line that should entirely be replaced because it is entirely encompassed by
                    // a multi-line comment
                    else {
                        String lineText = fileContents.get(currentLineNumber - 1 );
                        String replacement = buildDots(0, lineText.length() - 1);

                        fileContents.set(currentLineNumber - 1, replacement);
                        System.out.println("Line Number: " + currentLineNumber + ". New text: " + replacement);
                    }
                }
            }


        }
    }

    private String buildDots(int startIndex, int endIndex) {
        String dots = "";
        for (int i = 0; i < (endIndex - startIndex); i ++) {
            dots += ".";
        }

        return dots;
    }

    private void printTextToRemove() {
        for (int i = 0; i < charactersToRemove.size(); i++) {
            int[] tmp = charactersToRemove.get(i);
            System.out.println("Found removable text between lines " + tmp[0] + " and " + tmp[2] + ". Between index " + tmp[1] + " and " + tmp[3]);
        }
    }

    private void printFunctionBounds() {
        for(int[] functionBoundArray : functionBounds) {
            System.out.println("Opening function line: " + functionBoundArray[0] + ". Closing function line: " + functionBoundArray[1]);
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
