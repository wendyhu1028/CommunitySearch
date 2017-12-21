package main;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.util.ArrayList;
import java.util.List;

public class SGN {
    int truss;
    Integer idd;
    List<Edge> edgelist;

    public SGN(int truss, int tnid) {
        this.truss = truss;
        this.idd = Integer.valueOf(tnid);
        this.edgelist = new ArrayList<Edge>();
    }

    public void addEdge(Edge e) {
        this.edgelist.add(e);
    }
}
