package com.darnj.op;

import java.util.ArrayList;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Block extends Op {
    ArrayList<Op> stmts;
    
    @Override
    public Value eval(Context ctx) throws Error {
        
    }
}
