package id.co.dapenbi.basicsetup.controller;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.Date;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import id.co.dapenbi.basicsetup.finder.CompanyFinder;
import id.co.dapenbi.basicsetup.finder.LookupFinder;
import id.co.dapenbi.basicsetup.finder.LookupItemFinder;
import id.co.dapenbi.basicsetup.model.Company;
import id.co.dapenbi.basicsetup.model.Lookup;
import id.co.dapenbi.basicsetup.model.LookupItem;
import id.co.dapenbi.basicsetup.service.CompanyService;
import id.co.dapenbi.basicsetup.service.LookupItemService;
import id.co.dapenbi.basicsetup.service.LookupService;
import id.co.dapenbi.core.model.Privilege;
import id.co.dapenbi.core.util.HttpRequestUtil;
import id.co.dapenbi.core.util.MessageUtil;
import id.co.dapenbi.core.util.SecurityUtil;

@Controller
@RequestMapping("/basicsetup/lookup")
public class LookupController {
	@Autowired
	CompanyService companyService;
	
	
	@Autowired
	LookupService lookupService;

	@Autowired
	LookupItemService lookupItemService;

	@RequestMapping("/inquiry")
	public String inquiry(Model model, HttpServletRequest request) {
		Privilege privilege =  new Privilege();
		try {
			privilege = SecurityUtil.getUserPrivilege(SecurityUtil.getUsername(), request.getRequestURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("flagRead", privilege.isFlagRead());
		model.addAttribute("flagEdit", privilege.isFlagEdit());
		model.addAttribute("flagDelete", privilege.isFlagDelete());
		
		return "basic-setup/lookup/inquiry";
	}

	@RequestMapping("/item/{id}")
	public String item(Model model, @PathVariable("id") String id, HttpServletRequest request) {

		return "basic-setup/lookup/item";
	}

	/*
	 * START LOOKUP
	 */
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/getLookupDataTable")
	@ResponseBody
	public DataTablesOutput<Lookup> getLookupDataTable(@Valid @RequestBody DataTablesInput input)
			throws ParseException {
		return lookupService.findForDataTable(input);
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = { RequestMethod.POST,
			RequestMethod.GET }, value = "/getLookup")
	public ResponseEntity<Map<String, Object>> getLookup(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		LookupFinder lookupFinder = new LookupFinder();
		if (HttpRequestUtil.isValid(request.getParameter("lookupId")))
			lookupFinder.setLookupId(Long.parseLong(request.getParameter("lookupId")));

		if (HttpRequestUtil.isValid(request.getParameter("lookupCode")))
			lookupFinder.setLookupCode(request.getParameter("lookupCode"));

		List<Lookup> lookup = lookupService.findByCriteria(lookupFinder);

		data.put("error", false);
		data.put("data", lookup);

		return ResponseEntity.status(HttpStatus.OK).body(data);
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/saveLookup")
	public ResponseEntity<Map<String, Object>> saveLookup(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		Long lookupId = (HttpRequestUtil.isValid(request.getParameter("lookupId"))
				? Long.parseLong(request.getParameter("lookupId"))
				: 0);

		Lookup lookup = new Lookup();

		lookup.setLookupCode(request.getParameter("lookupCode"));
		lookup.setDescription(request.getParameter("description"));
		lookup.setFlagActive(Boolean.parseBoolean(request.getParameter("flagActive")));
		lookup.setFlagSystem(Boolean.parseBoolean(request.getParameter("flagSystem")));
		lookup.setLastUpdatedBy(SecurityUtil.getUsername());
		lookup.setLastUpdateDate(new Date());

		// UPDATE DATA
		if (lookupId != 0) {
			LookupFinder lookupFinder = new LookupFinder();
			lookupFinder.setLookupId(lookupId);

			List<Lookup> lookups = lookupService.findByCriteria(lookupFinder);

			if (lookups.size() > 0) {
				Lookup existLookup = lookups.get(0);

				lookup.setLookupId(lookupFinder.getLookupId());
				lookup.setCreatedBy(existLookup.getCreatedBy());
				lookup.setCreationDate(existLookup.getCreationDate());

				boolean result = lookupService.save(lookup);

				if (result) {
					data.put("error", false);
					data.put("data", lookup);
				} else {
					data.put("error", true);
					data.put("message", MessageUtil.ERROR_UPDATE_DATA);
				}
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.DATA_NOT_FOUND);
			}
		} else { // INSERT DATA

			lookup.setCreatedBy(SecurityUtil.getUsername());
			lookup.setCreationDate(new Date());
			boolean result = lookupService.save(lookup);

			if (result) {
				data.put("error", false);
				data.put("data", lookup);
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.ERROR_INSERT_DATA);
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(data);
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/deleteLookup")
	public ResponseEntity<Map<String, Object>> deleteLookup(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		LookupFinder lookupFinder = new LookupFinder();
		lookupFinder.setLookupId(Long.parseLong(request.getParameter("lookupId").toString()));

		List<Lookup> lookups = lookupService.findByCriteria(lookupFinder);

		if (lookups.size() > 0) {
			boolean result = lookupService.delete(lookups.get(0));

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

	/*
	 * END LOOKUP
	 */

	/*
	 * START LOOKUP ITEM
	 */

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/getLookupItemDataTable")
	@ResponseBody
	public DataTablesOutput<LookupItem> getLookupItemDataTable(@Valid @RequestBody DataTablesInput input)
			throws ParseException {
		
		return lookupItemService.findForDataTable(input);
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = { RequestMethod.POST,
			RequestMethod.GET }, value = "/getLookupItem")
	public ResponseEntity<Map<String, Object>> getLookupItem(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		LookupItemFinder lookupItemFinder = new LookupItemFinder();
		if (HttpRequestUtil.isValid(request.getParameter("lookupItemId")))
			lookupItemFinder.setLookupItemId(Long.parseLong(request.getParameter("lookupItemId")));

		if (HttpRequestUtil.isValid(request.getParameter("lookupItemCode")))
			lookupItemFinder.setLookupItemCode(request.getParameter("lookupItemCode"));
		
		if(HttpRequestUtil.isValid(request.getParameter("companyId"))){
			CompanyFinder companyFinder = new CompanyFinder();
			companyFinder.setCompanyId(Long.parseLong(request.getParameter("companyId")));
			
			Company company = companyService.findByCriteria(companyFinder).get(0);
			
			lookupItemFinder.setCompany(company);
		}
		
		if(HttpRequestUtil.isValid(request.getParameter("lookupCode"))){
			LookupFinder lookupFinder = new LookupFinder();
			lookupFinder.setLookupCode(request.getParameter("lookupCode"));
			
			List<Lookup> lookups = lookupService.findByCriteria(lookupFinder);
			
			if(lookups.size() > 0){
				lookupItemFinder.setLookup(lookups.get(0));
			}
		}

		List<LookupItem> lookupItem = lookupItemService.findByCriteria(lookupItemFinder);

		data.put("error", false);
		data.put("data", lookupItem);

		return ResponseEntity.status(HttpStatus.OK).body(data);
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/saveLookupItem")
	public ResponseEntity<Map<String, Object>> saveLookupItem(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		Long lookupItemId = (HttpRequestUtil.isValid(request.getParameter("lookupItemId"))
				? Long.parseLong(request.getParameter("lookupItemId"))
				: 0);
		
		// Get Parent Relation
		LookupFinder lookupFinder = new LookupFinder();
		lookupFinder.setLookupId(Long.parseLong(request.getParameter("lookupId")));
		
		Lookup lookup = lookupService.findByCriteria(lookupFinder).get(0);
		
		LookupItem lookupItem = new LookupItem();

		lookupItem.setLookup(lookup);
		lookupItem.setLookupItemCode(request.getParameter("lookupItemCode"));
		lookupItem.setLookupItemName(request.getParameter("lookupItemName"));
		lookupItem.setLastUpdatedBy(SecurityUtil.getUsername());
		lookupItem.setLastUpdateDate(new Date());

		// UPDATE
		if (lookupItemId != 0) {
			LookupItemFinder lookupItemFinder = new LookupItemFinder();
			lookupItemFinder.setLookupItemId(lookupItemId);

			List<LookupItem> lookupItems = lookupItemService.findByCriteria(lookupItemFinder);

			if (lookupItems.size() > 0) {
				LookupItem existLookupItem = lookupItems.get(0);
				
				lookupItem.setLookupItemId(lookupItemId);
				lookupItem.setCreatedBy(existLookupItem.getCreatedBy());
				lookupItem.setCreationDate(existLookupItem.getCreationDate());

				boolean result = lookupItemService.save(lookupItem);
				if (result) {
					data.put("error", false);
					data.put("data", lookupItem);
				} else {
					data.put("error", true);
					data.put("message", MessageUtil.ERROR_UPDATE_DATA);
				}
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.DATA_NOT_FOUND);
			}
		} else { // INSERT
			lookupItem.setCreatedBy(SecurityUtil.getUsername());
			lookupItem.setCreationDate(new Date());

			boolean result = lookupItemService.save(lookupItem);

			if (result) {
				data.put("error", false);
				data.put("data", lookupItem);
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.ERROR_INSERT_DATA);
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(data);

	}
	
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/deleteLookupItem")
	public ResponseEntity<Map<String, Object>> deleteLookupItem(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		LookupItemFinder lookupItemFinder = new LookupItemFinder();
		lookupItemFinder.setLookupItemId(Long.parseLong(request.getParameter("lookupItemId").toString()));

		List<LookupItem> lookupItems = lookupItemService.findByCriteria(lookupItemFinder);

		if (lookupItems.size() > 0) {
			boolean result = lookupItemService.delete(lookupItems.get(0));

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
