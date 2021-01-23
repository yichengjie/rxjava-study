package com.yicj.study.servlet.servlet;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
/**
 * Created by gao on 16-4-15.
 */
@WebServlet(urlPatterns = {"/asyncdemo"}, asyncSupported = true)
public class AsyncDemoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter out = resp.getWriter();
        out.println("进入Servlet的时间：" + new Date() + ".");
        out.flush();
        //在子线程中执行业务调用，并由其负责输出响应，主线程退出
        AsyncContext ctx = req.startAsync();
        new Thread(new Executor(ctx)).start();
        out.println("结束Servlet的时间：" + new Date() + ".");
        out.flush();
    }
    public class Executor implements Runnable {
        private AsyncContext ctx ;
        public Executor(AsyncContext ctx) {
            this.ctx = ctx;
        }
        public void run() {
            try {
                //等待10秒钟，以模拟业务方法的执行
                Thread.sleep(10000);
                PrintWriter out = ctx.getResponse().getWriter();
                out.println("业务处理完毕的时间：" + new Date() + ".");
                out.flush();
                ctx.complete();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    public void destroy() {
        //空
    }
    @Override
    public void init() throws ServletException {
        //空
    }
}