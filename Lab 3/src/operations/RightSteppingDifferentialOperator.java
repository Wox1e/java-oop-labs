package operations;

import functions.MathFunction;

public class RightSteppingDifferentialOperator  extends SteppingDifferentialOperator{
    @Override
    public MathFunction derive(MathFunction func) {
        return new MathFunction() {
            @Override
            public double apply(double x) {
                return (func.apply(x + step) - func.apply(x)) / step;
            }
        };
    }

    public RightSteppingDifferentialOperator  (double step){
        super(step);
    }

    @Override
    public double apply(double x) {
        throw new ArithmeticException("Unrealized method");
    }
}
