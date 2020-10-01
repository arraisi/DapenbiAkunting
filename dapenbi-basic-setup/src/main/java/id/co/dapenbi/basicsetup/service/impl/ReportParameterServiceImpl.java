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

import id.co.dapenbi.basicsetup.finder.ReportParameterFinder;
import id.co.dapenbi.basicsetup.model.ReportParameter;
import id.co.dapenbi.basicsetup.repository.ReportParameterRepository;
import id.co.dapenbi.basicsetup.service.ReportParameterService;

@Service
@Repository
public class ReportParameterServiceImpl implements ReportParameterService{
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	ReportParameterRepository reportParameterRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ReportParameterServiceImpl.class);

	@Override
	public List<ReportParameter> findAll() {
		// TODO Auto-generated method stub
		return (List<ReportParameter>) reportParameterRepository.findAll();
	}

	@Override
	public List<ReportParameter> findByCriteria(ReportParameterFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<ReportParameter> criteriaQuery = criteriaBuilder.createQuery(ReportParameter.class);
		
		Root<ReportParameter> reportParameter = criteriaQuery.from(ReportParameter.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;
		
		if(finder.getReportParameterId() != null && !finder.getReportParameterId().equals(0)) {
			predicate = criteriaBuilder.equal(reportParameter.get("reportParameterId"), finder.getReportParameterId());
			predicates.add(predicate);
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		
		List<Order> orderList = new ArrayList<>();

		orderList.add(criteriaBuilder.asc(reportParameter.get("reportParameterName")));

		criteriaQuery.orderBy(orderList);

		return entityManager.createQuery(criteriaQuery).getResultList();
		
	}

	@Override
	public boolean save(ReportParameter reportParameter) {
		// TODO Auto-generated method stub
		try {
			reportParameterRepository.save(reportParameter);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean delete(ReportParameter reportParameter) {
		// TODO Auto-generated method stub
		try {
			reportParameterRepository.delete(reportParameter);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public DataTablesOutput<ReportParameter> findForDataTable(DataTablesInput input) {
		// TODO Auto-generated method stub
		return reportParameterRepository.findAll(input);
	}
	
	
}
