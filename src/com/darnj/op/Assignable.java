package com.darnj.op;

import com.darnj.interpret.*;
import com.darnj.value.*;

public interface Assignable {
    public Value referent(Context ctx);
}
