package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPph;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkPphRepository extends DataTablesRepository<OjkPph, Integer> {
    List<OjkPph> findAll();
}
