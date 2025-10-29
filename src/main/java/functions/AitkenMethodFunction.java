package functions;

public class AitkenMethodFunction implements MathFunction{

    private final MathFunction baseFunction;


    public AitkenMethodFunction(MathFunction baseFunction) {
        this.baseFunction = baseFunction;
    }


    @Override
    public double apply(double x) {
        double x1 = baseFunction.apply(x);
        double x2 = baseFunction.apply(x1);
        double x3 = baseFunction.apply(x2);

        return applyAitkenFormula(x1, x2, x3);
    }


    private double applyAitkenFormula(double x1, double x2, double x3) {
        double denominator = x3 - 2 * x2 + x1;

        if (Math.abs(denominator) < 1e-12) {
            return x3;
        }

        return x1 - Math.pow(x2 - x1, 2) / denominator;
    }
}
