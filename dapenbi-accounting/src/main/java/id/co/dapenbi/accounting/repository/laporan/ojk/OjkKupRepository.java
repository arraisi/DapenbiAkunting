package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkKup;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkUmpj;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OjkKupRepository extends DataTablesRepository<OjkKup, Integer> {
    List<OjkKup> findAll();
}
