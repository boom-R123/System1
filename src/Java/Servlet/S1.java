package Servlet;

import Tools.JsonRW;
import com.alibaba.fastjson.JSONObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class S1 extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置字符
        response.setContentType("text/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("content-type", "text/html;charset=UTF-8");

        String action = request.getParameter("action");

        if(action.equals("test")){//通过传过来不同的事件来选取不同的文件进行读取
            //测试
            JSONObject Car = JsonRW.readJson("D:/testdata/test.json");
            System.out.println(Car);
            PrintWriter pw = response.getWriter();
            pw.print(Car);
            pw.close();
        }
        if(action.equals("DrawArea")){
            JSONObject data = JsonRW.readJson("D:/testdata/result11.json");
            System.out.println(data);
            PrintWriter pw = response.getWriter();
            pw.print(data);
            pw.close();
        }
    }
}
