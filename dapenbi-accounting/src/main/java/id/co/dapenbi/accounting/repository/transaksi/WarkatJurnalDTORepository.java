package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.dto.transaksi.WarkatJurnalDTO;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface WarkatJurnalDTORepository extends DataTablesRepository<WarkatJurnalDTO.newData, Integer> {
    Iterable<WarkatJurnalDTO.newData> findByNoWarkat(String noWarkat);

    int deleteByNoWarkat(String warkat);
}
