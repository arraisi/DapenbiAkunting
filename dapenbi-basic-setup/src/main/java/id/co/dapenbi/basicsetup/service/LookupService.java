package id.co.dapenbi.basicsetup.service;

import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.LookupFinder;
import id.co.dapenbi.basicsetup.model.Lookup;

public interface LookupService {
	public List<Lookup> findAll();
	public List<Lookup> findByCriteria(LookupFinder finder);
	public boolean save(Lookup lookup);
	public boolean delete(Lookup lookup);
	
	// For Datatables result
	public DataTablesOutput<Lookup> findForDataTable(DataTablesInput input);

}
