package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkObli;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkShm;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OjkObliRepository extends DataTablesRepository<OjkObli, Integer> {
}
