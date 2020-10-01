package id.co.dapenbi.basicsetup.service.impl;

import java.util.ArrayList;
import java.util.Date;
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

import id.co.dapenbi.basicsetup.finder.AutoNumberFinder;
import id.co.dapenbi.basicsetup.finder.AutoNumberLogFinder;
import id.co.dapenbi.basicsetup.model.AutoNumber;
import id.co.dapenbi.basicsetup.model.AutoNumberLog;
import id.co.dapenbi.basicsetup.repository.AutoNumberLogRepository;
import id.co.dapenbi.basicsetup.repository.AutoNumberRepository;
import id.co.dapenbi.basicsetup.service.AutoNumberLogService;
import id.co.dapenbi.basicsetup.service.AutoNumberService;
import id.co.dapenbi.core.constant.OrderByMode;

@Service
@Repository
public class AutoNumberLogServiceImpl implements AutoNumberLogService {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	AutoNumberLogRepository autoNumberLogRepository;
	
	@Autowired
	AutoNumberRepository autoNumberRepository;

	private static final Logger logger = LoggerFactory.getLogger(AutoNumberLogServiceImpl.class);

	@Override
	public List<AutoNumberLog> findAll() {
		// TODO Auto-generated method stub
		return (List<AutoNumberLog>) autoNumberLogRepository.findAll();
	}

	@Override
	public List<AutoNumberLog> findByCriteria(AutoNumberLogFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AutoNumberLog> criteriaQuery = criteriaBuilder.createQuery(AutoNumberLog.class);

		Root<AutoNumberLog> autoNumberLog = criteriaQuery.from(AutoNumberLog.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;

		if (finder.getAutoNumberLogId() != null && !finder.getAutoNumberLogId().equals(0)) {
			predicate = criteriaBuilder.equal(autoNumberLog.get("autoNumberLogId"), finder.getAutoNumberLogId());
			predicates.add(predicate);
		}

		if (finder.getAutoNumber() != null) {
			predicate = criteriaBuilder.equal(autoNumberLog.get("autoNumber"), finder.getAutoNumber());
			predicates.add(predicate);
		}

		if (finder.getDateLog() != null) {
			predicate = criteriaBuilder.equal(autoNumberLog.get("dateLog"), finder.getDateLog());
			predicates.add(predicate);
		}

		List<Order> orderList = new ArrayList<>();

		if (finder.getOrderByMode() != null && !finder.getOrderByMode().isEmpty()) {
			if(finder.getOrderByMode().equals(OrderByMode.ASC.getValue())){
				orderList.add(criteriaBuilder.asc(autoNumberLog.get("autoNumberLogId")));
			} else {
				orderList.add(criteriaBuilder.desc(autoNumberLog.get("autoNumberLogId")));
			}
		}
		
		criteriaQuery.orderBy(orderList);

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean save(AutoNumberLog autoNumberLog) {
		// TODO Auto-generated method stub
		try {
			autoNumberLogRepository.save(autoNumberLog);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean delete(AutoNumberLog autoNumberLog) {
		// TODO Auto-generated method stub
		try {
			autoNumberLogRepository.delete(autoNumberLog);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public DataTablesOutput<AutoNumberLog> findForDataTable(DataTablesInput input) {
		// TODO Auto-generated method stub
		return autoNumberLogRepository.findAll(input);
	}

	@Override
	public boolean updateAutoNumberLogInterface(String transactionCode, Long lastIncrement) {
		// TODO Auto-generated method stub
		
		Date currentDate = new Date();
		AutoNumberFinder autoNumberFinder = new AutoNumberFinder();
		autoNumberFinder.setTransactionCode(transactionCode);
		
		List<AutoNumber> autoNumberList = findAutoNumber(autoNumberFinder);
		
		if(autoNumberList.size() > 0){
			AutoNumber autoNumber = autoNumberList.get(0);
			
			AutoNumberLogFinder autoNumberLogFinder = new AutoNumberLogFinder();
			autoNumberLogFinder.setAutoNumber(autoNumber);
			autoNumberLogFinder.setOrderByMode(OrderByMode.DESC.getValue());
			
			List<AutoNumberLog> autoNumberLogList = findByCriteria(autoNumberLogFinder);
			
			if(autoNumberLogList.size() > 0){
				AutoNumberLog autoNumberLog = autoNumberLogList.get(0);
				autoNumberLog.setLastIncrement(lastIncrement);
				
				this.save(autoNumberLog);
			} else {
				AutoNumberLog autoNumberLog = new AutoNumberLog();
				autoNumberLog.setAutoNumber(autoNumber);
				autoNumberLog.setDateLog(currentDate);
				autoNumberLog.setLastIncrement(lastIncrement);
				
				save(autoNumberLog);
			}
		} else {
			return false;
		}
		
		return true;
	}
	
	public List<AutoNumber> findAutoNumber(AutoNumberFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<AutoNumber> criteriaQuery = criteriaBuilder.createQuery(AutoNumber.class);

		Root<AutoNumber> autoNumber = criteriaQuery.from(AutoNumber.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;

		if (finder.getAutoNumberId() != null && !finder.getAutoNumberId().equals(0)) {
			predicate = criteriaBuilder.equal(autoNumber.get("autoNumberId"), finder.getAutoNumberId());
			predicates.add(predicate);
		}

		if (finder.getTransactionCode() != null && !finder.getTransactionCode().isEmpty()) {
			predicate = criteriaBuilder.equal(autoNumber.get("transactionCode"), finder.getTransactionCode());
			predicates.add(predicate);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

}