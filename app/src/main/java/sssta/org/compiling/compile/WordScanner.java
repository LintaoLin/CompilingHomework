package sssta.org.compiling.compile;

import android.graphics.Color;
import android.util.ArrayMap;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import sssta.org.compiling.exception.IllegalTypeException;

/**
 * 词法分析器
 */
public class WordScanner {

    private static String digitString = "(\\d)+.{0,1}(\\d)*";

    private static Pattern digitPattern = Pattern.compile(digitString);

    private BufferedReader bufferedReader;

    private char[] stringChars;
    private int wordIndex = 0;
    private int line = 0;

    enum TokenType {
        ORIGIN, SCALE, ROT, IS, TO, STEP, DRAW, FOR, FROM,       //保留字
        T,    //参数
        DIGIT,  //数字
        PLUS, MINUS, MUL, DIV, POWER,  //运算符
        COMMA, //逗号
        L_BREAKET, R_BREAKET, //()
        FUNC, //函数
        CONST_ID,  //常数
        FILE_FIN, //文件结束
        LINE_FIN, //一行结束
        STROKE, //点的宽度
        COLOR //点的颜色
    }

    private static Map<String, Integer> colorMap = new ArrayMap<>();

    static Function<Double, Double> sin = new Function<Double, Double>() {
        @Override
        public Double apply(Double aDouble) {
            return Math.sin(aDouble);
        }
    };

    static Function<Double, Double> cos = Math::cos;

    static Function<Double, Double> tan = Math::tan;

    static Function<Double, Double> sqrt = Math::sqrt;

    static class Token {
        private TokenType type;
        private double value;
        private Function function;

        public Token(TokenType type, double value, Function function) {
            this.type = type;
            this.value = value;
            this.function = function;
        }

        public TokenType getType() {
            return type;
        }

        public double getValue() {
            return value;
        }

        public Function getFunction() {
            return function;
        }
    }

    private static Map<String, Token> tokenMap = new HashMap<>();

    static {
        tokenMap.put("PI", new Token(TokenType.CONST_ID, 3.1415926, null));
        tokenMap.put("E", new Token(TokenType.CONST_ID, 2.71828, null));
        tokenMap.put("T", new Token(TokenType.T, 0, null));
        tokenMap.put("TO", new Token(TokenType.TO, 0, null));
        tokenMap.put("SIN", new Token(TokenType.FUNC, 0, sin));
        tokenMap.put("COS", new Token(TokenType.FUNC, 0, cos));
        tokenMap.put("TAN", new Token(TokenType.FUNC, 0, tan));
        tokenMap.put("SQRT", new Token(TokenType.FUNC, 0, sqrt));
        tokenMap.put("ORIGIN", new Token(TokenType.ORIGIN, 0, null));
        tokenMap.put("ROT", new Token(TokenType.ROT, 0, null));
        tokenMap.put("IS", new Token(TokenType.IS, 0, null));
        tokenMap.put("FOR", new Token(TokenType.FOR, 0, null));
        tokenMap.put("FROM", new Token(TokenType.FROM, 0, null));
        tokenMap.put("STEP", new Token(TokenType.STEP, 0, null));
        tokenMap.put("DRAW", new Token(TokenType.DRAW, 0, null));
        tokenMap.put("SCALE", new Token(TokenType.SCALE, 0, null));
        tokenMap.put("(", new Token(TokenType.L_BREAKET, 0, null));
        tokenMap.put(")", new Token(TokenType.R_BREAKET, 0, null));
        tokenMap.put("+", new Token(TokenType.PLUS, 0, null));
        tokenMap.put("-", new Token(TokenType.MINUS, 0, null));
        tokenMap.put("*", new Token(TokenType.MUL, 0, null));
        tokenMap.put("/", new Token(TokenType.DIV, 0, null));
        tokenMap.put(",", new Token(TokenType.COMMA, 0, null));
        tokenMap.put("LINE_FIN", new Token(TokenType.LINE_FIN, 0, null));
        tokenMap.put("COLOR", new Token(TokenType.COLOR, 0, null));
        tokenMap.put("STROKE", new Token(TokenType.STROKE, 0, null));

        colorMap.put("BLACK", Color.BLACK);
        colorMap.put("BLUE", Color.BLUE);
        colorMap.put("CYAN", Color.CYAN);
        colorMap.put("DKGRAY", Color.DKGRAY);
        colorMap.put("GRAY", Color.GRAY);
        colorMap.put("GREEN", Color.GREEN);
        colorMap.put("LTGRAY", Color.LTGRAY);
        colorMap.put("MAGENTA", Color.MAGENTA);
        colorMap.put("RED", Color.RED);
        colorMap.put("WHITE", Color.WHITE);
        colorMap.put("YELLOW", Color.YELLOW);
    }

    static ArrayList<TokenType> operationArray = new ArrayList<>();

    static {
        operationArray.add(TokenType.PLUS);
        operationArray.add(TokenType.MINUS);
        operationArray.add(TokenType.MUL);
        operationArray.add(TokenType.DIV);
    }

    public WordScanner(File file) {
        openFile(file);
    }

    private void openFile(File file) {
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Token getWordToken() throws IllegalTypeException {
        Token token;
        if (stringChars == null || wordIndex > stringChars.length) {
            String s;
            try {
                while ((s = bufferedReader.readLine()) != null) {
                    line++;
                    if (s.length() > 0) {
                        stringChars = s.toUpperCase().toCharArray();
                        wordIndex = 0;
                        break;
                    }
                }
                if (s == null) {
                    token = new Token(TokenType.FILE_FIN, 0, null);
                    bufferedReader.close();
                    return token;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (wordIndex == stringChars.length) {
            wordIndex++;
            token = tokenMap.get(TokenType.LINE_FIN.name());
            return token;
        }

        StringBuilder stringBuilder = new StringBuilder();
        boolean isDigit = false;
        for (; wordIndex < stringChars.length; wordIndex++) {
            boolean isFinished = false;
            if (stringChars[wordIndex] == ' ') {
                if (stringBuilder.length() == 0) {
                    continue;
                } else {
                    break;
                }
            }

            switch (stringChars[wordIndex]) {
                case '*':
                case '+':
                case '-':
                case '/':
                case '(':
                case ')':
                case ',':
                    if (stringBuilder.length() == 0) {
                        stringBuilder.append(stringChars[wordIndex]);
                        wordIndex++;
                    }
                    isFinished = true;
                    break;
            }

            if (isFinished) {
                break;
            }

            if (stringChars[wordIndex] >= '0' && stringChars[wordIndex] <= '9') {
                if (stringBuilder.length() == 0) {
                    isDigit = true;
                }
                stringBuilder.append(stringChars[wordIndex]);
            } else {
                if (isDigit) {
                    throw new IllegalTypeException();
                } else {
                    stringBuilder.append(stringChars[wordIndex]);
                }
            }
        }

        String word = stringBuilder.toString();
        Matcher matcher = digitPattern.matcher(word);
        if (matcher.matches()) {
            double value = Double.valueOf(word.toString());
            if (value < 0) {
                throw new IllegalTypeException();
            }
            token = new Token(TokenType.DIGIT, value, null);
        } else if (colorMap.containsKey(word)) {
            token = new Token(TokenType.DIGIT, colorMap.get(word), null);
        } else {
            if (tokenMap.containsKey(word)) {
                token = tokenMap.get(word);
            } else {
                throw new RuntimeException("unsolved type " + word + " in line " + line);
            }
        }
        return token;
    }
}
