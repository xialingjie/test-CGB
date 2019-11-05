package com.xia.sp11.filter;

import javax.servlet.http.HttpServletRequest;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.xia.sp01.web.util.JsonResult;
@Component
public class AccessFilter extends ZuulFilter{
	
	@Override//用于判断当前用户请求是否要执行过滤请求，如果执行，则执行run方法
	public boolean shouldFilter() {
		//对指定的service过滤，如果要过滤所有服务，直接返回true
		//得到上下文对象
		RequestContext ctx = RequestContext.getCurrentContext();
		//得到service的id
		String serviceId = (String) ctx.get(FilterConstants.SERVICE_ID_KEY);
		//判断是否是商品服务
		if(serviceId.equals("item-service")) {
			return true;//返回true执行商品过滤器代码
		}
		return false;//返回false,跳过过滤器直接向后继续

	}

	@Override//如果不执行，则直接从run方法做出响应
	public Object run() throws ZuulException {
		RequestContext ctx = RequestContext.getCurrentContext();
		HttpServletRequest req = ctx.getRequest();
		//从request得到一个请求参数
		String at = req.getParameter("token");
		if (at == null) {//如果没有数据则直接返回
			//此设置会阻止请求被路由到后台微服务
			ctx.setSendZuulResponse(false);//阻止向后请求
			ctx.setResponseStatusCode(200);
			ctx.setResponseBody(JsonResult.err().code(JsonResult.NOT_LOGIN).toString());
		}
		//zuul过滤器返回的数据设计为以后扩展使用，
		//目前该返回值没有被使用
		return null;
	}

	@Override
	public String filterType() {
		// TODO Auto-generated method stub
		return FilterConstants.PRE_TYPE;//前置过滤器
	}

	@Override
	public int filterOrder() {
		//在上下文获取id
		 //该过滤器顺序要 > 5，才能得到 serviceid
		return FilterConstants.PRE_DECORATION_FILTER_ORDER+1;//设置的6，
	}

}
