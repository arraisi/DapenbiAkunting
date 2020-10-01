package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewDetail;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewLog;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.List;

public interface BudgetReviewLogRepository extends DataTablesRepository<BudgetReviewLog, Integer> {
    List<BudgetReviewLog> findByNoBudgetReview(String id);
}
