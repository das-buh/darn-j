package com.darnj;

// TODO make sure eof spans are handled correctly without index oob
public final class Error extends Exception {
    Span pos;
    String message;

    public Error(Span pos, String message) {
        this.pos = pos;
        this.message = message;
    }
}
