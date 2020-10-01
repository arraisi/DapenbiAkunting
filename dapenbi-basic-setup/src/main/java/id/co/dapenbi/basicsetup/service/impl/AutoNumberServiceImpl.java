package id.co.dapenbi.basicsetup.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import id.co.dapenbi.basicsetup.constant.IncrementResetMode;
import id.co.dapenbi.basicsetup.constant.NumberingType;
import id.co.dapenbi.basicsetup.finder.AutoNumberFinder;
import id.co.dapenbi.basicsetup.finder.AutoNumberLogFinder;
import id.co.dapenbi.basicsetup.model.AutoNumber;
import id.co.dapenbi.basicsetup.model.AutoNumberLog;
import id.co.dapenbi.basicsetup.repository.AutoNumberRepository;
import id.co.dapenbi.basicsetup.service.AutoNumberLogService;
import id.co.dapenbi.basicsetup.service.AutoNumberService;
import id.co.dapenbi.core.constant.OrderByMode;

@Service
@Repository
public class AutoNumberServiceImpl implements AutoNumberService {

	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	AutoNumberRepository autoNumberRepository;

	@Autowired
	AutoNumberLogService autoNumberLogService;

	private static final Logger logger = LoggerFactory.getLogger(AutoNumberServiceImpl.class);

	@Override
	public List<AutoNumber> findAll() {
		// TODO Auto-generated method stub
		return (List<AutoNumber>) autoNumberRepository.findAll();
	}

	@Override
	public List<AutoNumber> findByCriteria(AutoNumberFinder finder) {
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

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean save(AutoNumber autoNumber) {
		// TODO Auto-generated method stub
		try {
			autoNumberRepository.save(autoNumber);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean delete(AutoNumber autoNumber) {
		// TODO Auto-generated method stub
		try {
			autoNumberRepository.delete(autoNumber);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public DataTablesOutput<AutoNumber> findForDataTable(DataTablesInput input) {
		// TODO Auto-generated method stub
		return autoNumberRepository.findAll(input);
	}

	@Override
	public Map<String, Object> generateAutoNumber(String transactionCode, String companyCode) {
		// TODO Auto-generated method stub
		Map<String, Object> result = new HashMap<>();
		Date currentDate = new Date();
		AutoNumberFinder finder = new AutoNumberFinder();
		finder.setTransactionCode(transactionCode);

		List<AutoNumber> autoNumberList = findByCriteria(finder);

		if (autoNumberList.size() > 0) {
			AutoNumber autoNumber = autoNumberList.get(0);

			AutoNumberLogFinder autoNumberLogFinder = new AutoNumberLogFinder();
			autoNumberLogFinder.setAutoNumber(autoNumber);
			autoNumberLogFinder.setDateLog(currentDate);
			autoNumberLogFinder.setOrderByMode(OrderByMode.DESC.getValue());

			List<AutoNumberLog> autoNumberLogList = autoNumberLogService.findByCriteria(autoNumberLogFinder);

			if (autoNumber.getIncrementResetMode().equals(IncrementResetMode.YEARLY.getValue())) {
				autoNumberLogFinder.setDateLog(null);
				autoNumberLogList = autoNumberLogService.findByCriteria(autoNumberLogFinder);
			}

			Long defaultIncrementValue = 0L;
			Long incrementValue = 0L;
			String generatedNumber = "";
			String increment = "";

			String dateFormat = (autoNumber.getDateFormat() != null && !autoNumber.getDateFormat().isEmpty())
					? autoNumber.getDateFormat() : "";
					
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
			String dateSegment = simpleDateFormat.format(new Date());

			String prefix = autoNumber.getPrefix();
			String separator = autoNumber.getSeparator();

			if (autoNumber.getNumberingType().equals(NumberingType.COMBINATION.getValue())) {
				if (autoNumberLogList.size() > 0) {
					AutoNumberLog autoNumberLog = autoNumberLogList.get(0);

					char[] incArray = autoNumberLog.getLastIncrement().toString().toCharArray();

					for (int i = 0; i < autoNumber.getIncrementDigit() - incArray.length; i++) {
						increment += "0";
					}

					incrementValue = autoNumberLog.getLastIncrement() + 1;

					increment += incrementValue.toString();
					result.put("incrementValue", incrementValue.toString());
				} else {
					for (int i = 0; i < autoNumber.getIncrementDigit() - 1; i++) {
						increment += "0";
					}
					incrementValue = defaultIncrementValue + 1;
					increment += incrementValue.toString();
					result.put("incrementValue", incrementValue.toString());

					AutoNumberLog autoNumberLogNew = new AutoNumberLog();
					autoNumberLogNew.setAutoNumber(autoNumber);
					autoNumberLogNew.setDateLog(new Date());
					autoNumberLogNew.setLastIncrement(Long.parseLong(increment));

					autoNumberLogService.save(autoNumberLogNew);
				}

				if (autoNumber.getUseCompanyCode() == true) {
					prefix += companyCode;
				}

				if (dateSegment != "") {
					generatedNumber = prefix + separator + dateSegment + separator + increment;
				} else {
					generatedNumber = prefix + separator + increment;
				}

				result.put("generatedNumber", generatedNumber);
			} else {
				autoNumberLogFinder.setDateLog(null);
				autoNumberLogList = autoNumberLogService.findByCriteria(autoNumberLogFinder);

				if (autoNumberLogList.size() > 0) {
					AutoNumberLog autoNumberLog = autoNumberLogList.get(0);

					char[] incArray = autoNumberLog.getLastIncrement().toString().toCharArray();

					for (int i = 0; i < autoNumber.getIncrementDigit() - incArray.length; i++) {
						increment += "0";
					}

					incrementValue = autoNumberLog.getLastIncrement() + 1;
					increment += incrementValue.toString();
					result.put("incrementValue", incrementValue);
				} else {
					for (int i = 0; i < autoNumber.getIncrementDigit(); i++) {
						increment += "0";
					}

					incrementValue = defaultIncrementValue + 1;
					increment += incrementValue.toString();
					result.put("incrementValue", incrementValue);

					AutoNumberLog autoNumberLogNew = new AutoNumberLog();
					autoNumberLogNew.setAutoNumber(autoNumber);
					autoNumberLogNew.setDateLog(new Date());
					autoNumberLogNew.setLastIncrement(Long.parseLong(increment));
				}

				generatedNumber = increment;
				result.put("generatedNumber", generatedNumber);
			}

		}

		return result;
	}

}
