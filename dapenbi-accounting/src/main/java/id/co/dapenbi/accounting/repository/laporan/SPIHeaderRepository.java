package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.SPIHeader;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SPIHeaderRepository extends DataTablesRepository<SPIHeader, Integer> {

}
