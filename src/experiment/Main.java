package experiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import main.Edge;
import main.Graph;
import main.TecIndexG;
import main.TecIndexSB;

public class Main {

	public static void main(String[] args) throws IOException {
		ReadAndSort ras = new ReadAndSort("data/lj.ungraph.txt");
		ras.readGraph();
/*		
		String fileName;
        String pathtec;
        fileName = "data/youtube.ungraph.txt";
        pathtec = "youtube";
        TecIndexG tec = new TecIndexSB();
        Graph mg = new Graph();
//        long startTime = System.nanoTime();
        mg.read_GraphEdgelist(fileName);
//        long endTime = System.nanoTime();
        
		File theDir = new File(pathtec);
        if(!theDir.exists()) {
            System.out.println("given path for index files does not exists");
            System.exit(0);
        }

        tec.read_Indexlj(mg, pathtec);
        System.out.println("Index files are read and index is created ");
        
        int k = 4;
        BufferedReader br = new BufferedReader(new FileReader(new File("random/youtube_h.txt")));
        int[]varr = new int[100];
        String line = "";
        int i = 0;
        while((line=br.readLine())!=null) {
        	varr[i++] = Integer.valueOf(line);
        }
        br.close();
        long startTime = System.nanoTime();
        for(int j =0;j<100;j++) {
        	tec.findkCommunityForQuery(varr[j], k);
        }
        long endTime = System.nanoTime();
        

        System.out.print("query time:");
        System.out.println((double)(endTime - startTime) / 1.0E9D);
        System.out.println("\n\nenter query node id and  k truss value, -1 to exit");
  */    
	}
}
