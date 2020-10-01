package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.InvestasiDetail;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvestasiDetailRepository extends DataTablesRepository<InvestasiDetail, Integer> {

    void deleteByIdInvestasi(String idInvestasi);
}
