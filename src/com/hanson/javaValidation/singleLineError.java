package com.hanson.javaValidation;

/**
 * This class holds information about an error that exists on a single line within a java file.  It holds whether the
 * error can be underlined, the underline bounds, the line number, the type of error, and the error message.
 *
 * Created on 4/19/16.
 * @author Mitchell Hanson
 */
public class singleLineError {
    private int lineNumber;
    private boolean canBeUnderlined;
    private int openingUnderlineIndex;
    private int closingUnderlineIndex;
    private String errorMessage;

    private String errorType;

    public singleLineError(int line, int openingIndex, int closingIndex, String message, String type) {
        lineNumber = line;
        canBeUnderlined = true;
        openingUnderlineIndex = openingIndex;
        closingUnderlineIndex = closingIndex;
        errorMessage = message;
        errorType = type;
    }

    public singleLineError(int line, String errorMessage, String type) {
        this(line, -1, -1, errorMessage, type);
        canBeUnderlined = false;
    }
}
