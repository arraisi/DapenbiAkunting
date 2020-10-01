package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkLak;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OjkLakRepository extends DataTablesRepository<OjkLak, Integer> {
}
