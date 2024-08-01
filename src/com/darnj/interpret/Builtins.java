package com.darnj.interpret;

import com.darnj.value.*;

public final class Builtins {
    Context ctx;

    Builtins(Context ctx) {
        this.ctx = ctx;
    }

    public static void addBuiltins(Context ctx) {
        new Builtins(ctx).put();
    }

    void put() {
        fn("pow", c -> {
            c.arity(2);
            // var base = 
            return Value.makeUndefined();
        });

        // TODO
    }

    void fn(String name, Function impl) {
        ctx.funcs.put(ctx.symbols().intern(name), impl);
    }
}
