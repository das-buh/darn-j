package com.darnj.lex;

import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.Span;

public final class Lexer {
    private static Logger log = Logger.getGlobal();

    private String src;
    private int srcLen;

    private int pos;
    private int indent;
    private int line;
    private boolean inIndentMode;

    private Token lookahead;

    public Lexer(String src) {
        this.src = src;
        srcLen = src.length();

        pos = 0;
        indent = 0;
        line = 0;
        inIndentMode = true;

        bump();
    }

    public void bump() {
        while (true) {
            var token = lex();
            if (token != null) {
                lookahead = token;
                break;
            }
        }
    }

    public Token peek() {
        return lookahead;
    }

    private Span pos() {
        return new Span(pos, pos);
    }

    // Returns null if a non-significant token was lexed.
    private Token lex() {
        log.finest("lex");

        if (pos == srcLen) {
            return new Token(new Span(pos, pos), 0, line + 1, Token.Kind.EOF);
        }

        var start = pos;
        var next = src.charAt(pos);

        // Evaluates to null if a non-significant token was lexed.
        Token.Kind kind = switch (next) {
            case ' ' -> {
                if (inIndentMode) {
                    indent++;
                }
                
                pos++;
                yield null;
            }
            case '\t' -> {
                if (inIndentMode) {
                    throw new LangError(pos(), "tabs may not be used for indentation");
                } else {
                    pos++;
                    yield null;
                }
            }
            case '\r' -> {
                inIndentMode = false;
                pos++;
                yield null;
            }
            case '\n' -> {
                inIndentMode = true;
                indent = 0;
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
                inIndentMode = false;

                pos++;
                while (pos < srcLen) {
                    var c = src.charAt(pos);
                    var term = switch (c) {
                        case '"' -> {
                            yield true;
                        }
                        case '\\' -> {
                            pos++;
                            if (pos == srcLen) {
                                yield true;
                            }

                            yield switch (src.charAt(pos)) {
                                case '"' | 'n' | 't' | '0' -> {
                                    yield false;
                                }
                                default -> throw new LangError(pos(), "invalid escape character");
                            };
                        }
                        default -> {
                            pos++;
                            yield false;
                        }
                    };

                    if (term) {
                        break;
                    }
                }

                if (pos == srcLen) {
                    throw new LangError(pos(), "unclosed string literal");
                }

                pos++;
                yield Token.Kind.STR_LITERAL;
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
                        case "true" -> Token.Kind.TRUE;
                        case "false" -> Token.Kind.FALSE;
                        case "nil" -> Token.Kind.NIL;
                        case "if" -> Token.Kind.IF;
                        case "else" -> Token.Kind.ELSE;
                        case "while" -> Token.Kind.WHILE;
                        case "continue" -> Token.Kind.CONTINUE;
                        case "break" -> Token.Kind.BREAK;
                        case "return" -> Token.Kind.RETURN;
                        case "do" -> Token.Kind.DO;
                        case "fn" -> Token.Kind.FN;
                        case "int" -> Token.Kind.INT;
                        case "float" -> Token.Kind.FLOAT;
                        case "bool" -> Token.Kind.BOOL;
                        case "str" -> Token.Kind.STR;
                        case "list" -> Token.Kind.LIST;
                        case "not" -> Token.Kind.NOT;
                        case "and" -> Token.Kind.AND;
                        case "or" -> Token.Kind.OR;
                        default -> Token.Kind.IDENT;
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

                        yield Token.Kind.FLOAT_LITERAL;
                    } else {
                        yield Token.Kind.INT_LITERAL;
                    }
                }

                if (pos + 1 < srcLen && src.charAt(pos + 1) == '=') {
                    Token.Kind digraph = switch (next) {
                        case '+' -> Token.Kind.ADD_ASSIGN;
                        case '-' -> Token.Kind.SUB_ASSIGN;
                        case '*' -> Token.Kind.MUL_ASSIGN;
                        case '/' -> Token.Kind.DIV_ASSIGN;
                        case '%' -> Token.Kind.MOD_ASSIGN;
                        case '=' -> Token.Kind.EQ;
                        case '!' -> Token.Kind.NEQ;
                        case '<' -> Token.Kind.LTE;
                        case '>' -> Token.Kind.GTE;
                        default -> null;
                    };

                    if (digraph != null) {
                        pos += 2;
                        yield digraph;
                    }
                }

                var op = switch (next) {
                    case '=' -> Token.Kind.ASSIGN;

                    case '+' -> Token.Kind.PLUS;
                    case '-' -> Token.Kind.MINUS;
                    case '*' -> Token.Kind.STAR;
                    case '/' -> Token.Kind.SLASH;
                    case '%' -> Token.Kind.MODULO;
                    case '&' -> Token.Kind.REF;
                    
                    case '<' -> Token.Kind.LT;
                    case '>' -> Token.Kind.GT;

                    case '?' -> Token.Kind.QMARK;

                    case ',' -> Token.Kind.COMMA;
                    case '(' -> Token.Kind.PAREN_OPEN;
                    case ')' -> Token.Kind.PAREN_CLOSE;
                    case '[' -> Token.Kind.BRACKET_OPEN;
                    case ']' -> Token.Kind.BRACKET_CLOSE;
                    default -> throw new LangError(pos(), "invalid input while parsing");
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
