package cn.itcast;

import cn.itcast.redis.RedisApi;
import cn.itcast.redis.UUIDUtil;

public class TokenUtils {
	public static String createToken(final String name){
		String token = UUIDUtil.getUUID();
		if (RedisApi.exists(name)){
			String oldToken = RedisApi.get(name);
			RedisApi.del(oldToken);
		} /*else {
			RedisApi.set(name,token);
		}*/
		RedisApi.set(name,token);
		RedisApi.set(token,name,1800);
		return token;
		}
	
	public static void delToken(final String token) {
		/*if (RedisApi.exists(name)) {
			RedisApi.del(name);
		}*/
		if (RedisApi.exists(token)) {
			long ret =RedisApi.del(token);
			System.out.println("ret-="+ret);
		}
	}
}
