package id.co.dapenbi.basicsetup.controller;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import id.co.dapenbi.basicsetup.finder.CompanyFinder;
import id.co.dapenbi.basicsetup.model.Company;
import id.co.dapenbi.basicsetup.service.CompanyService;
import id.co.dapenbi.core.model.Privilege;
import id.co.dapenbi.core.util.HttpRequestUtil;
import id.co.dapenbi.core.util.MessageUtil;
import id.co.dapenbi.core.util.SecurityUtil;

@Controller
@RequestMapping("/basicsetup/company")
public class CompanyController {
	@Autowired
	CompanyService companyService;
	
	@RequestMapping("")
	public String index(Model model, HttpServletRequest request){
		Privilege privilege =  new Privilege();;
		try {
			privilege = SecurityUtil.getUserPrivilege(SecurityUtil.getUsername(), request.getRequestURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("flagRead", privilege.isFlagRead());
		model.addAttribute("flagEdit", privilege.isFlagEdit());
		model.addAttribute("flagDelete", privilege.isFlagDelete());
		
		return "basic-setup/company/index";
	}
	
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/getCompanyDataTable")
	@ResponseBody
	public DataTablesOutput<Company> getCompanyDataTable(@Valid @RequestBody DataTablesInput input) throws ParseException {
		return companyService.findForDataTable(input);
	}
	
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = { RequestMethod.POST,
			RequestMethod.GET }, value = "/getCompany")
	public ResponseEntity<Map<String, Object>> getEntity(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		CompanyFinder finder = new CompanyFinder();
		if (HttpRequestUtil.isValid(request.getParameter("companyId")))
			finder.setCompanyId(Long.parseLong(request.getParameter("companyId")));
		
		if (HttpRequestUtil.isValid(request.getParameter("companyCode")))
			finder.setCompanyCode(request.getParameter("companyCode"));

		List<Company> company = companyService.findByCriteria(finder);

		data.put("error", false);
		data.put("data", company);

		return ResponseEntity.status(HttpStatus.OK).body(data);
	}
	
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/saveCompany")
	public ResponseEntity<Map<String, Object>> saveCompany(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		Long companyId = (HttpRequestUtil.isValid(request.getParameter("companyId"))
				? Long.parseLong(request.getParameter("companyId"))
				: 0);

		Company company = new Company();

		company.setCompanyName(request.getParameter("companyName"));
		company.setCompanyCode(request.getParameter("companyCode"));
		
		if (companyId != 0) {
			CompanyFinder finder = new CompanyFinder();
			finder.setCompanyId(companyId);

			List<Company> companies = companyService.findByCriteria(finder);

			if (companies.size() > 0) {
				company.setCompanyId(companyId);

				boolean result = companyService.save(company);

				if (result) {
					data.put("error", false);
					data.put("data", company);
				} else {
					data.put("error", true);
					data.put("message", MessageUtil.ERROR_UPDATE_DATA);
				}
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.DATA_NOT_FOUND);
			}
		} else {			
			boolean result = companyService.save(company);
			
			if (result) {
				data.put("error", false);
				data.put("data", company);
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.ERROR_INSERT_DATA);
			}
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(data);
	}
	
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/deleteCompany")
	public ResponseEntity<Map<String, Object>> deleteCompany(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		CompanyFinder finder = new CompanyFinder();
		finder.setCompanyId(Long.parseLong(request.getParameter("companyId").toString()));

		List<Company> entities = companyService.findByCriteria(finder);

		if (entities .size() > 0) {
			boolean result = companyService.delete(entities.get(0));

			if (result) {
				data.put("error", false);
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.ERROR_DELETE_DATA);
			}
		} else {
			data.put("error", true);
			data.put("message", MessageUtil.DATA_NOT_FOUND);
		}

		return ResponseEntity.status(HttpStatus.OK).body(data);
	}
}
