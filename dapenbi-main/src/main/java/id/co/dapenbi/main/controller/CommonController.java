package id.co.dapenbi.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import id.co.dapenbi.core.service.SecurityService;

@Controller
@RequestMapping("/common")
public class CommonController {
	@Autowired
	SecurityService securityService;
	
}
