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

import id.co.dapenbi.basicsetup.finder.ParameterFinder;
import id.co.dapenbi.basicsetup.model.Parameter;
import id.co.dapenbi.basicsetup.repository.ParameterRepository;
import id.co.dapenbi.basicsetup.service.ParameterService;

@Service
@Repository
public class ParameterServiceImpl implements ParameterService {
	@PersistenceContext
	EntityManager entityManager;

	@Autowired
	ParameterRepository parameterRepository;

	private static final Logger logger = LoggerFactory.getLogger(LookupServiceImpl.class);

	@Override
	public List<Parameter> findAll() {
		// TODO Auto-generated method stub
		return (List<Parameter>) parameterRepository.findAll();
	}

	@Override
	public List<Parameter> findByCriteria(ParameterFinder finder) {
		// TODO Auto-generated method stub
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Parameter> criteriaQuery = criteriaBuilder.createQuery(Parameter.class);

		Root<Parameter> parameter = criteriaQuery.from(Parameter.class);
		List<Predicate> predicates = new ArrayList<Predicate>();
		Predicate predicate = null;

		if (finder.getParameterId() != null && !finder.getParameterId().equals(0)) {
			predicate = criteriaBuilder.equal(parameter.get("parameterId"),
					finder.getParameterId());
			predicates.add(predicate);
		}

		if (finder.getParameterCode() != null && !finder.getParameterCode().isEmpty()) {
			predicate = criteriaBuilder.equal(parameter.get("parameterCode"),
					finder.getParameterCode());
			predicates.add(predicate);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[] {}));

		return entityManager.createQuery(criteriaQuery).getResultList();
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean save(Parameter parameter) {
		// TODO Auto-generated method stub
		try {
			parameterRepository.save(parameter);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public boolean delete(Parameter parameter) {
		// TODO Auto-generated method stub
		try {
			parameterRepository.delete(parameter);
		} catch (Exception ex) {
			logger.error(ex.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public DataTablesOutput<Parameter> findForDataTable(DataTablesInput input) {
		// TODO Auto-generated method stub
		return parameterRepository.findAll(input);
	}

}
