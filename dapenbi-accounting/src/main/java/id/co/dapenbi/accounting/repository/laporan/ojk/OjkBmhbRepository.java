package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkBmhb;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkBmhbRepository extends DataTablesRepository<OjkBmhb, Integer> {
    List<OjkBmhb> findAll();
}
