package functions;

public class CompositeFunction implements MathFunction{
    private MathFunction firstFunction;
    private final MathFunction secondFunction;


    public CompositeFunction(MathFunction FirstFunc, MathFunction SecondFunc){
        firstFunction = FirstFunc;
        secondFunction = SecondFunc;
    }


    @Override
    public double apply(double x) {
        return secondFunction.apply(firstFunction.apply(x));
    }
}
