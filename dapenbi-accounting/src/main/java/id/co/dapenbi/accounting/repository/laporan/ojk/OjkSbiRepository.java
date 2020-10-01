package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSbi;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OjkSbiRepository extends DataTablesRepository<OjkSbi, Integer> {
}
