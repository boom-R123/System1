package DBscan;
import Tools.JsonRW;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DBScan {
    double e=1;//半径,单位千米
    int minp=3;//密度阈值
    List<Cluster> Clusters=new ArrayList<>();//用于保存结果

    public void ReadData() throws IOException {//读取数据
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
            Clusters.add(c);
            c = null;
        }
        br.close();
    }

    public void GetType(){//得到每个点的类型
        for(int i=0;i<Clusters.size();i++){//先得到核心点
            int count=0;//在这个点所形成的圆中的点的个数
            List<Integer> borderPoint=new ArrayList<>();//用于存储这个点所形成的圆中的点的编号
            for(int j=0;j<Clusters.size();j++){
                if(Clusters.get(i).GetFirstPoint().GetDistance(Clusters.get(j).GetFirstPoint())<e){//两个点的距离小于e
                    borderPoint.add(j);
                    count++;
                }
            }
            if(count>minp){//超过阈值，这个点是核心点
//                System.out.println(i);
                Clusters.get(i).type=0;
                for(int j=0;j<borderPoint.size();j++){
                    if(Clusters.get(borderPoint.get(j)).type==-1)//如果这个点本来是噪声点就改变为边界点
                        Clusters.get(borderPoint.get(j)).type=1;
                }
            }
        }
    }

    public void WriteCluster(){ //将数据按照Json格式存储到文件中
        JSONObject Json = new JSONObject();//存放Json数据
        JSONArray clusters = new JSONArray();//存放簇的集合
        for (int i=0;i<Clusters.size();i++) {
            JSONArray cluster = new JSONArray();//存放某个簇中的所有点
            if (Clusters.get(i).Points.size() < 20) {//限定簇的大小,簇中所包含的点要大于等于20个
                continue;
            }
            for (int j = 0; j < Clusters.get(i).Points.size(); j++) {
                double point[] = new double[2];
                point[0] = Clusters.get(i).Points.get(j).latitude;
                point[1] = Clusters.get(i).Points.get(j).longitude;
                cluster.add(point);
            }
            JSONObject C=new JSONObject();
            C.put("size",cluster.size());//簇的规模
            C.put("Clusters",cluster);//簇的集合
            clusters.add(C);
        }
        Json.put("clusters", clusters);
        System.out.println(clusters);
        JsonRW.writeJson("D:/testdata/result11.json", Json);
    }

    public DBScan(double e1,int minp1) throws IOException {
        ReadData();//读取数据
        this.e=e1;
        this.minp=minp1;
        GetType();
        for(int i=0;i<Clusters.size();i++){//删除噪声点
            if(Clusters.get(i).type==-1){
                Clusters.remove(i);//remove会是后面的元素位置前移
                i--;
            }
        }

        System.out.println(Clusters.size());
        while(true){//将相邻的核心点聚成簇
            boolean flag=false;//这次是否有簇更新
            for(int i=0;i<Clusters.size();i++){
                System.out.println(i);
                if(Clusters.get(i).type==0){
                    for(int j=i+1;j<Clusters.size();j++){
                        if(Clusters.get(i).type!=0)
                            continue;
                        //如果这两个簇中有点的距离小于minp,说明这两个簇中的核心点相邻,则合并
                        if(Clusters.get(i).GetMinDistance(Clusters.get(j))<minp){
                            flag=true;
                            Clusters.get(i).Merge(Clusters.get(j));
                            Clusters.remove(j);//删除被合并的簇
                            j--;
                        }
                    }
                }
            }
            if(!flag){//如果没有更新，则跳出循环
                break;
            }
        }

        for(int i=0;i<Clusters.size();i++){//将边界点分配给其关联核心点的簇
            if(Clusters.get(i).type==1){//是边界点
                for(int j=0;j<Clusters.size();j++){
                    if(Clusters.get(i).type!=0)//是核心点簇
                        continue;
                    if(Clusters.get(i).GetMinDistance(Clusters.get(j))<minp){
                        Clusters.get(j).Merge(Clusters.get(i));
                        Clusters.remove(i);//删除被合并的簇
                        i--;
                        break;//找到就不用找了
                    }
                }
            }
        }
        WriteCluster();
    }

    public static void main(String[] args) throws IOException {
        DBScan dbscan=new DBScan(1,3);
    }

}
