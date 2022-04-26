import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class MainTester {
    public static void main(String[] args) throws FileNotFoundException {
        System.out.println("Enter the name of the text file you want to read (Ex. 'tricky'.txt):");
        /*new Lexer(new File("Project 1/examples/" + new Scanner(System.in).nextLine() + ".txt"));

        while (!Lexer.kind().equals("end-of-text")){
            Lexer.next();
            Lexer.printLexer();
        }*/

        Parser parser = new Parser(new File("Project 1/examples/" + new Scanner(System.in).nextLine() + ".txt"));
        Parser.program();
    }
}
