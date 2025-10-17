import org.apache.commons.lang3.tuple.Pair;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;
import java.sql.Time;
import java.util.*;

public class MtsTable {
    //1、计算所有三角形的mts并存储 map<string,map<integer,pair<integer,integer>>>
    // 2、对mts进行裁剪
    //3、根据裁剪后的mts建立mtstable
    int n=0;
    int m=0;
    int trinum=0;

    // 辅助方法，将三条边排序并转换为字符串
    private static String generateKey(int a, int b, int c,int d, int e) {
        int[] sides = {a, b, c,d,e};
        return sides[0] + "_" + sides[1] + "_" + sides[2]+"_" + sides[3]+"_"+sides[4];
    }

    /**
     * 计算给定图中的所有三角形的mts。
     * 该方法遍历图中的所有顶点对，找出它们的共同邻居，并计算由这三个顶点形成的三角形的最短时间差。
     * 最短时间差三角形是指三角形的三条边的标注时间之差的最小值。
     *
     * @param readData 包含图的邻接关系和边的时间戳的数据结构。
     */
    public void CalculateMts(ReadData readData,String filePath) throws IOException {
        // 初始化用于存储顶点和时间戳的变量
        int vertice1 = 0;
        int vertice2 = 0;
        int vertice3 = 0;
        int maxt = 0;
        int mint = 0;
        int mts = 0;
        ArrayList<Integer> timestamp1 = new ArrayList<>();
        ArrayList<Integer> timestamp2 = new ArrayList<>();
        ArrayList<Integer> timestamp3 = new ArrayList<>();
        // 创建一个临时列表，用于存储当前键对应的所有时间对
        ArrayList<Pair<Integer, Integer>> tempTimePair = new ArrayList<>();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))){
        // 遍历所有顶点对
        for (int key1 : readData.neighbors.keySet()) {
            vertice1 = key1;
            for (int key2 : readData.neighbors.get(key1)) {
                vertice2 = key2;
                // 计算顶点1和顶点2的共同邻居
                //获取两个点的共同邻居
                Set<Integer> set1 = new HashSet<>(readData.neighbors.get(vertice1));
                Set<Integer> set2 = new HashSet<>(readData.neighbors.get(vertice2));
                set1.retainAll(set2);
                trinum=trinum+set1.size();
                // 如果有共同邻居，则进一步处理
                if (!set1.isEmpty()) {
                    for (int key3 : set1) {
                        vertice3 = key3;
                        //TreeMap<Integer, ArrayList<Pair<Integer, Integer>>> TempInnerMap = new TreeMap<>();
                        // 确保顶点顺序符合三角形的定义
                        if (vertice1 < vertice2 && vertice2 < vertice3) {
                            // 获取三条边的时间戳
                            timestamp1 = readData.EdgeToTime.get(vertice1+""+vertice2);
                            timestamp2 = readData.EdgeToTime.get(vertice1+""+vertice3);
                            timestamp3 = readData.EdgeToTime.get(vertice2+""+vertice3);
                            // 计算mts并存储
                            for (int t1 : timestamp1) {
                                for (int t2 : timestamp2) {
                                    for (int t3 : timestamp3) {
                                        maxt = Math.max(t1, Math.max(t2, t3));
                                        mint = Math.min(t1, Math.min(t2, t3));
                                        mts = maxt - mint;
                                        tempTimePair.add(Pair.of(mint, maxt));
                                    }
                                }
                            }
                            n=n+tempTimePair.size();
                            // 如果临时映射非空，剪切后，将结果添加到最终的结果表中
                            if (!tempTimePair.isEmpty()) {
                                // 创建一个临时映射表，用于存储剪切后的MTS条目
                                TreeMap<Integer, ArrayList<Pair<Integer, Integer>>> TempTempInnerMap = new TreeMap<>(Comparator.reverseOrder());
                                // 对临时列表中的时间对按时间差进行排序
                                tempTimePair.sort(((o1, o2) -> Integer.compare(o2.getRight() - o2.getLeft(), o1.getRight() - o1.getLeft())));
                                // 遍历排序后的时间对列表
                                int TminT = tempTimePair.get(0).getRight();
                                int TmaxT = tempTimePair.get(0).getLeft();
                                for (int i = 0; i <= tempTimePair.size() - 1; i++) {
                                    // 标记是否找到重复的时间对
                                    int flag = 0;
                                    // 获取当前时间对的起始和结束时间
                                    int first = tempTimePair.get(i).getLeft();
                                    int second = tempTimePair.get(i).getRight();
                                    // 检查后续时间对是否与当前时间对重复（一端重复）
                                    for (int j = i + 1; j < tempTimePair.size(); j++) {
                                        int first1 = tempTimePair.get(j).getLeft();
                                        int second1 = tempTimePair.get(j).getRight();
                                        if (first == first1 || second == second1) {
                                            flag = 1;
                                            break;
                                        }
                                    }
                                    // 如果没有找到重复时间对，则将当前时间对添加到临时映射表中
                                    if (flag == 0) {
                                        if (TempTempInnerMap.containsKey(tempTimePair.get(i).getRight() - tempTimePair.get(i).getLeft())) {
                                            TempTempInnerMap.get(tempTimePair.get(i).getRight() - tempTimePair.get(i).getLeft()).add(tempTimePair.get(i));
                                            if (TminT > tempTimePair.get(i).getLeft()) {
                                                TminT = tempTimePair.get(i).getLeft();
                                            }
                                            if (TmaxT < tempTimePair.get(i).getRight()) {
                                                TmaxT = tempTimePair.get(i).getRight();
                                            }
                                        } else {
                                            ArrayList<Pair<Integer, Integer>> TempList = new ArrayList<>();
                                            TempList.add(tempTimePair.get(i));
                                            TempTempInnerMap.put(tempTimePair.get(i).getRight() - tempTimePair.get(i).getLeft(), TempList);
                                            if (TminT > tempTimePair.get(i).getLeft()) {
                                                TminT = tempTimePair.get(i).getLeft();
                                            }
                                            if (TmaxT < tempTimePair.get(i).getRight()) {
                                                TmaxT = tempTimePair.get(i).getRight();
                                            }
                                        }
                                    }
                                }
                                String triangleString = generateKey(vertice1, vertice2, vertice3, TminT, TmaxT);
                                writer.write(triangleString+" ");
                                for (Map.Entry<Integer, ArrayList<Pair<Integer, Integer>>> entry : TempTempInnerMap.entrySet()){
                                    Integer key = entry.getKey();
                                    ArrayList<Pair<Integer, Integer>> value = entry.getValue();
                                    m=m+value.size();
                                    for (Pair<Integer, Integer> pair : value) {
                                        writer.write(key.toString()+pair.toString()+" ");
                                    }
                                }
                                writer.newLine();
                                writer.flush();
                                //MtsTableAll.put(triangleString, TempTempInnerMap);
                                tempTimePair.clear();
                            }
                        }
                    }
                }
            }
        }
    }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void CalculateMts1(ReadData readData,String filePath) throws IOException {
        // 初始化用于存储顶点和时间戳的变量
        int vertice1 = 0;
        int vertice2 = 0;
        int vertice3 = 0;
        int maxt = 0;
        int mint = 0;
        int mts = 0;
        ArrayList<Integer> timestamp1 = new ArrayList<>();
        ArrayList<Integer> timestamp2 = new ArrayList<>();
        ArrayList<Integer> timestamp3 = new ArrayList<>();
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filePath,false))) {
            // 遍历所有顶点对
            for (int key1 : readData.neighbors.keySet()) {
                vertice1 = key1;
                for (int key2 : readData.neighbors.get(key1)) {
                    vertice2 = key2;
                    // 计算顶点1和顶点2的共同邻居
                    //获取两个点的共同邻居
                    Set<Integer> set1 = new HashSet<>(readData.neighbors.get(vertice1));
                    Set<Integer> set2 = new HashSet<>(readData.neighbors.get(vertice2));
                    set1.retainAll(set2);
                    // 如果有共同邻居，则进一步处理
                    if (!set1.isEmpty()) {
                        for (int key3 : set1) {
                            vertice3 = key3;
                            TreeMap<Integer, ArrayList<Pair<Integer, Integer>>> TempInnerMap = new TreeMap<>();
                            // 确保顶点顺序符合三角形的定义
                            // 获取三条边的时间戳
                            timestamp1 = readData.EdgeToTime.get(vertice1 + "" + vertice2);
                            timestamp2 = readData.EdgeToTime.get(vertice1 + "" + vertice3);
                            timestamp3 = readData.EdgeToTime.get(vertice2 + "" + vertice3);
                            // 计算mts并存储
                            for (int t1 : timestamp1) {
                                for (int t2 : timestamp2) {
                                    for (int t3 : timestamp3) {
                                        maxt = Math.max(t1, Math.max(t2, t3));
                                        mint = Math.min(t1, Math.min(t2, t3));
                                        mts = maxt - mint;
                                        if (TempInnerMap.containsKey(mts)) {
                                            if (!TempInnerMap.get(mts).contains(Pair.of(mint, maxt))) {
                                                TempInnerMap.get(mts).add(Pair.of(mint, maxt));
                                            }
                                        } else {
                                            ArrayList<Pair<Integer, Integer>> TempList = new ArrayList<>();
                                            TempList.add(Pair.of(mint, maxt));
                                            TempInnerMap.put(mts, TempList);
                                        }
                                    }
                                }
                            }
                            // 如果临时映射非空，剪切后，将结果添加到最终的结果表中
                            if (!TempInnerMap.isEmpty()) {
                                String key = generateKey(vertice1, vertice2, vertice3,0,0);
                                writer.write(key+" ");
                                for (Map.Entry<Integer, ArrayList<Pair<Integer, Integer>>> entry : TempInnerMap.entrySet()){
                                    Integer k = entry.getKey();
                                    ArrayList<Pair<Integer, Integer>> value = entry.getValue();
                                    for (Pair<Integer, Integer> pair : value) {
                                        writer.write(k.toString()+pair.toString()+" ");
                                    }
                                }
                                writer.newLine();
                                writer.flush();
                            }
                        }
                    }
                }
            }
        }
    }


