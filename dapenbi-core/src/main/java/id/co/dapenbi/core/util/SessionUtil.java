package id.co.dapenbi.core.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class SessionUtil {
	public static String getSession(String name) {
		RequestAttributes requestAttributes = RequestContextHolder
	            .currentRequestAttributes();
	    ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
	    HttpServletRequest request = attributes.getRequest();
	    HttpSession httpSession = request.getSession(true);
	    
	    String result = httpSession.getAttribute(name) != null ? httpSession.getAttribute(name).toString() : "";
	    
	    return result;
	}
	
	public static void setSession(String name, String value) {
		RequestAttributes requestAttributes = RequestContextHolder
	            .currentRequestAttributes();
	    ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
	    HttpServletRequest request = attributes.getRequest();
	    HttpSession httpSession = request.getSession(true);

	    httpSession.setAttribute(name, value);
	}
}
