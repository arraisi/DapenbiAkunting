package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkUtln;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkUtlnRepository extends DataTablesRepository<OjkUtln, Integer> {
    List<OjkUtln> findAll();
}
