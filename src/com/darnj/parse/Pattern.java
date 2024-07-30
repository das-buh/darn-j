package com.darnj.parse;

import com.darnj.Error;
import com.darnj.op.*;

public abstract class Pattern {
    abstract Op parse(Parser parser) throws Error;
}