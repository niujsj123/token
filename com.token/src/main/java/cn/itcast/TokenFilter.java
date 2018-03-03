package cn.itcast;

import io.jsonwebtoken.Claims;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

import cn.itcast.redis.RedisApi;
//@Component
public class TokenFilter implements Filter{

	private String excludedPages;
	private String[] excludedPageArray;
	private String redirectPath;
	/*@Autowired
	private JwtTokenProvider jwtTokenProvider;*/
	public void init(FilterConfig filterConfig) throws ServletException {
		excludedPages = filterConfig.getInitParameter("excludedPages");
		if (StringUtils.isNotEmpty(excludedPages)) {     
			excludedPageArray = excludedPages.split(",");     
			} 
		redirectPath = filterConfig.getInitParameter("redirectPath");
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		boolean isExcludedPage = false;     
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		redirectPath = req.getContextPath() + redirectPath;// 没有登陆转向页面
		String uri = req.getRequestURI();
		uri = uri.replace(req.getContextPath(),"");
		for (String page : excludedPageArray) {// 判断是否在过滤url之外
			if (uri.equals(page)) {
				isExcludedPage = true;
				break;
			}
		} 
		
		if (isExcludedPage){
			chain.doFilter(request, response); 
		} else {
			/*String auth = req.getHeader("Authorization");
			// 检验token是否正确
			// 这里只是通过使用key对token进行解码是否成功，并没有对有效期、已经token里面的内容进行校验。
			Claims claims = jwtTokenProvider.parseToken(auth);
			
			String token = JSONObject.toJSONString(claims);*/
			String token = req.getHeader("Authorization");
			System.out.println("token="+token);
			if (StringUtils.isEmpty(token) || !RedisApi.exists(token)){
				resp.sendRedirect(redirectPath);
			} else if (uri.equals("/logout")){
				TokenUtils.delToken(token);
				chain.doFilter(request, response);
			}else {
				String value = RedisApi.get(token);
				RedisApi.set(token, value, 1800);
				chain.doFilter(request, response); 
			}
			
		}
		
	}

	public void destroy() {
		// TODO Auto-generated method stub
		
	}

}
