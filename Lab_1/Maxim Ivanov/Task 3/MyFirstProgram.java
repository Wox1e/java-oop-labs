class MyFirstClass {
    public static void main(String[] s) {
        for (int i = 0; i < s.length; i++) {
            System.out.println(s[i]);
        }
    }
}


class MySecondClass {
    private int first;
    private int second;

    public MySecondClass(){
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

}