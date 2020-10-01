package id.co.dapenbi.accounting.service.impl.anggaran;

import id.co.dapenbi.accounting.entity.NumberGenerator;
import id.co.dapenbi.accounting.entity.anggaran.AnggaranSatker;
import id.co.dapenbi.accounting.entity.master.LookupMaster;
import id.co.dapenbi.accounting.entity.parameter.PengaturanSistem;
import id.co.dapenbi.accounting.entity.parameter.Rekening;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.repository.NumberGeneratorRepository;
import id.co.dapenbi.accounting.repository.anggaran.AnggaranSatkerRepository;
import id.co.dapenbi.accounting.repository.parameter.PengaturanSistemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Optional;

@Slf4j
@Service
public class AnggaranSatkerService {

    @Autowired
    private AnggaranSatkerRepository repository;

    @Autowired
    private PengaturanSistemRepository pengaturanSistemRepository;

    @Autowired
    private NumberGeneratorRepository numberGeneratorRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AnggaranSatker save(AnggaranSatker anggaran) {
        anggaran.setNoAnggaran(generateNoAT());
        anggaran = repository.save(anggaran);
        numberGeneratorRepository.incrementByName("ACC_ANGGARAN_NO_AT");
        return anggaran;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public AnggaranSatker update(AnggaranSatker newAnggaran) {
        Optional<AnggaranSatker> oldAnggaran = repository.findById(newAnggaran.getNoAnggaran());
        if (oldAnggaran.isPresent()) {
            newAnggaran.setKeterangan(oldAnggaran.get().getKeterangan());
            newAnggaran.setTerbilang(oldAnggaran.get().getTerbilang());
            newAnggaran.setTglValidasi(oldAnggaran.get().getTglValidasi());
            newAnggaran.setUserValidasi(oldAnggaran.get().getUserValidasi());
            newAnggaran.setCatatanValidasi(oldAnggaran.get().getCatatanValidasi());
            newAnggaran.setCreatedBy(oldAnggaran.get().getCreatedBy());
            newAnggaran.setCreatedDate(oldAnggaran.get().getCreatedDate());
        }
        newAnggaran = repository.save(newAnggaran);
        return newAnggaran;
    }

    @Transactional
    public String generateNoAT() {
        Optional<PengaturanSistem> pengaturanSistem = pengaturanSistemRepository.findByStatusAktif();
        Optional<NumberGenerator> numberGenerator = numberGeneratorRepository.findByName("ACC_ANGGARAN_NO_AT");

        String kodeTahunBuku = pengaturanSistem.get().getKodeTahunBuku();
        String kodePeriode = pengaturanSistem.get().getKodePeriode();

        StringBuilder noAT = new StringBuilder();
        BigInteger counter = numberGenerator.get().getGenerateNumber().add(new BigInteger("1"));
        Integer prefixTotal = 6 - String.valueOf(counter).length();
        String prefixValue = "";
        while (prefixTotal > 0) {
            prefixValue = prefixValue + "0";
            prefixTotal--;
        }
        return noAT.append(kodeTahunBuku).append(kodePeriode).append(prefixValue).append(counter).toString();
    }

    public DataTablesOutput<AnggaranSatker> datatables(DataTablesInput input, String idSatker) {
        Specification<AnggaranSatker> specification = (anggaran, cq, cb) -> cb.equal(anggaran.get("idSatker"), new LookupMaster(idSatker));
        return repository.findAll(input, specification);
    }

    public DataTablesOutput<AnggaranSatker> datatables(DataTablesInput input) {
        return repository.findAll(input);
    }

    public Optional<AnggaranSatker> findByRekeningAndSatker(Integer rekening, String satker) {
        return repository.findByIdRekeningAndIdSatker(rekening, satker);
    }

    public Iterable<AnggaranSatker> findAnggaranByIdRekening(Integer idRekening, String kodeTahunBuku) {
        return repository.findByIdRekeningAndTahunBuku(new Rekening(idRekening), new TahunBuku(kodeTahunBuku));
    }
}
