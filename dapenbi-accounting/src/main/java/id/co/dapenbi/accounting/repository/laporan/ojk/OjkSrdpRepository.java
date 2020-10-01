package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkAlm;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSrdp;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OjkSrdpRepository extends DataTablesRepository<OjkSrdp, Integer> {
}
