package com.iss;

import java.util.Date;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@WebListener
public class SessionListener implements HttpSessionListener{
	private static long sessionCount = 0;
	@Override
	public void sessionCreated(HttpSessionEvent se) {
		sessionCount ++;
		System.out.println("一个session被创建,当前session数："+sessionCount+"time="+new Date());
		se.getSession().setAttribute("sessionCount", sessionCount);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		if(sessionCount > 0) {
			sessionCount --;
		}
		System.out.println("一个session被销毁，当前session数："+sessionCount+"，time="+new Date());
		se.getSession().setAttribute("sessionCount", sessionCount);
	}

}
