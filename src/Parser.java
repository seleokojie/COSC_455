/** Parser.java
 *
 * This program will read the tokens parsed from the Lexer.java program and
 * check for the correct syntax based on the given grammar.
 *
 * Note: No libraries/modules that support regular expressions such as regex are used in this program.
 * Note: All input files are assumed to be in the 'examples' directory.
 *
 * @author: Sele Okojie
 * @version: 1.1.2
 * @date: 2022-04-26
 * @email: eokoji1@students.towson.edu
 * @github: https://github.com/seleokojie/COSC455-Project-2
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Parser {
    static String filename;
    public static void defineParser(String fileName) throws FileNotFoundException {
        filename = fileName;
        Lexer.defineLexer(new File("examples/" + fileName + ".txt"));
        Lexer.next();
    }

    public static void match(String value){
        if (Lexer.kind().equals(value))
            Lexer.next();
        else
            expected(List.of(value));
    }

    //Program  =  "program"  Identifier  ":"  Body  "end"
    //Identifier  =  Letter { Letter | Digit | "_" } `This is done by the lexer`
    public static void program() {
        //Stops the program if the end-of-file token is reached
        if (!Lexer.kind().equals("end-of-text")) {
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
        if (Lexer.value().equals("bool") || Lexer.value().equals("int"))
            declarations();
        statements();
    }

    //Declarations  =  Declaration { Declaration }
    public static void declarations() {
        declaration();
        while (Lexer.value().equals("bool") || Lexer.value().equals("int"))
            declaration();
    }

    //Declaration  =  ( "bool" | "int" )  Identifier ";"
    public static void declaration() {
        if (Lexer.value().equals("bool"))
            match("bool");
        else
            match("int");
        match("ID");
        match(";");
    }

    //Statements  =  Statement { ";" Statement }
    public static void statements() {
        statement();
        while (Lexer.value().equals(";")) {
            match(";");
            statement();
        }
    }

    /* Statement  =  AssignmentStatement
                  |  ConditionalStatement
                  |  IterativeStatement
                  |  PrintStatement .*/

    public static void statement() {
        switch (Lexer.kind()) {
            case "ID" -> assignmentStatement();
            case "if" -> conditionalStatement();
            case "while" -> iterativeStatement();
            case "print" -> printStatement();
            default -> expected(Arrays.asList("if", "ID", "while", "print"));
        }
    }

    //This function takes a list if the token type is not in that list
    //it will raise an error returning the position and details of the error.
    public static void expected(List<String> list) {
        if (!list.contains(Lexer.value())) {
            System.out.println("../examples/" + filename + ".txt(" + Lexer.lineNum() + ":" + Lexer.position() + ")>>>>>Bad symbol '" + Lexer.value() + "': expected " + list);

            //Prints out the line and then a caret at the position of the error
            for(String x : Lexer.line)
                System.out.print(x);
            System.out.println();

            //Prints out the caret at the position of the error
            for(int i = 0; i < Lexer.position()-1; i++)
                System.out.print(" ");
            System.out.println("^");

            System.exit(0);
        }
    }

    //AssignmentStatement  =  Identifier  ":="  Expression
    public static void assignmentStatement() {
        match("ID");
        match(":=");
        expression();
    }

    //ConditionalStatement  =  "if"  Expression  "then"  Body  [ "else" Body ]  "fi"
    public static void conditionalStatement() {
        match("if");
        expression();
        match("then");
        body();
        if (Lexer.value().equals("else")) {
            Lexer.next();
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
        if (Lexer.value().equals("<") || Lexer.value().equals("=<") || Lexer.value().equals("=") || Lexer.value().equals("!=") || Lexer.value().equals(">=") || Lexer.value().equals(">")) {
            Lexer.next();
            simpleExpression();
        }
    }

    //SimpleExpression  =  Term { AdditiveOperator Term }
    //AdditiveOperator  =  "+" | "-" | "or"
    public static void simpleExpression() {
        term();
        while (Lexer.value().equals("+") || Lexer.value().equals("-") || Lexer.value().equals("or")) {
            Lexer.next();
            term();
        }
    }

    //Term  =  Factor { MultiplicativeOperator Factor }
    //MultiplicativeOperator  =  "*" | "/" | "and"
    public static void term() {
        factor();
        while (Lexer.value().equals("*") || Lexer.value().equals("/") || Lexer.value().equals("and")) {
            Lexer.next();
            factor();
        }
    }

    //Factor  =  [ UnaryOperator ] ( Literal  |  Identifier  | "(" Expression ")" )
    //UnaryOperator  =  "-" | "not"
    public static void factor() {
        if (Lexer.value().equals("-") || Lexer.value().equals("not"))
            Lexer.next();
        switch (Lexer.kind()) {
            case "true", "false", "NUM" -> literal();
            case "ID" -> Lexer.next();
            case "(" -> {
                Lexer.next();
                expression();
                match(")");
            }
            default -> expected(Arrays.asList("true", "false", "NUM", "ID", "("));
        }
    }

    //Literal  =  BooleanLiteral  |  IntegerLiteral
    //IntegerLiteral  =  Digit { Digit } `This is done by the lexer`
    public static void literal() {
        if(Lexer.kind().equals("NUM")) {
            Lexer.next();
            if (!(Arrays.asList(";", ")", "<", ">", "<=", ">=", "!=", "=", "+", "-", "or", "*", "/", "and", "do", "od", "fi", "then").contains(Lexer.value())))
                expected(List.of(";"));
        }else
            booleanLiteral();
    }

    //BooleanLiteral  =  "true"  |  "false"
    public static void booleanLiteral() {
        if (Lexer.value().equals("true") || Lexer.value().equals("false"))
            Lexer.next();
        else
            expected(Arrays.asList("true", "false"));
    }

    public static void main(String[] args) {
        System.out.println("Enter the name of the text file you want to read (Ex. 'tricky'.txt):");
        try{
            filename = new Scanner(System.in).nextLine();
            defineParser(filename);
            program();
        } catch (FileNotFoundException e){
            System.out.println("File not found.");
            System.exit(0);
        }
    }
}