package com.hanson.javaValidation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class takes an array of file path arguments and runs various validation processes on each.  It produces output files
 * of meta data and marked-up code showing any errors.
 *
 * Created by student on 2/9/16.
 * @author Mitchell Hanson
 */
public class FileValidator {
    private final int MINIMUM_REQUIRED_NUMBER_OF_ARGUMENTS = 1;
    private final boolean DEBUG = false;
    private String fileName;


    /* TODO: allow arguments array to be greater than one and run validation on every arg
     * consider using 'throws' to skip over files that cause any errors (handle by outputing error or whatever)
     */
    public void runValidation(String[] arguments) {
        // Check number of args
        if (!numberOfArgumentsPassed(arguments)) {
            return;
        }

        fileName = arguments[0];

        if (DEBUG) {
            File file = new File(fileName);
            String path = file.getAbsolutePath();
            System.out.println(fileName);
            System.out.println(path);
        }

        // Create and run fileParser
        FileParser myFileParser = new FileParser(fileName);
        myFileParser.runFileParser();


        // Create fileInformation object and run its main method
        //TODO: Change to pass in list<string> instead of parser object
        FileInformation myFileInformation = new FileInformation(myFileParser.getFileContents());
        myFileInformation.runFileInformation();

        // Create validation objects using file provided by command line arg
        SingleLineValidator mySingleLineValidator = new SingleLineValidator(fileName);
        MultiLineValidator myMultiLineValidator = new MultiLineValidator(fileName);

        // Run both objects' validation methods
        myMultiLineValidator.runMultiLineValidation();
        mySingleLineValidator.runSingleLineValidation();

        //System.out.println(multiLineErrors);
        mySingleLineValidator.outputErrors();

        ErrorFileWriter myWriter = new ErrorFileWriter();
        myWriter.writeMarkupFile(fileName);
        //TODO: The output below doesn't work because of pass by reference issues
        myWriter.writeFileFromArray(myFileInformation.getFileContents(), "cleanJavaCode.html");
        myWriter.writeFileFromArray(myFileInformation.getOriginalFileContents(), "originalJavaCode.html");
        //myWriter.writeSummaryFile("This is the summary", multiLineErrors);
        List<String> originalFile = myFileInformation.getOriginalFileContents();
        

        if (DEBUG) {
            //myFileInformation.debugTerminalOutput();
        }

    }

    /**
     * Checks that the number of arguments passed to the application is correct
     *
     * @param arguments arguments passed to the application
     * @return returns true if the number of arguments passed is correct
     */
    private boolean numberOfArgumentsPassed(String[] arguments) {
        if (arguments.length == MINIMUM_REQUIRED_NUMBER_OF_ARGUMENTS) {
            return true;
        } else {
            System.out.println("This program requires " + MINIMUM_REQUIRED_NUMBER_OF_ARGUMENTS + " arguments to run. Exiting application");
            return false;
        }
    }
}
