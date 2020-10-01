package id.co.dapenbi.accounting.repository.laporan.ojk;

import id.co.dapenbi.accounting.entity.laporan.ojk.OjkLphu;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OjkLphuRepository extends DataTablesRepository<OjkLphu, Integer> {
    List<OjkLphu> findAll();
}
