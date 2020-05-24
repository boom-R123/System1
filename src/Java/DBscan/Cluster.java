package DBscan;

import java.util.ArrayList;
import java.util.List;

//用于存储一个簇的数据
public class Cluster {
    List<Point> Points = new ArrayList<>();
    int type=-1;//代表第一次(簇中只有一个点)簇中点的类型,0代表核心点，1代表边界点，-1代表噪声点
    public Cluster(Point p){//一开始每个点为一个簇
        Points.add(p);
    }

    public void addPoint(Point p){//给簇添加点
        Points.add(p);
    }

    public double GetMinDistance(Cluster c) {//获取两个簇之间的最小距离
        double min = 999;
        for (int i = 0; i < c.Points.size(); i++) {
            for (int j = 0; j < this.Points.size(); j++) {
                double dis = c.Points.get(i).GetDistance(this.Points.get(j));
                if (min > dis) {
                    min = dis;
                }
            }
        }
        return min;
    }

    public void Merge(Cluster c){//将两个簇的内容合并

        for(int i=0;i<c.Points.size();i++){
            this.addPoint(c.Points.get(i));
        }
    }

    public Point GetFirstPoint(){//得到簇中第一个点
        return Points.get(0);
    }

}
