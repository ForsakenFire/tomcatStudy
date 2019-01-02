package tomcat.digester.bean;

import java.util.HashMap;
import java.util.Map;

public class Student {

	private String name;
	private String code;
	private Map<String,String> extension = new HashMap<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void putExtension(String key, String value) {
		this.extension.put(key, value);
	}
	
	
}
