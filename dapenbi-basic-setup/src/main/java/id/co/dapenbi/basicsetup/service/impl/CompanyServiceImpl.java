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

import id.co.dapenbi.basicsetup.finder.CompanyFinder;
import id.co.dapenbi.basicsetup.model.Company;
import id.co.dapenbi.basicsetup.repository.CompanyRepository;
import id.co.dapenbi.basicsetup.service.CompanyService;

@Service
@Repository
public class CompanyServiceImpl implements CompanyService{
	@PersistenceContext
	EntityManager entityManager;
	
	@Autowired
	CompanyRepository companyRepository;
	
	private static final Logger logger = LoggerFactory.getLogger(CompanyServiceImpl.class);
	
	@Override
	public List<Company> findAll() {
		// TODO Auto-generated method stub
		return (List<Company>) companyRepository.findAll();
	}

	@Override
	public List<Company> findByCriteria(CompanyFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Company> criteriaQuery = criteriaBuilder.createQuery(Company.class);
		
		Root<Company> company = criteriaQuery.from(Company.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;
		
		if(finder.getCompanyId() != null && !finder.getCompanyId().equals(0)) {
			predicate = criteriaBuilder.equal(company.get("companyId"), finder.getCompanyId());
			predicates.add(predicate);
		}
		
		if(finder.getCompanyCode() != null && !finder.getCompanyCode().equals(0)) {
			predicate = criteriaBuilder.equal(company.get("companyCode"), finder.getCompanyCode());
			predicates.add(predicate);
		}
		
		criteriaQuery.where(predicates.toArray(new Predicate[] {}));
		
		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean save(Company company) {
		// TODO Auto-generated method stub
		try {
			companyRepository.save(company);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean delete(Company company) {
		// TODO Auto-generated method stub
		try {
			companyRepository.delete(company);
		} catch(Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public DataTablesOutput<Company> findForDataTable(DataTablesInput input) {
		// TODO Auto-generated method stub
		return companyRepository.findAll(input);
	}

}
