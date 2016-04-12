package com.hanson.javaValidation;

/**
 * This class holds the bounds of a section of text that is intended to be replaced in a file.  It includes an opening
 * line, opening index, closing line, and closing index.
 *
 * Created on 4/12/16.
 * @author Mitchell Hanson
 */
public class SectionToRemove {

    private int openingLine;
    private int openingIndex;
    private int closingLine;
    private int closingIndex;

    SectionToRemove (int openLine, int openIndex, int closeLine, int closeIndex) {
        openingLine = openLine;
        openingIndex = openIndex;
        closingLine = closeLine;
        closingIndex = closeIndex;
    }

    public int getOpeningLine() {
        return openingLine;
    }

    public int getOpeningIndex() {
        return openingIndex;
    }

    public int getClosingLine() {
        return closingLine;
    }

    public int getClosingIndex() {
        return closingIndex;
    }



}
