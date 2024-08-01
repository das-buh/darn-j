# Darn-J: A Toy Interpreter in Java

Yep, that's what it is.

- Runtime-checked static typing for function parameters and returns
    - Function parameters and returns are manifest-typed
- Variable declaration and assignment have one syntax (Python-esque)
- All variables are function-scoped; functions cannot access variables outside their scope
    - Function declarations must be in global scope
- Strings are immutable and non-resizeable
- Nil-able types must be marked with `?`
- Compound types include:
    - `type?`, nil-able `type`, can be either `type` or `nil`
    - `type*`, reference to `type` (values, except for lists, are pass-by-value)
- No implicit type casts (save interpreter-internal casts)

(there may be bugs, i havent completely thoroughly tested it yet)

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