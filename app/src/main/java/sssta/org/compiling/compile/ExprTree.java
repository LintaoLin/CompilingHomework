package sssta.org.compiling.compile;

public class ExprTree {
    private WordScanner.TokenType tokenType;

    private double constParam;

    private Object tokenValue;

    public ExprTree() {

    }

    public ExprTree(WordScanner.TokenType tokenType, double constParam) {
        this.tokenType = tokenType;
        this.constParam = constParam;
    }

    public ExprTree(WordScanner.TokenType tokenType, Object tokenValue) {
        this.tokenType = tokenType;
        this.tokenValue = tokenValue;
    }

    public WordScanner.TokenType getTokenType() {
        return tokenType;
    }

    public void setTokenType(WordScanner.TokenType tokenType) {
        this.tokenType = tokenType;
    }

    public double getConstParam() {
        return constParam;
    }

    public void setConstParam(double constParam) {
        this.constParam = constParam;
    }

    public Object getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(Object tokenValue) {
        this.tokenValue = tokenValue;
    }

    public static class FuncNode {
        private ExprTree child;
        private Function function;

        public FuncNode(ExprTree child, Function function) {
            this.child = child;
            this.function = function;
        }

        public ExprTree getChild() {
            return child;
        }

        public void setChild(ExprTree child) {
            this.child = child;
        }

        public Function getFunction() {
            return function;
        }

        public void setFunction(Function function) {
            this.function = function;
        }
    }

    public static class OperationNode {
        private ExprTree leftParam, rightParam;

        public OperationNode(ExprTree leftParam, ExprTree rightParam) {
            this.leftParam = leftParam;
            this.rightParam = rightParam;
        }

        public ExprTree getLeftParam() {
            return leftParam;
        }

        public void setLeftParam(ExprTree leftParam) {
            this.leftParam = leftParam;
        }

        public ExprTree getRightParam() {
            return rightParam;
        }

        public void setRightParam(ExprTree rightParam) {
            this.rightParam = rightParam;
        }

        public boolean isValiable() {
            return leftParam != null && rightParam != null;
        }
    }
}
