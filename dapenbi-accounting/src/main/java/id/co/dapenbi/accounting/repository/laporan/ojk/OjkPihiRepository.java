package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPihi;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkPihiRepository extends DataTablesRepository<OjkPihi, Integer> {
    List<OjkPihi> findAll();
}
