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
    private FileParser fileParser;
    private List<String[]> lineInformation = new ArrayList<>();
    private List<Integer> javadocComments = new ArrayList<>();

    private boolean previousLineJavadocComment = false;
    private boolean javadocCommentOpen = false;

    FileInformation(FileParser contents) {
        fileParser = contents;
    }

    /**
     * Stores information about its file in a list.
     */
    public void runFileParser() {
        List<String> fileContents = fileParser.getFileContents();

        for(int index = 0; index <= fileContents.size(); index ++) {
            runInfoGatherers(index + 1, fileContents.get(index));
        }

    }

    private void runInfoGatherers(int lineNumber, String lineText) {
        checkJavadocComment(lineNumber, lineText);
    }

    private void checkJavadocComment(int lineNumber, String lineText) {

    }

    public List getLineINformation() {
        return lineInformation;
    }
}
