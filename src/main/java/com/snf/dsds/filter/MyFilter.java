//package com.snf.dsds.filter;
//
//
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.BufferedReader;
//import java.io.IOException;
//
///**
// * @program: dsds
// * @description:
// * @author: zhouyuj
// * @create: 2021-12-22 17:27
// **/
//public class MyFilter extends OncePerRequestFilter {
//
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
//        System.out.println("进入自定义过滤器");
//        System.out.println(httpServletRequest.getContentLength());
//        System.out.println(getBodyParams(httpServletRequest));
//        filterChain.doFilter(httpServletRequest,httpServletResponse);
//    }
//
//    String getBodyParams(ServletRequest request) {
//        if (request.getContentLength() <= 0) {
//            return "";
//        }
//        BufferedReader reader = null;
//        StringBuilder builder = null;
//        try {
//            String str;
//            builder = new StringBuilder();
//            reader = request.getReader();
//            while ((str = reader.readLine()) != null) {
//                builder.append(str);
//            }
//        } catch (Exception e) {
//            return "";
//        } finally {
//            if (reader != null) {
//                try {
//                    reader.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return builder.toString();
//    }
//
//}
