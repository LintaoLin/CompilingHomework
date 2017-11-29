package sssta.org.compiling.compile;

import android.graphics.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import sssta.org.compiling.Application;
import sssta.org.compiling.exception.IllegalTypeException;
import sssta.org.compiling.exception.SyntaxErrorException;

public class SyntaxParsing {

    public interface OnParsingFinishedListener {
        void onFinished(List<ViewParams> list);
    }

    private WordScanner wordScanner;
    private WordScanner.Token token;
    ViewParams.Origin origin;
    ViewParams.Scale scale;
    private int strokeWidth = 2;
    private int color = Color.DKGRAY;

    private OnParsingFinishedListener onParsingFinishedListener;

    public SyntaxParsing(OnParsingFinishedListener onParsingFinishedListener) {
        this.onParsingFinishedListener = onParsingFinishedListener;
    }

    public void setOnParsingFinishedListener(OnParsingFinishedListener onParsingFinishedListener) {
        this.onParsingFinishedListener = onParsingFinishedListener;
    }

    private List<ViewParams> viewParamsList = new ArrayList<>();

    private void clear() {
        strokeWidth = 2;
        color = Color.DKGRAY;
        viewParamsList.clear();
    }

    public void parse() throws IllegalTypeException, SyntaxErrorException, FileNotFoundException {
        File file = new File(Application.mInstance.getAppDirPath(), Application.fileName);

        if (!file.exists()) {
            throw new FileNotFoundException("file not exist");
        }
        clear();
        wordScanner = new WordScanner(file);
        fetchToken();

        origin = new ViewParams.Origin(0, 0);
        scale = new ViewParams.Scale(1, 1);
        while (token.getType() != WordScanner.TokenType.FILE_FIN) {

            switch (token.getType()) {
                case ORIGIN:
                    matchOrigin();
                    break;
                case FOR:
                    matchFor();
                    break;
                case SCALE:
                    matchScale();
                    break;
                case STROKE:
                    matchStokeWidth();
                    break;
                case COLOR:
                    matchColor();
                    break;
            }
        }
        if (onParsingFinishedListener != null) {
            onParsingFinishedListener.onFinished(viewParamsList);
        }
    }

    private void matchFor() throws IllegalTypeException, SyntaxErrorException {
        matchToken(WordScanner.TokenType.FOR);
        matchToken(WordScanner.TokenType.T);
        matchToken(WordScanner.TokenType.FROM);
        ExprTree startExpr, endExpr, stepExpr, xExpr, yExpr;
        startExpr = expression();
        double startValue = countExprValue(startExpr);
        matchToken(WordScanner.TokenType.TO);
        endExpr = expression();
        double endValue = countExprValue(endExpr);
        matchToken(WordScanner.TokenType.STEP);
        stepExpr = expression();

        double stepValue = countExprValue(stepExpr);

        matchToken(WordScanner.TokenType.DRAW);
        matchToken(WordScanner.TokenType.L_BREAKET);
        xExpr = expression();
        matchToken(WordScanner.TokenType.COMMA);
        yExpr = expression();
        matchToken(WordScanner.TokenType.R_BREAKET);
        ParamT paramT = new ParamT(startValue, endValue);
        matchToken(WordScanner.TokenType.LINE_FIN);

        ViewParams viewParams = new ViewParams(paramT, stepValue, xExpr, yExpr);
        viewParams.setOrigin(origin);
        viewParams.setScale(scale);
        viewParams.setColor(color);
        viewParams.setStrokeWidth(strokeWidth);
        viewParamsList.add(viewParams);

        System.out.println(viewParams.getOrigin().getX() + " " + viewParams.getOrigin().getY() + " " +
            " " + viewParams.getParamT().getStart() + " " + viewParams.getParamT().getEnd());
        System.out.println(stepValue);
    }

    private void matchScale() throws IllegalTypeException, SyntaxErrorException {
        matchToken(WordScanner.TokenType.SCALE);
        matchToken(WordScanner.TokenType.IS);
        matchToken(WordScanner.TokenType.L_BREAKET);
        ExprTree xTree = expression();
        double x = countExprValue(xTree);
        matchToken(WordScanner.TokenType.COMMA);
        ExprTree yTree = expression();
        double y = countExprValue(yTree);
        matchToken(WordScanner.TokenType.R_BREAKET);
        matchToken(WordScanner.TokenType.LINE_FIN);
        scale = new ViewParams.Scale(x, y);
    }

