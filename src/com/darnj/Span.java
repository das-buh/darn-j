package com.darnj;

public record Span(int start, int end) {
    public Span to(Span other) {
        return new Span(start, other.end);
    }

    @Override
    public final String toString() {
        return String.format("%d..%d", start, end);
    }
}
