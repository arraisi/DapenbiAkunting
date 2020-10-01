package id.co.dapenbi.basicsetup.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import id.co.dapenbi.basicsetup.finder.LookupFinder;
import id.co.dapenbi.basicsetup.model.Lookup;
import id.co.dapenbi.basicsetup.repository.LookupRepository;
import id.co.dapenbi.basicsetup.service.LookupService;

@Service
@Repository
public class LookupServiceImpl implements LookupService {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	LookupRepository lookupRepository;

	private static final Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

	@Override
	public List<Lookup> findAll() {
		// TODO Auto-generated method stub
		return (List<Lookup>) lookupRepository.findAll();
	}

	@Override
	public List<Lookup> findByCriteria(LookupFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Lookup> criteriaQuery = criteriaBuilder.createQuery(Lookup.class);

		Root<Lookup> lookup = criteriaQuery.from(Lookup.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;

		if (finder.getLookupId() != null && !finder.getLookupId().equals(0)) {
			predicate = criteriaBuilder.equal(lookup.get("lookupId"), finder.getLookupId());
			predicates.add(predicate);
		}

		if (finder.getLookupCode() != null && !finder.getLookupCode().isEmpty()) {
			predicate = criteriaBuilder.equal(lookup.get("lookupCode"), finder.getLookupCode());
			predicates.add(predicate);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean save(Lookup lookup) {
		// TODO Auto-generated method stub
		try {
			
			lookupRepository.save(lookup);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}

		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean delete(Lookup lookup) {
		// TODO Auto-generated method stub
		try {
			lookupRepository.delete(lookup);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public DataTablesOutput<Lookup> findForDataTable(DataTablesInput input) {
		return lookupRepository.findAll(input);
	}

}
