import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

public class ReadData {
    //public int[] Degree = new int[1000000];
    public Map<Integer, ArrayList<Integer>> neighbors = new HashMap<Integer, ArrayList<Integer>>();
    public Map<String, ArrayList<Integer>> EdgeToTime = new HashMap<String, ArrayList<Integer>>();
    public Map<Integer,ArrayList<Pair<Integer, Integer>>> TimeToEdge = new HashMap<Integer,ArrayList<Pair<Integer, Integer>>>();
    public int number=0;
    int []Degree= new int[10000000];

    public void EdgeTime(){
        List<Map.Entry<String, Integer>> list = new ArrayList<>(EdgeToTime.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), entry.getValue().size()))
                .collect(Collectors.toList()));
        list.sort((entry1, entry2) -> Integer.compare(entry2.getValue(), entry1.getValue()));
        int count = 0;
        for (Map.Entry<String, Integer> entry : list) {
            if (count >= 100) {
                break;
            }
            System.out.println("Key: " + entry.getKey() + ", Size: " + entry.getValue());
            count++;
        }
        Arrays.sort(Degree);
        int left = 0, right = Degree.length - 1;
        while (left < right) {
            int temp = Degree[left];
            Degree[left] = Degree[right];
            Degree[right] = temp;
            left++;
            right--;
        }
    }

    public void addNeighbor(int start, int end) {
        if (end==-1){
            if (neighbors.containsKey(start)) {
                return;
            } else {
                neighbors.put(start, new ArrayList<Integer>());
                return;
            }
        }
        if (neighbors.containsKey(start)) {
            neighbors.get(start).add(end);
        } else {
            neighbors.put(start, new ArrayList<Integer>());
            neighbors.get(start).add(end);
        }
    }


    public void readData(String fileName) {
        BufferedReader reader = null;
        try {
            //初始化BufferedReader
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                // 通过空格分隔每行数据为多个部分
                String[] tokens = line.split(" ");
                // 将第一部分转换为整型的时间戳
                int timestamp = Integer.parseInt(tokens[0]);
                // 获取开始和结束标志
                int start = Integer.parseInt(tokens[1]);
                int end = Integer.parseInt(tokens[2]);
                if (start > end) {
                    int temp = start;
                    start = end;
                    end = temp;
                }
                if (EdgeToTime.containsKey(start+""+end)) {
                    EdgeToTime.get(start+""+end).add(timestamp);
                } else {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(timestamp);
                    EdgeToTime.put(start+""+end, list);
                    //将邻居加入到点
                    addNeighbor(start, end);
                    addNeighbor(end, -1);
                }

                /*if (TimeToEdge.containsKey(timestamp)){
                    TimeToEdge.get(timestamp).add(Pair.of(start, end));
                } else {
                    ArrayList<Pair<Integer, Integer>> list=new ArrayList<Pair<Integer, Integer>>();
                    list.add(Pair.of(start, end));
                    TimeToEdge.put(timestamp, list);
                }*/


                //度数+1
                //Degree[start] += 1;
                //Degree[end] += 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 确保BufferedReader在使用后被正确关闭
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // 在关闭过程中发生的IO异常同样抛出运行时异常
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void BasereadData(String fileName) {
        BufferedReader reader = null;
        try {
            //初始化BufferedReader
            reader = new BufferedReader(new FileReader(fileName));
            String line;
            while ((line = reader.readLine()) != null) {
                // 通过空格分隔每行数据为多个部分
                String[] tokens = line.split(" ");
                // 将第一部分转换为整型的时间戳
                int timestamp = Integer.parseInt(tokens[0]);
                // 获取开始和结束标志
                int start = Integer.parseInt(tokens[1]);
                int end = Integer.parseInt(tokens[2]);
                if (start > end) {
                    int temp = start;
                    start = end;
                    end = temp;
                }
                if (EdgeToTime.containsKey(start+""+end)) {
                    EdgeToTime.get(start+""+end).add(timestamp);
                } else {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(timestamp);
                    EdgeToTime.put(start+""+end, list);
                    //将邻居加入到点
                    addNeighbor(start, end);
                    addNeighbor(end, -1);
                }

                if (TimeToEdge.containsKey(timestamp)){
                    TimeToEdge.get(timestamp).add(Pair.of(start, end));
                } else {
                    ArrayList<Pair<Integer, Integer>> list=new ArrayList<Pair<Integer, Integer>>();
                    list.add(Pair.of(start, end));
                    TimeToEdge.put(timestamp, list);
                }
                Degree[start] += 1;
                Degree[end] += 1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // 确保BufferedReader在使用后被正确关闭
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    // 在关闭过程中发生的IO异常同样抛出运行时异常
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void WriteData(String fileName,int b) throws IOException {
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,false))) {
            for(Map.Entry<String, ArrayList<Integer>> entry : EdgeToTime.entrySet()){
                Set<Integer> time = new HashSet<>();
                String[] key = entry.getKey().split(" ");
                ArrayList<Integer> value = entry.getValue();
                for (Integer i : value) {
                    time.add(i/b);
                }
                for (Integer i : time) {
                    writer.write(i+" "+key[0]+" "+key[1]);
                    writer.newLine();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void Clear(){
        this.EdgeToTime.clear();
        this.TimeToEdge.clear();
        this.neighbors.clear();
        this.Degree=null;
    }

    public void printEdgetoTime() {
        for (Map.Entry<String, ArrayList<Integer>> entry : EdgeToTime.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
    public void printTimeToEdge() {
        for (Map.Entry<Integer, ArrayList<Pair<Integer, Integer>>> entry : TimeToEdge.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }

    public void print() {
        System.out.println("总共节点数目: "+neighbors.size());
        System.out.println("总共边数目: "+EdgeToTime.size());
    }

    public void TriangleCal(){
        for (int key1 : neighbors.keySet()){
            if(neighbors.get(key1).isEmpty()){
                continue;
            }
            for (int key2 : neighbors.get(key1)){
                if(neighbors.get(key2).isEmpty()){
                    continue;
                }
                Set<Integer> set1 = new HashSet<>(neighbors.get(key1));
                Set<Integer> set2 = new HashSet<>(neighbors.get(key2));
                set1.retainAll(set2);
                if (!set1.isEmpty()) {
                    number=number+set1.size();
                }
            }
        }
    }
}


