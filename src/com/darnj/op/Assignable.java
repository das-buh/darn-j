package com.darnj.op;

import com.darnj.Error;
import com.darnj.interpret.*;

public interface Assignable {
    public void assign(Context ctx) throws Error;
}
