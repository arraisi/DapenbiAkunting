package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.entity.transaksi.WarkatLog;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface BudgetReviewRepository extends DataTablesRepository<BudgetReview, String> {
}
