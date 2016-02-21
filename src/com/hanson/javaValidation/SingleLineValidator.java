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
        String lineText;

        // Check each line for each error.  If error is found, add error to array.
        try (BufferedReader input = new BufferedReader(new FileReader(filePath))) {
            while (input.ready()) {
                //lineText = ;
                runErrorTests(input.readLine(), lineNumber);
                lineNumber ++;
            }
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("Failed to read input file");
            fnfe.printStackTrace();
        } catch (Exception exception) {
            System.out.println("General Error");
            exception.printStackTrace();
        }

        //characterCountPerLine();
    }

    private void runErrorTests(String lineText, int lineNumber) {
        if (!characterCountPerLine(lineText)) {
            System.out.println(lineNumber);
            System.out.println("TOo Long");
        }
    }
    private boolean characterCountPerLine(String lineText) {
        if (lineText.length() > 80) {
            return false;
        }
        return true;

    }
}
