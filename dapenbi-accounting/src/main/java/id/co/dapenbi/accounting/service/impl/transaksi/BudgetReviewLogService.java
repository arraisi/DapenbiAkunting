package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.BudgetReviewLog;
import id.co.dapenbi.accounting.repository.transaksi.BudgetReviewLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetReviewLogService {

    @Autowired
    private BudgetReviewLogRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public BudgetReviewLog save(BudgetReviewLog value) {
        return repository.save(value);
    }

    public Optional<BudgetReviewLog> findById(Integer id) {
        return repository.findById(id);
    }

    public List<BudgetReviewLog> findByNoBudgetReview(String id) {
        return repository.findByNoBudgetReview(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer id) {
        repository.deleteById(id);
    }

}
