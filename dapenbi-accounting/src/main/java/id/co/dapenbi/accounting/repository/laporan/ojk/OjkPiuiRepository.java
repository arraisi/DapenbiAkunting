package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPiui;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkRekinv;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface OjkPiuiRepository extends DataTablesRepository<OjkPiui, Integer> {
    List<OjkPiui> findAll();
}
