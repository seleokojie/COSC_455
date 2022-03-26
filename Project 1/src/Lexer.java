/** Lexer.java
 *
 * This program reads in a file and tokenizes it, that is it prints out the
 * position, kind and value of each Lexeme, using the given grammar.
 *
 * Note: No libraries/modules such as regex are used in this program.
 *
 * COSC 455.002
 * Sele Okojie
 * 3/22/2022
 **/

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Lexer{
    // The scanner that reads the file
    static Scanner sc;

    static {
        try {
            sc = new Scanner(new File("examples/tricky.txt")); //The file to be read
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //All the key grammatical symbols we will reference to match the grammar
    static List<String> letters = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
            "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z");
    static List<String> numbers = List.of("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");

    static List<String> keywords = List.of("program", "bool", "int", "if", "then", "else", "fi", "while", "do", "od", "print", "or", "and", "not", "true", "false", "end-of-text", "end");
    static List<String> specialChars = List.of("<", ">", "(", ")", "=", "!", "*", "/", "+", "-", ":", ";");
    static List<String> multiDigitSpecialChars = List.of("//", "<=", ">=", "!=", ":=");

    private static final String underscore = "_";
    private static final String comment = "//";
    private static final String newline = "\n";
    private static final String whitespace = " ";
    private static final String tab = "\t";

    static int firstPos; //Returns the position of the first character for multi-character chars
    static int pos = 1; //The position of the token in a line
    static int lineNum = 0; //The line number of the token
    static String[] line; //The current line
    static String kind = ""; //The kind of token ("ID", "NUM", value)
    static String value = ""; //The characters of the token

    //Constructor
    public Lexer(int line, int pos, String kind, String value){
        Lexer.pos = pos;
        Lexer.lineNum = line;
        Lexer.kind = kind;
        Lexer.value = value;
    }

    public static void setLexer(int line, int pos, String kind, String value){
        Lexer.pos = pos;
        Lexer.lineNum = line;
        Lexer.kind = kind;
        Lexer.value = value;
    }

    //Prints out the line number and position of the illegal token
    public static void illegalChar(String c){
        System.out.println(lineNum() + ":" + (pos+1) + ":>>>>> Illegal character '" + c + "'");
    }

    //Check if the current sequence of characters is an Identifier
    public static void recognizeID(int lineNum, int pos){
        int position = firstPos = pos;
        kind = "ID";
        value = "";

        StringBuilder newValue = new StringBuilder(); //We use a StringBuilder to avoid concatenating strings
        while (position+1 <= line.length && (letters.contains(line[position]) || numbers.contains(line[position]) || underscore.equals(line[position]))){
            newValue.append(line[position]);
            position++;
        }
        value = newValue.toString();

        if (keywords.contains(value)) kind = value;

        setLexer(lineNum, position, kind, value);
    }

    //Checks if the current sequence of characters is a Number
    public static void recognizeNum(int lineNum, int pos){
        int position = firstPos = pos;
        kind = "NUM";
        value = "";

        StringBuilder newValue = new StringBuilder(); //We use a StringBuilder to avoid concatenating strings
        while (position+1 <= line.length && numbers.contains(line[position])){
            newValue.append(line[position]);
            position++;
        }
        value = newValue.toString();
        setLexer(lineNum, position, kind, value);
    }

    //Checks if the current sequence of characters is a Special Character
    public static void recognizeSpecialChar(int lineNum, int pos){
        int position = firstPos = pos;
        value = line[pos];

        //Check if the current sequence is a Multi-Character Special Character
        if (position+1 < line.length && multiDigitSpecialChars.contains(value + line[position+1]) && !(line[position+1].equals(newline) || line[position+1].equals(whitespace))) {
            value = line[pos] + line[pos+1];
            if(value.equals(comment)) {
                setLexer(lineNum, pos, kind = comment, value = comment);
                line = new String[]{};
                return;
            }
            position += 2;
        }else if (!value.equals("!")) position++;
        else {
            illegalChar(value);
            System.exit(0);
        }
        pos = position;
        setLexer(lineNum, pos, value, value);
    }

    //Reads the next lexeme in the input file
    public static void next(){
        if (sc.hasNextLine() || pos < line.length) {
            while (pos < line.length && (line[pos].equals(whitespace) || line[pos].equals(newline) || line[pos].equals(tab))) //White space recognition
                pos++;

            //Takes in the current symbol and checks if it is a keyword, ID, number or special character
            String current = line[pos];
            if (letters.contains(current)) recognizeID(lineNum, pos);
            else if (numbers.contains(current)) recognizeNum(lineNum, pos);
            else if (specialChars.contains(current)) recognizeSpecialChar(lineNum, pos);

            else {
                illegalChar(current);
                System.exit(0);
            }
        } else {
            firstPos = pos;
            setLexer(lineNum, firstPos, "end-of-text", "end-of-text");
        }

    }

    public static void printLexer(){
        if (kind.equals(value)) System.out.println(lineNum() + ":" + position() + ":'" + value() + "'");
        else System.out.println(lineNum() + ":" + position() + ":'" + kind() + "' " + value());
    }

    public static int lineNum(){ return lineNum; }

    public static int position(){ return firstPos + 1; }

    public static String kind(){ return kind; }

    public static String value(){ return value; }

    public static void main(String[] args){
        while (sc.hasNextLine()) {
            lineNum++;

            //This section of code skips blank newlines
            String tempLine = sc.nextLine();
            if(tempLine.length() == 0) continue;
            line = new String[tempLine.length()];

            //Ideally, I'd like to use String.split() to split the line into an array of characters, but that method uses regex, which is not allowed in this project
            for(int i = 0; i < tempLine.length(); i++)
                line[i] = tempLine.charAt(i) + "";

            pos = 0;
            while (pos < line.length) {
                next();
                printLexer();
            }
        }
        next(); //Prints the end-of-text token
        printLexer();
    }
}