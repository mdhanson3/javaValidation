package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 3/30/16.
 * @author Mitchell Hanson
 */
public class QuoteAndCommentReplacer {
    private int quoteOpenIndex;
    private int multiLineOpenCommentIndex;
    private int multiLineOpenLine;
    private boolean openQuote = false;
    private boolean openMultiLineComment = false;
    private boolean openJavadocComment = false;
    private boolean previousCharacterIsEscape = false;
    private List<SectionToRemove> sectionsToRemove = new ArrayList<>(); //List of sections to remove.
    private List<String> fileContents;  // Contents of file to find and replace text
    private List<String> originalFileContents;

    QuoteAndCommentReplacer(List<String> contents) {
        fileContents = contents;
        //TODO: Initialize objects here (remove initialization from above)
    }

    public List<String> getFileContents(){
        return fileContents;
    }
    public void replaceQuotesAndComments() {

        findTextToRemoveBounds();
        replaceQuotedText();


    }

    private void findTextToRemoveBounds() {
        // Find text to replace on each line of the input file
        for (int index = 0; index < fileContents.size(); index ++) {
            removeQuotedAndCommentedText(index + 1, fileContents.get(index));
        }
    }

    private void removeQuotedAndCommentedText(int lineNumber, String lineText) {

        for (int index = 0; index < lineText.length(); index ++) {

            if (openQuote) {
                checkForClosingQuote(lineNumber, lineText, index);
            } else if (openJavadocComment) {
                index = checkForClosingJavadoc(lineNumber, lineText, index);
            } else if (openMultiLineComment) {
                index = checkForClosingMultiLine(lineNumber, lineText, index);
            } else {

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
                            System.out.println("FOUND SINGLE ON LINE: " + lineNumber);
                            System.out.println("Line Number: " + lineNumber + " start Index: " + (index + 2) + " Line Number: " + lineNumber + ". Line Length: " + (lineText.length() - 1));

                            SectionToRemove tmpSectionToRemove = new SectionToRemove(lineNumber, index + 1, lineNumber, lineText.length());
                            sectionsToRemove.add(tmpSectionToRemove);

                            return;
                        }
                    }

                    // Check only for multi-line comment if there is only one character following the /
                    else if (index < lineText.length() - 1) {
                        if (lineText.charAt(index + 1) == '*') {
                            System.out.println("FOUND MULTI-LINE COMMENT ON LINE: " + lineNumber);
                            openMultiLineComment = true;
                            multiLineOpenCommentIndex = index + 1;
                            multiLineOpenLine = lineNumber;
                            index += 1;
                        }
                    }
                }
            }
        }
    }

    private int checkForClosingMultiLine(int lineNumber, String lineText, int index) {
        // Check for first character of closing comment
        if (lineText.charAt(index) == '*' && (index < lineText.length() - 1)) {
            // If first character is found, check it isn't the last character in the string, and that the next
            // character is the second character of the closing comment
            if (lineText.charAt(index + 1) == '/') {
                System.out.println("Found closing multi-line comment");

                SectionToRemove tmpSectionToRemove = new SectionToRemove(multiLineOpenLine, multiLineOpenCommentIndex, lineNumber, index);
                sectionsToRemove.add(tmpSectionToRemove);

                openMultiLineComment = false;
                index ++;
            }
        }
        return index;
    }

    private int checkForClosingJavadoc(int lineNumber, String lineText, int index) {
        // Check for first character of closing comment
        if (lineText.charAt(index) == '*' && (index < lineText.length() - 1)) {
            // If first character is found, check it isn't the last character in the string, and that the next
            // character is the second character of the closing comment
            if (lineText.charAt(index + 1) == '/') {
                System.out.println("FOUND CLOSE JAVADOC - Opening line: " + multiLineOpenLine + ". Closing line: " + lineNumber + "OpeningIndex "
                        + multiLineOpenCommentIndex + ". Closing index: " + index);

                SectionToRemove tmpSectionToRemove = new SectionToRemove(multiLineOpenLine, multiLineOpenCommentIndex, lineNumber, index);
                sectionsToRemove.add(tmpSectionToRemove);

                openJavadocComment = false;
                index ++;
            }
        }
        return index;
    }

    /**
     * Search for a quotation mark not preceded by an escape character.  If found add bounds of quoted text to
     * an int array and add that array to the charactersToRemoveList and set openQuote to false;
     */
    private void checkForClosingQuote(int lineNumber, String lineText, int index) {
        if (lineText.charAt(index) == '\"') {
            if (previousCharacterIsEscape) {
                previousCharacterIsEscape = false;
            } else {
                SectionToRemove tmpSectionToRemove = new SectionToRemove(lineNumber, quoteOpenIndex, lineNumber, index);
                sectionsToRemove.add(tmpSectionToRemove);

                // TODO: Remove this line for when debug complete
                //quotedTextArray.add(lineText.substring(quoteOpenIndex + 1, index));
                openQuote = false;
            }

        } else if (lineText.charAt(index) == '\\') {
            previousCharacterIsEscape = true;
        }
    }

    private void replaceQuotedText() {
        for(int index = 0; index < sectionsToRemove.size(); index ++) {

            SectionToRemove tmpSection = sectionsToRemove.get(index);

            int openingLine = tmpSection.getOpeningLine();
            int openingIndex = tmpSection.getOpeningIndex();
            int closingLine = tmpSection.getClosingLine();
            int closingIndex = tmpSection.getClosingIndex();


            // If the text to replace spans only one line, use this simple code.  Else, remove text from each line.
            if (openingLine == closingLine) {
                if (openingIndex == closingIndex + 1) {

                }
                else if (openingIndex < closingIndex) {

                    String lineText = fileContents.get(openingLine - 1);
                    String replacement = buildDots(openingIndex, closingIndex - 1);
                    String newText;
                    int startIndex = openingIndex + 1;
                    int endIndex = closingIndex;

                    if (endIndex >= lineText.length() - 1) {
                        newText = lineText.substring(0, startIndex ) + replacement;
                    } else {
                        newText = lineText.substring(0, startIndex) + replacement + lineText.substring(endIndex, lineText.length());
                    }

                    fileContents.set(openingLine - 1, newText);
                }

            } else {
                int startIndex = openingIndex + 1;

                // Replace all text between the first line number first index and second line number second index
                for (int currentLineNumber = openingLine; currentLineNumber <= closingLine; currentLineNumber ++) {

                    // If this is the last line of text to replace, replace from index 0 to the closing index of the comment
                    if (currentLineNumber == closingLine) {
                        if (closingIndex < 1) {

                        } else {
                            String replacement = buildDots(0, closingIndex);
                            String lineText = fileContents.get(currentLineNumber - 1);

                            String newText = replacement + lineText.substring(closingIndex);

                            fileContents.set(closingLine - 1, newText);

                            System.out.println("Line Number: " + currentLineNumber + ". New text: " + newText);
                        }

                    }

                    // If this is the first line of text to replace, replace from the opening index to the end of the string
                    else if (currentLineNumber == openingLine) {
                        String lineText = fileContents.get(currentLineNumber - 1);
                        String replacement = buildDots(startIndex, lineText.length() - 1);

                        String newText = lineText.substring(0, startIndex) + replacement;

                        fileContents.set(openingLine -1, newText);
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
}
