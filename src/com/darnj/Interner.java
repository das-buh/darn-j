package com.darnj;

import java.util.ArrayList;
import java.util.HashMap;

public final class Interner {
    HashMap<String, Integer> map;
    ArrayList<String> strings;

    public Interner() {
        this.map = new HashMap<String, Integer>();
        this.strings = new ArrayList<String>();
    }

    public int intern(String s) {
        var id = map.get(s);
        if (id == null) {
            id = strings.size();
            strings.add(s);
            map.put(s, id);
        }
        return id;
    }

    public String resolve(int id) {
        return strings.get(id);
    }
}