//经过了上面的方法，我们得到了所有的三角形在全局时间段的mts值。我们后面要做的事是对于一个时间区间，我们判断这个三角形是否有
//满足条件的mts值，如果有的话，我们需要为这个三角形创建一个节点，将这个节点放到一个数组中，数组的下标就是三角形的id,对于这个三角形存在
//的mts，我们为每一对时间对创建一个timespan节点，并将时间对的头与三角形节点相连，时间对的尾与时间对的头相连。插入使用头插法比较好。

    public ArrayList<Triangle> TriangleList = new ArrayList<>();
    public ArrayList<TimeList> TimeListHead = new ArrayList<>();

    //索引访问方式 time-start  **应该需要修改，中间有一些时间区间是没有三角形存在的，我们可以考虑不创建它的时间节点
    public void createTimeNode(int start, int end) {
        if (!TimeListHead.isEmpty()) {
            TimeListHead.clear();
        }
        if (start > end) {
            System.out.println("开始时间大于结束时间");
            return;
        }
        TimeList timeList = new TimeList(start, null, null);
        TimeListHead.add(timeList);
        for (int i = start + 1; i <= end; i++) {
            TimeList timeList1 = new TimeList(i, TimeListHead.get(i - 1 - start), null);
            TimeListHead.get(i - 1 - start).nextTime = timeList1;
            TimeListHead.add(timeList1);
        }
    }

    public List<Integer> extractNumbers(String input){
        List<Integer> list = new ArrayList<>();
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        while (matcher.find()) {
            // 将匹配到的字符串转换为整数并添加到列表中
            list.add(Integer.parseInt(matcher.group()));
        }
        return list;
    }

    public void createTriangleNode(int start, int end,String fileName) throws FileNotFoundException {
        int id = -1;
        createTimeNode(start, end);
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null){
                String[] tokens = line.split(" ");
                String[] edge = tokens[0].split("_");
//                if (Integer.parseInt(edge[3])>end||Integer.parseInt(edge[4])<start){
//                    continue;
//                }
                boolean firstTriangle = true;
                for (int i=1;i<=tokens.length-1;i++){
                    List<Integer> Mts = extractNumbers(tokens[i]);
                    //if (Mts.get(1) >=start && Mts.get(2) <=end){
                        //如果对于一个三角形来说，它有一个mts值的区间在我们所给的区间内，如果还没有创建这个三角形，我们就创建这个三角形，并给它一个id。
                        if (firstTriangle) {
                            id++;
                            TriangleList.add(new Triangle(id, Integer.parseInt(edge[0]), Integer.parseInt(edge[1]), Integer.parseInt(edge[2])));
                            firstTriangle = false;
                        }
                        //如果创建了这个三角形之后，我们要根据这个mts对应的Pair<，>来创建对应的TriangleTimeSpan节点，并连接到这个三角形节点和对应的时间节点上。
                        //首先是创建pair的左边部分，即一个区间的头节点,然后创建一个尾结点，然后连接起来。
                        TriangleTimeSpan timeSpanHead = new TriangleTimeSpan(id, true, Mts.get(0));
                        TriangleList.get(id).addHeadTriangleTimeSpan(timeSpanHead);
                        TimeListHead.get(Mts.get(1) - start).addTriangle(timeSpanHead);
                        TriangleTimeSpan timeSpanTail = new TriangleTimeSpan(id, false, Mts.get(0));
                        TriangleList.get(id).addTailTriangleTimeSpan(timeSpanTail);
                        TimeListHead.get(Mts.get(2) - start).addTriangle(timeSpanTail);
                    //}
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


//        for (String key : MtsTableAll.keySet()) {//这层循环表示遍历三角形
//            if (Integer.parseInt(key.split("_")[3])>end||Integer.parseInt(key.split("_")[4])<start){
//                continue;
//            }
//            boolean firstTriangle = true;
//            for (int mts : MtsTableAll.get(key).keySet()) {//这层循环表示遍历三角形的所有mts值
//                for (Pair<Integer, Integer> timePair : MtsTableAll.get(key).get(mts)) {//这层循环表示遍历一个mts值中的所有时间对
//                    if (timePair.getLeft() >= start && timePair.getRight() <= end) {
//                        //如果对于一个三角形来说，它有一个mts值的区间在我们所给的区间内，如果还没有创建这个三角形，我们就创建这个三角形，并给它一个id。
//                        if (firstTriangle == true) {
//                            id++;
//                            TriangleList.add(new Triangle(id, Integer.parseInt(key.split("_")[0]), Integer.parseInt(key.split("_")[1]), Integer.parseInt(key.split("_")[2])));
//                            firstTriangle = false;
//                        }
//                        //如果创建了这个三角形之后，我们要根据这个mts对应的Pair<，>来创建对应的TriangleTimeSpan节点，并连接到这个三角形节点和对应的时间节点上。
//                        //首先是创建pair的左边部分，即一个区间的头节点,然后创建一个尾结点，然后连接起来。
//                        TriangleTimeSpan timeSpanHead = new TriangleTimeSpan(id, true, mts);
//                        TriangleList.get(id).addHeadTriangleTimeSpan(timeSpanHead);
//                        TimeListHead.get(timePair.getLeft() - start).addTriangle(timeSpanHead);
//                        TriangleTimeSpan timeSpanTail = new TriangleTimeSpan(id, false, mts);
//                        TriangleList.get(id).addTailTriangleTimeSpan(timeSpanTail);
//                        TimeListHead.get(timePair.getRight() - start).addTriangle(timeSpanTail);
//                    }
//                }
//            }
//        }


        //对timelist节点进行删减
        Iterator<TimeList> iterator = TimeListHead.iterator();
        while (iterator.hasNext()) {
            TimeList current = iterator.next();
            if (current.nextTriangle.timeNextTri == null) {
                current.deleteTime(); // 调用deleteTime方法
                iterator.remove(); // 通过迭代器安全地移除元素
            }
        }
    }

    public void printTriangleList() {
        for (Triangle triangle : TriangleList) {
            if (triangle!=null){
            System.out.println(triangle.id + " " + triangle.e1 + " " + triangle.e2 + " " + triangle.e3);
            TriangleTimeSpan timeSpan = triangle.timeSpanHead.TriNext;
            while (timeSpan != null) {
                System.out.println(timeSpan);
                timeSpan = timeSpan.TriNext;
            }
            }
        }
    }
    public void printTimeList() {
        for (TimeList timeList : TimeListHead) {
            System.out.println(timeList.time);
            TriangleTimeSpan timeSpan = timeList.nextTriangle.timeNextTri;
            while (timeSpan != null) {
                System.out.println(timeSpan);
                timeSpan = timeSpan.timeNextTri;
            }
        }
    }
    //经过上面的方法，我们创立了mtstable的所有节点，之后我们需要对mtstable进行遍历，来判断哪些节点需要删除
    //首先是对于某一个时刻的删除,不能直接将头节点的next置为null，是因为后面的节点还需要处理左右的方向，才会被回收。
    public void DeleteTriangleInTimeLeft() {
        //对时间区间的左边部分进行缩减
        while(TimeListHead.get(0).nextTriangle.timeNextTri!=null){
            TimeListHead.get(0).nextTriangle.timeNextTri.deleteTimeSpan(TimeListHead.get(0).nextTriangle.timeNextTri);
        }
        if(TimeListHead.get(0).nextTime!=null){
            TimeListHead.get(0).nextTime.preTime = null;}
        TimeListHead.get(0).nextTime=null;
        //System.out.println("删除了时间节点："+TimeListHead.get(0).time);
        TimeListHead.remove(0);
    }

    public void DeleteTimeInTimeLeft(int ts) {
        while(TimeListHead.get(0).time<ts){
            DeleteTriangleInTimeLeft();
        }
    }

    public void DeleteTriangleInTimeRight() {
        //对时间区间右边部分进行缩减
        while(TimeListHead.get(TimeListHead.size()-1).nextTriangle.timeNextTri!=null){
            TimeListHead.get(TimeListHead.size()-1).nextTriangle.timeNextTri.deleteTimeSpan(TimeListHead.get(TimeListHead.size()-1).nextTriangle.timeNextTri);
        }
        if(TimeListHead.get(TimeListHead.size()-1).preTime!=null) {
            TimeListHead.get(TimeListHead.size() - 1).preTime.nextTime = null;
        }
        TimeListHead.get(TimeListHead.size()-1).preTime=null;
        TimeListHead.remove(TimeListHead.size()-1);
    }

    public void DeleteTimeInTimeRight(int te) {
        while(TimeListHead.get(TimeListHead.size()-1).time>te){
            DeleteTriangleInTimeRight();
            if(TimeListHead.isEmpty()) break;
        }
    }


    //之后是对三角形mts值的判断，因为我们存储的时候mts的值是按照从小到大排序，所以我们只需要检查当前节点的mts值是否符合条件，如果符合则三角形存在，如果不符合则三角形不存在
    public void DeleteTriangleInMts(int mts) {
        for (Triangle triangle : TriangleList) {
            if (triangle != null){
                triangle.deleteTriangleTimeSpan(mts);
                if (triangle.timeSpanHead.TriNext == null) {
                    if (!this.EdgeSupport.isEmpty()){
                        EdgeSupport.get(triangle.e1).remove( triangle.id);
                        EdgeSupport.get(triangle.e2).remove( triangle.id);
                        EdgeSupport.get(triangle.e3).remove( triangle.id);
                    }
                    TriangleList.set(triangle.id, null);
                }
            }
        }
    }
    //之后是对得到的三角形进行truss分解
    Map<String,ArrayList<Integer>> EdgeSupport = new HashMap<>();   //来存储edge的支持度
    public void InitEdgeSupport() {
        for (Triangle triangle : TriangleList) {
            if (triangle == null) {
                continue;
            }
            if (triangle.timeSpanHead.TriNext == null) {
                continue;
            }
            if (EdgeSupport.containsKey(triangle.e1)) {
                EdgeSupport.get(triangle.e1).add(triangle.id);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(triangle.id);
                EdgeSupport.put(triangle.e1, list);
            }
            if (EdgeSupport.containsKey(triangle.e2)) {
                EdgeSupport.get(triangle.e2).add(triangle.id);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(triangle.id);
                EdgeSupport.put(triangle.e2, list);
            }
            if (EdgeSupport.containsKey(triangle.e3)) {
                EdgeSupport.get(triangle.e3).add(triangle.id);
            } else {
                ArrayList<Integer> list = new ArrayList<>();
                list.add(triangle.id);
                EdgeSupport.put(triangle.e3, list);
            }
        }
    }
    public void trussDecompose(int trusskey) {
        //现在我们已经有个所有边的支持度的map，接下来我们只需要遍历这个map。
        //先将小于k值的边加入到队列中，然后遍历队列，将队列中的边删除，然后遍历删除的边的所有三角形，将三角形删除。
        Queue<String> queue = new LinkedList<>();
        for (String key : EdgeSupport.keySet()){
            if (EdgeSupport.get(key).size() < trusskey){
                queue.add(key);
            }
        }
        while (!queue.isEmpty()) {
            String key = queue.poll();
            for (int id : EdgeSupport.get(key)) {
                Triangle triangle = TriangleList.get(id);
                if (triangle == null){
                    continue;
                }
                if (!Objects.equals(key, triangle.e1) &&EdgeSupport.get(triangle.e1).size() >= trusskey){
                    EdgeSupport.get(triangle.e1).remove((Integer) id);
                    if (EdgeSupport.get(triangle.e1).size() < trusskey){
                        queue.add(triangle.e1);
                    }
                }
                if (!Objects.equals(key, triangle.e2) &&EdgeSupport.get(triangle.e2).size() >= trusskey){
                    EdgeSupport.get(triangle.e2).remove((Integer) id);
                    if (EdgeSupport.get(triangle.e2).size() < trusskey){
                        queue.add(triangle.e2);
                    }
                }
                if (!Objects.equals(key, triangle.e3) &&EdgeSupport.get(triangle.e3).size() >= trusskey){
                    EdgeSupport.get(triangle.e3).remove((Integer) id);
                    if (EdgeSupport.get(triangle.e3).size() < trusskey){
                        queue.add(triangle.e3);
                    }
                }
                while (TriangleList.get(id).timeSpanHead.TriNext != null){
                    TriangleList.get(id).timeSpanHead.TriNext.deleteTimeSpan(TriangleList.get(id).timeSpanHead.TriNext);
                }
                TriangleList.set(id, null);
            }
            EdgeSupport.remove(key);
        }
        //对timelist进行裁剪
        Iterator<TimeList> iterator = TimeListHead.iterator();
        while (iterator.hasNext()) {
            TimeList current = iterator.next();
            if (current.nextTriangle.timeNextTri == null) {
                current.deleteTime(); // 调用deleteTime方法
                iterator.remove(); // 通过迭代器安全地移除元素
            }
        }
        //if (!TimeListHead.isEmpty()){
        //System.out.println(TimeListHead.get(0).time+" "+TimeListHead.get(TimeListHead.size()-1).time);}
        //PrintSupport();
    }
    //生成一个十字链表的复制  因为我们在对一个区间的所有子区间进行计算的时候，肯定会重复利用到一个区间的结果，所以我们需要将一个区间的结构生成一个副本
    //这之前，我们需要TriangleList和TimeListHead进行一个深拷贝，因为这两个结构是引用结构，所以直接进行拷贝即可

    public void DeleteLeftAndMtsAndTruss(int mts,int trusskey){
        DeleteTriangleInTimeLeft();
        DeleteTriangleInMts(mts);
        trussDecompose(trusskey);
    }

    public void PrintSupport() {
        for (String key : EdgeSupport.keySet()) {
            System.out.println(key + " " + EdgeSupport.get(key).size());
        }
    }

    public void DeleteRightAndMtsAndTruss(int mts, int trusskey) {
        DeleteTriangleInTimeRight();
        DeleteTriangleInMts(mts);
        trussDecompose(trusskey);
    }

    public void Clear(){
        TimeListHead.clear();
        TriangleList.clear();
        EdgeSupport.clear();

    }
}
