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

    SingleLineValidator(String path) {
        filePath = path;
    }

    public List<String> runSingleLineValidation() {
        List<String> singleLineErrors = new ArrayList<String>();

        singleLineErrors = characterCountPerLine(filePath);

        return singleLineErrors;
    }

    private List<String> characterCountPerLine(String filePath) {
        int lineNumber = 0;
        String lineText;
        List<String> characterCountErrors = new ArrayList<String>();

        try (BufferedReader input = new BufferedReader(new FileReader(filePath))) {
            while (input.ready()) {
                lineText = input.readLine();
                if (!characterTest(lineText)) {
                    characterCountErrors.add("Line " + lineNumber + " has over 80 characters");
                }
                lineNumber ++;
            }
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("Failed to read input file");
            fnfe.printStackTrace();
        } catch (Exception exception) {
            System.out.println("General Error");
            exception.printStackTrace();
        }

        return characterCountErrors;

    }

    private boolean characterTest(String line) {
        if (line.length() > 80) {
            return false;
        }
        return true;
    }
}
