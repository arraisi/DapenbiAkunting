package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkBopr;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkBoprRepository extends DataTablesRepository<OjkBopr, Integer> {
    List<OjkBopr> findAll();
}
