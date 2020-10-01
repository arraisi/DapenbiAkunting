package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.BukaSistem;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BukaSistemRepository extends DataTablesRepository<BukaSistem, Long> {

    List<BukaSistem> findAll();

    @Query("from BukaSistem where 1 = 1 order by createdDate desc")
    List<BukaSistem> latest();
}
