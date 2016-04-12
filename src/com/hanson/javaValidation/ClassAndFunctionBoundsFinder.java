package com.hanson.javaValidation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 4/12/16.
 */
public class ClassAndFunctionBoundsFinder {
    private List<FunctionBounds> functionBoundsList;
    private FunctionBounds classBounds;
    private List<String> fileContents;


    ClassAndFunctionBoundsFinder(List<String> passedContents) {
        functionBoundsList = new ArrayList<>();
        classBounds = new FunctionBounds();
        fileContents = passedContents;

    }
}
