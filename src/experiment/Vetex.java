package experiment;

public class Vetex implements Comparable<Vetex>{
public int idx;
public int degree;

public Vetex(int idx, int degree) {
	super();
	this.idx = idx;
	this.degree = degree;
}

@Override
public int compareTo(Vetex v) {
	if(this.degree>v.degree)
		return -1;
	if(this.degree<v.degree)
		return 1;
	return 0;
}
}
