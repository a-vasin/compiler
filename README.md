# Compiler
Compiler from C-like programming language to LLVM
## Language description
PL has static typing with simple datatypes and functions.

Entry point is main function with type int:
```
int main(){}
```

Full grammar for PL is available [here](https://github.com/a-vasin/compiler/blob/master/src/main/java/ProgrammingLanguage.g4)
### Datatypes
* int - standard 32-bit integer number
* bool - boolean with two possible values: true and false
* string - string value with access to characters by index
* void - type for function, which returns no value

### Operators
There is only a brief overview. Please, see examples for more.
* while - standard while cycle, which keeps running until condition is true. Supports break and continue operators
* if/then/else - conditional operator with optional else clause
* switch - conditional operator for choice based on value equality
* union - operator for uniting bits for different variables

### Built-in functions
Again only a brief everview. Please, see examples for more.
* read - read values from standard input
* write - write value to standard output (please, use \0A for new lines)
* length - returns length of a string
* ==, !=, &&, || - logic operators
* +, -, *, /, \% - arithmetic operators

## Compiler optimizations
Same thing with examples =)
* forward propagation - invokate function before declaration
* constant folding - fold simple arithmetic expressions to constant value

## Examples
All code examples listed in example_*.txt files for ability to compile and run them.

Compiler usage examples:
```
compiler.jar input.txt output.txt
```

input.txt - input code

output.txt - produced LLVM code

Please, use [lli](https://releases.llvm.org/5.0.0/docs/CommandGuide/lli.html) for running LLVM code.
