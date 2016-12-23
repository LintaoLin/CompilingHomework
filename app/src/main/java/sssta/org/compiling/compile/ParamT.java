package sssta.org.compiling.compile;

/**
 * Created by lint on 16/12/23.
 */
public class ParamT {
    private double start, end;

    public ParamT(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getEnd() {
        return end;
    }

    public void setEnd(double end) {
        this.end = end;
    }
}
