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

import id.co.dapenbi.basicsetup.finder.ReportFinder;
import id.co.dapenbi.basicsetup.model.Report;
import id.co.dapenbi.basicsetup.repository.ReportRepository;
import id.co.dapenbi.basicsetup.service.ReportService;

@Service
@Repository
public class ReportServiceImpl implements ReportService{
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	ReportRepository reportRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(ReportServiceImpl.class);

	@Override
	public List<Report> findAll() {
		// TODO Auto-generated method stub
		return (List<Report>) reportRepository.findAll();
	}

	@Override
	public List<Report> findByCriteria(ReportFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Report> criteriaQuery = criteriaBuilder.createQuery(Report.class);
		
		Root<Report> report = criteriaQuery.from(Report.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;
		
		if(finder.getReportId() != null && !finder.getReportId().equals(0)) {
			predicate = criteriaBuilder.equal(report.get("reportId"), finder.getReportId());
			predicates.add(predicate);
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		
		List<Order> orderList = new ArrayList<>();

		orderList.add(criteriaBuilder.asc(report.get("reportName")));

		criteriaQuery.orderBy(orderList);

		return entityManager.createQuery(criteriaQuery).getResultList();
		
	}

	@Override
	public boolean save(Report report) {
		// TODO Auto-generated method stub
		try {
			reportRepository.save(report);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean delete(Report report) {
		// TODO Auto-generated method stub
		try {
			reportRepository.delete(report);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public DataTablesOutput<Report> findForDataTable(DataTablesInput input) {
		// TODO Auto-generated method stub
		return reportRepository.findAll(input);
	}
	
	
}
