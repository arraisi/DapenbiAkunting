package id.co.dapenbi.core.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpRequestUtil {
	public static boolean isValid(String value) {
		if(value != null && !value.isEmpty())
			return true;
		else
			return false;
	}
	
	public static String getBaseUrl() {
		String baseEnvLinkURL = null;
		HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		// lazy about determining protocol but can be done too
		String hostname = currentRequest.getServerName();
		if (hostname.equals("0:0:0:0:0:0:0:1"))
			hostname = "localhost";

		baseEnvLinkURL = "http://" + hostname;

		if (hostname.equals("202.59.167.19")) {
			baseEnvLinkURL += ":2424";

		} else {
			if (currentRequest.getLocalPort() != 80) {
				baseEnvLinkURL += ":" + currentRequest.getLocalPort();
			}
		}

		if (!StringUtils.isEmpty(currentRequest.getContextPath())) {
			baseEnvLinkURL += currentRequest.getContextPath();
		}
		return baseEnvLinkURL;
	}
}
