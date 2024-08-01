package com.darnj;

import java.util.ArrayList;

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
    Span pos;
    String message;

    int line;
    int column;
    Span underline; // Undefined if multiline.

    public LangError(Span pos, String message) {
        this.pos = pos;
        this.message = message;
    }

    public void render(String src) {
        System.out.println("Error: " + message);
        System.out.println(String.format("  + at line %d, column %d", line, column));
        System.out.println("  |");

        var snippet = findSnippet(src);
        if (snippet.size() == 1) {
            renderLine(snippet.get(0));
        } else {
            renderMultiline(snippet);
        }
    }

    void renderLine(String line) {
        System.out.println("  | " + line);
        System.out.println("  | " + " ".repeat(underline.start()) + "^".repeat(underline.end() - underline.start()));
    }

    void renderMultiline(ArrayList<String> snippet) {
        for (var line : snippet) {
            System.out.println("  > " + line);
        }
        System.out.println("  | ");
    }

    ArrayList<String> findSnippet(String src) {
        line = 0;
        column = 0;

        var start = pos.start();
        var end = pos.end();

        var snippet = new ArrayList<String>();
        var snipping = false;
        var lineStart = 0;

        var underlineStart = 0;
        var underlineEnd = 0;

        var i = 0;
        var len = src.length();
        for (i = 0; i < len; i++) {
            if (i == start) {
                snipping = true;
                underlineStart = column;
            } else if (i == end) {
                snipping = false;
            }

            column++;
            if (src.charAt(i) == '\n') {
                if (snipping) {
                    snippet.add(src.substring(lineStart, i));
                }
                
                line++;
                column = 0;
                lineStart = i + 1;
            }

            if (i == end) {
                snipping = false;
                underlineEnd = column;
            }
        }

        if (i == end) {
            snipping = false;
            underlineEnd = column;
        }

        if (i == start) {
            snipping = true;
            underlineStart = column;
        }

        if (snipping) {
            snippet.add(src.substring(lineStart, i));
        }

        if (underlineStart == underlineEnd) {
            underlineEnd++;
        }

        underline = new Span(underlineStart, underlineEnd);
        return snippet;
    }
}
