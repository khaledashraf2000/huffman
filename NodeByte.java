
public class NodeByte implements Comparable<NodeByte>{
    NodeByte left = null;
    NodeByte right = null;
    NodeByte parent = null;
    Byte b;
    Integer freq;

    public NodeByte(Byte b, Integer freq) {
        this.b = b;
        this.freq = freq;
    }

    public NodeByte(NodeByte left, NodeByte right, Byte b, Integer freq) {
        this.left = left;
        this.right = right;
        this.b = b;
        this.freq = freq;
    }

    public NodeByte(NodeByte parent, NodeByte left, NodeByte right, Byte b, Integer freq) {
        this.parent = parent;
        this.left = left;
        this.right = right;
        this.b = b;
        this.freq = freq;
    }

    /*
     compareTo is overridden to obtain a minimum heap structure
     */
    @Override
    public int compareTo(NodeByte o) {
        return o.freq < this.freq ? 1 : -1;
    }

    public NodeByte getLeft() {
        return left;
    }

    public NodeByte getRight() {
        return right;
    }

    public Byte getByte() {
        return b;
    }

    public Integer getFreq() {
        return freq;
    }

    public void print() {
        System.out.println(this.freq);
        System.out.println(this.b);
    }

    public String toString() {
        return (b + "\n" + freq + "\n");
    }
}
