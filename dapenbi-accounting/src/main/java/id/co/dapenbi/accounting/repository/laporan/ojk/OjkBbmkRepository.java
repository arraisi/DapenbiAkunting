package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkBbmk;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkBbmkRepository extends DataTablesRepository<OjkBbmk, Integer> {
    List<OjkBbmk> findAll();
}
