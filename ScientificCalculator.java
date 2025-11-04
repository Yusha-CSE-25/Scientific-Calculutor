/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.scientificcalculator;

/**
 *
 * @author yousha
 */
import java.util.*;
import java.io.*;

public class ScientificCalculator {

    /* ---------- Token Types ---------- */
    private enum TokenType { NUMBER, OPERATOR, FUNCTION, LEFT_PAREN, RIGHT_PAREN }

    /** Simple token holder **/
    private static class Token {
        final TokenType type;
        final String value;          // e.g. "sin", "+", "3.14"
        Token(TokenType t, String v) { this.type = t; this.value = v; }
    }

    /* ---------- Operator Properties ---------- */
    private static class OpInfo {
        int precedence;
        boolean rightAssociative;
        int arity;                    // 1 or 2
        OpInfo(int p, boolean r, int a){ precedence=p; rightAssociative=r; arity=a;}
    }
    private static final Map<String,OpInfo> OPERATORS = new HashMap<>();
    static {
        OPERATORS.put("+", new OpInfo(2,false,2));
        OPERATORS.put("-", new OpInfo(2,false,2));
        OPERATORS.put("*", new OpInfo(3,false,2));
        OPERATORS.put("/", new OpInfo(3,false,2));
        OPERATORS.put("^", new OpInfo(4,true ,2));   // right‑associative
    }

    /* ---------- Supported Functions ---------- */
    private static final Set<String> FUNCTIONS = new HashSet<>(Arrays.asList(
            "sin","cos","tan",
            "asin","acos","atan",
            "log","ln","exp","sqrt","abs","fact"
    ));

