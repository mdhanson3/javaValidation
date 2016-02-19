package com.hanson.javaValidation;


import java.io.*;
import java.util.*;

/**
 * Created by student on 2/10/16.
 */
public class MultiLineValidator {
    private String filePath = null;

    MultiLineValidator(String path) {
        filePath = path;
    }

    public List<String> runMultiLineValidation() {
        List<String> multiLineErrors = new ArrayList<String>();
        multiLineErrors.add(lineCount());
        multiLineErrors.add(javadocErrors());
        return multiLineErrors;
    }

    private String lineCount() {
        int count = 0;
        try (BufferedReader input = new BufferedReader(new FileReader(filePath))) {
            System.out.println("In buffered reader try");
            while (input.ready()) {
                input.readLine();
                count ++;
            }
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("Failed to read input file");
            fnfe.printStackTrace();
        } catch (Exception exception) {
            System.out.println("General Error");
            exception.printStackTrace();
        }


        return "line count = " + count;
    }

    private String javadocErrors() {
        return "javadoc Errors";
    }
}
