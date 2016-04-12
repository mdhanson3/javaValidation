package com.hanson.javaValidation;

/**
 * This class holds the bounds of anything that can be described with an opening and closing int.  It is intended to
 * be used with function and class bounds (via opening and closing lines).
 *
 * Created by student on 4/12/16.
 * @author Mitchell Hanson
 */
public class FunctionBounds {

    private int openingLine;
    private int closingLine;

    FunctionBounds() {
        openingLine = 0;
        closingLine = 0;
    }

    FunctionBounds(int open, int close) {
        this();
        openingLine = open;
        closingLine = close;
    }

    public int getOpeningLine() {
        return openingLine;
    }

    public void setOpeningLine(int openingLine) {
        this.openingLine = openingLine;
    }

    public int getClosingLine() {
        return closingLine;
    }

    public void setClosingLine(int closingLine) {
        this.closingLine = closingLine;
    }
}
