package tomcat.digester;

import java.io.File;

import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.InputSource;

import tomcat.digester.bean.School;

/**
 * 类似于tomcat解析serve.xml并创建servlet容器
 * @author TD
 *
 */
public class Main {
	public static void main(String[] args) {
		Digester digester = new Digester();
		School school = new School();
		File file = null;
		//是否需要验证xml文档的合法性，false表示不需要进行DTD规则校验
		digester.setValidating(false);
		//是否需要进行节点设置规则校验
		digester.setRulesValidation(true);
		//匹配school节点，创建school对象
		digester.addObjectCreate("school", "tomcat.digester.bean.School");
		//匹配school节点，设置对象属性
		digester.addSetProperties("school");
		//匹配school/teacher节点，创建teacher对象
		digester.addObjectCreate("school/teacher", "tomcat.digester.bean.Teacher");
		//匹配school/teacher节点，设置对象属性
		digester.addSetProperties("school/teacher");
		//匹配节点时调用方法，实现父子关系
		digester.addSetNext("school/teacher", "addTeacher", "tomcat.digester.bean.Teacher");
		//匹配school/teacher/student节点，创建student对象
		digester.addObjectCreate("school/teacher/student", "tomcat.digester.bean.Student");
		//匹配school/teacher/student节点，设置对象属性
		digester.addSetProperties("school/teacher/student");
		//匹配节点时调用方法，实现父子关系
		digester.addSetNext("school/teacher/student", "addStudent", "tomcat.digester.bean.Student");
		//匹配school/teacher/student/extension节点
		digester.addCallMethod("school/teacher/student/extension", "putExtension",2);
		//调用方法的第一个参数
		digester.addCallParam("school/teacher/student/extension/property-name", 0);
		//第二个参数
		digester.addCallParam("school/teacher/student/extension/property-value", 1);
		try {
			file = new File("E:\\EclipsePhotonWorkSpace\\tomcat\\src\\main\\java\\tomcat\\digester\\digester.xml");
			school = (School) digester.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(school);
	}
	
}
