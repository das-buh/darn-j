package com.darnj.op;

import java.util.logging.Logger;

import com.darnj.LangError;
import com.darnj.Span;
import com.darnj.interpret.*;
import com.darnj.value.*;

public abstract class Op {
    static Logger log = Logger.getGlobal();

    Span pos;

    public Span pos() {
        return pos;
    }

    public abstract Value eval(Context ctx);

    LangError error(String message) { 
        return new LangError(pos, message);
    }

    LangError error(String format, Object... values) {
        return new LangError(pos, String.format(format, values));
    }
}
