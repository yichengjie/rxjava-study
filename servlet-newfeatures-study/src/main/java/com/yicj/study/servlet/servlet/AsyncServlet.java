package com.yicj.study.servlet.servlet;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@WebServlet(name="asyncServlet", urlPatterns="/async", asyncSupported=true)
public class AsyncServlet extends HttpServlet {
	private static final long serialVersionUID = 3903580630389463919L;
 
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out = resp.getWriter();
		out.write("hello world <br/>");
		out.println("start："+new Date()+". <br/>");
		out.flush();
		final AsyncContext ctx = req.startAsync(req,resp);
		ctx.setTimeout(3000);
		ctx.start(()->{
			try {
				Thread.sleep(2000);
				out.write("aync thread processing");
				out.flush();
				out.println("async end："+new Date()+".<br/>");
				out.flush();
				ctx.complete();
			}catch (Exception e){
				e.printStackTrace();
			}
		});
		out.println("end："+new Date()+".<br/>");
		out.flush();
	}
}