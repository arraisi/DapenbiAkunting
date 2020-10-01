package id.co.dapenbi.basicsetup.service;

import id.co.dapenbi.basicsetup.finder.LookupDependentFinder;
import id.co.dapenbi.basicsetup.model.LookupDependent;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface LookupDependentService {
    List<LookupDependent> findAll();

    List<LookupDependent> findByCriteria(LookupDependentFinder finder);

    boolean save(LookupDependent lookupDependent);

    boolean delete(LookupDependent lookupDependent);

    DataTablesOutput<LookupDependent> findForDataTable(DataTablesInput input);
}
