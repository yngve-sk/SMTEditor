package model;

public class Link {

    private SMTNode n1, n2;

    public Link(SMTNode n1, SMTNode n2) {
        this.n1 = n1;
        this.n2 = n2;
    }

    public SMTNode getN1() {
        return n1;
    }

    public SMTNode getN2() {
        return n2;
    }
}
