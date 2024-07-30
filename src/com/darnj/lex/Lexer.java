package com.darnj.lex;

import com.darnj.Error;
import com.darnj.Span;

public final class Lexer {
    String src;
    int srcLen;

    int pos;
    int indent;
    int line;
    boolean inIndentMode;

    static final int LOOKAHEAD = 2;
    static final int LOOKAHEAD_MINUS_ONE = LOOKAHEAD - 1;
    Token[] lookahead;

    public Lexer(String src) throws Error {
        this.src = src;
        srcLen = src.length();

        pos = 0;
        indent = 0;
        line = 0;
        inIndentMode = true;

        lookahead = new Token[LOOKAHEAD];
        for (var i = 0; i < LOOKAHEAD; i++) {
            bump();
        }
    }

    public void bump() throws Error {
        while (true) {
            var token = lex();
            if (token != null) {
                for (var i = 0; i < LOOKAHEAD_MINUS_ONE; i++) {
                    lookahead[i] = lookahead[i + 1];
                }
                lookahead[LOOKAHEAD_MINUS_ONE] = token;
            }
        }
    }

    public Token peek(int idx) {
        return lookahead[idx];
    }

    Span pos() {
        return new Span(pos, pos);
    }

    // Returns null if a non-significant token was lexed.
    Token lex() throws Error {
        if (pos == srcLen) {
            return new Token(new Span(pos, pos), 0, line, TokenKind.EOF);
        }

        var start = pos;
        var next = src.charAt(pos);

        // Evaluates to null if a non-significant token was lexed.
        TokenKind kind = switch (next) {
            case ' ' -> {
                if (inIndentMode) {
                    indent++;
                }
                
                pos++;
                yield null;
            }
            case '\t' -> {
                if (inIndentMode) {
                    throw new Error(pos(), "tabs may not be used for indentation");
                } else {
                    pos++;
                    yield null;
                }
            }
            case '\n' -> {
                inIndentMode = true;
                pos++;
                line++;
                yield null;
            }
            case '#' -> {
                while (pos < srcLen && src.charAt(pos) != '\n') {
                    pos++;
                }

                yield null;
            }
            case '"' -> {
                pos++;
                while (pos < srcLen) {
                    var c = src.charAt(pos);
                    switch (c) {
                        case '"' -> {
                            break;
                        }
                        case '\\' -> {
                            pos++;
                            if (pos == srcLen) {
                                break;
                            }

                            switch (src.charAt(pos)) {
                                case '"' | 'n' | 't' | '0' -> {
                                    continue;
                                }
                                default -> throw new Error(pos(), "invalid escape character");
                            }
                        }
                        default -> {
                            pos++;
                            continue;
                        }
                    }
                }

                if (pos == srcLen) {
                    throw new Error(pos(), "unclosed string literal");
                }

                pos++;
                yield TokenKind.STR_LITERAL;
            }
            default -> {
                inIndentMode = false;

                if (Character.isLetter(next)) {
                    while (pos < srcLen) {
                        var c = src.charAt(pos);
                        if (c != '_' && !Character.isLetterOrDigit(c)) {
                            break;
                        }
                        pos++;
                    }

                    yield switch (src.substring(start, pos)) {
                        case "true" -> TokenKind.TRUE;
                        case "false" -> TokenKind.FALSE;
                        case "nil" -> TokenKind.NIL;
                        case "if" -> TokenKind.IF;
                        case "else" -> TokenKind.ELSE;
                        case "while" -> TokenKind.WHILE;
                        case "continue" -> TokenKind.CONTINUE;
                        case "break" -> TokenKind.BREAK;
                        case "return" -> TokenKind.RETURN;
                        case "do" -> TokenKind.DO;
                        case "fn" -> TokenKind.FN;
                        case "int" -> TokenKind.INT;
                        case "float" -> TokenKind.FLOAT;
                        case "bool" -> TokenKind.BOOL;
                        case "str" -> TokenKind.STR;
                        case "not" -> TokenKind.NOT;
                        case "and" -> TokenKind.AND;
                        case "or" -> TokenKind.OR;
                        default -> TokenKind.IDENT;
                    };
                }

                if (Character.isDigit(next)) {
                    while (pos < srcLen && Character.isDigit(src.charAt(pos))) {
                        pos++;
                    }

                    if (pos < srcLen && src.charAt(pos) == '.') {
                        pos++;
                        while (pos < srcLen && Character.isDigit(src.charAt(pos))) {
                            pos++;
                        }

                        yield TokenKind.FLOAT_LITERAL;
                    } else {
                        yield TokenKind.INT_LITERAL;
                    }
                }

                if (pos + 1 < srcLen && src.charAt(pos + 1) == '=') {
                    TokenKind digraph = switch (next) {
                        case '=' -> TokenKind.EQ;
                        case '!' -> TokenKind.NEQ;
                        case '<' -> TokenKind.LTE;
                        case '>' -> TokenKind.GTE;
                        default -> null;
                    };

                    if (digraph != null) {
                        pos += 2;
                        yield digraph;
                    }
                }

                var op = switch (next) {
                    case '=' -> TokenKind.ASSIGN;

                    case '+' -> TokenKind.PLUS;
                    case '-' -> TokenKind.MINUS;
                    case '*' -> TokenKind.STAR;
                    case '/' -> TokenKind.SLASH;
                    case '%' -> TokenKind.MODULO;
                    case '&' -> TokenKind.REF;
                    
                    case '<' -> TokenKind.LT;
                    case '>' -> TokenKind.GT;

                    case '?' -> TokenKind.QMARK;

                    case ',' -> TokenKind.COMMA;
                    case '(' -> TokenKind.PAREN_OPEN;
                    case ')' -> TokenKind.PAREN_CLOSE;
                    case '[' -> TokenKind.BRACKET_OPEN;
                    case ']' -> TokenKind.BRACKET_CLOSE;
                    default -> throw new Error(pos(), "invalid input while parsing");
                };

                pos++;
                yield op;
            }
        };

        if (kind == null) {
            return null;
        }

        return new Token(new Span(start, pos), indent, line, kind);
    }
}
