# COSC 455

Programming Languages: Design and Implementation
## Project 1

A Lexical Analyzer that prints out the position, kind, and value of each Lexeme in a text file, using a given grammar.

Simply run the file `Lexer.java` and input a file name

#### Example

```java
Enter the name of the text file you want to read (Ex. 'tricky'.txt):
if

1:1:'program'
1:9:'ID' condtional
1:19:':'
2:3:'if'
2:6:'NUM' 3
2:8:'<'
2:10:'NUM' 3
2:12:'then'
3:5:'print'
3:11:'false'
4:3:'fi'
5:1:'end'
5:4:'end-of-text'
```

## Project 2

A top-down, predictive, recursive descent parser that checks for syntax errors in a given grammar.

Simply run the file `Parser.java` and input a file name

#### Example

```java
Enter the name of the text file you want to read (Ex. 'tricky'.txt):
ab3
        
../examples/ab3.txt(5:5)>>>>>Bad symbol ':': expected [:=]
a : = 2;
  ^
```
##### Note: All runnable files must be located in the /src folder and all examples must be located in the /examples folder