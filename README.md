# Darn-J: A Toy Interpreter in Java

Yep, that's what it is.

- Significant indentation (no tabs, spaces only)
- Runtime-checked function types
- No implicit type casts
- Blocks evaluate to their last statement

(there may be bugs, i havent completely thoroughly tested it yet)

Code sample:

```
fn factorial(n int) int do
    out = 1
    while n > 0 do
        out = out * n
        n = n - 1
    return out
```

Features:

- Types (only appear in function parameters and returns)
    - `int`
    - `float`
    - `bool`
    - `str`
    - `list`
    - `nil` (unit type)
        - Values of type `nil` are instantiated via keyword `nil`
        - Function parameters are never type `nil`, but can be nil-able (`T?`)
            - Nil-able type `T?` is a supertype of type `T` and type `nil`
    - References (`T*`)
        - References to variables are created via prefix `&`, ie. `&foo`
        - References are dereferenced via prefix `*`, ie. `*foo`
    - `undefined`
        - Only exists interpreter-internally
        - Returned from variable assignment or function with no return-type annotation 
        - Never annotated or instantiated by the user
- Builtins
    - Operator precedence (greatest to least)
        1. `()` (function call)
        2. `- * &` (unary)
        3. `* / %`
        4. `+ -`
        5. `== != < > <= >=`
        6. `not` (unary)
        7. `and`
        8. `or`
    - Functions
        - `print(str)`, prints to stdout
        - `fmt(any) str`, formats a value into a string representation
	        - For strings, this adds delimiting `"` to the string
	        - Panics if supplied type `undefined`
		- `concat(str, ...) str`, concatenates one or more strings
		- `substr(str, int, int) str?`, gets a substring between two indices from a string
		- `push(list, any)`, pushes a value to the end of a list
		- `pop(list) any`, pops a value from the end of a list
		- `idx(list, int) any*?`, indexes into a list and returns a reference to the element
		- `len(list) int`, `len(str) int`, gets the length of a list or string
		- `swap(any*, any*)`, swaps the referent values of two references
		- `put(any*, any) any`, sets the referent value of a reference and returns the old referent value
		- `assert(bool)`, panics if the argument is `false`
		- `throw(str)`, panics with the argument string as the error message

```
# This is a comment.

# This is a variable assignment.
# Assignment to an uninitialized variable is declaration.
# Variables are not type checked.
my_var = 10

# This is a function.
# Function parameters and returns are type checked.
# Functions cannot access outside variables.
fn my_function(my_param int?) int do
    # This is an if-statement.
    # If-statements evaluate to values.
    return if my_param != nil do my_param else 0

# Non-string values must be `fmt`'d before printing.
foo = my_function(10)
bar = my_function(nil)
sum = foo + bar
print(concat("sum: ", fmt(sum)))

# This is a do-block.
# Unless they are the body of a function,
# blocks evaluate to the value of their last statement.
# Blocks do not define a new scope.
thirty = do
    foo = 10
    bar = 20
    foo + bar

# This is an assertion.
assert(thirty == 30)
```