package id.co.dapenbi.basicsetup.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
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

import id.co.dapenbi.basicsetup.finder.LookupItemFinder;
import id.co.dapenbi.basicsetup.model.LookupItem;
import id.co.dapenbi.basicsetup.repository.LookupItemRepository;
import id.co.dapenbi.basicsetup.service.LookupItemService;

@Service
@Repository
public class LookupItemServiceImpl implements LookupItemService {
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	LookupItemRepository lookupItemRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

	@Override
	public List<LookupItem> findAll() {
		// TODO Auto-generated method stub
		return (List<LookupItem>) lookupItemRepository.findAll();
	}

	@Override
	public List<LookupItem> findByCriteria(LookupItemFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<LookupItem> criteriaQuery = criteriaBuilder.createQuery(LookupItem.class);
		
		Root<LookupItem> lookupItem = criteriaQuery.from(LookupItem.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;
		
		if(finder.getLookupItemId() != null && !finder.getLookupItemId().equals(0)) {
			predicate = criteriaBuilder.equal(lookupItem.get("lookupItemId"), finder.getLookupItemId());
			predicates.add(predicate);
		}
		
		if(finder.getLookupItemCode() != null && !finder.getLookupItemCode().isEmpty()) {
			predicate = criteriaBuilder.equal(lookupItem.get("lookupItemCode"), finder.getLookupItemCode());
			predicates.add(predicate);
		}
		
		if(finder.getLookup() != null){
			predicate = criteriaBuilder.equal(lookupItem.get("lookup"), finder.getLookup());
			predicates.add(predicate);
		}
		
		if(finder.getCompany() != null){
			predicate = criteriaBuilder.equal(lookupItem.get("company"), finder.getCompany());
			predicates.add(predicate);
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		List<Order> orderList = new ArrayList<>();

		orderList.add(criteriaBuilder.asc(lookupItem.get("lookupItemName")));

		criteriaQuery.orderBy(orderList);
		
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean save(LookupItem lookupItem) {
		// TODO Auto-generated method stub
		try {
			lookupItemRepository.save(lookupItem);
		}catch (Exception ex) {
			// TODO: handle exception
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean delete(LookupItem lookupItem) {
		// TODO Auto-generated method stub
		try {
			lookupItemRepository.delete(lookupItem);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public DataTablesOutput<LookupItem> findForDataTable(DataTablesInput input) {
		// TODO Auto-generated method stub
		return lookupItemRepository.findAll(input);
	}
	
	

}
