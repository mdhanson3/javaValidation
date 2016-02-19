package com.hanson.javaValidation;

import java.io.*;
import java.util.*;

/**
 * Created by student on 2/9/16.
 */
public class FileValidator {
    private final int REQUIRED_NUMBER_OF_ARGUMENTS = 1;
    private final boolean DEBUG = true;

    public void runValidation(String[] arguments) {
        // Check number of args
        if (!numberOfArgumentsPassed(arguments)) {
            return;
        }

        if (DEBUG) {
            File file = new File(arguments[0]);
            String path = file.getAbsolutePath();
            System.out.println(arguments[0]);
            System.out.println(path);
        }

        /*
        SingleLineValidator mySingleLineValidator = new SingleLineValidator();
        List<String> listOfSingleLineErrors = mySingleLineValidator(arguments[0]);
        */
        MultiLineValidator myMultiLineValidator = new MultiLineValidator(arguments[0]);
        List<String> multiLineErrors = myMultiLineValidator.runMultiLineValidation();

        System.out.println(multiLineErrors);

        ErrorFileWriter myWriter = new ErrorFileWriter();
        myWriter.writeMarkupFile(arguments[0]);
        myWriter.writeSummaryFile("This is the summary", multiLineErrors);
    }

    /**
     * Checks that the number of arguments passed to the application is correct
     *
     * @param arguments arguments passed to the application
     * @return returns true if the number of arguments passed is correct
     */
    private boolean numberOfArgumentsPassed(String[] arguments) {
        if (arguments.length == REQUIRED_NUMBER_OF_ARGUMENTS) {
            return true;
        } else {
            System.out.println("This program requires " + REQUIRED_NUMBER_OF_ARGUMENTS + " arguments to run. Exiting application");
            return false;
        }
    }
}
