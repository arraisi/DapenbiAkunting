package id.co.dapenbi.accounting.repository.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.BudgetReview;
import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewDetail;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

import java.util.Collection;
import java.util.List;

public interface BudgetReviewDetailRepository extends DataTablesRepository<BudgetReviewDetail, Integer> {
    List<BudgetReviewDetail> findByNoBudgetReview(String id);

    int deleteByNoBudgetReview(String noBudgetReview);
}
