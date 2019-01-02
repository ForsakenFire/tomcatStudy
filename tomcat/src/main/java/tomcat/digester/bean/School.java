package tomcat.digester.bean;

import java.util.ArrayList;
import java.util.List;

public class School {
	private String name;
	private List<Teacher> teachers = new ArrayList<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addTeacher(Teacher teacher) {
		teachers.add(teacher);
	}
	@Override
	public String toString() {
		return "School [name=" + name + ", teachers=" + teachers + "]";
	}
	
	
}
