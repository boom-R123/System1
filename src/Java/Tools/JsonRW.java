package Tools;

import com.alibaba.fastjson.JSONObject;

import java.io.*;

//读写Json文件
public class JsonRW {

    public static JSONObject readJson(String path){
        //从给定位置获取文件
        File file = new File(path);
        BufferedReader reader = null;
        StringBuffer data = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));
            String temp;
            while((temp = reader.readLine()) != null){
                data.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return JSONObject.parseObject(data.toString());
    }

    public static void writeJson(String path,Object json){
        BufferedWriter writer = null;
        File file = new File(path);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
