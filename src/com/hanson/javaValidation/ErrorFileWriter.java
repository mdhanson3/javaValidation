package com.hanson.javaValidation;

import java.io.*;
import java.util.*;
import java.io.FileReader;

/**
 * Created by student on 2/9/16.
 */
public class ErrorFileWriter {

    public void writeMarkupFile(String file) {
        String line = null;
        String outputLine = "";
        int lineNumber = 1;
        try(BufferedReader input = new BufferedReader(new FileReader(file));
                BufferedWriter out = new BufferedWriter(new java.io.FileWriter("output/withHighlightedErrors.html"))) {
            while((line = input.readLine()) != null) {
                outputLine = "Line number " + lineNumber + " " + line;
                out.write(outputLine);
                out.newLine();
                lineNumber ++;
            }
        } catch (IOException ioException) {
            System.out.println("Error writing the markup file");
            ioException.printStackTrace();
        } catch (Exception exception) {
            System.out.println("Error opening the file");
            exception.printStackTrace();
        }
    }

    public void writeSummaryFile(String summaryText, List<String> errors) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new java.io.FileWriter("output/Summary.txt")))) {
            out.println(summaryText);
            for( String lineError : errors) {
                out.println(lineError);
            }
        } catch (IOException ioException) {
            System.out.println("Error writing the summary file");
            ioException.printStackTrace();
        } catch (Exception exception) {
            System.out.println("Error opening the file");
            exception.printStackTrace();
        }
    }
}
