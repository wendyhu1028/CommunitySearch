package main;

import java.util.HashMap;

public class Edge {
	int s;
	int t;
	int support;
	int truss = -1;
	HashMap<Integer, Integer> list= new HashMap<Integer, Integer>();
	
	public Edge(int ss, int tt) {
		this.s = ss;
		this.t = tt;
	}
}
