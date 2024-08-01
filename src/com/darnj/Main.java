package com.darnj;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.darnj.interpret.*;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Error: no source file path supplied");
            return;    
        }

        var path = args[0];
        String src;
        try {
            src = Files.readString(Paths.get(path));
        } catch (IOException e) {
            System.out.println("Error: failed to read source file");
            return;
        }

        Interpreter.interpret(src);
    }
}
