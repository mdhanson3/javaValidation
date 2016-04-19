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
    private QuoteAndCommentReplacer quoteAndTextReplacer;                   //Sanitizes the passed file contents
    private ClassAndFunctionBoundsFinder classAndFunctionBoundsFinder;      //Finds class and functions bounds
    private KeywordFinder keywordFinder;

    private List<String> fileContents;                // Holds original file contents
    private List<KeywordInstance> lineInformation;     // List that holds where keywords were found in the file contents
    private List<String> sanitizedFileContents;       // Holds the contents sanitized by quoteAndTextReplacer

    FileInformation(List<String> contents) {
        lineInformation = new ArrayList<>();

        //**********************************************************************************************
        // TODO: Figure out pass by reference issues, how to maintain original file contents?
        sanitizedFileContents = new ArrayList<>();
        fileContents = contents;
        quoteAndTextReplacer = new QuoteAndCommentReplacer(contents);
        //**********************************************************************************************

        classAndFunctionBoundsFinder = new ClassAndFunctionBoundsFinder();
        keywordFinder = new KeywordFinder();
    }

    public List<KeywordInstance> getLineInformation() {
        return lineInformation;
    }
    public List<String> getFileContents() {return sanitizedFileContents;};
    public List<String> getOriginalFileContents() {return fileContents;};

    /**
     * Sanitizes the file content and runs the information gatherers on the sanitized file content
     */
    public void runFileInformation() {
        // Get sanitized file contents
        quoteAndTextReplacer.replaceQuotesAndComments();
        sanitizedFileContents = quoteAndTextReplacer.getFileContents();

        // Find Function bounds of file contents
        classAndFunctionBoundsFinder.findClassAndFunctionBounds(sanitizedFileContents);


        //Find Keywords in file contents
        keywordFinder.generateSingleLineInformation(sanitizedFileContents, classAndFunctionBoundsFinder);
        lineInformation = keywordFinder.getKeywords();

    }
}
