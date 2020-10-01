package id.co.dapenbi.main.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import id.co.dapenbi.core.constant.LoginStatus;
import id.co.dapenbi.core.model.User;
import id.co.dapenbi.core.service.SecurityService;
import id.co.dapenbi.main.security.SimpleUserDetail;
 
@Service
public class UserDetailsServiceImpl implements UserDetailsService {	
	@Autowired
	private SecurityService securityService;
	
	private boolean enabled = false;
	
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    	LoginStatus loginStatus = null;
		
		User user =  new User();
		try {
			user = securityService.getActiveUserByUsername(username);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (user.getNip() != null && !user.getNip().isEmpty() ) {
			enabled = true;
			loginStatus = LoginStatus.EMPLOYEE;
		} else {
			user.setPassword("***");
			throw new UsernameNotFoundException(
					"Username or password did not match");
		}
		
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
	    authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		return new SimpleUserDetail(username, user.getPassword(), enabled, true, true,
				true, authorities, loginStatus);
    }
 
}