    /* ---------- Main Entry Point ---------- */
    public static void main(String[] args) throws IOException {
        System.out.println("Scientific Calculator (type 'exit' to quit)");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String line = br.readLine();
            if (line == null || line.trim().equalsIgnoreCase("exit")) break;
            try {
                double result = evaluate(line);
                System.out.println(result);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    /* ---------- Public API: Evaluate a string expression ---------- */
    public static double evaluate(String expr) throws Exception {
        List<Token> rpn = shuntingYard(tokenize(expr));
        return evalRPN(rpn);
    }

    /* ---------- Tokenizer: split input into tokens ---------- */
    private static List<Token> tokenize(String expr) throws Exception {
        List<Token> tokens = new ArrayList<>();
        int i=0;
        while (i<expr.length()){
            char ch = expr.charAt(i);

            if (Character.isWhitespace(ch)) { i++; continue; }

            /* Number (int or decimal) */
            if (Character.isDigit(ch) || ch=='.'){
                int start=i;
                while (i<expr.length() && (Character.isDigit(expr.charAt(i))|| expr.charAt(i)=='.')) i++;
                tokens.add(new Token(TokenType.NUMBER, expr.substring(start,i)));
                continue;
            }

            /* Identifier: function name or variable?  Only functions are supported. */
            if (Character.isLetter(ch)){
                int start=i;
                while (i<expr.length() && Character.isLetter(expr.charAt(i))) i++;
                String id = expr.substring(start,i).toLowerCase();
                if (FUNCTIONS.contains(id))
                    tokens.add(new Token(TokenType.FUNCTION, id));
                else
                    throw new Exception("Unknown function: "+id);
                continue;
            }

            /* Operators and parentheses */
            switch(ch){
                case '+': case '-': case '*': case '/': case '^':
                    tokens.add(new Token(TokenType.OPERATOR, String.valueOf(ch)));
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LEFT_PAREN, "("));
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_PAREN, ")"));
                    break;
                default:
                    throw new Exception("Unexpected character: "+ch);
            }
            i++;
        }
        return tokens;
    }

    /* ---------- Shunting‑Yard algorithm → RPN list ---------- */
    private static List<Token> shuntingYard(List<Token> tokens) throws Exception {
        List<Token> output = new ArrayList<>();
        Deque<Token> stack  = new ArrayDeque<>();

        for (Token t : tokens){
            switch(t.type){
                case NUMBER:
                    output.add(t);
                    break;
                case FUNCTION:
                    stack.push(t);
                    break;
                case OPERATOR:
                    while (!stack.isEmpty()){
                        Token top = stack.peek();
                        if ((top.type==TokenType.OPERATOR &&
                            ( (OPERATORS.get(top.value).precedence > OPERATORS.get(t.value).precedence) ||
                              (OPERATORS.get(top.value).precedence == OPERATORS.get(t.value).precedence &&
                               !OPERATORS.get(t.value).rightAssociative) ))
                        ){
                            output.add(stack.pop());
                        } else break;
                    }
                    stack.push(t);
                    break;
                case LEFT_PAREN:
                    stack.push(t);
                    break;
                case RIGHT_PAREN:
                    while (!stack.isEmpty() && stack.peek().type != TokenType.LEFT_PAREN){
                        output.add(stack.pop());
                    }
                    if (stack.isEmpty())
                        throw new Exception("Mismatched parentheses");
                    stack.pop(); // pop '('
                    if (!stack.isEmpty() && stack.peek().type==TokenType.FUNCTION)
                        output.add(stack.pop());   // function after its argument
                    break;
            }
        }

        while (!stack.isEmpty()){
            Token top = stack.pop();
            if (top.type==TokenType.LEFT_PAREN || top.type==TokenType.RIGHT_PAREN)
                throw new Exception("Mismatched parentheses");
            output.add(top);
        }
        return output;
    }

    /* ---------- Evaluate RPN expression ---------- */
    private static double evalRPN(List<Token> rpn) throws Exception {
        Deque<Double> stack = new ArrayDeque<>();

        for (Token t : rpn){
            switch(t.type){
                case NUMBER:
                    stack.push(Double.parseDouble(t.value));
                    break;
                case OPERATOR:
                    if (stack.size()<2) throw new Exception("Insufficient operands");
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(applyOperator(a,b,t.value));
                    break;
                case FUNCTION:
                    if (stack.isEmpty()) throw new Exception("No operand for function "+t.value);
                    double arg = stack.pop();
                    stack.push(applyFunction(arg, t.value));
                    break;
                default:
                    throw new Exception("Invalid token in RPN: "+t.value);
            }
        }

        if (stack.size()!=1) throw new Exception("Malformed expression");
        return stack.pop();
    }

    /* ---------- Helper: apply operator ---------- */
    private static double applyOperator(double a, double b, String op){
        switch(op){
            case "+" : return a + b;
            case "-" : return a - b;
            case "*" : return a * b;
            case "/" :
                if (b==0) throw new ArithmeticException("Division by zero");
                return a / b;
            case "^" : return Math.pow(a,b);
        }
        throw new IllegalArgumentException("Unknown operator: "+op);
    }

    /* ---------- Helper: apply function ---------- */
    private static double applyFunction(double arg, String fn){
        switch(fn){
            case "sin":   return Math.sin(Math.toRadians(arg));
            case "cos":   return Math.cos(Math.toRadians(arg));
            case "tan":   return Math.tan(Math.toRadians(arg));

            case "asin":  return Math.toDegrees(Math.asin(arg));
            case "acos":  return Math.toDegrees(Math.acos(arg));
            case "atan":  return Math.toDegrees(Math.atan(arg));

            case "log":   return Math.log10(arg);
            case "ln":    return Math.log(arg);

            case "exp":   return Math.exp(arg);
            case "sqrt":  {
                if (arg<0) throw new ArithmeticException("sqrt of negative");
                return Math.sqrt(arg);
            }
            case "abs":   return Math.abs(arg);

            case "fact":
                if (arg < 0 || arg != Math.floor(arg))
                    throw new IllegalArgumentException("factorial requires non‑negative integer");
                long n = (long)arg;
                return factorial(n);

            default:
                throw new IllegalArgumentException("Unknown function: "+fn);
        }
    }

    /* ---------- Factorial for integers (up to 20!) ---------- */
    private static double factorial(long n){
        if (n==0 || n==1) return 1;
        double res=1;
        for (long i=2;i<=n;i++) res*=i;
        return res;
    }
}

