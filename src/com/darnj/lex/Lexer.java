package com.darnj.lex;

import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.Span;

public final class Lexer {
    private static Logger log = Logger.getGlobal();

    String src;
    int srcLen;

    int pos;
    int indent;
    int line;
    boolean inIndentMode;

    static final int LOOKAHEAD = 2;
    static final int LOOKAHEAD_MINUS_ONE = LOOKAHEAD - 1;
    Token lookahead;

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

    Span pos() {
        return new Span(pos, pos);
    }

    // Returns null if a non-significant token was lexed.
    Token lex() {
        log.finest("lex");

        if (pos == srcLen) {
            return new Token(new Span(pos, pos), 0, line + 1, TokenKind.EOF);
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
                        case "list" -> TokenKind.LIST;
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
                        case '+' -> TokenKind.ADD_ASSIGN;
                        case '-' -> TokenKind.SUB_ASSIGN;
                        case '*' -> TokenKind.MUL_ASSIGN;
                        case '/' -> TokenKind.DIV_ASSIGN;
                        case '%' -> TokenKind.MOD_ASSIGN;
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
