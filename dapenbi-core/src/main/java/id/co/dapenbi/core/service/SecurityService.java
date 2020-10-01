package id.co.dapenbi.core.service;

import java.net.URISyntaxException;
import java.text.ParseException;

import id.co.dapenbi.core.security.SecurityProfile;
import id.co.dapenbi.core.model.User;

public interface SecurityService {
	
	public SecurityProfile getSecurityProfile();

	public SecurityProfile getSecurityProfile(String userName);
	
	public User getActiveUserByUsername(String userName) throws URISyntaxException, ParseException, Exception;

}
