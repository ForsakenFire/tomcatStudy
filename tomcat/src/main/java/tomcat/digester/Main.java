package tomcat.digester;

import java.io.File;

import org.apache.tomcat.util.digester.Digester;
import org.xml.sax.InputSource;

import tomcat.digester.bean.School;

/**
 * ������tomcat����serve.xml������servlet����
 * @author TD
 *
 */
public class Main {
	public static void main(String[] args) {
		Digester digester = new Digester();
		School school = new School();
		File file = null;
		//�Ƿ���Ҫ��֤xml�ĵ��ĺϷ��ԣ�false��ʾ����Ҫ����DTD����У��
		digester.setValidating(false);
		//�Ƿ���Ҫ���нڵ����ù���У��
		digester.setRulesValidation(true);
		//ƥ��school�ڵ㣬����school����
		digester.addObjectCreate("school", "tomcat.digester.bean.School");
		//ƥ��school�ڵ㣬���ö�������
		digester.addSetProperties("school");
		//ƥ��school/teacher�ڵ㣬����teacher����
		digester.addObjectCreate("school/teacher", "tomcat.digester.bean.Teacher");
		//ƥ��school/teacher�ڵ㣬���ö�������
		digester.addSetProperties("school/teacher");
		//ƥ��ڵ�ʱ���÷�����ʵ�ָ��ӹ�ϵ
		digester.addSetNext("school/teacher", "addTeacher", "tomcat.digester.bean.Teacher");
		//ƥ��school/teacher/student�ڵ㣬����student����
		digester.addObjectCreate("school/teacher/student", "tomcat.digester.bean.Student");
		//ƥ��school/teacher/student�ڵ㣬���ö�������
		digester.addSetProperties("school/teacher/student");
		//ƥ��ڵ�ʱ���÷�����ʵ�ָ��ӹ�ϵ
		digester.addSetNext("school/teacher/student", "addStudent", "tomcat.digester.bean.Student");
		//ƥ��school/teacher/student/extension�ڵ�
		digester.addCallMethod("school/teacher/student/extension", "putExtension",2);
		//���÷����ĵ�һ������
		digester.addCallParam("school/teacher/student/extension/property-name", 0);
		//�ڶ�������
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
