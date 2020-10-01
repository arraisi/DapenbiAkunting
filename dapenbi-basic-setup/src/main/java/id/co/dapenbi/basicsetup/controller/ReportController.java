package id.co.dapenbi.basicsetup.controller;

import java.text.ParseException;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import id.co.dapenbi.basicsetup.model.Report;
import id.co.dapenbi.basicsetup.service.ReportParameterService;
import id.co.dapenbi.basicsetup.service.ReportService;

@Controller
@RequestMapping("/basicsetup/report")
public class ReportController {
	@Autowired
	ReportService reportService;
	
	@Autowired
	ReportParameterService reportParameterService;
	
	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
	
	@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.POST, value = "/getReportDataTable")
	@ResponseBody
	public DataTablesOutput<Report> getReportDataTable(@Valid @RequestBody DataTablesInput input) throws ParseException {
		return reportService.findForDataTable(input);
	}
}
