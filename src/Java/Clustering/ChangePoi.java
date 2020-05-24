package Clustering;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//转换POI.csv中的数据，生成指定类型的POI类型所对应的经纬度，要先将原来的csv文件转换成utf-8编码
public class ChangePoi {
    public ChangePoi(String s_type) throws IOException {
        FileInputStream fis = new FileInputStream("d:/testdata/POI.csv");
        InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        FileOutputStream fo= new FileOutputStream("d:/testdata/POI1.txt");
        OutputStreamWriter os=new OutputStreamWriter(fo,"UTF-8");
        PrintWriter pw=new PrintWriter(os);
        String line = null;
        int i=0;
        while ((line = br.readLine()) != null) {
            i++;
            System.out.println(i);
            String pattern = "\".*?\",";
            Pattern p = Pattern.compile(pattern);//分离每一行的元素
            Matcher m = p.matcher(line);
            ArrayList<String> item=new ArrayList();
            while(m.find()) {
                item.add(m.group());
            }
            String []x=line.split(",");
            item.add(x[x.length-1]);//最后一个没有, 需要再加上
            String []temp=item.get(1).split("],");
            String []temp1=temp[0].split(",");
            if(temp1.length<2) //该记录无POI类别,跳过
                continue;
            String type=temp1[temp1.length-1].replaceAll("]","");
            String Type=type.replaceAll("\"", "");
            if(Type.equals(s_type)){//是指定的POI类别
                //pw.print(type.replaceAll("\"", "") + ",");
                pw.print(item.get(5).replaceAll("\"", ""));
                pw.print(item.get(6).replaceAll("\"", ""));
                pw.println();
                pw.flush();
            }

        }
        fis.close();
        pw.flush();
        pw.close();
        System.out.println("POI类别数据转换完成!");
    }
}
