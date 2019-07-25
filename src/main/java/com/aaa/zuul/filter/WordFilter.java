package com.aaa.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
@Component

public class WordFilter extends ZuulFilter {

    @Value("${bad_words}")
    private String badWords;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {

        System.out.println("进入了脏字敏感词汇过滤器...");
        //获取上下文对象
        RequestContext currentContext = RequestContext.getCurrentContext();
        //使用zuul组件提供上下文对象获取HttpServletRequest获取HttpServletResponse对象
        HttpServletRequest request = currentContext.getRequest();
        HttpServletResponse response = currentContext.getResponse();
        response.setCharacterEncoding("utf-8");
        //假如请求参数为 url?a=1&b=2&c=3&d=4....
        //获取所有的请求参数名称{a,b,c,d,...}
        Enumeration<String> parameterNames = request.getParameterNames();
        System.out.println(badWords + "..........");
        String[] tmpBadWords = badWords.split(",");
        //循环
        while (parameterNames.hasMoreElements()) {
            //判断有没有更多的元素
            String parameterName = parameterNames.nextElement();//第一次a,第二次b，第三次c ...
            //第一次得到1 第二次得到2 第三次得到 3 ...
            String parameterValue = request.getParameter(parameterName);
            //判断 parameter有没有包含 "共产党","毛主席","资本主义","马建腾" "任意关键字"
            for (String tmpBadWord : tmpBadWords) {
                if (parameterValue.contains(tmpBadWord)) {
                    try {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "你提交的内容含有非法字符,禁止访问我的主页....");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }
}