    private void matchOrigin() throws IllegalTypeException, SyntaxErrorException {
        matchToken(WordScanner.TokenType.ORIGIN);
        matchToken(WordScanner.TokenType.IS);
        matchToken(WordScanner.TokenType.L_BREAKET);
        ExprTree xTree = expression();
        double x = countExprValue(xTree);
        matchToken(WordScanner.TokenType.COMMA);
        ExprTree yTree = expression();
        double y = countExprValue(yTree);
        matchToken(WordScanner.TokenType.R_BREAKET);
        matchToken(WordScanner.TokenType.LINE_FIN);
        origin = new ViewParams.Origin(x, y);
    }

    private void matchStokeWidth() throws IllegalTypeException, SyntaxErrorException {
        matchToken(WordScanner.TokenType.STROKE);
        matchToken(WordScanner.TokenType.IS);
        ExprTree valueTree = expression();
        double value = countExprValue(valueTree);
        matchToken(WordScanner.TokenType.LINE_FIN);
        strokeWidth = (int) value;
    }

    private void matchColor() throws IllegalTypeException, SyntaxErrorException {
        matchToken(WordScanner.TokenType.COLOR);
        matchToken(WordScanner.TokenType.IS);
        final WordScanner.Token tmpToken = token;
        matchToken(WordScanner.TokenType.DIGIT);
        matchToken(WordScanner.TokenType.LINE_FIN);
        double value = tmpToken.getValue();
        color = (int) value;
    }

    private void matchToken(WordScanner.TokenType tokenType)
        throws SyntaxErrorException, IllegalTypeException {
        if (token.getType() != tokenType) {
            throw new SyntaxErrorException();
        }
        fetchToken();
    }

    private void fetchToken() throws IllegalTypeException {
        token = wordScanner.getWordToken();
    }

    // T -> +F|-F|å
    private ExprTree expression() throws IllegalTypeException, SyntaxErrorException {
        ExprTree left, right;

        WordScanner.TokenType tmpTokenType;
        left = term();

        while (token.getType() == WordScanner.TokenType.PLUS
            || token.getType() == WordScanner.TokenType.MINUS) {
            tmpTokenType = token.getType();
            matchToken(token.getType());
            right = term();
            left = makeExprTree(tmpTokenType, left, right);
        }

        return left;
    }

    // F -> *F¨|/F¨|å
    private ExprTree term() throws IllegalTypeException, SyntaxErrorException {
        ExprTree left, right;
        left = factor();
        WordScanner.TokenType tmpTokenType;
        while (token.getType() == WordScanner.TokenType.MUL || token.getType() == WordScanner.TokenType.DIV) {
            tmpTokenType = token.getType();
            matchToken(tmpTokenType);
            right = factor();
            left = makeExprTree(tmpTokenType, left, right);
        }
        return left;
    }

    // F¨ -> +S | -S | å
    private ExprTree factor() throws IllegalTypeException, SyntaxErrorException {
        ExprTree left, right;
        if (token.getType() == WordScanner.TokenType.PLUS || token.getType() == WordScanner.TokenType.MINUS) {
            matchToken(token.getType());
            right = factor();
        } else {
            right = atom();
        }
        return right;
    }

    /**
     * S -> (E)|const num|T|Func
     *
     * @return ExprTree
     */
    private ExprTree atom() throws IllegalTypeException, SyntaxErrorException {
        ExprTree addressTree, tempTree;
        final WordScanner.Token mToken = this.token;
        switch (mToken.getType()) {
            case CONST_ID:
                matchToken(WordScanner.TokenType.CONST_ID);
                addressTree = makeExprTree(WordScanner.TokenType.CONST_ID, mToken.getValue());
                break;
            case T:
                matchToken(WordScanner.TokenType.T);
                ExprTree exprTree = new ExprTree();
                exprTree.setTokenType(WordScanner.TokenType.T);
                addressTree = exprTree;
                break;
            case DIGIT:
                matchToken(WordScanner.TokenType.DIGIT);
                addressTree = makeExprTree(WordScanner.TokenType.DIGIT, mToken.getValue());
                break;
            case FUNC:
                matchToken(WordScanner.TokenType.FUNC);
                tempTree = expression();
                addressTree = makeExprTree(WordScanner.TokenType.FUNC, tempTree, mToken.getFunction());
                break;
            case L_BREAKET:
                matchToken(WordScanner.TokenType.L_BREAKET);
                addressTree = expression();
                matchToken(WordScanner.TokenType.R_BREAKET);
                break;
            default:
                throw new SyntaxErrorException(mToken.getType().name());
        }
        return addressTree;
    }

