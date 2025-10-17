import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class Baseline {
    public HashMap<Pair<Integer, Integer>,ArrayList<Integer>> SelectedEdges = new HashMap<>();
    public Map<Integer, HashSet<Integer>> neighbors = new HashMap<Integer, HashSet<Integer>>();
    public Map<Pair<Integer, Integer>,Integer> Support = new HashMap<Pair<Integer, Integer>,Integer>();
    public Map<Integer, HashSet<Integer>> supportneighbors = new HashMap<Integer, HashSet<Integer>>();
    public int start = 0;
    public int end = 0;

    public void SelectedEdge(ReadData readData,int timestart,int timeend,int k,int mts){
        //1、获取【timestart，timeend】之间的所有边 添加邻居 初始化support
        Clear();
        for(int i=timestart;i<=timeend;i++) {
            if (readData.TimeToEdge.containsKey(i)) {
                for (Pair<Integer, Integer> edge : readData.TimeToEdge.get(i)) {
                    if (!Support.containsKey(edge)) {
                        Support.put(edge, 0);
                    }
                    if(SelectedEdges.containsKey(edge)){
                        SelectedEdges.get(edge).add(i);
                    }else{
                        SelectedEdges.put(edge,new ArrayList<Integer>());
                        SelectedEdges.get(edge).add(i);
                    }
                    if (!neighbors.containsKey(edge.getKey())) {
                        neighbors.put(edge.getKey(), new HashSet<Integer>());
                    }
                    neighbors.get(edge.getKey()).add(edge.getValue());
                    if (!neighbors.containsKey(edge.getValue())) {
                        neighbors.put(edge.getValue(), new HashSet<Integer>());
                    }
                }
            }
        }


        //2、计算每一条边的support
        int vertice1=0;
        int vertice2=0;
        int vertice3=0;
        ArrayList<Integer> timestamp1 = new ArrayList<Integer>();
        ArrayList<Integer> timestamp2 = new ArrayList<Integer>();
        ArrayList<Integer> timestamp3 = new ArrayList<Integer>();
        for (Pair<Integer, Integer> edge:SelectedEdges.keySet()){
            vertice1=edge.getKey();
            vertice2=edge.getValue();
            Set<Integer> set1 = new HashSet<>();
            Set<Integer> set2 = new HashSet<>();
            //获取两个点的共同邻居
            for (int i:this.neighbors.get(vertice1)){
                set1.add(i);
            }
            for (int i:this.neighbors.get(vertice2)){
                set2.add(i);
            }
            set1.retainAll(set2);
            if(!set1.isEmpty()){
                for(int i:set1){
                    vertice3 = i;
                    if(vertice1 < vertice2 && vertice2 < vertice3){
                        timestamp1 = SelectedEdges.get(edge);
                        timestamp2 = SelectedEdges.get(Pair.of(vertice2,vertice3));
                        timestamp3 = SelectedEdges.get(Pair.of(vertice1,vertice3));
                        boolean fillmts=false;
                        for(int t1:timestamp1){
                            if (fillmts){
                                break;
                            }
                            for(int t2:timestamp2){
                                if (fillmts){
                                    break;
                                }
                                for(int t3:timestamp3){
                                    int maxt=Math.max(t1,Math.max(t2,t3));
                                    int mint=Math.min(t1,Math.min(t2,t3));
                                    int tempmts=maxt-mint;
                                    if(tempmts<=mts){
                                        fillmts=true;
                                    }
                                    if(fillmts){
                                        break;
                                    }
                                }
                            }
                        }
                        if(fillmts){
                            Support.put(edge,Support.get(edge)+1);
                            Support.put(Pair.of(vertice2,vertice3),Support.get(Pair.of(vertice2,vertice3))+1);
                            Support.put(Pair.of(vertice1,vertice3),Support.get(Pair.of(vertice1,vertice3))+1);
                            if (!supportneighbors.containsKey(vertice1)) {
                                supportneighbors.put(vertice1, new HashSet<Integer>());
                            }
                            supportneighbors.get(vertice1).add(vertice2);
                            supportneighbors.get(vertice1).add(vertice3);
                            if (!supportneighbors.containsKey(vertice2)) {
                                supportneighbors.put(vertice2, new HashSet<Integer>());
                            }
                            supportneighbors.get(vertice2).add(vertice1);
                            supportneighbors.get(vertice2).add(vertice3);
                            if (!supportneighbors.containsKey(vertice3)) {
                                supportneighbors.put(vertice3, new HashSet<Integer>());
                            }
                            supportneighbors.get(vertice3).add(vertice1);
                            supportneighbors.get(vertice3).add(vertice2);
                        }
                    }
                }
            }
        }

        //3、根据support进行k-truss分解
        Queue<Pair<Integer, Integer>> queue = new LinkedList<>();
        for (Pair<Integer, Integer> edge:Support.keySet()){
            if(Support.get(edge)<k){
                queue.add(edge);
            }
        }
        while(!queue.isEmpty()) {
            Pair<Integer, Integer> edge = queue.poll();
            int tempv1 = edge.getKey();
            int tempv2 = edge.getValue();
            //获取两个点的共同邻居  这里的邻居表示能够提供边支持度的邻居
            Set<Integer> tempset1 = new HashSet<>();
            if (this.supportneighbors.get(tempv1) != null) {
                tempset1 = new HashSet<>(this.supportneighbors.get(tempv1));
            }
            Set<Integer> tempset2 = new HashSet<>();
            if (this.supportneighbors.get(tempv2) != null) {
                tempset2 = new HashSet<>(this.supportneighbors.get(tempv2));
            }
            tempset1.retainAll(tempset2);
            if (this.supportneighbors.get(tempv1)!=null){
                this.supportneighbors.get(tempv1).remove(tempv2);
            }
            if (this.supportneighbors.get(tempv2)!=null){
                this.supportneighbors.get(tempv2).remove(tempv1);
            }
            if (!tempset1.isEmpty()) {
                for (int i : tempset1) {
                    if (i < tempv1 && tempv1 < tempv2) {
                        // i <tempv1 < tempv2
                        if (Support.containsKey(Pair.of(i, tempv1))) {
                            Support.put(Pair.of(i, tempv1), Support.get(Pair.of(i, tempv1)) - 1);
                            if (Support.get(Pair.of(i, tempv1)) < k &&!queue.contains(Pair.of(i, tempv1))) {
                                queue.add(Pair.of(i, tempv1));
                            }
                        }
                        if (Support.containsKey(Pair.of(i, tempv2))) {
                            Support.put(Pair.of(i, tempv2), Support.get(Pair.of(i, tempv2)) - 1);
                            if (Support.get(Pair.of(i, tempv2)) < k &&!queue.contains(Pair.of(i, tempv2))) {
                                queue.add(Pair.of(i, tempv2));
                            }
                        }
                    } else if (i > tempv2 && tempv1 < tempv2) {
                        //i>tempv2>tempv1
                        if (Support.containsKey(Pair.of(tempv1, i))) {
                            Support.put(Pair.of(tempv1, i), Support.get(Pair.of(tempv1, i)) - 1);
                            if (Support.get(Pair.of(tempv1, i)) < k &&!queue.contains(Pair.of(tempv1, i))) {
                                queue.add(Pair.of(tempv1, i));
                            }
                        }
                        if (Support.containsKey(Pair.of(tempv2, i))) {
                            Support.put(Pair.of(tempv2, i), Support.get(Pair.of(tempv2, i)) - 1);
                            if (Support.get(Pair.of(tempv2, i)) < k &&!queue.contains(Pair.of(tempv2, i))) {
                                queue.add(Pair.of(tempv2, i));
                            }
                        }
                    } else {
                        //tempv1<i<tempv2
                        if (Support.containsKey(Pair.of(tempv1, i))) {
                            Support.put(Pair.of(tempv1, i), Support.get(Pair.of(tempv1, i)) - 1);
                            if (Support.get(Pair.of(tempv1, i)) < k &&!queue.contains(Pair.of(tempv1, i))) {
                                queue.add(Pair.of(tempv1, i));
                            }
                        }
                        if (Support.containsKey(Pair.of(i, tempv2))) {
                            Support.put(Pair.of(i, tempv2), Support.get(Pair.of(i, tempv2)) - 1);
                            if (Support.get(Pair.of(i, tempv2)) < k &&!queue.contains(Pair.of(i, tempv2))) {
                                queue.add(Pair.of(i, tempv2));
                            }
                        }
                    }
                }
            }
            Support.remove(edge);
        }
        StartAndEnd();
        //System.out.println(start+" "+end);
        //PrintSupport();

//        for (Pair<Integer, Integer> edge : Support.keySet()){
//            for (int i : SelectedEdges.get(edge)){
//                System.out.println(edge.getKey()+" "+edge.getValue()+" "+i);
//            }
//        }
    }

    public void StartAndEnd(){
        int mint=100000;
        int maxt=-1;
        for(Pair<Integer, Integer> edge:Support.keySet()){
            for(Integer time:SelectedEdges.get(edge)){
                if(time<mint){
                    mint=time;
                }
                if(time>maxt){
                    maxt=time;
                }
            }
        }
        start=mint;
        end=maxt;
    }
    public void Clear(){
        Support.clear();
        SelectedEdges.clear();
        neighbors.clear();
        supportneighbors.clear();
    }

    public void PrintSupport(){
        for(Pair<Integer, Integer> edge:Support.keySet()){
            System.out.println(edge.getKey()+" "+edge.getValue()+" "+Support.get(edge));
        }
    }
}
