package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewDetail;
import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.Query;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface SaldoRepository extends DataTablesRepository<Saldo, Integer> {
    @Query("from Saldo where createdDate = ?1")
    List<Saldo> findAllUpdatedToday(Timestamp createdDate);
}
