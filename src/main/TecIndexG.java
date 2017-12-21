package main;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public abstract class TecIndexG {
    Map<Integer, Set<Integer>> vtoSGN = new HashMap<Integer, Set<Integer>>();
    Map<Integer, SGN> idSGN = new HashMap<Integer, SGN>();
    Map<Integer, Set<Integer>> SG = new HashMap<Integer, Set<Integer>>();
    private Double size = Double.valueOf(0.0D);

    public TecIndexG() {
    }

    public abstract void constructIndex(Map<Integer, LinkedHashSet<Edge>> var1, Map<Edge, Integer> var2, Graph var3);

    public abstract LinkedList<LinkedList<Edge>> findkCommunityForQuery(int var1, int var2) throws IOException;

    public Double getSize() {
        return this.size;
    }

    public void computeSize() {
        this.size = Double.valueOf(0.0D);

        Integer v;
        Iterator<Integer> var2;
        for(var2 = this.vtoSGN.keySet().iterator(); var2.hasNext(); this.size = Double.valueOf(this.size.doubleValue() + (double)(8 + ((Set<Integer>)this.vtoSGN.get(v)).size() * 4))) {
            v = (Integer)var2.next();
        }

        for(var2 = this.idSGN.keySet().iterator(); var2.hasNext(); this.size = Double.valueOf(this.size.doubleValue() + (double)(4 * ((SGN)this.idSGN.get(v)).edgelist.size()))) {
            v = (Integer)var2.next();
            this.size = Double.valueOf(this.size.doubleValue() + 4.0D);
            this.size = Double.valueOf(this.size.doubleValue() + 4.0D);
            this.size = Double.valueOf(this.size.doubleValue() + 4.0D);
        }

        this.size = Double.valueOf(this.size.doubleValue() + (double)(8 * this.SG.keySet().size()));

        for(var2 = this.SG.keySet().iterator(); var2.hasNext(); this.size = Double.valueOf(this.size.doubleValue() + (double)(4 * ((Set<Integer>)this.SG.get(v)).size()))) {
            v = (Integer)var2.next();
        }

    }

    public void writeIndex(String path) throws IOException {
        FileWriter fileWriter = new FileWriter(path.concat("/superNodes.txt"));
        BufferedWriter bw = new BufferedWriter(fileWriter);
        Iterator<Integer> var5 = this.idSGN.keySet().iterator();

        Integer kid;
        while(var5.hasNext()) {
            kid = (Integer)var5.next();
            SGN sg = (SGN)this.idSGN.get(kid);
            bw.write("id,");
            bw.write(Integer.toString(kid.intValue()));
            bw.write(",truss,");
            bw.write(Integer.toString(sg.truss));
            bw.write("\n");
            ListIterator<Edge> listIterator = sg.edgelist.listIterator();

            while(listIterator.hasNext()) {
                Edge e = (Edge)listIterator.next();
                bw.write(Integer.toString(e.s));
                bw.write(",");
                bw.write(Integer.toString(e.t));
                bw.write("\n");
            }
        }

        bw.close();
        fileWriter = new FileWriter(path.concat("/ogn_ign_dic.txt"));
        bw = new BufferedWriter(fileWriter);
        bw.write("original_node_id index_graph_node_id\n");
        var5 = this.vtoSGN.keySet().iterator();

        Integer nid;
        Iterator<Integer> var10;
        while(var5.hasNext()) {
            kid = (Integer)var5.next();
            var10 = ((Set<Integer>)this.vtoSGN.get(kid)).iterator();

            while(var10.hasNext()) {
                nid = (Integer)var10.next();
                bw.write(Integer.toString(kid.intValue()));
                bw.write(" ");
                bw.write(Integer.toString(nid.intValue()));
                bw.write("\n");
            }
        }

        bw.close();
        fileWriter = new FileWriter(path.concat("/summaryIndexGraph.txt"));
        bw = new BufferedWriter(fileWriter);
        var5 = this.SG.keySet().iterator();

        while(var5.hasNext()) {
            kid = (Integer)var5.next();
            var10 = ((Set<Integer>)this.SG.get(kid)).iterator();

            while(var10.hasNext()) {
                nid = (Integer)var10.next();
                bw.write(Integer.toString(kid.intValue()));
                bw.write(",");
                bw.write(Integer.toString(nid.intValue()));
                bw.write("\n");
            }
        }

        bw.close();
    }

    public void read_Indexlj(Graph g, String path) throws IOException {
        FileReader reader = new FileReader(path.concat("/superNodes.txt"));
        BufferedReader br = new BufferedReader(reader);
        String line = br.readLine();
        String[] sr = line.trim().split(",");
        int id = Integer.parseInt(sr[1]);
        int truss = Integer.parseInt(sr[3]);
        SGN sg = new SGN(truss, id);

        HashSet<Integer> nl;
        while((line = br.readLine()) != null) {
            sr = line.trim().split(",");
            if(sr[0].equals("id")) {
                this.idSGN.put(Integer.valueOf(id), sg);
                nl = new HashSet<Integer>();
                this.SG.put(Integer.valueOf(id), nl);
                id = Integer.parseInt(sr[1]);
                truss = Integer.parseInt(sr[3]);
                sg = new SGN(truss, id);
            } else {
                sg.edgelist.add(g.getEdge(Integer.parseInt(sr[0]), Integer.parseInt(sr[1])));
            }
        }

        this.idSGN.put(Integer.valueOf(id), sg);
        nl = new HashSet<Integer>();
        this.SG.put(Integer.valueOf(id), nl);
        br.close();
        Iterator<Integer> var12 = this.idSGN.keySet().iterator();

        while(var12.hasNext()) {
            Integer ci = (Integer)var12.next();

            Edge e;
            for(Iterator<Edge> var14 = ((SGN)this.idSGN.get(ci)).edgelist.iterator(); var14.hasNext(); ((Set<Integer>)this.vtoSGN.get(Integer.valueOf(e.t))).add(ci)) {
                e = (Edge)var14.next();
                if(!this.vtoSGN.containsKey(Integer.valueOf(e.s))) {
                    this.vtoSGN.put(Integer.valueOf(e.s), new HashSet<Integer>());
                }

                ((Set<Integer>)this.vtoSGN.get(Integer.valueOf(e.s))).add(ci);
                if(!this.vtoSGN.containsKey(Integer.valueOf(e.t))) {
                    this.vtoSGN.put(Integer.valueOf(e.t), new HashSet<Integer>());
                }
            }
        }

        reader = new FileReader(path.concat("/summaryIndexGraph.txt"));
        br = new BufferedReader(reader);

        while((line = br.readLine()) != null && !line.equals("vertex")) {
            sr = line.trim().split(",");
            ((Set<Integer>)this.SG.get(Integer.valueOf(Integer.parseInt(sr[0])))).add(Integer.valueOf(Integer.parseInt(sr[1])));
            ((Set<Integer>)this.SG.get(Integer.valueOf(Integer.parseInt(sr[1])))).add(Integer.valueOf(Integer.parseInt(sr[0])));
        }

        br.close();
    }
}
