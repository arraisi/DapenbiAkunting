package id.co.dapenbi.accounting.service.impl;

import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.repository.NumberGeneratorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
public class NumberGeneratorService {

    @Autowired
    private NumberGeneratorRepository repository;

    public Iterable<NumberGenerator> getAll() {
        return repository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public NumberGenerator save(NumberGenerator value) {
        return repository.save(value);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(Integer id) {
        this.repository.deleteById(id);
    }

    public Iterable<NumberGenerator> findAll() {
        return repository.findAll();
    }

    @Transactional
    public NumberGenerator findByName(String name) {
        Optional<NumberGenerator> numberGenerator = this.repository.findByName(name);
        incrementByName(name);
        return numberGenerator.get();
    }

    public int incrementByName(String name) {
        return this.repository.incrementByName(name);
    }
}
