package id.co.dapenbi.basicsetup.service;

import java.util.List;

import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import id.co.dapenbi.basicsetup.finder.ParameterFinder;
import id.co.dapenbi.basicsetup.model.Parameter;

public interface ParameterService {
	public List<Parameter> findAll();
	public List<Parameter> findByCriteria(ParameterFinder finder);
	public boolean save(Parameter parameter);
	public boolean delete(Parameter parameter);
	
	public DataTablesOutput<Parameter> findForDataTable(DataTablesInput input);
}
