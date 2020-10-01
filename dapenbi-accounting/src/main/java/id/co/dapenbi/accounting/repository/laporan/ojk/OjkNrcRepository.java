package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkNrc;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OjkNrcRepository extends DataTablesRepository<OjkNrc, Integer> {
    List<OjkNrc> findAll();
}
