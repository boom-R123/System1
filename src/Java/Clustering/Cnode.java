package Clustering;

//map中的节点,存储距离该簇最近的簇的编号和距离
public class Cnode{
    int to;//最近簇的编号
    double dis;//最短的距离
    public Cnode(int id,double d){
        to=id;
        dis=d;
    }
}

