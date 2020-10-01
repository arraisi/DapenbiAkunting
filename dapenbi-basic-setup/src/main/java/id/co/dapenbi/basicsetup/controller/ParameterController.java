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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import id.co.dapenbi.basicsetup.finder.ParameterFinder;
import id.co.dapenbi.basicsetup.model.Parameter;
import id.co.dapenbi.basicsetup.service.ParameterService;
import id.co.dapenbi.core.model.Privilege;
import id.co.dapenbi.core.util.HttpRequestUtil;
import id.co.dapenbi.core.util.MessageUtil;
import id.co.dapenbi.core.util.SecurityUtil;

@Controller
@RequestMapping("basicsetup/parameter")
public class ParameterController {
	@Autowired
	ParameterService parameterService;

	@RequestMapping("")
	public String inquiry(Model model, HttpServletRequest request) {
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
		
		return "basic-setup/parameter/index";
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/getParameterDataTable")
	@ResponseBody
	public DataTablesOutput<Parameter> getParameterDataTable(@Valid @RequestBody DataTablesInput input)
			throws ParseException {
		return parameterService.findForDataTable(input);
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = { RequestMethod.POST,
			RequestMethod.GET }, value = "/getParameter")
	public ResponseEntity<Map<String, Object>> getParameter(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		ParameterFinder finder = new ParameterFinder();
		if (HttpRequestUtil.isValid(request.getParameter("parameterId")))
			finder.setParameterId(Long.parseLong(request.getParameter("parameterId")));
		
		if(HttpRequestUtil.isValid(request.getParameter("parameterCode")))
			finder.setParameterCode(request.getParameter("parameterCode"));

		List<Parameter> parameter = parameterService.findByCriteria(finder);

		data.put("error", false);
		data.put("data", parameter);

		return ResponseEntity.status(HttpStatus.OK).body(data);
	}

	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/saveParameter")
	public ResponseEntity<Map<String, Object>> saveParameter(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		Long parameterId = (HttpRequestUtil.isValid(request.getParameter("parameterId"))
				? Long.parseLong(request.getParameter("parameterId"))
				: 0);

		Parameter parameter = new Parameter();
		parameter.setParameterCode(request.getParameter("parameterCode"));
		parameter.setParameterValue(request.getParameter("parameterValue"));
		parameter.setDescription(request.getParameter("description"));
		parameter.setFlagActive(Boolean.parseBoolean(request.getParameter("flagActive")));
		parameter.setLastUpdatedBy(SecurityUtil.getUsername());
		parameter.setLastUpdateDate(new Date());

		// Update
		if (parameterId != 0) {
			ParameterFinder finder = new ParameterFinder();
			finder.setParameterId(parameterId);

			List<Parameter> parameters = parameterService.findByCriteria(finder);

			if (parameters.size() > 0) {
				Parameter existParameter = parameters.get(0);
				
				parameter.setParameterId(existParameter.getParameterId());
				parameter.setCreatedBy(existParameter.getCreatedBy());
				parameter.setCreationDate(existParameter.getCreationDate());

				boolean result = parameterService.save(parameter);

				if (result) {
					data.put("error", false);
					data.put("data", parameter);
				} else {
					data.put("error", true);
					data.put("message", MessageUtil.ERROR_UPDATE_DATA);
				}
			}

		} else { // insert
			parameter.setCreatedBy(SecurityUtil.getUsername());
			parameter.setCreationDate(new Date());

			boolean result = parameterService.save(parameter);

			if (result) {
				data.put("error", false);
				data.put("data", parameter);
			} else {
				data.put("error", true);
				data.put("message", MessageUtil.ERROR_INSERT_DATA);
			}
		}
		
		return ResponseEntity.status(HttpStatus.OK).body(data);

	}
	
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/deleteParameter")
	public ResponseEntity<Map<String, Object>> deleteParameter(HttpServletRequest request) throws ParseException {
		Map<String, Object> data = new HashMap<String, Object>();

		ParameterFinder finder = new ParameterFinder();
		finder.setParameterId(Long.parseLong(request.getParameter("parameterId").toString()));

		List<Parameter> parameters = parameterService.findByCriteria(finder);

		if (parameters .size() > 0) {
			boolean result = parameterService.delete(parameters.get(0));

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
