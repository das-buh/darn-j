package com.darnj.op;

import com.darnj.Error;
import com.darnj.Span;
import com.darnj.interpret.*;;

public class IfElse extends Op {
    Op cond;
    Op ifBranch;
    Op elseBranch; // Null if no else branch.
    boolean isStmt;

    public IfElse(Span pos, Op cond, Op ifBranch, Op elseBranch) {
        super(pos);
        this.cond = cond;
        this.ifBranch = ifBranch;
        this.elseBranch = elseBranch;
        this.isStmt = false;
    }

    public void setStmt() {
        isStmt = true;
    }

    @Override
    public Value eval(Context ctx) throws Error {

    }
}
