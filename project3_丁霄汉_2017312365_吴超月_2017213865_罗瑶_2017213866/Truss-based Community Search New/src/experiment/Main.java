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
        String fileName = "data/youtube.ungraph.txt";	//原始图文件
        String pathtec = "youtube";					//索引等存放的文件夹路径
        String vertexFile = "random/youtube_h.txt";//保存待搜索的100个点的index的文件路径
        int k = 4;								//truss k
        TecIndexG tec = new TecIndexSB();
        Graph mg = new Graph();
        mg.read_GraphEdgelist(fileName);
        
		File theDir = new File(pathtec);
        if(!theDir.exists()) {
            System.out.println("given path for index files does not exists");
            System.exit(0);
        }

        tec.read_Indexlj(mg, pathtec);
        System.out.println("Index files are read and index is created ");

        BufferedReader br = new BufferedReader(new FileReader(new File(vertexFile)));
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
	}
}
