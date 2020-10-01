package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.WarkatJurnalDTO;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface WarkatJurnalDtoDataTableRepository extends DataTablesRepository<WarkatJurnalDTO.datatables, Integer> {
    Iterable<WarkatJurnalDTO.datatables> findByNoWarkat(String noWarkat);
}
