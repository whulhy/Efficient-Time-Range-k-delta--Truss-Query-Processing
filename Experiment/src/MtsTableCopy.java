import java.util.*;

public class MtsTableCopy {
    public ArrayList<Triangle> TriangleList = new ArrayList<>() ;
    public ArrayList<TimeList> TimeListHead = new ArrayList<>() ;
    Map<String,ArrayList<Integer>> EdgeSupport = new HashMap<>();
    public int start;
    public int end;

    public MtsTableCopy(ArrayList<TimeList> OriginalTimeListHead,ArrayList<Triangle> OriginalTriangleList,Map<String,ArrayList<Integer>> OriginalEdgeSupport){
        this.start=OriginalTimeListHead.get(0).time;
        this.end=OriginalTimeListHead.get(OriginalTimeListHead.size()-1).time;
        CopyMtsTable(OriginalTimeListHead,OriginalTriangleList,OriginalEdgeSupport);
    }

    private void CopyMtsTable(ArrayList<TimeList> OriginalTimeListHead,ArrayList<Triangle> OriginalTriangleList,Map<String,ArrayList<Integer>> OriginalEdgeSupport)
    {
        Map<TriangleTimeSpan,TriangleTimeSpan> map = new HashMap<>();
        Map<TimeList,TimeList> timeListMap = new HashMap<>();
        Map<Triangle,Triangle> triangleMap = new HashMap<>();

        for(TimeList timeList : OriginalTimeListHead){
            TimeList copyTimeList = getTimeList(timeList,timeListMap);
            copyTimeList.preTime=getTimeList(timeList.preTime,timeListMap);
            copyTimeList.nextTime=getTimeList(timeList.nextTime,timeListMap);
            copyTimeList.nextTriangle=getTriangleTime(timeList.nextTriangle,map);
            TimeListHead.add(copyTimeList);
        }

        for(Triangle triangle : OriginalTriangleList){
            if (triangle==null){
                TriangleList.add(null);
            }
            if (triangle!=null){
            Triangle copyTriangle = getTriangle(triangle,triangleMap);
            copyTriangle.timeSpanHead=getTriangleTime(triangle.timeSpanHead,map);
            TriangleList.add(copyTriangle);
            }
        }

        //先对时间链表复制
        for (int i = 0; i < OriginalTimeListHead.size(); i++) {
           TriangleTimeSpan temp = OriginalTimeListHead.get(i).nextTriangle;
           while (temp!=null)
           {
               TriangleTimeSpan triangleTimeSpan = getTriangleTime(temp,map);
               triangleTimeSpan.timeNextTri=getTriangleTime(temp.timeNextTri,map);
               triangleTimeSpan.timePreTri=getTriangleTime(temp.timePreTri,map);
               triangleTimeSpan.TriNext=getTriangleTime(temp.TriNext,map);
               triangleTimeSpan.TriPre=getTriangleTime(temp.TriPre,map);
               temp=temp.timeNextTri;
           }
        }
        //对三角形表复制
        for (int i = 0; i < OriginalTriangleList.size(); i++) {
            if (OriginalTriangleList.get(i)!=null){
                this.TriangleList.get(i).timeSpanHead.TriNext=getTriangleTime(OriginalTriangleList.get(i).timeSpanHead.TriNext,map);
            }
        }

        //对边支持度进行复制
        for (String key : OriginalEdgeSupport.keySet()) {
            ArrayList<Integer> integers = new ArrayList<>(OriginalEdgeSupport.get(key));
            EdgeSupport.put(key,integers);
        }
    }

    public TriangleTimeSpan getTriangleTime(TriangleTimeSpan original,Map<TriangleTimeSpan,TriangleTimeSpan> map){
        if (original==null){
            return null;
        }
        if (map.containsKey(original)){
            return map.get(original);
        }else{
            TriangleTimeSpan triangleTimeSpan = new TriangleTimeSpan(original.id,original.isHead,original.mts);
            map.put(original,triangleTimeSpan);
            return triangleTimeSpan;
        }
    }

    public TimeList getTimeList(TimeList original,Map<TimeList,TimeList> map){
        if (original==null){
            return null;
        }
        if (map.containsKey(original)){
            return map.get(original);
        }else{
            TimeList timeList = new TimeList(original.time,null,null);
            map.put(original,timeList);
            return timeList;
        }
    }

    public Triangle getTriangle(Triangle original,Map<Triangle,Triangle> map){
        if (original==null){
            return null;
        }
        if (map.containsKey(original)){
            return map.get(original);
        }else{
            Triangle triangle = new Triangle(original.id,original.e1,original.e2,original.e3);
            map.put(original,triangle);
            return triangle;
        }
    }

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
        while(TimeListHead.get(0).time<=ts){
            DeleteTriangleInTimeLeft();
        }
    }

    public void DeleteTriangleInTimeRight() {
        //对时间区间右边部分进行缩减
        while(TimeListHead.get(TimeListHead.size()-1).nextTriangle.timeNextTri!=null){
            TimeListHead.get(TimeListHead.size()-1).nextTriangle.timeNextTri.deleteTimeSpan(TimeListHead.get(TimeListHead.size()-1).nextTriangle.timeNextTri);
        }
        if(TimeListHead.get(TimeListHead.size()-1).preTime!=null){
        TimeListHead.get(TimeListHead.size()-1).preTime.nextTime = null;}
        TimeListHead.get(TimeListHead.size()-1).preTime=null;
        //System.out.println("删除了时间节点："+TimeListHead.get(TimeListHead.size()-1).time);
        TimeListHead.remove(TimeListHead.size()-1);
    }

    public void DeleteTimeInTimeRight(int te) {
        while(TimeListHead.get(TimeListHead.size()-1).time>=te){
            DeleteTriangleInTimeRight();
        }
    }

    public void DeleteTriangleInMts(int mts) {
        for (Triangle triangle : TriangleList) {
            if (triangle == null) {
                continue;
            }
            triangle.deleteTriangleTimeSpan(mts);
            if (triangle.timeSpanHead.TriNext == null) {
                if (!this.EdgeSupport.isEmpty()){
                    EdgeSupport.get(triangle.e1).remove(triangle.id);
                    EdgeSupport.get(triangle.e2).remove(triangle.id);
                    EdgeSupport.get(triangle.e3).remove(triangle.id);
                }
                TriangleList.set(triangle.id, null);
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
    }

    public void NewStartAndEnd(){
        if (!TimeListHead.isEmpty()){
        start = TimeListHead.get(0).time;
        end = TimeListHead.get(TimeListHead.size()-1).time;
        }
    }

    public void DeleteLeftAndMtsAndTruss(int mts,int trusskey){
        DeleteTriangleInTimeLeft();
        DeleteTriangleInMts(mts);
        trussDecompose(trusskey);
        NewStartAndEnd();
    }

    public void DeleteRightAndMtsAndTruss(int mts,int trusskey){
        DeleteTriangleInTimeRight();
        DeleteTriangleInMts(mts);
        trussDecompose(trusskey);
        NewStartAndEnd();
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
}
