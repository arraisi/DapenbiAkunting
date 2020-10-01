package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkObsud;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkSukuk;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OjkObsudRepository extends DataTablesRepository<OjkObsud, Integer> {
}
