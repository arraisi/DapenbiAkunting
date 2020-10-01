package id.co.dapenbi.basicsetup.repository;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import id.co.dapenbi.basicsetup.model.Report;

@Repository
public interface ReportRepository extends DataTablesRepository<Report, Long> {

}
