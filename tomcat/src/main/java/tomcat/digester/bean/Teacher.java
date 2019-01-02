package tomcat.digester.bean;

import java.util.ArrayList;
import java.util.List;

public class Teacher {

	private String name;
	private List<Student> students = new ArrayList<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void addStudent(Student student) {
		students.add(student);
	}
	
	
	
}
