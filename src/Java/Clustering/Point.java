package Clustering;

//用于存储坐标点
public class Point {
    double longitude;//经度
    double latitude;//维度
    Point(double x, double y) {
        longitude = x;
        latitude = y;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double LonA, double LatA, double LonB, double LatB)//由经纬度转换成距离（千米）
    {
        double R = 6371.004;
        double C = Math.sin(rad(LatA)) * Math.sin(rad(LatB)) + Math.cos(rad(LatA)) * Math.cos(rad(LatB)) * Math.cos(rad(LonA - LonB));
        return (R * Math.acos(C));
    }

    double GetDistance(Point p) { //计算两个坐标点间的距离
        return getDistance(this.longitude,this.latitude,p.longitude,p.latitude);
    }
}