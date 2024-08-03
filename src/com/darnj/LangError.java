package com.darnj;

import java.util.ArrayList;
import java.util.logging.*;

/*

PROTOTYPICAL ERROR MESSAGES

Error: function `foo` expected type int but got type str
  + at line 10, column 20
  |
  | foo("blah")
  |     ^^^^^^
  
Error: function `foo` expected type int but got type str
  + at line 10, column 20
  |
  > foo(concat(
  >     "lorem",
  >     "ipsum"))
  |

*/

public final class LangError extends RuntimeException {
    private static Logger log = Logger.getGlobal();

    final Span pos;
    final String message;

    private int line;
    private int column;
    private int underline; // Undefined if multiline.

    public LangError(Span pos, String message) {
        this.pos = pos;
        this.message = message;
    }

    public void render(String src) {
        log.finer(() -> "error pos " + pos.toString());
        log.finest(() -> {
            printStackTrace();
            return "printed stack trace";
        });

        var snippet = findSnippet(src);

        System.out.println("Error: " + message);
        System.out.println(String.format("  + at line %d, column %d", line + 1, column + 1));
        System.out.println("  |");

        if (snippet.size() == 1) {
            renderLine(snippet.get(0));
        } else {
            renderMultiline(snippet);
        }
    }

    private void renderLine(String line) {
        var len = pos.end() - pos.start();
        System.out.println("  | " + line);
        System.out.println("  | " + " ".repeat(underline) + "^".repeat(len > 0 ? len : 1));
    }

    private void renderMultiline(ArrayList<String> snippet) {
        for (var line : snippet) {
            System.out.println("  > " + line);
        }
        System.out.println("  | ");
    }

    private ArrayList<String> findSnippet(String src) {
        line = 0;
        column = 0;
        underline = 0;

        var start = pos.start();
        var end = pos.end();

        var snippet = new ArrayList<String>();
        var lookingForSnippet = true;
        var snipping = false;
        var snipLine = false;
        var lineStart = 0;

        var i = 0;
        var len = src.length();
        for (i = 0; i < len; i++) {
            if (i == start) {
                lookingForSnippet = false;
                snipping = true;
                snipLine = true;
                underline = column;
            }
            
            if (i == end) {
                snipping = false;
            }

            if (lookingForSnippet) {
                column++;
            }

            if (src.charAt(i) == '\n') {
                if (snipLine) {
                    snippet.add(src.substring(lineStart, i));
                    snipLine = snipping;
                }
                
                if (lookingForSnippet) {
                    line++;
                    column = 0;
                }

                lineStart = i + 1;
            }
        }

        if (i == end) {
            snipping = false;
        }

        if (i == start) {
            snipLine = true;
            underline = column;
        }

        if (snipLine) {
            snippet.add(src.substring(lineStart, i));
        }

        return snippet;
    }
}
