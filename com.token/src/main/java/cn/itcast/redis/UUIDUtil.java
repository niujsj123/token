package cn.itcast.redis;

import java.util.UUID;

public class UUIDUtil {
	/** 
	* 获得一个UUID 
	* @return String UUID 
	*/ 
	public static String getUUID(){ 
	String uuid = UUID.randomUUID().toString(); 
	//去掉"-"符号 
	return uuid.replaceAll("-", "");
	}
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
}
