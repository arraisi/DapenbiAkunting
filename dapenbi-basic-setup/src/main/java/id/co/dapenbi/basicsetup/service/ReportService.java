package id.co.dapenbi.basicsetup.service;

import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.ReportFinder;
import id.co.dapenbi.basicsetup.model.Report;

public interface ReportService {
	public List<Report> findAll();
	public List<Report> findByCriteria(ReportFinder finder);
	public boolean save(Report report);
	public boolean delete(Report report);
	
	public DataTablesOutput<Report> findForDataTable(DataTablesInput input);
}
