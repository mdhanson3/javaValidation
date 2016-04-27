package com.hanson.javaValidation;

import javax.swing.text.html.HTML;
import java.io.*;
import java.util.*;
import java.io.FileReader;

/**
 * Created by student on 2/9/16.
 */
public class ErrorFileWriter {
    private final String HTML_STYLE = "<head><meta charset=\"utf-8\"/><style>.underline {text-decoration: underline; -moz-text-decoration-color: red; text-decoration-color: red; background-color: yellow;} </style> </head>";

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

    public void writeFileFromArray(List<String> output, String fileName) {
        try {
            BufferedWriter out = null;
            try /*(BufferedWriter out = new BufferedWriter(new FileWriter("output/" + fileName)))*/ {
                System.out.println("==================IN WRITER=====================");

                File outputFile = new File("output/" + fileName);
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }

                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));

                out.write(HTML_STYLE);
                for (String outputLine : output) {
                    System.out.println("writing");
                    out.write(outputLine + "<br />");
                    out.newLine();
                }
            } catch (IOException ioException) {
                System.out.println("Error writing the markup file");
                ioException.printStackTrace();
            } catch (Exception exception) {
                System.out.println("Error writing the cleansed file");
                exception.printStackTrace();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception e) {

        }
    }
}
