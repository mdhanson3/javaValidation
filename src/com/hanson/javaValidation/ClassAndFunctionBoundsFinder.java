package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class iterates over a list of strings representing the contents of a java file searching for the bounds of the
 * class and functions found within.  The class bounds are stored as a FunctionBounds object.  For each function found
 * a FunctionBounds object is created and added to a List of FunctionBounds.
 *
 * Created on 4/12/16.
 * @author Mitchell Hanson
 */
public class ClassAndFunctionBoundsFinder {
    private List<FunctionBounds> functionBoundsList;
    private FunctionBounds classBounds;
    private List<String> fileContents;

    // Class bounds vars
    private Boolean foundClassBounds = false;
    private Boolean foundOpeningClassLine = false;
    private int openBrackets;

    // These variables are for finding the function bounds
    private Boolean functionIsOpen = false;
    private int openFunctionBrackets;
    private int openingFunctionLine = 0;
    private int closingFunctionLine = 0;


    ClassAndFunctionBoundsFinder() {
        functionBoundsList = new ArrayList<>();
        classBounds = new FunctionBounds();
        fileContents = new ArrayList<>();

    }

    public FunctionBounds getClassBounds() {
        return classBounds;
    }
    public FunctionBounds getFirstFunctionBounds() {
        if(!functionBoundsList.isEmpty()) {
            return functionBoundsList.get(0);
        }
        else return null;
    }

    public void findClassAndFunctionBounds(List<String> contents) {
        fileContents = contents;

        for (int index = 0; index < fileContents.size(); index ++) {
            findClassBounds(index + 1, fileContents.get(index));
        }

        findFunctionBounds();
        printFunctionBounds();
        System.out.println("Class opening line: " + classBounds.getOpeningLine() +". Class closing line: " + classBounds.getClosingLine());
    }

    private void findClassBounds(int lineNumber, String lineText) {
        if (!foundClassBounds) {
            if (foundOpeningClassLine) {
                for (char k : lineText.toCharArray()) {
                    if (k == '{') {
                        openBrackets ++;
                    } else if (k == '}') {
                        openBrackets --;
                    }
                }

                if (openBrackets == 0) {
                    classBounds.setClosingLine(lineNumber);
                    foundClassBounds = true;
                }
            } else {
                if (lineText.contains("{")) {
                    foundOpeningClassLine = true;
                    openBrackets = 1;
                    classBounds.setOpeningLine(lineNumber);
                }
            }
        }
    }

    private void findFunctionBounds() {
        // Check lines within class bounds for function bounds
        // It seems like the index should start at openingClassLine + 1, but line numbers are one greater than the index of the array
        // so we must actually remove one from the closing line
        String lineText = "";
        for (int index = classBounds.getOpeningLine(); index < classBounds.getClosingLine() - 1; index ++) {
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
                    functionBoundsList.add(new FunctionBounds(openingFunctionLine, closingFunctionLine));
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
        for(FunctionBounds bounds : functionBoundsList) {
            System.out.println("Opening function line: " + bounds.getOpeningLine() + ". Closing function line: " + bounds.getClosingLine());
        }
    }
}
