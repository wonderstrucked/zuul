package com.aaa.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class IPFilter extends ZuulFilter {
@Value("${bad_ips}")
private  String ips;

    @Override
    public String filterType() {
        /**
         * 支持返回值，四种参数
         * pre  在调用业务方法之前操作
         * route 在调用业务方法时操作
         * post  在调用业务方法之后操作
         * error 发生错误的时候操作
         */
        return "pre";
    }

    @Override
    public int filterOrder() {
        //优先级，加入编写了3个filter 先后循序执行
        // 返回值越小，优先级越高
        //例子：
        // filter1的filterOrder返回值为10
        // filter2的filterOrder返回值为5
        // filter3的filterOrder返回值为1
        // 他们的优先级就是filter3->filter2->filter1
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //返回true代表当前filter可用，false当前filter失效
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        System.out.println("进入非法IP过滤器");

        //执行具体过滤业务的方法
        //先获取上下文对象
        RequestContext context=RequestContext.getCurrentContext();
        //使用zuul组件提供上下文对象获取HttServletRequest或者HttpServletResponse对象
        HttpServletRequest request=context.getRequest();
        HttpServletResponse response=context.getResponse();
        //使用request获取请求的IP地址
        String remoteAddr=request.getRemoteAddr();
        System.out.println("请求的IP："+remoteAddr);
        response.setCharacterEncoding("utf-8");
        //返回在IP地址中最后出现的位置
       String lastIp=remoteAddr.substring(remoteAddr.lastIndexOf(".")+1);
       //判断IP192.168.7.45 中是否包含45，11，19（过滤的IP）
       if (ips.contains(lastIp)){
            try {
                response.sendError(403,"你是黑客，拒绝访问");;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
