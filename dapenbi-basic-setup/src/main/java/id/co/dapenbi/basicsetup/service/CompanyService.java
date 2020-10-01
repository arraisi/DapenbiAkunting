package id.co.dapenbi.basicsetup.service;

import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.CompanyFinder;
import id.co.dapenbi.basicsetup.model.Company;

public interface CompanyService {
	public List<Company> findAll();
	public List<Company> findByCriteria(CompanyFinder finder);
	public boolean save(Company company);
	public boolean delete(Company company);
	
	public DataTablesOutput<Company> findForDataTable(DataTablesInput input);
}
