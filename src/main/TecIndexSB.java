package main;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class TecIndexSB extends TecIndexG {
    public TecIndexSB() {
    }

    public void constructIndex(Map<Integer, LinkedHashSet<Edge>> klistdict, Map<Edge, Integer> trussd, Graph mg) {
        Map<Edge, HashMap<Integer, Integer>> edgeigd = new HashMap<Edge, HashMap<Integer, Integer>>();
        int tnid = 0;
        if(klistdict.containsKey(Integer.valueOf(2))) {
            klistdict.remove(Integer.valueOf(2));
        }

        long var25;
        for(Iterator<Integer> var18 = klistdict.keySet().iterator(); var18.hasNext(); var25 = System.nanoTime()) {
            int t = ((Integer)var18.next()).intValue();
            long startTime = System.nanoTime();

            for(LinkedHashSet Kedgelist = (LinkedHashSet)klistdict.get(Integer.valueOf(t)); !Kedgelist.isEmpty(); ++tnid) {
                Edge ek = (Edge)Kedgelist.iterator().next();
                Kedgelist.remove(ek);
                HashSet<Edge> proes = new HashSet();
                Queue<Edge> Qk = new LinkedList();
                Qk.add(ek);
                proes.add(ek);
                SGN Vk = new SGN(t, tnid);
                this.idSGN.put(Integer.valueOf(tnid), Vk);
                Set<Integer> nl = new HashSet();
                this.SG.put(Integer.valueOf(tnid), nl);

                while(!Qk.isEmpty()) {
                    Edge e = (Edge)Qk.poll();
                    int x = e.s;
                    int y = e.t;
                    if(((HashMap)mg.g.get(Integer.valueOf(x))).size() > ((HashMap)mg.g.get(Integer.valueOf(y))).size()) {
                        y = e.s;
                        x = e.t;
                    }

                    Vk.addEdge(e);
                    this.addcomVertex(x, tnid, this.vtoSGN);
                    this.addcomVertex(y, tnid, this.vtoSGN);
                    this.addEdgeToTrussCom(e, tnid, edgeigd);
                    mg.removeEdge(x, y);
                    Iterator var24 = ((HashMap)mg.g.get(Integer.valueOf(x))).keySet().iterator();

                    while(var24.hasNext()) {
                        Integer ne = (Integer)var24.next();
                        if(((HashMap)mg.g.get(Integer.valueOf(y))).containsKey(ne)) {
                            Edge e1 = mg.getEdge(x, ne.intValue());
                            int t1 = ((Integer)trussd.get(e1)).intValue();
                            Edge e2 = mg.getEdge(y, ne.intValue());
                            int t2 = ((Integer)trussd.get(e2)).intValue();
                            processTrangleedge(e1, t1, proes, Kedgelist, Qk, Vk, edgeigd);
                            processTrangleedge(e2, t2, proes, Kedgelist, Qk, Vk, edgeigd);
                        }
                    }
                }
            }
        }

    }

    private void addcomVertex(int x, int tns, Map<Integer, Set<Integer>> vtoSGN) {
        if(vtoSGN.containsKey(Integer.valueOf(x))) {
            ((Set)vtoSGN.get(Integer.valueOf(x))).add(Integer.valueOf(tns));
        } else {
            Set<Integer> cl = new HashSet();
            cl.add(Integer.valueOf(tns));
            vtoSGN.put(Integer.valueOf(x), cl);
        }

    }

    private static void processTrangleedge(Edge e1, int t1, HashSet<Edge> proes, LinkedHashSet<Edge> kedgelist, Queue<Edge> Qk, SGN Vk, Map<Edge, HashMap<Integer, Integer>> edgeigd) {
        if(!proes.contains(e1)) {
            if(t1 == Vk.truss) {
                kedgelist.remove(e1);
                Qk.add(e1);
            } else {
                addEdgeforedgespec(e1, Vk, edgeigd);
            }

            proes.add(e1);
        }

    }

    private static void addEdgeforedgespec(Edge e1, SGN Vk, Map<Edge, HashMap<Integer, Integer>> edgeigd) {
        if(!edgeigd.containsKey(e1)) {
            HashMap<Integer, Integer> nl = new HashMap();
            nl.put(Vk.idd, Integer.valueOf(Vk.truss));
            edgeigd.put(e1, nl);
        } else if(!((HashMap)edgeigd.get(e1)).containsKey(Vk.idd)) {
            ((HashMap)edgeigd.get(e1)).put(Vk.idd, Integer.valueOf(Vk.truss));
        }

    }

    private void addEdgeToTrussCom(Edge e, int tns, Map<Edge, HashMap<Integer, Integer>> edgeigd) {
        if(edgeigd.containsKey(e)) {
            Iterator var5 = ((HashMap)edgeigd.get(e)).keySet().iterator();

            while(var5.hasNext()) {
                Integer cm = (Integer)var5.next();
                if(!((Set)this.SG.get(Integer.valueOf(tns))).contains(cm)) {
                    ((Set)this.SG.get(Integer.valueOf(tns))).add(cm);
                    ((Set)this.SG.get(cm)).add(Integer.valueOf(tns));
                }
            }

            edgeigd.remove(e);
        }

    }

    public LinkedList<LinkedList<Edge>> findkCommunityForQuery(int query, int k) throws IOException {
    	LinkedList<LinkedList<Edge>> cl = new LinkedList();
    	if(this.vtoSGN.get(Integer.valueOf(query))== null) {
			return cl;
		}
        LinkedList<Integer> qIn = new LinkedList((Collection)this.vtoSGN.get(Integer.valueOf(query)));
        
        HashSet ignidl = new HashSet();

        while(true) {
            Integer qid;
            do {
                do {
                    if(qIn.isEmpty()) {
                        return cl;
                    }

                    qid = (Integer)qIn.removeFirst();
                } while(((SGN)this.idSGN.get(qid)).truss < k);
            } while(ignidl.contains(qid));

            Queue<Integer> ignidq = new LinkedList();
            LinkedList<Edge> community = null;
            community = new LinkedList();
            ignidq.add(qid);
            ignidl.add(qid);
            community.addAll(((SGN)this.idSGN.get(qid)).edgelist);

            while(!ignidq.isEmpty()) {
                Integer ig = (Integer)ignidq.poll();
                Iterator var11 = ((Set)this.SG.get(ig)).iterator();

                while(var11.hasNext()) {
                    Integer nid = (Integer)var11.next();
                    if(((SGN)this.idSGN.get(nid)).truss >= k && !ignidl.contains(nid)) {
                        ignidq.add(nid);
                        ignidl.add(nid);
                        community.addAll(((SGN)this.idSGN.get(nid)).edgelist);
                    }
                }
            }

            cl.add(community);
            System.out.print("Number of edges in this community: ");
            System.out.println(community.size());
        }
    }
}
