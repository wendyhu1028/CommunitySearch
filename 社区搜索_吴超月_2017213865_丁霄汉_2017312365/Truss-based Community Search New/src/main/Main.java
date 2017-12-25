package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class Main {

	public static void main(String[] args) throws IOException{
		Main main = new Main();
		// TODO Auto-generated method stub
		String fileName;
        String pathtec;
        int c;
        Scanner scan = new Scanner(System.in);
        System.out.println("You want to calculate index(1) or search community(2)");
        c = scan.nextInt();        
        fileName = "data/facebook_combined.txt";
        pathtec = "toy";
        System.out.println("Please input the filename");
		fileName = scan.next();
        TecIndexG tec = new TecIndexSB();
        Graph mg = new Graph();
        long startTime = System.nanoTime();
        mg.read_GraphEdgelist(fileName);
        long endTime = System.nanoTime();
        System.out.print("graph read time: ");
        System.out.println((double)(endTime - startTime) / 1.0E9D);
        
        if(c == 1) {
        		System.out.println("Please input the index path");
        		pathtec = scan.next();
            Map<Edge, Integer> trussd = new HashMap<Edge, Integer>();
            main.createDir(pathtec);
            startTime = System.nanoTime();
            Map<Integer, LinkedHashSet<Edge>> klistdict = mg.computeTruss(pathtec);
            endTime = System.nanoTime();
            System.out.print("truss computation time: ");
            System.out.println((double)(endTime - startTime) / 1.0E9D);
            mg.write_support(pathtec + "/truss.txt", trussd);
            startTime = System.nanoTime();
            tec.constructIndex(klistdict, mg);
            endTime = System.nanoTime();
            System.out.print("Index for given graph is created. Index creation time: ");
            System.out.println((double)(endTime - startTime) / 1.0E9D);
            tec.writeIndex(pathtec);
            main.test(tec);
//            main.testefficiency(tec);
        }else {
        		System.out.println("Please input the index path");
        		pathtec = scan.next();
            File theDir = new File(pathtec);
            if(!theDir.exists()) {
                System.out.println("given path for index files does not exists");
                System.exit(0);
            }

            tec.read_Indexlj(mg, pathtec);
            System.out.println("Index files are read and index is created ");
            main.test(tec);
//            main.testefficiency(tec);
        }
        scan.close();
	}
	
	public  void test(TecIndexG tec) throws IOException {
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
                    System.out.print("Number of edges in this community: ");
                    System.out.println(c.size());
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
	
		public  void testefficiency(TecIndexG tec) throws IOException{
//		System.out.println("testefficiency");
        int[] nodes = {0,107,348,414,686,698,1684,1912,3437,3980};
//		int[] nodes = {0,107,348,414,686,698,1684,1912,3437,3980};
        int[] ks = {4,6,8,10,13,16};
//        int[] ks = {16};
        double[] averageF1 = {0,0,0,0,0,0};
//        double[] averageF1 = {0};
        
        for(int i = 0 ; i < nodes.length ; i ++) {  
            String fileName = "data/facebook/"+nodes[i] +".circles";
            File f = new File(fileName);
            if(!f.exists()) {
                System.out.println("File does not exist");
                System.exit(0);
            }
            FileReader reader = new FileReader(fileName);
            BufferedReader br = new BufferedReader(reader);
            String line;
            ArrayList<Set<Integer>> listCircle = new ArrayList<Set<Integer>>();
            while((line=br.readLine())!=null) {
//            	System.out.println(line);
            	String[] strs = line.split("\t");
            	Set<Integer> set = new 	HashSet<Integer>();
            	for(int m = 1 ; m < strs.length ; m ++) {
            		set.add(Integer.valueOf(strs[m]));
            	}
            	set.add(nodes[i]);
            	listCircle.add(set);
            }
            br.close();
            System.out.println("circles size:"+listCircle.size());
        	for(int j = 0 ; j < ks.length ; j ++) {
        		int query = nodes[i];
        		int k = ks[j];
        		long startTime = System.nanoTime();
                LinkedList<LinkedList<Edge>> com = tec.findkCommunityForQuery(query, k);
                long endTime = System.nanoTime();
                if(com.size() == 0) {
                    System.out.println("There is no community for this query with given truss value");
                } else {
                	ArrayList<Set<Integer>> listIJ = new ArrayList<Set<Integer>>();
                	System.out.println("communities size:" + com.size());
                    Iterator<LinkedList<Edge>> var10 = com.iterator();

                    while(var10.hasNext()) {
                        LinkedList<Edge> c = (LinkedList<Edge>)var10.next();
//                        System.out.print("Number of edges in this community: ");
//                        System.out.println(c.size());
                        Iterator<Edge> var12 = c.iterator();
                        Set<Integer> tmpSet = new HashSet<Integer>();

                        while(var12.hasNext()) {
                            Edge e = (Edge)var12.next();
//                            System.out.print("(" + e.s + "," + e.t + "), ");
                            tmpSet.add(e.s);
                            tmpSet.add(e.t);
                        }
                        
                        listIJ.add(tmpSet);
                        
//                        Iterator<Integer> itr = tmpSet.iterator();
//                    	while(itr.hasNext()) {
//                    		System.out.print(itr.next() + " ");
//                    	}
//                    	System.out.println();

//                        System.out.println();
                    }
                    
                    double result = calculateMax(listCircle,listIJ);
                    averageF1[j] += result;
                    System.out.println("node: " + query + " k:  " + k + " result: " + result);
                }
                System.out.print("query time:");
                System.out.println((double)(endTime - startTime) / 1.0E9D);
                
        	}
        }
    	for(int j = 0 ; j < ks.length ; j ++) {
    		System.out.println("k: " + ks[j] +" average: " + averageF1[j]/nodes.length);
    	}
	}
	
	public  void createDir(String path) {
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
	
	public  double calculateMax(ArrayList<Set<Integer>> listCirle , ArrayList<Set<Integer>> listIJ) {
		double max = 0;
		int m = listIJ.size();
		int n = listCirle.size();
//		System.out.println(m +":" + n);
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> listAll = new ArrayList<ArrayList<Integer>>();
		for(int i = 0 ; i < m ; i ++) {
			list.add(i);
		}
		ArrayList<Integer> listtmp = new ArrayList<Integer>();
		listAll(listAll,list,listtmp);
		
		Pair[][] F1Array = new Pair[m][n];
		
		for(int i = 0 ; i < m ; i ++) {
			F1Array[i] = new Pair[n];
			for(int j = 0 ; j < n ; j ++) {
				double result = calculateF1(listIJ.get(i),listCirle.get(j));
				F1Array[i][j] = new Pair(j,result);
			}
			Arrays.sort(F1Array[i],new MyComprator());
		}
		
		
		for(int i = 0 ; i < listAll.size() ; i ++) {
			double total = 0;
			ArrayList<Integer> listIndex = listAll.get(i);
			Set<Integer> s = new HashSet<Integer>();
			for(int j = 0 ; j < listIndex.size() ; j ++) {
				int index = listIndex.get(j);
				Pair[] f1List = F1Array[index];
				for(int a = 0 ; a < f1List.length ; a ++) {
					Pair p = f1List[a];
					if(s.contains(p.index) == false) {
						total += p.value;
						s.add(p.index);
						break;
					}
				}
			}
			total = total / m;
			if(total > max) {
				max = total;
			}
		}
		
		return max;
	}
	
	public  void listAll(ArrayList<ArrayList<Integer>> listAll , ArrayList<Integer> candidates , ArrayList<Integer> list) {
		if(candidates.size() == 0) {
			listAll.add(list);
		}
		
		for (int i = 0 ; i < candidates.size() ; i ++) {
			ArrayList<Integer> temp = new ArrayList<Integer>(candidates);
			ArrayList<Integer> newList = new ArrayList<Integer>(list);
			newList.add(temp.get(i));
			temp.remove(i);
			listAll(listAll , temp , newList);
		}
	}
	
	public  double calculateF1(Set<Integer> s , Set<Integer> circle) {
		Set<Integer> stmp = new HashSet<Integer> (s);
		stmp.retainAll(circle);
		int tp = stmp.size();
		stmp = new HashSet<Integer> (s);
		stmp.removeAll(circle);
		int fp = stmp.size();
		stmp = new HashSet<Integer> (circle);
		stmp.removeAll(s);
		int fn = stmp.size();
		
		double precision = tp/(tp + fp + 0.0);
		double recall = tp/(tp+fn +0.0);
		double f1 = 2*(precision * recall)/(precision + recall);
//		System.out.println("tp: "+tp);
//		System.out.println("fp: "+fp);
//		System.out.println("fn: "+fn);
//		System.out.println("precision: "+precision);
//		System.out.println("recall: "+recall);
//		System.out.println("f1: "+f1);
		return f1;
	}
	class Pair{
		double value;
		int index;
		public Pair(int i , double d) {
			this.value = d;
			this.index = i;
		}
	}
	class MyComprator implements Comparator<Pair> {
		@Override
		public int compare(Pair o1, Pair o2) {
			// TODO Auto-generated method stub
			if(o1.value > o2.value) {
				return -1;
			}
			else if(o1.value < o2.value) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
}
