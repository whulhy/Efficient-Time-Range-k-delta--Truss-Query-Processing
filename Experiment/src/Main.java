import org.apache.commons.lang3.tuple.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Main {

    public static Set<Pair<Integer, Integer>> timepair =new HashSet<>();
    public static Set<Pair<Integer, Integer>> timepair2 =new HashSet<>();
    private static int MtotalTime = 0;
    private static int BtotalTime = 0;
    private static int DCtotalTime = 0;
    public static void main(String[] args) throws IOException {
        // 输入参数
        int Ts = 0; // 大时间区间的开始时间
        int Te = 803; // 大时间区间的结束时间
        int a = 250; // 每个子区间的长度
        int numSubIntervals = 10; // 需要生成的子区间数量

        int mts = 10;
        int trusskey = 3;

        // 生成随机子区间
        List<int[]> subIntervals = generateRandomSubIntervalsOptimized(Ts, Te, a, numSubIntervals);

//        // 输出结果
//        for (long[] interval : subIntervals) {
//            System.out.println("[" + interval[0] + ", " + interval[1] + "]");
//        }


        for (int[] subInterval: subIntervals) {
            extracted(subInterval[0],subInterval[1],trusskey, mts);
        }

        int avg_runtime = MtotalTime/numSubIntervals;
        System.out.println("MTS平均运行时间: "+avg_runtime);

        for (int[] subInterval: subIntervals) {
            extracted2(subInterval[0],subInterval[1],trusskey, mts);
        }

        int avg_runtime2 = BtotalTime/numSubIntervals;
        System.out.println("Baseline平均运行时间: "+avg_runtime2);



        //extracted(ts,te,trusskey, mts);
        //extracted2(ts,te,trusskey, mts);
        //text();
    }

    private static void extracted(int ts,int te,int trusskey,int mts) throws IOException {
        String file = "C:\\Users\\lhy\\Desktop\\实验\\mts\\Email-Eu_out.txt";
        MtsTable mtsTable=new MtsTable();
        long ss = System.currentTimeMillis();
        mtsTable.createTriangleNode(0,803,file);
        long ss2 = System.currentTimeMillis();
        //System.out.println("创建索引时间为： "+(ss2-ss));
        //System.out.println("三角形个数为： "+mtsTable.TriangleList.size());
        long s = System.currentTimeMillis();
        mtsTable.DeleteTimeInTimeLeft(ts);
        mtsTable.DeleteTimeInTimeRight(te);
        mtsTable.DeleteTriangleInMts(mts);
        mtsTable.InitEdgeSupport();
        mtsTable.trussDecompose(trusskey);
        //System.out.println(mtsTable.TimeListHead.get(0).time+" "+mtsTable.TimeListHead.get(mtsTable.TimeListHead.size()-1).time);
        //timepair2.add(Pair.of(mtsTable.TimeListHead.get(0).time,mtsTable.TimeListHead.get(mtsTable.TimeListHead.size()-1).time));
        mtsTableOutput(mtsTable,mts,trusskey);
        long e = System.currentTimeMillis();
        long runtime = e - s;
        MtotalTime+= (int) runtime;
        //System.out.println("运行时间： "+(e-s));
        //System.out.println(timepair2.size());
        mtsTable.Clear();
        //printTimepair2();
    }

    private static void extracted2(int ts,int te,int trusskey,int mts){
        long start=System.currentTimeMillis();
        ReadData readData2=new ReadData();
        readData2.BasereadData("C:\\Users\\lhy\\Desktop/实验/data/Email-Eu.txt");
        baseline2 baseline=new baseline2();
        baseline.SelectedEdge(readData2,ts,te,trusskey,mts);
        //System.out.println(baseline.start+" "+baseline.end);
        BaseShearTime2(readData2,baseline,ts,te,trusskey,mts);
        long end=System.currentTimeMillis();
        long runtime = end - start;
        BtotalTime+= (int) runtime;
        baseline.Clear();
        readData2.Clear();
//      System.out.println("运行时间： "+(end-start));
//      System.out.println(timepair.size());
        //printTimepair();
    }
    private static void BaseShearTime2(ReadData readData,baseline2 baseline,int timestart,int timeend,int k,int mts){
        //来记录上一个baseline的时间区间
        int osTime=timestart;
        int oeTime=timeend;
        //来记录当前的baseline的时间区间
        int time1=baseline.start;
        int time2=baseline.end;
        while(osTime<=time1&&baseline.end!=-1&&baseline.start!=-1){
            //System.out.println("2");
            baseline2 baseline2=new baseline2();
            baseline2.Copy(baseline);
            timepair.add(Pair.of(baseline2.start,baseline2.end));
            while (oeTime>=time2&&baseline2.end!=-1&&baseline2.start!=-1){
                baseline2.DeleteTime(readData,time2,oeTime,mts,k);
                //System.out.print("1");
                timepair.add(Pair.of(baseline2.start,baseline2.end));
                oeTime=time2-1;
                time2=baseline2.end;
            }
            baseline.DeleteTime(readData,osTime,time1,mts,k);
            osTime=time1+1;
            time1=baseline.start;
            oeTime=timeend;
            time2=baseline.end;
        }
    }

    public static void mtsTableOutput(MtsTable mtsTable,int mts,int trusskey) {
        while (!mtsTable.TimeListHead.isEmpty()) {
            MtsTableCopy mtsTableCopy1 = new MtsTableCopy(mtsTable.TimeListHead, mtsTable.TriangleList, mtsTable.EdgeSupport);
            timepair2.add(Pair.of(mtsTableCopy1.start, mtsTableCopy1.end));
            while (!mtsTableCopy1.TimeListHead.isEmpty()) {
                mtsTableCopy1.DeleteRightAndMtsAndTruss(mts, trusskey);
                timepair2.add(Pair.of(mtsTableCopy1.start, mtsTableCopy1.end));
                if (mtsTableCopy1.start!=mtsTable.TimeListHead.get(0).time){
                    break;
                }
            }
            mtsTable.DeleteLeftAndMtsAndTruss(mts, trusskey);
        }
    }

    /**
     * 使用洗牌算法高效生成随机且不重复的子区间
     * @param Ts 大时间区间的开始时间
     * @param Te 大时间区间的结束时间
     * @param a 每个子区间的长度
     * @param numSubIntervals 需要生成的子区间数量
     * @return 包含所有子区间的列表，每个子区间表示为long数组{ts, te}
     */
    public static List<int[]> generateRandomSubIntervalsOptimized(int Ts, int Te, int a, int numSubIntervals) {
        List<int[]> subIntervals = new ArrayList<>();

        // 生成所有可能的起始点
        List<Integer> possibleStartTimes = new ArrayList<>();
        for (int ts = Ts; ts <= Te - a; ts++) {
            possibleStartTimes.add(ts);
        }

        // 随机打乱起始点
        Collections.shuffle(possibleStartTimes);

        // 取前 numSubIntervals 个起始点
        Random random = new Random();
        for (int i = 0; i < numSubIntervals; i++) {
            int ts = possibleStartTimes.get(i);
            int te = ts + a;
            subIntervals.add(new int[]{ts, te});
        }

        return subIntervals;
    }

    public static void printTimepair(){
        for (Pair<Integer, Integer> pair:timepair){
            System.out.print(pair+" ");
        }
        System.out.println();
        System.out.println(timepair.size());
    }
    public static void printTimepair2(){
        for (Pair<Integer, Integer> pair:timepair2){
            System.out.print(pair+" ");
        }
        System.out.println();
        System.out.println(timepair2.size());
    }
}