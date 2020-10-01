package id.co.dapenbi.basicsetup.service;

import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.AutoNumberLogFinder;
import id.co.dapenbi.basicsetup.model.AutoNumberLog;

public interface AutoNumberLogService {
	public List<AutoNumberLog> findAll();
	public List<AutoNumberLog> findByCriteria(AutoNumberLogFinder finder);
	public boolean save(AutoNumberLog autoNumberLog);
	public boolean delete(AutoNumberLog autoNumberLog);
	
	public boolean updateAutoNumberLogInterface(String transactionCode, Long lastIncrement);
	
	public DataTablesOutput<AutoNumberLog> findForDataTable(DataTablesInput input);
}
