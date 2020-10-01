package id.co.dapenbi.basicsetup.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.AutoNumberFinder;
import id.co.dapenbi.basicsetup.model.AutoNumber;

public interface AutoNumberService {
	public List<AutoNumber> findAll();
	public List<AutoNumber> findByCriteria(AutoNumberFinder finder);
	public boolean save(AutoNumber autoNumber);
	public boolean delete(AutoNumber autoNumber);
	
	public Map<String, Object> generateAutoNumber(String transactionCode, String companyCode);
	
	public DataTablesOutput<AutoNumber> findForDataTable(DataTablesInput input);
}
