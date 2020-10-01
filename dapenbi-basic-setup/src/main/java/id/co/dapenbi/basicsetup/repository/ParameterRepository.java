package id.co.dapenbi.basicsetup.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import id.co.dapenbi.basicsetup.model.Parameter;

public interface ParameterRepository extends DataTablesRepository<Parameter, Integer> {

}
