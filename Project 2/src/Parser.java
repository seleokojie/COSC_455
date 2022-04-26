//a topdown, predictive, recursive descent parser for it.
//The parser should take an input file from the command line. Then, lex should be called to tokenize the input file.
//The parser should then call the AST constructor to create the AST.
//The parser should then call the AST.print() method to print the AST
//As soon as a syntax error is encountered the parser should stop (terminate execution) and
//return the position of the error.
//The parser should also print the line number and the line of the input file where the error was encountered.

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {
    static Lexer lex;
    public Parser(File file) throws FileNotFoundException {
        Lexer lex = new Lexer(file);
        lex.next();
    }

    public static void match(String value){
        if (lex.kind().equals(value)) {
            lex.next();
        }
        else {
            System.out.println("(" + lex.lineNum() + ":" + lex.position() + ")>>>>>Expected \"" + value + "\" but found \"" + lex.value() + "\"");
            System.exit(0);
        }
    }

    //Program  =  "program"  Identifier  ":"  Body  "end"
    //Identifier  =  Letter { Letter | Digit | "_" } `This is done by the lex`
    public static void program() {
        //Stops the program if the end-of-file token is reached
        if (!lex.kind().equals("end-of-text")) {
            match("program");
            match("ID");
            match(":");
            body();
            match("end");
        }
        System.out.println(true);
    }

    //Body = [ Declarations ] Statements
    public static void body() {
        if (lex.value().equals("bool") || lex.value().equals("int"))
            declarations();
        statements();
    }

    //Declarations  =  Declaration { Declaration }
    public static void declarations() {
        declaration();
        while (lex.value().equals("bool") || lex.value().equals("int"))
            declaration();
    }

    //Declaration  =  ( "bool" | "int" )  Identifier ";"
    public static void declaration() {
        if (lex.value().equals("bool"))
            match("bool");
        else
            match("int");
        match("ID");
        match(";");
    }

    //Statements  =  Statement { ";" Statement }
    public static void statements() {
        statement();
        while (lex.value().equals(";")) {
            match(";");
            statement();
        }
    }

    /* Statement  =  AssignmentStatement
                  |  ConditionalStatement
                  |  IterativeStatement
                  |  PrintStatement .
    */
    public static void statement() {
        switch (lex.kind()) {
            case "ID" -> assignmentStatement();
            case "if" -> conditionalStatement();
            case "while" -> iterativeStatement();
            case "print" -> printStatement();
            default -> expected(Arrays.asList("if", "ID", "while", "print"));
        }
    }

    //This function takes a list if the token type is not in that list
    //it will raise an error returning the position and details of the error.
    public static void expected(List list) {
        if (!list.contains(lex.value())) {
            System.out.println("(" + lex.lineNum() + ":" + lex.position() + ")>>>>> Error: Expected " + list + " but found " + lex.value() + "\"");
            System.exit(0);
        }
    }

    //AssignmentStatement  =  Identifier  ":="  Expression
    public static void assignmentStatement() {
        match("ID");
        //To avoid the error of ": =" when the user types ": =" instead of ":=".
        if (lex.value().equals(":")) {
            match(":");
            match("=");
        }else if(lex.value().equals(":="))
            match(":=");
        expression();
    }

    //ConditionalStatement  =  "if"  Expression  "then"  Body  [ "else" Body ]  "fi"
    public static void conditionalStatement() {
        match("if");
        expression();
        match("then");
        body();
        if (lex.value().equals("else")) {
            lex.next();
            body();
        }
        match("fi");
    }

    //IterativeStatement  =  "while"  Expression  "do"  Body  "od"
    public static void iterativeStatement() {
        match("while");
        expression();
        match("do");
        body();
        match("od");
    }

    //PrintStatement  =  "print"  Expression
    public static void printStatement() {
        match("print");
        expression();
    }

    //Expression  =  SimpleExpression [ RelationalOperator SimpleExpression ]
    //RelationalOperator  =  "<" | "=<" | "=" | "!=" | ">=" | ">"
    public static void expression() {
        simpleExpression();
        if (lex.value().equals("<") || lex.value().equals("=<") || lex.value().equals("=") || lex.value().equals("!=") || lex.value().equals(">=") || lex.value().equals(">")) {
            lex.next();
            simpleExpression();
        }
    }

    //SimpleExpression  =  Term { AdditiveOperator Term }
    //AdditiveOperator  =  "+" | "-" | "or"
    public static void simpleExpression() {
        term();
        while (lex.value().equals("+") || lex.value().equals("-") || lex.value().equals("or")) {
            lex.next();
            term();
        }
    }

    //Term  =  Factor { MultiplicativeOperator Factor }
    //MultiplicativeOperator  =  "*" | "/" | "and"
    public static void term() {
        factor();
        while (lex.value().equals("*") || lex.value().equals("/") || lex.value().equals("and")) {
            lex.next();
            factor();
        }
    }

    //Factor  =  [ UnaryOperator ] ( Literal  |  Identifier  | "(" Expression ")" )
    //UnaryOperator  =  "-" | "not"
    public static void factor() {
        if (lex.value().equals("-") || lex.value().equals("not"))
            lex.next();
        switch (lex.kind()) {
            case "true", "false", "NUM" -> literal();
            case "ID" -> lex.next();
            case "(" -> {
                lex.next();
                expression();
                match(")");
            }
            default -> expected(Arrays.asList("true", "false", "NUM", "ID", "("));
        }
    }

    //Literal  =  BooleanLiteral  |  IntegerLiteral
    //IntegerLiteral  =  Digit { Digit } `This is done by the lex`

    public static void literal() {
        if(lex.kind().equals("NUM")) {
            lex.next();
            if (!(Arrays.asList(";", ")", "<", ">", "<=", ">=", "!=", "=", "+", "-", "or", "*", "/", "and", "do", "od", "fi", "then").contains(lex.value())))
                expected(List.of(";"));
        }else
            booleanLiteral();
    }

    //BooleanLiteral  =  "true"  |  "false"
    public static void booleanLiteral() {
        if (lex.value().equals("true") || lex.value().equals("false"))
            lex.next();
        else
            expected(Arrays.asList("true", "false"));
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Enter the name of the text file you want to read (Ex. 'tricky'.txt):");
        lex = new Lexer(new File("Project 1/examples/" + new Scanner(System.in).nextLine() + ".txt"));

        while (!lex.value().equals("end-of-text")){
            program();
        }
    }
}