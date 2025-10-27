package operations;

import functions.MathFunction;

public class LeftSteppingDifferentialOperator extends SteppingDifferentialOperator{
    @Override
    public MathFunction derive(MathFunction func) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (func.apply(x) - func.apply(x - step)) / step;
            }
        };
    }

    public LeftSteppingDifferentialOperator (double step){
        super(step);
    }

    @Override
    public double apply(double x) {
        throw new ArithmeticException("Unrealized method");
    }
}
