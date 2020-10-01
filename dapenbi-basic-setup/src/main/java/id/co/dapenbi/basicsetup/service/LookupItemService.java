package id.co.dapenbi.basicsetup.service;

import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.LookupItemFinder;
import id.co.dapenbi.basicsetup.model.LookupItem;

public interface LookupItemService {
	public List<LookupItem> findAll();
	public List<LookupItem> findByCriteria(LookupItemFinder finder);
	public boolean save(LookupItem lookupItem);
	public boolean delete(LookupItem lookupItem);
	
	public DataTablesOutput<LookupItem> findForDataTable(DataTablesInput input);
	
}
