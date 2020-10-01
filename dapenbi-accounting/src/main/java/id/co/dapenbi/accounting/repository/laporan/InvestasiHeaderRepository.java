package id.co.dapenbi.accounting.repository.laporan;

import id.co.dapenbi.accounting.entity.laporan.InvestasiHeader;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvestasiHeaderRepository extends DataTablesRepository<InvestasiHeader, String> {

    @Query("from InvestasiHeader where idLaporanHeader = ?1 and idLaporanDetail = ?2 and statusData = 'Y'")
    Optional<InvestasiHeader> findByIdLaporanHdrAndidLaporanDtl(Integer idLaporanHdr, Integer idLaporanDtl);
}
