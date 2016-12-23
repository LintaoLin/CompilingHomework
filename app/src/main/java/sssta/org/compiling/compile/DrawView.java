package sssta.org.compiling.compile;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;


/**
 * Created by lint on 16/12/23.
 */

public class DrawView extends View {

    private static final String TAG = "MyView";
    private float[][] points;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private List<ViewParams> paramsList;

    public DrawView(Context context) {
        super(context);
    }
    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setViewParamsArrayList(List<ViewParams> viewParamsArrayList) {
        this.paramsList = viewParamsArrayList;
        if (viewParamsArrayList != null && viewParamsArrayList.size() > 0) {
            points = new float[viewParamsArrayList.size()][];
            for (int i = 0; i < viewParamsArrayList.size(); i++) {
                ViewParams params = viewParamsArrayList.get(i);
                ViewParams.Scale scale = params.getScale();
                ViewParams.Origin origin = params.getOrigin();
                ParamT paramT = params.getParamT();
                double length = paramT.getEnd() - paramT.getStart();
                points[i] = new float[(int) (length / params.getStepValue() + 1) * 2];
                for (int j = 0; j < ( points[i].length / 2 ); j++) {
                    try {
                        points[i][j*2] =
                                (float) (origin.getX() + scale.getScaleX() * countExprValue(params.getxExprTree(),paramT.getStart() + params.getStepValue() * j));
                        points[i][j*2 + 1] =
                                (float) (origin.getY() + scale.getScaleY() * countExprValue(params.getyExprTree(),paramT.getStart() + params.getStepValue() * j));
                    } catch (SyntaxErrorException e) {
                        Log.e(TAG, "setViewParamsArrayList: ",e );
                    }
                }
            }
            invalidate();
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (points != null) {
            for (int i = 0; i < points.length; i++){
                paint.setStrokeWidth(paramsList.get(i).getStrokeWidth());
                paint.setColor(paramsList.get(i).getColor());
                canvas.drawPoints(points[i],paint);
            }
        }
    }

    double countExprValue(ExprTree exprTree,double value) throws SyntaxErrorException {
        double result = 0;
        if (exprTree.getTokenType() != WordScanner.TokenType.DIGIT && exprTree.getTokenType() != WordScanner.TokenType.CONST_ID) {
            if (WordScanner.operationArray.contains(exprTree.getTokenType())) {
                ExprTree.OperationNode operationNode = (ExprTree.OperationNode) exprTree.getTokenValue();
                if (operationNode != null && operationNode.isValiable()) {
                    result += countExprValue(exprTree.getTokenType(), operationNode.getLeftParam(), operationNode.getRightParam(),value);
                } else {
                    throw new SyntaxErrorException("syntax error");
                }
            } else if (exprTree.getTokenType() == WordScanner.TokenType.FUNC) {
                ExprTree.FuncNode funcNode = (ExprTree.FuncNode) exprTree.getTokenValue();
                result += (Double)funcNode.getFunction().apply(countExprValue(funcNode.getChild(),value));
            } else if (exprTree.getTokenType() == WordScanner.TokenType.T) {
                result += value;
            }
        } else {
            result += exprTree.getConstParam();
        }
        return result;
    }

    double countExprValue(WordScanner.TokenType tokenType, ExprTree left, ExprTree right,double value) throws SyntaxErrorException {
        switch (tokenType) {
            case MINUS:
                return countExprValue(left,value) - countExprValue(right,value);
            case DIV:
                return countExprValue(left,value) / countExprValue(right,value);
            case MUL:
                return countExprValue(left,value) * countExprValue(right,value);
            case PLUS:
                return countExprValue(left,value) + countExprValue(right,value);
        }
        return 0;
    }
}
