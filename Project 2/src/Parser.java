//a topdown, predictive, recursive descent parser for it.
//The parser should take an input file from the command line. Then, Lexer should be called to tokenize the input file.
//The parser should then call the AST constructor to create the AST.
//The parser should then call the AST.print() method to print the AST
//As soon as a syntax error is encountered the parser should stop (terminate execution) and
//return the position of the error.
//The parser should also print the line number and the line of the input file where the error was encountered.

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Parser {
    private Lexer lexer;
    private String currentToken;
    static Scanner sc;

    public Parser(File file) throws FileNotFoundException {
        //lexer = new Lexer(file);
        //lexer.next();
    }

    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Enter the name of the text file you want to read (Ex. 'tricky'.txt):");
        Lexer lexer = new Lexer(new File("Project 1/examples/" + new Scanner(System.in).nextLine() + ".txt"));

        while (!Lexer.kind().equals("end-of-text")){
            Lexer.next();
            Lexer.printLexer();
        }
    }
}