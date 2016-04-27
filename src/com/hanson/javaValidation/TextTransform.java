package com.hanson.javaValidation;

import java.util.List;

/**
 * Created by student on 4/20/16.
 */
public class TextTransform {
    private final String KEYWORD_OPENING_SPAN = "<span class=\"keyword underline\">";
    private final String CONSTANT_OPENING_SPAN = "<span class=\"constant underline\">";
    private final String CLOSING_SPAN = "</span>";

    public void convertSpacesToHtmlNbs(List<String> contents) {
        System.out.println(contents.size());
        for (int lineNumber = 0; lineNumber < contents.size(); lineNumber ++) {
            System.out.println(lineNumber);
            System.out.println(contents.get(lineNumber));
            String lineText = contents.get(lineNumber);
            lineText = lineText.replace(" ","\u00A0");
            contents.set(lineNumber, lineText);
        }
    }

    public List<String> augmentContentsWithUnderlines(List<String> contents, List<SingleLineError> errorList) {
        System.out.println();
        for(SingleLineError error : errorList) {
            if (error.isCanBeUnderlined()) {
                transformConstant(error, contents);
            }

        }

        return contents;
    }

    private void transformConstant(SingleLineError error, List<String> contents) {

        // Get indices to underline
        int openingSpanIndex = error.getOpeningUnderlineIndex();
        int closingSpanIndex = error.getClosingUnderlineIndex();

        // Get line contents to update
        String lineText = contents.get(error.getLineNumber() - 1);

        // Split string into three pieces to insert underline html into
        String start = lineText.substring(0, openingSpanIndex);
        String middle = lineText.substring(openingSpanIndex, closingSpanIndex);
        String end = lineText.substring(closingSpanIndex);

        String newLineText = start + CONSTANT_OPENING_SPAN + middle + CLOSING_SPAN + end;

        if (!error.getErrorType().equals("LineLength")) {
            contents.set(error.getLineNumber() -1, newLineText);
        }
    }

    private void transformKeyword(SingleLineError error) {
        System.out.println(error.getErrorMessage());
    }
}
