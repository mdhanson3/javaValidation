package com.hanson.javaValidation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 3/1/16.
 */
public class FileParser {
    private String filePath;
    private List<String> fileContents;

    FileParser(String path) {
        filePath = path;
        fileContents = new ArrayList();
    }

    public void runFileParser() {
        try (BufferedReader input = new BufferedReader(new FileReader(filePath))) {
            System.out.println("In buffered reader try");
            while (input.ready()) {
                fileContents.add(input.readLine());
            }
        } catch (java.io.FileNotFoundException fnfe) {
            System.out.println("Failed to read input file");
            fnfe.printStackTrace();
        } catch (Exception exception) {
            System.out.println("General Error");
            exception.printStackTrace();
        }
    }

    public List getFileContents() {
        return fileContents;
    }

}
