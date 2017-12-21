package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Map.Entry;

public class Graph {
	public Map<Integer,Map<Integer,Edge>> g;
	long numberOfEdge;
	long numberOfNodes;
	public void read_GraphEdgelist(String fileName) throws IOException{
		this.g = new HashMap<Integer,Map<Integer,Edge>>();
		File f = new File(fileName);
        if(!f.exists()) {
            System.out.println("File does not exist");
            System.exit(0);
        }
        FileReader reader = new FileReader(fileName);
        BufferedReader br = new BufferedReader(reader);
        int noe = 0;
        String line;
        while((line = br.readLine()) != null) {
        		int a = this.processLine(line);
        		if(a == 1) {
        			noe ++;
        		}
        }
        this.numberOfEdge = noe;
        this.numberOfNodes = this.g.size();
        br.close();
        
        int dmax = 0;
        Iterator<Integer> it = g.keySet().iterator();
        while(it.hasNext()) {
        	int i = it.next();
        if(	g.get(i).size() > dmax) {
        	dmax = g.get(i).size();
        }
        }
        
        System.out.println("# of vertices: " + this.g.size());
        System.out.println("# of edges: " + this.numberOfEdge);
        System.out.println("# dmax: " + dmax);
	}
	
	protected int processLine(String aLine) {
		StringTokenizer st = new StringTokenizer(aLine, "\t");
        int id1 = Integer.parseInt(st.nextToken().trim());
        int id2 = Integer.parseInt(st.nextToken().trim());
        if(id1 == id2) {
        	return 0;
        }
        else {
        		Map<Integer,Edge> nl;
        		Edge me = new Edge(id1, id2);
        		
        		//更新节点1
        		if(!this.g.containsKey(Integer.valueOf(id1))) {
        			//如果节点不存在在g中，就需要向g中新增一个HashMap<Integer,Edge>
        			nl = new HashMap<Integer,Edge>();
        			nl.put(id2, me);
        			g.put(id1, nl);
        		}
        		else {
        			//如果节点存在在g中，只需要向已有的HashMap<Integer,Edge>新增
        			if(((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(id1))).containsKey(Integer.valueOf(id2))) {
        				//已存在这个边了
                        return 0;
                 }
        			((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(id1))).put(id2, me);
        		}
        		
        		//更新节点2
        		if(!this.g.containsKey(Integer.valueOf(id2))) {
        			nl = new HashMap<Integer,Edge>();
        			nl.put(Integer.valueOf(id1), me);
        			this.g.put(Integer.valueOf(id2), nl);
        		} else {
                ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(id2))).put(Integer.valueOf(id1), me);
            }
        		return 1;
        }
	}
	
	public Map<Integer, LinkedHashSet<Edge>> computeTruss(String path, Map<Edge, Integer> trussd) throws IOException{
		/*
		 * 计算每条边的support
		 * */
		HashMap<Edge, Integer> sp = new HashMap<Edge, Integer>();
        int kmax = this.computeSupport(sp);
//        System.out.println("#kmax : " + kmax);
        
        /*
         * 将support从小到大重新排序
         * */
        Edge[] sorted_elbys = new Edge[sp.size()];
        Map<Edge, Integer> sorted_ep = new HashMap<Edge, Integer>();
        Map<Integer, Integer> svp = new HashMap<Integer, Integer>();
        bucketSortedgeList(kmax, sp, sorted_elbys, svp, sorted_ep);
		
        int k = 2;
		Map<Integer, LinkedHashSet<Edge>> klistdict = new HashMap<Integer, LinkedHashSet<Edge>>();
		LinkedHashSet<Edge> kedgelist = new LinkedHashSet<Edge>();
		for(int i = 0; i < sorted_elbys.length; ++i) {
			Edge e = sorted_elbys[i];
			int val = ((Integer)sp.get(e)).intValue();
			if(val > k - 2) {
                klistdict.put(Integer.valueOf(k), kedgelist);
                k = val + 2;
                kedgelist = new LinkedHashSet<Edge>();
            }
			Integer src = Integer.valueOf(e.s);
            Integer dst = Integer.valueOf(e.t);
            Set<Integer> nls = ((HashMap<Integer,Edge>)this.g.get(src)).keySet();
            if(nls.size() > ((HashMap<Integer,Edge>)this.g.get(dst)).size()) {
                dst = Integer.valueOf(e.s);
                src = Integer.valueOf(e.t);
                nls = ((HashMap<Integer,Edge>)this.g.get(src)).keySet();
            }
            Iterator<Integer> var18 = nls.iterator();
            
            while(var18.hasNext()) {
                Integer v = (Integer)var18.next();
                if(((HashMap<Integer,Edge>)this.g.get(v)).containsKey(dst)) {
                    Edge e1 = this.getEdge(v.intValue(), src.intValue());
                    Edge e2 = (Edge)((HashMap<Integer,Edge>)this.g.get(v)).get(dst);
                    if(!trussd.containsKey(e1) && !trussd.containsKey(e2)) {
                        if(((Integer)sp.get(e1)).intValue() > k - 2) {
                            reorderEL(sorted_elbys, sorted_ep, sp, svp, e1);
                        }

                        if(((Integer)sp.get(e2)).intValue() > k - 2) {
                            reorderEL(sorted_elbys, sorted_ep, sp, svp, e2);
                        }
                    }
                }
            }
            kedgelist.add(e);
            trussd.put(e, Integer.valueOf(k));
		}
		System.out.println("#kmax : " + k);
		klistdict.put(Integer.valueOf(k), kedgelist);
        return klistdict;
	}
	
