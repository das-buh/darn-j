# Darn-J: A Toy Interpreter in Java

Yep, that's what it is.

(im not actually sure if this works very well i didnt really debug it)

- Runtime-checked static typing
- Variable declaration and assignment have one syntax (Python-esque)
- Variable declaration infers typing; function parameters are manifest-typed
- All variables are function-scoped; no global variables
    - Function declarations are hoisted to global or function scope
- Nil-able types must be marked with `?`
- Strings are mutable but non-resizeable
- Compound types include:
    - `type?`, nil-able `type`
    - `type*`, reference to `type` (values, except for slices, are pass-by-value)
    - `[type]`, a Go-esque non-resizeable slice with `type` elements with some capacity

Code samples:

```
foo = 10
bar = &foo
*bar = *bar + 20
baz = *bar // baz = 30
```

```
fn factorial(n int) int do
    out = 1
    while n > 0 do
        out = out * n
        n = n - 1
    return out
```