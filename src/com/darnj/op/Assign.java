package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;

public final class Assign extends Op implements Assignable {
    Op assignee;
    Op value;

    public Assign(Span pos, Op assignee, Op value) {
        super(pos);
        this.assignee = assignee;
        this.value = value;
    }
    
    @Override
    public Value eval(Context ctx) throws Error {
        
    }

    @Override
    public void assign(Context ctx) throws Error {
        
    }
}
