class MyFirstClass {
    public static void main(String[] s) {
        MySecondClass o = new MySecondClass();
        System.out.println(o.div());
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                o.setFirst(i);
                o.setSecond(j);
                System.out.print(o.div());
                System.out.print(" ");
            }
            System.out.println();
        }

    }
}


class MySecondClass {
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

    int div(){
        return first/second;
    }
}