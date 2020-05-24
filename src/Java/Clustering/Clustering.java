package Clustering;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import Tools.JsonRW;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Clustering {
    Map<Integer, Cluster> Clusters = new HashMap<Integer, Cluster>();//簇的哈希表，键为簇的id,值为簇本身
    Map<Integer, Cnode> map = new HashMap<Integer, Cnode>();//代替矩阵，键代表本身簇的编号,Cnode.to代表其距离最短簇的编号,Cnode.dis代表这两个簇之间的距离

    public void Merge(int c1, int c2) {//将编号是c1和c2的两个簇合并
        //取两个中层级较高的再加1
        int nextlevel = (Clusters.get(c1).level > Clusters.get(c2).level ? Clusters.get(c1).level : Clusters.get(c2).level) + 1;
        Clusters.get(c1).Merge(Clusters.get(c2));
        Clusters.get(c1).level = nextlevel;
        Clusters.remove(c2);
    }

    Clustering(int n) throws IOException {//层次聚类,合并到n个簇为止
        FileInputStream fis = new FileInputStream("d:/testdata/POI1.txt");
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        String line = null;
        int i = 0;//簇的编号
        while ((line = br.readLine()) != null) {//读取所有的点并存到Clusters中
            i++;
            String p[] = line.split(",");
            double x = Double.parseDouble(p[0]);
            double y = Double.parseDouble(p[1]);
            Cluster c = new Cluster(new Point(x, y));//初始时，一个点为一个簇
            Clusters.put(i, c);
            c = null;
        }
        br.close();
        for (Map.Entry<Integer, Cluster> entry1 : Clusters.entrySet()) {//遍历寻找最近的簇,初始化表
            double min = 999;//最小距离
            int to = 0;//所到簇的编号
            for (Map.Entry<Integer, Cluster> entry2 : Clusters.entrySet()) {
                if (entry1.getKey() != entry2.getKey()) {
                    double d = entry1.getValue().GetMinDistance(entry2.getValue());
                    if (d < min) {
                        min = d;
                        to = entry2.getKey();
                    }
                }
            }
            Cnode cn = new Cnode(to, min);
            map.put(entry1.getKey(), cn);
        }

//        for (Map.Entry<Integer, Cnode> entry : map.entrySet()) {
//            System.out.println(entry.getKey() + "  " + entry.getValue().to + "   " + entry.getValue().dis);
//        }

        while (Clusters.size() > n) { //生成n个簇
            System.out.println(Clusters.size());
            int c1 = 0;//要合并簇的编号
            int c2 = 0;
            double min = 999;
            for (Map.Entry<Integer, Cnode> entry : map.entrySet()) {
                if (entry.getValue().dis < min) {
                    min = entry.getValue().dis;
                    c1 = entry.getKey();
                    c2 = entry.getValue().to;
                }
            }
            //System.out.print(c1 + "   " + c2+"   "+min+"   ");
            Merge(c1, c2);//合并两个簇到c1
            map.remove(c2);//在表中删除c2
            for (Map.Entry<Integer, Cnode> entry : map.entrySet()) {//更新所有与c2有关的簇
                if (entry.getValue().to == c2) {
                    double temp = 999;
                    int to = 0;
                    for (Map.Entry<Integer, Cnode> entry1 : map.entrySet()) {
                        if (entry.getKey() == entry1.getKey())
                            continue;
                        double dis = Clusters.get(entry.getKey()).GetMinDistance(Clusters.get(entry1.getKey()));
                        if (temp > dis) {
                            temp = dis;
                            to = entry1.getKey();
                        }
                    }
                    map.get(entry.getKey()).to = to;
                    map.get(entry.getKey()).dis = temp;
                    //System.out.print("update:" +entry.getKey()+"  "+to+" ");
                }
            }

        }

        //将数据按照Json格式存储到文件中
        JSONObject Json = new JSONObject();//存放Json数据
        JSONArray clusters = new JSONArray();//存放簇的集合
        for (Map.Entry<Integer, Cluster> entry : Clusters.entrySet()) {
            JSONArray cluster = new JSONArray();//存放某个簇中的所有点
            if (entry.getValue().Points.size() < 20) {//限定簇的大小,簇中所包含的点要大于等于20个
                continue;
            }
            for (int j = 0; j < entry.getValue().Points.size(); j++) {
                double point[] = new double[2];
                point[0] = entry.getValue().Points.get(j).latitude;
                point[1] = entry.getValue().Points.get(j).longitude;
                cluster.add(point);

            }
            JSONObject C=new JSONObject();
            C.put("size",cluster.size());//簇的规模
            C.put("Clusters",cluster);//簇的集合
            clusters.add(C);
        }
        Json.put("clusters", clusters);
        System.out.println(clusters);
        JsonRW.writeJson("D:/testdata/result.json", Json);
    }

    public static void main(String[] args) throws IOException {
        ChangePoi changepoi=new ChangePoi("行政地标");
        long startTime = System.currentTimeMillis(); //获取开始时间
        Clustering c = new Clustering(666);
        long endTime = System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); //输出程序运行时间
    }

}
