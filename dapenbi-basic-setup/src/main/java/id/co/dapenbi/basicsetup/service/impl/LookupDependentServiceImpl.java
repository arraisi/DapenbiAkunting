package id.co.dapenbi.basicsetup.service.impl;

import id.co.dapenbi.basicsetup.finder.LookupDependentFinder;
import id.co.dapenbi.basicsetup.model.LookupDependent;
import id.co.dapenbi.basicsetup.repository.LookupDependentRepository;
import id.co.dapenbi.basicsetup.service.LookupDependentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Repository
public class LookupDependentServiceImpl implements LookupDependentService {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    LookupDependentRepository lookupDependentRepository;

    private static final Logger logger = LoggerFactory.getLogger(LookupDependentServiceImpl.class);

    @Override
    public List<LookupDependent> findAll() {
        return (List<LookupDependent>) lookupDependentRepository.findAll();
    }

    @Override
    public List<LookupDependent> findByCriteria(LookupDependentFinder finder) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<LookupDependent> criteriaQuery = criteriaBuilder.createQuery(LookupDependent.class);

        Root<LookupDependent> lookupDependent = criteriaQuery.from(LookupDependent.class);
        List<Predicate> predicates = new ArrayList<Predicate>();
        Predicate predicate = null;

        if (finder.getLookupDependentId() != null && !finder.getLookupDependentId().equals(0)) {
            predicate = criteriaBuilder.equal(lookupDependent.get("lookupDependentId"), finder.getLookupDependentId());
            predicates.add(predicate);
        }

        if (finder.getLookupItemId() != null && !finder.getLookupItemId().equals(0)) {
            predicate = criteriaBuilder.equal(lookupDependent.get("lookupItem").get("lookupItemId"), finder.getLookupItemId());
            predicates.add(predicate);
        }

        criteriaQuery.where(predicates.toArray(new Predicate[]{}));

        List<Order> orderList = new ArrayList<>();

        orderList.add(criteriaBuilder.asc(lookupDependent.get("lookupDependentValue")));

        criteriaQuery.orderBy(orderList);

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public boolean save(LookupDependent lookupDependent) {
        try {
            lookupDependentRepository.save(lookupDependent);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public boolean delete(LookupDependent lookupDependent) {
        try {
            lookupDependentRepository.delete(lookupDependent);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public DataTablesOutput<LookupDependent> findForDataTable(DataTablesInput input) {
        return lookupDependentRepository.findAll(input);
    }
}
