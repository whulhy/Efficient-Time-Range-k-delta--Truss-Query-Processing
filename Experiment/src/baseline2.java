import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class baseline2 {
    public HashMap<Pair<Integer, Integer>,ArrayList<Integer>> SelectedEdges = new HashMap<>();
    public Map<Integer, HashSet<Integer>> neighbors = new HashMap<Integer, HashSet<Integer>>();
    public Map<Pair<Integer, Integer>,Integer> Support = new HashMap<Pair<Integer, Integer>,Integer>();
    public Map<Integer, HashSet<Integer>> supportneighbors = new HashMap<Integer, HashSet<Integer>>();
    public Set<String> triangle = new HashSet<>();
    public int start = 0;
    public int end = 0;

    public void SelectedEdge(ReadData readData,int timestart,int timeend,int k,int mts)
    {
        //1、获取【timestart，timeend】之间的所有边 添加邻居 初始化support
        for(int i=timestart;i<=timeend;i++) {
            if (readData.TimeToEdge.containsKey(i)){
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
                   if (!neighbors.containsKey(edge.getKey())){
                       neighbors.put(edge.getKey(),new HashSet<Integer>());
                   }
                   neighbors.get(edge.getKey()).add(edge.getValue());
                    if (!neighbors.containsKey(edge.getValue())) {
                        neighbors.put(edge.getValue(), new HashSet<Integer>());
                    }
                }
            }
        }
        CalculateSupport(mts);
        Iterator<Pair<Integer, Integer>> iterator = Support.keySet().iterator();
        while (iterator.hasNext()) {
            Pair<Integer, Integer> edge = iterator.next();
            if (Support.get(edge) == 0) {
                iterator.remove();
            }
        }
        TrussDecompose(k);
        StartAndEnd(timestart,timeend);
    }

    public void CalculateSupport(int mts)
    {
        int vertice1=0;
        int vertice2=0;
        int vertice3=0;
        ArrayList<Integer> timestamp1 = new ArrayList<Integer>();
        ArrayList<Integer> timestamp2 = new ArrayList<Integer>();
        ArrayList<Integer> timestamp3 = new ArrayList<Integer>();
        for (Pair<Integer, Integer> edge:SelectedEdges.keySet()){
            vertice1=edge.getKey();
            vertice2=edge.getValue();
            //获取两个点的共同邻居
            Set<Integer> set1 = new HashSet<>(this.neighbors.get(vertice1));
            Set<Integer> set2 = new HashSet<>(this.neighbors.get(vertice2));
            set1.retainAll(set2);
            if(!set1.isEmpty()){
                for(int i:set1){
                    vertice3 = i;
                    timestamp1 = SelectedEdges.get(edge);
                    timestamp2 = SelectedEdges.get(Pair.of(vertice2,vertice3));
                    timestamp3 = SelectedEdges.get(Pair.of(vertice1,vertice3));
                    int tempmts=Math.max(timestamp1.get(0),Math.max(timestamp2.get(0),timestamp3.get(0)))-Math.min(timestamp1.get(0),Math.min(timestamp2.get(0),timestamp3.get(0)));
                    for (int t1:timestamp1){
                        for (int t2:timestamp2){
                            for (int t3:timestamp3){
                                int ttempmts=Math.max(t1,Math.max(t2,t3))-Math.min(t1,Math.min(t2,t3));
                                if (ttempmts<tempmts){
                                    tempmts=ttempmts;
                                }
                            }
                        }
                    }
                    if (tempmts<=mts){
                        Support.put(edge,Support.get(edge)+1);
                        Support.put(Pair.of(vertice2,vertice3),Support.get(Pair.of(vertice2,vertice3))+1);
                        Support.put(Pair.of(vertice1,vertice3),Support.get(Pair.of(vertice1,vertice3))+1);
                        triangle.add(vertice1+"-"+vertice2+"-"+vertice3);
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

    public void TrussDecompose(int k)
    {
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
                    int tempv3=i;
                    int[] temparray={edge.getKey(),edge.getValue(),tempv3};
                    Arrays.sort(temparray);
                    tempv1=temparray[0];
                    tempv2=temparray[1];
                    tempv3=temparray[2];
                    if (triangle.contains(tempv1+"-"+tempv2+"-"+tempv3)){
                        if (Support.containsKey(Pair.of(tempv1, tempv2))){
                            if (Support.get(Pair.of(tempv1, tempv2))>=k){
                                Support.put(Pair.of(tempv1, tempv2),Support.get(Pair.of(tempv1, tempv2))-1);
                                if (Support.get(Pair.of(tempv1, tempv2))<k){
                                    queue.add(Pair.of(tempv1, tempv2));
                                }
                            }
                        }
                        if (Support.containsKey(Pair.of(tempv1, tempv3))){
                            if (Support.get(Pair.of(tempv1, tempv3))>=k){
                                Support.put(Pair.of(tempv1, tempv3),Support.get(Pair.of(tempv1, tempv3))-1);
                                if (Support.get(Pair.of(tempv1, tempv3))<k){
                                    queue.add(Pair.of(tempv1, tempv3));
                                }
                            }
                        }
                        if (Support.containsKey(Pair.of(tempv2, tempv3))){
                            if (Support.get(Pair.of(tempv2, tempv3))>=k){
                                Support.put(Pair.of(tempv2, tempv3),Support.get(Pair.of(tempv2, tempv3))-1);
                                if (Support.get(Pair.of(tempv2, tempv3))<k){
                                    queue.add(Pair.of(tempv2, tempv3));
                                }
                            }
                        }
                        triangle.remove(tempv1+"-"+tempv2+"-"+tempv3);
                    }
                }
            }
            Support.remove(edge);
        }
    }

    public void StartAndEnd(int s,int e)
    {
        int mint=e;
        int maxt=s;
        if(Support.isEmpty()){
            start=-1;
            end=-1;
            return;
        }
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


    public void DeleteTime(ReadData readData,int sTime,int etime,int mts,int k){
        if(sTime>etime){
            return;
        }
        Set<Pair<Integer, Integer>> edges=new HashSet<>();
        ArrayList<Integer> timestamp1 = new ArrayList<Integer>();
        ArrayList<Integer> timestamp2 = new ArrayList<Integer>();
        ArrayList<Integer> timestamp3 = new ArrayList<Integer>();
        //将时间区间变化的边记录下来
        for (int time=sTime;time<=etime;time++){
            if (readData.TimeToEdge.containsKey(time)){
                edges.addAll(readData.TimeToEdge.get(time));
            }
        }
        //将变化的边的时间戳变化
        for (Pair<Integer, Integer> edge:edges){
            SelectedEdges.get(edge).removeIf(temp -> temp >= sTime && temp <= etime);
        }
        //重新计算变化过的三角形的mts值
        for (Pair<Integer, Integer> edge:edges) {
            int v1 = edge.getKey();
            int v2 = edge.getValue();
            Set<Integer> tempset1 = new HashSet<>();
            if (this.supportneighbors.get(v1) != null) {
                tempset1 = new HashSet<>(this.supportneighbors.get(v1));
            }
            Set<Integer> tempset2 = new HashSet<>();
            if (this.supportneighbors.get(v2) != null) {
                tempset2 = new HashSet<>(this.supportneighbors.get(v2));
            }
            tempset1.retainAll(tempset2);
            if (!tempset1.isEmpty()) {
                for (int i : tempset1) {
                    int v3 = i;
                    int[] temparray={edge.getKey(),edge.getValue(),v3};
                    Arrays.sort(temparray);
                    v1=temparray[0];
                    v2=temparray[1];
                    v3=temparray[2];
                    timestamp1 = SelectedEdges.get(edge);
                    timestamp2 = SelectedEdges.get(Pair.of(v2,v3));
                    timestamp3 = SelectedEdges.get(Pair.of(v1,v3));
                    if (timestamp1.isEmpty()||timestamp2.isEmpty()||timestamp3.isEmpty()){
                        if (Support.containsKey(edge)) {
                            Support.put(edge, Support.get(edge) - 1);
                        }
                        if (Support.containsKey(Pair.of(v2,v3))) {
                            Support.put(Pair.of(v2, v3), Support.get(Pair.of(v2, v3)) - 1);
                        }
                        if (Support.containsKey(Pair.of(v1,v3))) {
                            Support.put(Pair.of(v1, v3), Support.get(Pair.of(v1, v3)) - 1);
                        }
                        continue;
                    }
                    int tempmts=Math.max(timestamp1.get(0),Math.max(timestamp2.get(0),timestamp3.get(0)))-Math.min(timestamp1.get(0),Math.min(timestamp2.get(0),timestamp3.get(0)));
                    for (int t1:timestamp1){
                        for (int t2:timestamp2){
                            for (int t3:timestamp3){
                                int ttempmts=Math.max(t1,Math.max(t2,t3))-Math.min(t1,Math.min(t2,t3));
                                if (ttempmts<tempmts){
                                    tempmts=ttempmts;
                                }
                            }
                        }
                    }
                    //如果改变后的三角形的mts值大于约束值，则将对应边的support-1
                    if (tempmts>mts){
                        if (triangle.contains(v1+"-"+v2+"-"+v3)){
                            if (Support.containsKey(edge)) {
                                Support.put(edge, Support.get(edge) - 1);
                            }
                            if (Support.containsKey(Pair.of(v2,v3))) {
                                Support.put(Pair.of(v2, v3), Support.get(Pair.of(v2, v3)) - 1);
                            }
                            if (Support.containsKey(Pair.of(v1,v3))) {
                                Support.put(Pair.of(v1, v3), Support.get(Pair.of(v1, v3)) - 1);
                            }
                            triangle.remove(v1+"-"+v2+"-"+v3);
                        }
                    }
                }
            }
        }
        //分解
        TrussDecompose(k);
        StartAndEnd(start,end);
    }

    public void Copy(baseline2 baseline){
        this.start=baseline.start;
        this.end=baseline.end;
        for (Map.Entry<Pair<Integer, Integer>, ArrayList<Integer>> edge:baseline.SelectedEdges.entrySet()){
            ArrayList<Integer> arrayList=new ArrayList<>(edge.getValue());
            this.SelectedEdges.put(Pair.of(edge.getKey().getLeft(),edge.getKey().getRight()),arrayList);
        }
        for (Map.Entry<Integer,HashSet<Integer>> neighbor:baseline.neighbors.entrySet()){
            Integer newKey=neighbor.getKey();
            HashSet<Integer> newSet=new HashSet<>(neighbor.getValue());
            this.neighbors.put(newKey,newSet);
        }
        for (Map.Entry<Pair<Integer, Integer>,Integer> support:baseline.Support.entrySet()){
            Integer newValue=support.getValue();
            this.Support.put(Pair.of(support.getKey().getLeft(),support.getKey().getRight()),newValue);
        }
        for (Map.Entry<Integer, HashSet<Integer>> supportNeighbor:baseline.supportneighbors.entrySet()){
            Integer newKey=supportNeighbor.getKey();
            HashSet<Integer> newSet=new HashSet<>(supportNeighbor.getValue());
            this.supportneighbors.put(newKey,newSet);
        }
        for (String tri : baseline.triangle){
            String newTri=tri;
            this.triangle.add(newTri);
        }
    }

    public  void Clear(){
        this.supportneighbors.clear();
        this.triangle.clear();
        this.Support.clear();
        this.neighbors.clear();
        this.SelectedEdges.clear();
    }

    public void PrintSupport(){
        for(Pair<Integer, Integer> edge:Support.keySet()){
            System.out.println(edge.getKey()+" "+edge.getValue()+" "+Support.get(edge));
        }
    }

}
