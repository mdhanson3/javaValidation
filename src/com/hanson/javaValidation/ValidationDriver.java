package com.hanson.javaValidation;

/**
 *
 * Created by student on 2/9/16.
 * @author Mitchell Hanson
 */
public class ValidationDriver {
    public static void main(String[] args) {
        FileValidator validator = new FileValidator();
        validator.runValidation(args);
    }
}
