package main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileName;
        String pathtec;
        int c;
        c = 1;
//        System.out.println("path for graph file and path for index files is not given. Toy graph will be used");
//        fileName = "data/amazon.ungraph.txt";
        fileName = "data/amazon.ungraph.txt";
        pathtec = "amazon";
        TecIndexG tec = new TecIndexSB();
        Graph mg = new Graph();
        long startTime = System.nanoTime();
        mg.read_GraphEdgelist(fileName);
        long endTime = System.nanoTime();
        System.out.print("graph read time: ");
        System.out.println((double)(endTime - startTime) / 1.0E9D);
        
        if(c == 1) {
            Map<Edge, Integer> trussd = new HashMap<Edge, Integer>();
            createDir(pathtec);
            startTime = System.nanoTime();
            Map<Integer, LinkedHashSet<Edge>> klistdict = mg.computeTruss(pathtec, trussd);
            endTime = System.nanoTime();
            System.out.print("truss computation time: ");
            System.out.println((double)(endTime - startTime) / 1.0E9D);
            mg.write_support(pathtec + "/truss.txt", trussd);
            startTime = System.nanoTime();
            tec.constructIndex(klistdict, trussd, mg);
            endTime = System.nanoTime();
            System.out.print("Index for given graph is created. Index creation time: ");
            System.out.println((double)(endTime - startTime) / 1.0E9D);
            tec.writeIndex(pathtec);
            test(tec);
        }else {
            File theDir = new File(pathtec);
            if(!theDir.exists()) {
                System.out.println("given path for index files does not exists");
                System.exit(0);
            }

            tec.read_Indexlj(mg, pathtec);
            System.out.println("Index files are read and index is created ");
            test(tec);
        }
	}
	
	public static void test(TecIndexG tec) throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("enter query node id and k:truss value");
        Integer query = Integer.valueOf(Integer.parseInt(s.next()));

        do {
            int k = s.nextInt();
            long startTime = System.nanoTime();
            LinkedList<LinkedList<Edge>> com = tec.findkCommunityForQuery(query.intValue(), k);
            long endTime = System.nanoTime();
            if(com.size() == 0) {
                System.out.println("There is no community for this query with given truss value");
            } else {
                Iterator<LinkedList<Edge>> var10 = com.iterator();

                while(var10.hasNext()) {
                    LinkedList<Edge> c = (LinkedList<Edge>)var10.next();
                    Iterator<Edge> var12 = c.iterator();

                    while(var12.hasNext()) {
                        Edge e = (Edge)var12.next();
                        System.out.print("(" + e.s + "," + e.t + "), ");
                    }

                    System.out.println();
                }
            }

            System.out.print("query time:");
            System.out.println((double)(endTime - startTime) / 1.0E9D);
            System.out.println("\n\nenter query node id and  k truss value, -1 to exit");
            query = Integer.valueOf(Integer.parseInt(s.next()));
        } while(query.intValue() != -1);

        s.close();
    }
	
	public static void createDir(String path) {
        File theDir = new File(path);
        if(!theDir.exists()) {
            System.out.println("creating directory: " + path);
//            boolean var2 = false;

            try {
                theDir.mkdir();
//                var2 = true;
            } catch (SecurityException var4) {
                System.out.println("there is a problem with creating directory");
            }
        }

    }
}
