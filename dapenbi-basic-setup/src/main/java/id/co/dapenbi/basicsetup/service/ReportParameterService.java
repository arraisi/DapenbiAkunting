package id.co.dapenbi.basicsetup.service;

import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.ReportParameterFinder;
import id.co.dapenbi.basicsetup.model.ReportParameter;

public interface ReportParameterService {
	public List<ReportParameter> findAll();
	public List<ReportParameter> findByCriteria(ReportParameterFinder finder);
	public boolean save(ReportParameter reportParameter);
	public boolean delete(ReportParameter reportParameter);
	
	public DataTablesOutput<ReportParameter> findForDataTable(DataTablesInput input);
}