//	public int computeSupport2(HashMap<Edge, Integer> sp) {
//		int maxs = 0; 
//		Iterator<Integer> var5 = this.g.keySet().iterator();
//		while(var5.hasNext()) {
//			int v = var5.next();
//			Iterator<Integer> var7 = g.get(v).keySet().iterator();
//			
//		}
//	}
	
    public int computeSupport(HashMap<Edge, Integer> sp) {
    	//计算每条边的support
//        int s = false;
        int maxs = 0; 
        //遍历每个点
        Iterator<Integer> var5 = this.g.keySet().iterator();

        label39:
        while(var5.hasNext()) {
            int v = ((Integer)var5.next()).intValue();
            //遍历每个点的每条边
            Iterator<Integer> var7 = ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(v))).keySet().iterator();

            while(true) {
                int v2;
                do {
                    if(!var7.hasNext()) {
                        continue label39;
                    }

                    v2 = ((Integer)var7.next()).intValue();
                } while(sp.containsKey(((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(v))).get(Integer.valueOf(v2))));

                int s = 0;
                Iterator<Integer> var9 = ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(v))).keySet().iterator();

                while(var9.hasNext()) {
                    int v3 = ((Integer)var9.next()).intValue();
                    if(v2 != v3 && ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(v2))).containsKey(Integer.valueOf(v3))) {
                        ++s;
                    }
                }

                if(s > maxs) {
                    maxs = s;
                }

                sp.put(this.getEdge(v, v2), Integer.valueOf(s));
            }
        }

        return maxs;
    }
    
    private static void bucketSortedgeList(int kmax, HashMap<Edge, Integer> sp, Edge[] sorted_elbys, Map<Integer, Integer> svp, Map<Edge, Integer> sorted_ep) {
        int[] bucket = new int[kmax + 1];

        int tmp;
        for(tmp = 0; tmp < bucket.length; ++tmp) {
            bucket[tmp] = 0;
        }

        Edge ee;
        for(Iterator<Edge> var7 = sp.keySet().iterator(); var7.hasNext(); ++bucket[((Integer)sp.get(ee)).intValue()]) {
            ee = (Edge)var7.next();
        }

        int p = 0;

        int i;
        for(i = 0; i < kmax + 1; ++i) {
            tmp = bucket[i];
            bucket[i] = p;
            p += tmp;
        }

        for(i = 0; i < sorted_elbys.length; ++i) {
            sorted_elbys[i] = null;
        }

        Entry e;
        for(Iterator var9 = sp.entrySet().iterator(); var9.hasNext(); ++bucket[((Integer)e.getValue()).intValue()]) {
            e = (Entry)var9.next();
            sorted_elbys[bucket[((Integer)e.getValue()).intValue()]] = (Edge)e.getKey();
            sorted_ep.put((Edge)e.getKey(), Integer.valueOf(bucket[((Integer)e.getValue()).intValue()]));
            if(!svp.containsKey(e.getValue())) {
                svp.put((Integer)e.getValue(), Integer.valueOf(bucket[((Integer)e.getValue()).intValue()]));
            }
        }
    }
    
    private static void reorderEL(Edge[] sorted_elbys, Map<Edge, Integer> sorted_ep, Map<Edge, Integer> supd, Map<Integer, Integer> svp, Edge e1) {
        int val = ((Integer)supd.get(e1)).intValue();
        int pos1 = ((Integer)sorted_ep.get(e1)).intValue();
        int cp = ((Integer)svp.get(Integer.valueOf(val))).intValue();
        if(cp != pos1) {
            Edge tmp2 = sorted_elbys[cp];
            sorted_ep.put(e1, Integer.valueOf(cp));
            sorted_ep.put(tmp2, Integer.valueOf(pos1));
            sorted_elbys[pos1] = tmp2;
            svp.put(Integer.valueOf(val), Integer.valueOf(cp + 1));
            sorted_elbys[cp] = e1;
        } else if(sorted_elbys.length > cp + 1 && ((Integer)supd.get(sorted_elbys[cp + 1])).intValue() == val) {
            svp.put(Integer.valueOf(val), Integer.valueOf(cp + 1));
        } else {
            svp.put(Integer.valueOf(val), Integer.valueOf(-1));
        }

        if(!svp.containsKey(Integer.valueOf(val - 1)) || ((Integer)svp.get(Integer.valueOf(val - 1))).intValue() == -1) {
            svp.put(Integer.valueOf(val - 1), Integer.valueOf(cp));
        }

        supd.put(e1, Integer.valueOf(val - 1));
    }
    
    Edge removeEdge(int u, int v) {
        if(((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(u))).containsKey(Integer.valueOf(v))) {
            Edge re = (Edge)((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(u))).get(Integer.valueOf(v));
            ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(u))).remove(Integer.valueOf(v));
            ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(v))).remove(Integer.valueOf(u));
            return re;
        } else {
            return null;
        }
    }
    
    Edge getEdge(int u, int v) {
        return (Edge)((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(u))).get(Integer.valueOf(v));
    }
    
    public void write_support(String filename, Map<Edge, Integer> trussd) throws IOException {
        FileWriter fileWriter = new FileWriter(filename);
        BufferedWriter bw = new BufferedWriter(fileWriter);
        Iterator<Edge> var6 = trussd.keySet().iterator();

        while(var6.hasNext()) {
            Edge e = (Edge)var6.next();
            bw.write(Integer.toString(e.s));
            bw.write(",");
            bw.write(Integer.toString(e.t));
            bw.write(",");
            bw.write(Integer.toString(((Integer)trussd.get(e)).intValue()));
            bw.write("\n");
        }

        bw.close();
    }
}
