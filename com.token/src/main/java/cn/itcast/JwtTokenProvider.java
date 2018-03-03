package cn.itcast;

import java.util.Date;
import java.util.UUID;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodecs;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;

// jwt 工具类
// 这个博客中描述了jwt是什么东西
// http://www.jianshu.com/p/576dbf44b2ae
//@Component
public class JwtTokenProvider {
	// 注入配置类
	private JwtConfiguration configuration;

	public JwtTokenProvider(JwtConfiguration configuration) {
		this.setConfiguration(configuration);
	}

	/**
	 * 生成token
	 * 
	 * @return
	 */
	public String createToken(String name,String pwd) {
		String compactJws = Jwts.builder().setPayload(JSONObject.toJSONString(parseClaims(name,pwd)))
				.compressWith(CompressionCodecs.DEFLATE)
				.signWith(SignatureAlgorithm.HS512, configuration.getSecretKeySpec()).compact();
		return compactJws;
	}
	// UAAClaims这个对象就是token中的内容
		private UAAClaims parseClaims(String name,String pwd) {
			UAAClaims uaaClaims = new UAAClaims();
			uaaClaims.setIssuer(configuration.getIss());
			uaaClaims.setIssuedAt(new Date());
			uaaClaims.setAudience(name);
			uaaClaims.setId(UUID.randomUUID().toString());
			uaaClaims.setUserName(name);
			uaaClaims.setExpiration(new Date(System.currentTimeMillis() + configuration.getExpm() * 1000 * 60));
//			uaaClaims.setEmail(user.getEmail());
//			uaaClaims.setPhone(user.getPhone());
			uaaClaims.setSubject(name);
			uaaClaims.setNotBefore(new Date());
			return uaaClaims;

		}
	/** token转换 */
	public Claims parseToken(String token) {
		try {
			return Jwts.parser().setSigningKey(configuration.getSecretKeySpec()).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public JwtConfiguration getConfiguration() {
		return configuration;
	}

	public void setConfiguration(JwtConfiguration configuration) {
		this.configuration = configuration;
	}
}
