package com.darnj.parse;

@FunctionalInterface
interface Pattern<T> {
    T parse(Parser parser);
}