package functions;

public abstract class LinkedListTabulatedFunction extends AbstractTabulatedFunction{
    private Node head;

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues){
        this.count = 0;
        this.head = null;
        for(int i = 0;i<xValues.length;i++){
            this.addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) {

        if (xFrom > xTo) {
            double temp = xFrom;
            xFrom = xTo;
            xTo = temp;
        }

        if (xFrom == xTo) {
            for (int i = 0; i < count; i++) {
                this.addNode(xFrom, source.apply(xFrom));
            }
        } else {

            double step = (xTo - xFrom) / (count - 1);

            for (int i = 0; i < count; i++) {
                double x = xFrom + i * step;
                double y = source.apply(x);
                this.addNode(x, y);
            }
        }
    }

    private void addNode(double x, double y) {
        Node newNode = new Node(x, y);

        if (head == null) {
            head = newNode;
            newNode.next = newNode;
            newNode.prev = newNode;
        } else {
            Node last = head.prev;
            newNode.prev = last;
            newNode.next = head;
            last.next = newNode;
            head.prev = newNode;
        }
        count++;
    }

    private Node getNode(int index) {
        Node node;
        if (index <= count / 2) {
            node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
        } else {
            node = head.prev;
            for (int i = count - 1; i > index; i--) {
                node = node.prev;
            }
        }
        return node;
    }

    @Override
    public double getX(int index){
        return getNode(index).x;
    }

    @Override
    public double getY(int index){
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value){
        getNode(index).y = value;
    }

    @Override
    public int indexOfY(double y){
        Node node = head;
        int i = 0;
        do {
            if (Math.abs(node.y - y) < 1e-9) {
                node = node.next;
                i++;
            } else {
                return i;
            }
        } while (node != head);
        return -1;
    }

    @Override
    public int indexOfX(double x){
        Node node = head;
        int i = 0;
        do {
            if (Math.abs(node.x - x) < 1e-9) {
                node = node.next;
                i++;
            } else {
                return i;
            }
        } while (node != head);
        return -1;
    }

    @Override
    protected int floorIndexOfX(double x) {
        Node node = head;

        if (head.x > x) {
            return 0;
        }

        if (head.prev.x < x) {
            return count;
        }

        int i = 0;
        while (i < count - 1) {
            if (node.x <= x && node.next.x > x) {
                return i;
            }
            node = node.next;
            i++;
        }
        return i;
    }

    public int getCount() {
        return super.getCount();
    }

    @Override
    public double rightBound(){
        return this.head.prev.x;
    }

    @Override
    public double leftBound(){
        return this.head.x;
    }
}
