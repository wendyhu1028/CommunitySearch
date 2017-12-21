package experiment;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import main.Edge;
import main.Graph;
public class ReadAndSort {
private String fileName;

	public ReadAndSort(String fileName) {
	super();
	this.fileName = fileName;
}

	public void readGraph() throws IOException {
        Graph mg = new Graph();
        try {
			mg.read_GraphEdgelist(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        List<Vetex> vlist = new ArrayList<Vetex>();
        int size = 0;
        for(Map.Entry<Integer, Map<Integer,Edge>> entry:mg.g.entrySet()) {
        	Vetex v = new Vetex(entry.getKey(),entry.getValue().size());
        	vlist.add(v);
        	size++;
        }
        Collections.sort(vlist);
        for(Vetex v:vlist) {
        	System.out.println(v.idx+"degree: "+v.degree);
        }
        System.out.println("size:"+size);
        FileWriter fw = new FileWriter("random/lj_1.txt");
        int min = (int)(size*0);
        int max = (int)(size*0.1);
        Random random = new Random();
        for(int i = 0;i<100;i++) {
        	int idx = random.nextInt(max)%(max-min+1) + min;
        	//System.out.println(idx);
        	fw.write(String.valueOf(vlist.get(idx).idx));
        	fw.write("\n");
        }
        fw.flush();
        fw.close();
	}
}