    private ExprTree makeExprTree(WordScanner.TokenType tokenType, double value) {
        ExprTree exprTree = new ExprTree();
        exprTree.setTokenType(tokenType);
        exprTree.setConstParam(value);
        return exprTree;
    }

    private ExprTree makeExprTree(WordScanner.TokenType tokenType, ExprTree child, Function function) {
        ExprTree exprTree = new ExprTree();
        exprTree.setTokenType(tokenType);
        exprTree.setTokenValue(new ExprTree.FuncNode(child, function));
        return exprTree;
    }

    private ExprTree makeExprTree(WordScanner.TokenType tokenType, ExprTree left, ExprTree right) {
        ExprTree exprTree = new ExprTree();
        exprTree.setTokenType(tokenType);
        exprTree.setTokenValue(new ExprTree.OperationNode(left, right));
        return exprTree;
    }

    double countExprValue(ExprTree exprTree) throws SyntaxErrorException {
        double result = 0;
        if (exprTree.getTokenType() != WordScanner.TokenType.DIGIT
            && exprTree.getTokenType() != WordScanner.TokenType.CONST_ID) {

            if (WordScanner.operationArray.contains(exprTree.getTokenType())) {
                ExprTree.OperationNode operationNode = (ExprTree.OperationNode) exprTree.getTokenValue();
                if (operationNode != null && operationNode.isValiable()) {
                    result += countExprValue(exprTree.getTokenType(), operationNode.getLeftParam(),
                        operationNode.getRightParam());
                } else {
                    throw new SyntaxErrorException("syntax error");
                }
            } else if (exprTree.getTokenType() == WordScanner.TokenType.FUNC) {
                ExprTree.FuncNode funcNode = (ExprTree.FuncNode) exprTree.getTokenValue();
                result += (Double) funcNode.getFunction().apply(countExprValue(funcNode.getChild()));
            }
        } else {
            result += exprTree.getConstParam();
        }
        return result;
    }

    double countExprValue(WordScanner.TokenType tokenType, ExprTree left, ExprTree right)
        throws SyntaxErrorException {
        switch (tokenType) {
            case MINUS:
                return countExprValue(left) - countExprValue(right);
            case DIV:
                return countExprValue(left) / countExprValue(right);
            case MUL:
                return countExprValue(left) * countExprValue(right);
            case PLUS:
                return countExprValue(left) + countExprValue(right);
        }
        return 0;
    }

    private void printTree(ExprTree exprTree) throws SyntaxErrorException {
        if (exprTree.getTokenType() != WordScanner.TokenType.DIGIT
            && exprTree.getTokenType() != WordScanner.TokenType.CONST_ID) {

            if (WordScanner.operationArray.contains(exprTree.getTokenType())) {
                ExprTree.OperationNode operationNode = (ExprTree.OperationNode) exprTree.getTokenValue();
                if (operationNode != null && operationNode.isValiable()) {
                    printTree(operationNode.getLeftParam());
                    System.out.print(exprTree.getTokenType().name());
                    printTree(operationNode.getRightParam());
                } else {
                    throw new SyntaxErrorException("syntax error");
                }
            } else if (exprTree.getTokenType() == WordScanner.TokenType.FUNC) {
                ExprTree.FuncNode funcNode = (ExprTree.FuncNode) exprTree.getTokenValue();
                System.out.print(funcNode.getFunction().toString());
                printTree(funcNode.getChild());
            }
        } else {
            System.out.print(exprTree.getTokenType().name());
        }
    }
}
