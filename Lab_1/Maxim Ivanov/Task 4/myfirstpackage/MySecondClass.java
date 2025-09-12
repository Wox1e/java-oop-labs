package myfirstpackage;

public class MySecondClass {
    private int first;
    private int second;

    public MySecondClass(int fir, int sec){
        first = fir;
        second = sec;
    }

    public  MySecondClass(){
        first = 8000;
        second = 5000;
    }

    public int getFirst(){
        return first;
    }

    public void setFirst(int value){
        first = value;
    }

    public int getSecond(){
        return second;
    }

    public void setSecond(int value){
        second = value;
    }

    public int div(){
        return first/second;
    }
}