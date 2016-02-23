package com.hanson.javaValidation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 2/10/16.
 */
public class SingleLineValidator {
    private String filePath = null;
    private List<String[]> singleLineErrors = new ArrayList<>();

    SingleLineValidator(String path) {
        filePath = path;
    }

    public void runSingleLineValidation() {
        int lineNumber = 1;

        // Check each line for each error.  If error is found, add error to array.
        try (BufferedReader input = new BufferedReader(new FileReader(filePath))) {
            while (input.ready()) {
                runErrorTests(lineNumber, input.readLine());
                lineNumber ++;
            }
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("Failed to read input file - File not found");
            fnfe.printStackTrace();
        } catch (Exception exception) {
            System.out.println("Failed to read input file - Generic Error");
            exception.printStackTrace();
        }
    }

    private void runErrorTests(int lineNumber, String lineText) {
        // These test check all text regardless of quotes or comments
        characterCountPerLine(lineNumber, lineText);

        /*
        TODO: Strip unnecessary text (comments, multi-line comments, java-doc comments, text in quotes, text in double-quotes)
        Consider:  How will this affect line numbers, character indices for producing output
        */

        // The tests below should not check text between quotes, comments, and after comments.
        testKeywordSpacing(lineNumber, lineText);

    }

    private void characterCountPerLine(int lineNumber, String lineText) {
        if (lineText.length() > 80) {
            createError(lineNumber, "Line over 80 characters.");
        }
    }

    private void testKeywordSpacing(int lineNumber, String lineText) {
        // Ignore characters in between '', "", and /* */, and after //
        // look for while
        // If next character is not a space, produce while error
    }

    private void createError(int lineNumber, String error) {
        System.out.println(lineNumber + " " + error);
        String[] tempArray = new String[2];
        tempArray[0] = String.valueOf(lineNumber);
        tempArray[1] = error;
        singleLineErrors.add(tempArray);
    }

    public void outputErrors() {
        for (String[] tempArray :
                singleLineErrors) {
            System.out.println("Line " + tempArray[0] + " " + tempArray[1]);
        }
    }
}
