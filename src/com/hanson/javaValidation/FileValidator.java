package com.hanson.javaValidation;

import java.io.*;

/**
 * Created by student on 2/9/16.
 */
public class FileValidator {
    private final int REQUIRED_NUMBER_OF_ARGUMENTS = 1;
    private final boolean DEBUG = false;

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

        // Create and run fileParser
        FileParser myFileParser = new FileParser(arguments[0]);
        myFileParser.runFileParser();

        // Create fileInformation object and run its main method
        FileInformation myFileInformation = new FileInformation(myFileParser);
        myFileInformation.runFileInformation();

        // Create validation objects using file provided by command line arg
        SingleLineValidator mySingleLineValidator = new SingleLineValidator(arguments[0]);
        MultiLineValidator myMultiLineValidator = new MultiLineValidator(arguments[0]);

        // Run both objects' validation methods
        myMultiLineValidator.runMultiLineValidation();
        mySingleLineValidator.runSingleLineValidation();

        //System.out.println(multiLineErrors);
        mySingleLineValidator.outputErrors();

        ErrorFileWriter myWriter = new ErrorFileWriter();
        myWriter.writeMarkupFile(arguments[0]);
        myWriter.writeFileFromArray(myFileInformation.getFileContents());
        //myWriter.writeSummaryFile("This is the summary", multiLineErrors);

        if (DEBUG) {
            myFileInformation.debugTerminalOutput();
        }

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
