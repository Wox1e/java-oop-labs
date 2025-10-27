package functions;

import exceptions.InterpolationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.*;
import java.io.IOException;


public class LinkedListTabulatedFunction extends AbstractTabulatedFunction implements Insertable, Removable, Iterable<Point>, Serializable {
    private Node head;

    @Serial
    private static final long serialVersionUID = -7854477956806081056L;

    private static final Logger logger = Logger.getLogger(LinkedListTabulatedFunction.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("myLog.log");
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            fileHandler.setLevel(Level.ALL);
            logger.setLevel(Level.WARNING);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Couldn't create a file for logs", e);
        }
    }

    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) throws IllegalArgumentException{
        checkLengthIsTheSame(xValues, yValues);
        checkSorted(xValues);
        if (xValues.length < 2) {
            logger.warning("lower than 2 strings (length: " + count + ")");
            throw new IllegalArgumentException("lower than 2 strings");
        }
        this.count = 0;
        this.head = null;
        for(int i = 0;i<xValues.length;i++){
            this.addNode(xValues[i], yValues[i]);
        }
    }

    public LinkedListTabulatedFunction(MathFunction source, double xFrom, double xTo, int count) throws IllegalArgumentException{
        if (count < 2) {
            logger.warning("lower than 2 strings (length: " + count + ")");
            throw new IllegalArgumentException("lower than 2 strings");
        }
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

    public static class Node implements Serializable{
        @Serial
        private static final long serialVersionUID = 3307299702883519117L;
        public Node next;
        public Node prev;
        public double x;
        public double y;

        public Node(double x, double y){
            this.x = x;
            this.y = y;
        }
    }

    public void addNode(double x, double y) {
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
        logger.info("Added node");
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
    public double getX(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of range");
        }
        return getNode(index).x;
    }

    @Override
    public double getY(int index){
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of range");
        }
        return getNode(index).y;
    }

    @Override
    public void setY(int index, double value) throws IndexOutOfBoundsException {
        if (index < 0 || index >= count) {
            throw new IndexOutOfBoundsException("Index " + index + " is out of range");
        }
        getNode(index).y = value;
        logger.info("Y on index " + index + " changed");
    }

    @Override
    public int indexOfY(double y){
        Node node = head;
        int i = 0;
        do {
            if (Math.abs(node.y - y) < 1e-9) {
                return i;
            } else {
                node = node.next;
                i++;
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
                return i;
            } else {
                node = node.next;
                i++;
            }
        } while (node != head);
        return -1;
    }

    @Override
    public int floorIndexOfX(double x) throws IllegalArgumentException{
        if (x < leftBound()) throw new IllegalArgumentException("Index " + x + " is lower than left bound");
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

    @Override
    public double interpolate(double x, int floorIndex){

        if (x < getX(floorIndex) || x > getX(floorIndex+1)) {
            logger.warning("The x value out of interpolate interval");
            throw new InterpolationException("The x value out of interpolate interval");}

        double y1 = this.getNode(floorIndex).y;
        double x1 = this.getNode(floorIndex).x;
        double y2 = this.getNode(floorIndex + 1).y;
        double x2 = this.getNode(floorIndex + 1).x;

        logger.info("Interpolation for x: " + x);
        return y1 + ((y2 - y1) / (x2 - x1)) * (x - x1);
    }

    @Override
    public double extrapolateLeft(double x) {
        if (this.count == 1) return this.head.y;

        double y1 = getNode(0).y;
        double x1 = getNode(0).x;
        double y2 = getNode(1).y;
        double x2 = getNode(1).x;

        logger.info("extrapolateLeft for x: " + x);
        return y1 + ((y2 - y1) / (x2 - x1)) * (x - x1);
    }

    @Override
    public double extrapolateRight(double x){
        if (this.count == 1) return this.head.y;

        double y1 = getNode(count - 2).y;  // предпоследний
        double x1 = getNode(count - 2).x;
        double y2 = getNode(count - 1).y;  // последний
        double x2 = getNode(count - 1).x;

        logger.info("extrapolateRight for x: " + x);
        return y1 + ((y2 - y1) / (x2 - x1)) * (x - x1);
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

    @Override
    public void insert(double x, double y) {
        if (head == null || x > rightBound()) {
            addNode(x, y);
            return;
        }

        int idx = indexOfX(x);
        if (idx != -1){
            setY(idx, y);
            return;
        }

        if (x < leftBound()){
            Node newNode = new Node(x, y);
            Node oldHead = head;
            Node last = head.prev;

            newNode.next = oldHead;
            oldHead.prev = newNode;
            last.next = newNode;
            newNode.prev = last;
            head = newNode;
            count++;
            logger.info("Inserted y: " + y + " in x: " + x);
            return;
        }

        Node nextNode = head;
        while (nextNode.x < x && nextNode.next != head){
            nextNode = nextNode.next;
        }

        if (nextNode.next != head) {
            Node prevNode = nextNode.prev;
            Node newNode = new Node(x, y);
            prevNode.next = newNode;
            newNode.prev = prevNode;
            newNode.next = nextNode;
            nextNode.prev = newNode;
            count++;
            logger.info("Inserted y: " + y + " in x: " + x);
        }else {
            logger.warning("Unexpected condition");
            throw new RuntimeException("Unexpected condition");
        }

    }

    @Override
    public void remove(int index) {
        Node nodeToRemove = getNode(index);

        if (count == 1) {
            head = null;
        } else {
            nodeToRemove.prev.next = nodeToRemove.next;
            nodeToRemove.next.prev = nodeToRemove.prev;

            if (nodeToRemove == head) {
                head = head.next;
            }
        }

        nodeToRemove.next = null;
        nodeToRemove.prev = null;

        logger.info("Node on index: " + index + " removed");
        count--;
    }

    @Override
    public Iterator<Point> iterator() {
        return new Iterator<>() {
            private Node node = head;

            @Override
            public boolean hasNext() {
                return node != null;
            }

            @Override
            public Point next() {
                if (!hasNext()) throw new NoSuchElementException();

                Point point = new Point(node.x, node.y);

                if (node.next == head) {
                    node = null;
                } else {
                    node = node.next;
                }

                return point;
            }
        };
    }
}
