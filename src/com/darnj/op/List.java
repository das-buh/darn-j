package com.darnj.op;

import java.util.ArrayList;
import java.util.stream.Collectors;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public final class List extends Op {
    ArrayList<Op> elems;

    public List(Span pos, ArrayList<Op> elems) {
        this.pos = pos;
        this.elems = elems;
    }

    @Override
    public Value eval(Context ctx) {
        ArrayList<Value> elems = this.elems.stream()
            .map(op -> {
                var elem = op.eval(ctx);
                if (elem.inner instanceof UndefinedValue) {
                    throw new LangError(op.pos(), "list element cannot be type undefined");
                }
                return elem;
            })
            .collect(Collectors.toCollection(ArrayList::new));
        return Value.makeList(elems);
    }
}