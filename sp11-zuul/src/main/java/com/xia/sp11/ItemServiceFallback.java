package com.xia.sp11;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import com.xia.sp01.web.util.JsonResult;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class ItemServiceFallback implements FallbackProvider {

	@Override
	public String getRoute() {
		//路由规则，返回一个service-id
		//当执行item-service失败，
	    //当前这个降级类,只对指定的微服务有效
		return "item-service";//
		//星号和null都表示所有微服务失败都应用当前降级类
		//"*"; //null;
	}

	
	
	//该方法返回封装降级响应的对象
    //ClientHttpResponse中封装降级响应
	@Override
	public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        return response();//降级的响应，封装成一个Response
	}

	private ClientHttpResponse response() {
        return new ClientHttpResponse() {
            //下面三个方法都是协议号
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;//200 ok
            }
            @Override
            public int getRawStatusCode() throws IOException {
                return HttpStatus.OK.value();
            }
            @Override
            public String getStatusText() throws IOException {
                return HttpStatus.OK.getReasonPhrase();//文本
            }

            @Override
            public void close() {
            }

            @Override
            public InputStream getBody() throws IOException {
            	//返回协议体
            	log.info("fallback body");
            	String s = JsonResult.err().msg("后台服务错误").toString();
                return new ByteArrayInputStream(s.getBytes("UTF-8"));
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders headers = new HttpHeaders();
                //添加协议属性
                headers.setContentType(MediaType.APPLICATION_JSON);
                return headers;
            }
        };
    }
}
