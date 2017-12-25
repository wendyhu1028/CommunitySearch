package main;

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
    public void constructIndex(Map<Integer, LinkedHashSet<Edge>> klistdict, Graph mg) {
    		int tnid = 0; // super node的id
    		if(klistdict.containsKey(Integer.valueOf(2))) {
            klistdict.remove(Integer.valueOf(2));
        }
    		for(Iterator<Integer> var18 = klistdict.keySet().iterator(); var18.hasNext();) {
        		//var18是truss值相同的edge的list的itr
            int t = var18.next();

            for(LinkedHashSet<Edge> Kedgelist = (LinkedHashSet<Edge>)klistdict.get(Integer.valueOf(t)); !Kedgelist.isEmpty(); ++tnid) {
                Edge ek = (Edge)Kedgelist.iterator().next();
                Kedgelist.remove(ek);
                HashSet<Edge> proes = new HashSet<Edge>();//存储正在处理的边
                Queue<Edge> Qk = new LinkedList<Edge>();
                Qk.add(ek);
                proes.add(ek);
                SGN Vk = new SGN(t, tnid);
                this.idSGN.put(tnid, Vk);//将tnid映射到SGN
                Set<Integer> nl = new HashSet<Integer>();
                this.SG.put(tnid, nl);//与每个超节点相连的节点id

                while(!Qk.isEmpty()) {
                    Edge e = (Edge)Qk.poll();
                    int x = e.s;//u
                    int y = e.t;//v
                    if((mg.g.get(x)).size() > (mg.g.get(y)).size()) {
                        y = e.s;
                        x = e.t;
                    }

                    Vk.addEdge(e);
                    this.addcomVertex(x, tnid, this.vtoSGN);
                    this.addcomVertex(y, tnid, this.vtoSGN);
                    this.addEdgeToTrussCom2(e, tnid);
                    mg.removeEdge(x, y);
                    Iterator<Integer> var24 = (mg.g.get(Integer.valueOf(x))).keySet().iterator();

                    while(var24.hasNext()) {
                        Integer ne = (Integer)var24.next();
                        if((mg.g.get(Integer.valueOf(y))).containsKey(ne)) {
                            Edge e1 = mg.getEdge(x, ne.intValue());
                            int t1 = e1.truss;
                            Edge e2 = mg.getEdge(y, ne.intValue());
                            int t2 = e2.truss;
                            processTrangleedge(e1, t1, proes, Kedgelist, Qk, Vk);
                            processTrangleedge(e2, t2, proes, Kedgelist, Qk, Vk);
                        }
                    }
                }
            }
        }
    }

    private void addcomVertex(int x, int tns, Map<Integer, Set<Integer>> vtoSGN) {
        if(vtoSGN.containsKey(Integer.valueOf(x))) {
        		vtoSGN.get(x).add(tns);
        } else {
            Set<Integer> cl = new HashSet<Integer>();
            cl.add(Integer.valueOf(tns));
            vtoSGN.put(Integer.valueOf(x), cl);
        }

    }
    
    private static void processTrangleedge(Edge e1, int t1, HashSet<Edge> proes, LinkedHashSet<Edge> kedgelist, Queue<Edge> Qk, SGN Vk) {
        if(!proes.contains(e1)) {
            if(t1 == Vk.truss) {
                kedgelist.remove(e1);
                Qk.add(e1);
            } else {
                addEdgeforedgespec(e1, Vk);
            }
            proes.add(e1);
        }

    }
    
    private static void addEdgeforedgespec(Edge e1, SGN Vk) {
        e1.list.put(Vk.idd, Integer.valueOf(Vk.truss));
    }
    
    private void addEdgeToTrussCom2(Edge e, int tns) {
        HashMap<Integer, Integer> list = e.list;
        Iterator<Integer> var5 = list.keySet().iterator();
        while(var5.hasNext()) {
            Integer cm = (Integer)var5.next();
            this.SG.get(tns).add(cm);
            this.SG.get(cm).add(tns);
        }
    }

    public LinkedList<LinkedList<Edge>> findkCommunityForQuery(int query, int k) throws IOException {
    	LinkedList<LinkedList<Edge>> cl = new LinkedList<LinkedList<Edge>>();
    	if(this.vtoSGN.get(Integer.valueOf(query))== null) {
			return cl;
		}
        LinkedList<Integer> qIn = new LinkedList<Integer>((Collection<Integer>)this.vtoSGN.get(Integer.valueOf(query)));
        
        HashSet<Integer> ignidl = new HashSet<Integer>();

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

            Queue<Integer> ignidq = new LinkedList<Integer>();
            LinkedList<Edge> community = null;
            community = new LinkedList<Edge>();
            ignidq.add(qid);
            ignidl.add(qid);
            community.addAll(((SGN)this.idSGN.get(qid)).edgelist);

            while(!ignidq.isEmpty()) {
                Integer ig = (Integer)ignidq.poll();
                Iterator<Integer> var11 = ((Set<Integer>)this.SG.get(ig)).iterator();

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
        }
    }
}
