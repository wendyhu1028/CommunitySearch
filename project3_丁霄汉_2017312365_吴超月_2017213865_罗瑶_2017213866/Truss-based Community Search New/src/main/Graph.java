package main;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public class Graph {
	Map<Integer,Map<Integer,Edge>> g;
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
                //读入一条边处理
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
		StringTokenizer st = new StringTokenizer(aLine, "\t ");
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
	
	public Map<Integer, LinkedHashSet<Edge>> computeTruss(String path) throws IOException{
		ArrayList<Edge> sp2 = new ArrayList<Edge>((int) numberOfEdge);
		this.computeSupport(sp2);
		System.out.println("computeSupport complete!");
		
		MyComprator com = new MyComprator();
		sp2.sort(com);
		System.out.println("sort complete!");
		
        //根据support从小到大排序的边的数组
		Edge[] sorted_elbys = new Edge[sp2.size()];
        //每条边在sorted_elbys中的位置
		Map<Edge, Integer> sorted_ep = new HashMap<Edge, Integer>();
        //每个k对应的边的集合在数组的开始位置
        Map<Integer, Integer> svp = new HashMap<Integer, Integer>();
        int s = -1;
		for(int i = 0 ; i < sp2.size() ; i ++) {
			Edge e = sp2.get(i);;
			sorted_elbys[i] = e;
			sorted_ep.put(e, i);
			if(e.support != s) {
				svp.put(e.support, i);
				s = e.support;
			}
		}
		
		int k = 2;
        //klistdict为不同k与truss为k的边的集合的map
		Map<Integer, LinkedHashSet<Edge>> klistdict = new HashMap<Integer, LinkedHashSet<Edge>>();
		LinkedHashSet<Edge> kedgelist = new LinkedHashSet<Edge>();
		for(int i = 0; i < sorted_elbys.length; ++i) {
			Edge e = sorted_elbys[i];
			int val = e.support;
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
                        Edge e2 = this.g.get(v).get(dst);
                    if(e1.truss < 0 && e2.truss < 0) {
                        if(e1.support > k - 2) {
                            //更细e1的k值并重排，当然变动非常小，时间复杂度为常数，只需要更换位置
                            reorderEL2(sorted_elbys, sorted_ep, svp, e1);
                        }

                        if(e2.support > k - 2) {
                            reorderEL2(sorted_elbys, sorted_ep, svp, e2);
                        }
                    }
            		}
            }
            kedgelist.add(e);
            e.truss = k;
		}
		System.out.println("#kmax : " + k);
		klistdict.put(Integer.valueOf(k), kedgelist);
        return klistdict;
	}
	
    /*
     计算每条边的support，就是包含在几个三角形里面
     */
	public int computeSupport(ArrayList<Edge> sp) {
		long startTime = System.nanoTime();
        
		int maxs = 0; 
		Iterator<Integer> var5 = this.g.keySet().iterator();
		while(var5.hasNext()) {
			int v = var5.next();
			Iterator<Integer> var7 = g.get(v).keySet().iterator();
			while(var7.hasNext()) {
				int v2 = var7.next();
//				if(sp.contains(g.get(v).get(v2))) {
//					continue;
//				}
                //为了避免一条边重复计算，所以需要限制v2 < v
				if(v2 < v) {
					continue;
				}
				int s = 0;
                Iterator<Integer> var9 = ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(v))).keySet().iterator();
                while(var9.hasNext()) {
                    int v3 = ((Integer)var9.next()).intValue();
                    if(v2 != v3 && ((HashMap<Integer,Edge>)this.g.get(Integer.valueOf(v2))).containsKey(Integer.valueOf(v3))) {
                        //找到一个点与v和v2均有边
                        ++s;
                    }
                }
                if(s > maxs) {
                    maxs = s;
                }
                
                this.getEdge(v, v2).support = s;
                sp.add(this.getEdge(v, v2));
			}
		}
		long endTime = System.nanoTime();
		System.out.println("support time:"+(double)(endTime - startTime) / 1.0E9D);
		return maxs;
	}
	
    private static void reorderEL2(Edge[] sorted_elbys, Map<Edge, Integer> sorted_ep, Map<Integer, Integer> svp, Edge e1) {
    		int val = e1.support;
        int pos1 = ((Integer)sorted_ep.get(e1)).intValue();
        int cp = ((Integer)svp.get(Integer.valueOf(val))).intValue();
        if(cp != pos1) {
            //如果e1不是数组中同一support的list的第一个对象。将list第一个对象和e1互换
            Edge tmp2 = sorted_elbys[cp];
            sorted_ep.put(e1, Integer.valueOf(cp));
            sorted_ep.put(tmp2, Integer.valueOf(pos1));
            sorted_elbys[pos1] = tmp2;
            svp.put(Integer.valueOf(val), Integer.valueOf(cp + 1));
            sorted_elbys[cp] = e1;
        } else if(sorted_elbys.length > cp + 1 && sorted_elbys[cp + 1].support == val) {
            svp.put(Integer.valueOf(val), Integer.valueOf(cp + 1));
        } else {
            svp.put(Integer.valueOf(val), Integer.valueOf(-1));
        }

        if(!svp.containsKey(Integer.valueOf(val - 1)) || ((Integer)svp.get(Integer.valueOf(val - 1))).intValue() == -1) {
            svp.put(Integer.valueOf(val - 1), Integer.valueOf(cp));
        }

        e1.support = val - 1;
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
    class MyComprator implements Comparator<Edge> {
		@Override
		public int compare(Edge o1, Edge o2) {
			// TODO Auto-generated method stub
			if(o1.support < o2.support) {
				return -1;
			}
			else if(o1.support > o2.support) {
				return 1;
			}
			else {
				return 0;
			}
		}
	}
}
