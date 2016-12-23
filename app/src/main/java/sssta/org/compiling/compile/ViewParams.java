package sssta.org.compiling.compile;

import android.graphics.Color;

/**
 * Created by lint on 16/12/23.
 * 画图参数
 */
public class ViewParams {

    private Origin origin;
    private Scale scale;
    private int color = Color.BLUE;
    private int strokeWidth = 2;

    private ParamT paramT;

    private double stepValue;

    private ExprTree xExprTree, yExprTree;

    public ViewParams(ParamT paramT, double stepValue, ExprTree xExprTree, ExprTree yExprTree) {
        this.paramT = paramT;
        this.stepValue = stepValue;
        this.xExprTree = xExprTree;
        this.yExprTree = yExprTree;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    static class Origin {
        private double x, y;

        public Origin(double x, double y) {
            this.x = x;
            this.y = y;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }

    static class Scale{

        private double scaleX, scaleY;

        public Scale(double scaleX, double scaleY) {
            this.scaleX = scaleX;
            this.scaleY = scaleY;
        }

        public double getScaleX() {
            return scaleX;
        }

        public void setScaleX(double scaleX) {
            this.scaleX = scaleX;
        }

        public double getScaleY() {
            return scaleY;
        }
        public void setScaleY(double scaleY) {
            this.scaleY = scaleY;
        }

    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Scale getScale() {
        return scale;
    }

    public void setScale(Scale scale) {
        this.scale = scale;
    }

    public double getStepValue() {
        return stepValue;
    }

    public void setStepValue(double stepValue) {
        this.stepValue = stepValue;
    }

    public Origin getOrigin() {
        return origin;
    }

    public void setOrigin(Origin origin) {
        this.origin = origin;
    }

    public ParamT getParamT() {
        return paramT;
    }

    public void setParamT(ParamT paramT) {
        this.paramT = paramT;
    }


    public ExprTree getxExprTree() {
        return xExprTree;
    }

    public void setxExprTree(ExprTree xExprTree) {
        this.xExprTree = xExprTree;
    }

    public ExprTree getyExprTree() {
        return yExprTree;
    }

    public void setyExprTree(ExprTree yExprTree) {
        this.yExprTree = yExprTree;
    }
}
