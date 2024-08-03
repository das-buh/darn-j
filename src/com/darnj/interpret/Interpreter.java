package com.darnj.interpret;

import com.darnj.LangError;
import com.darnj.op.*;
import com.darnj.parse.*;

public final class Interpreter {
    String src;
    Context ctx;
    
    Interpreter(String src) {
        this.src = src;
        ctx = new Context();
        Builtins.addBuiltins(ctx);
    }

    public static void interpret(String src) {
        try {
            new Interpreter(src).parseAndEval();
        } catch (LangError e) {
            e.render(src);
        }
    }

    void parseAndEval() {
        try {
            Parser.parse(src, ctx).eval(ctx);
        } catch (Continue.Effect e) {
            throw new LangError(e.pos(), "cannot continue outside of a loop");
        } catch (Break.Effect e) {
            throw new LangError(e.pos(), "cannot break outside of a loop");
        } catch (Return.Effect e) {
            throw new LangError(e.pos(), "cannot return outside of a function");
        }
    }
}
