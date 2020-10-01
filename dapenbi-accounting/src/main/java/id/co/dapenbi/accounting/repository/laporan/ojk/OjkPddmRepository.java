package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPddm;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkPddmRepository extends DataTablesRepository<OjkPddm, Integer> {
    List<OjkPddm> findAll();
}
