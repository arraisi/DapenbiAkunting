package id.co.dapenbi.accounting.service.impl.parameter;

import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.repository.parameter.TahunBukuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TahunBukuService {

    @Autowired
    private TahunBukuRepository tahunBukuRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public TahunBuku save(TahunBuku tahunBuku) {
        tahunBuku.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        tahunBuku = tahunBukuRepository.save(tahunBuku);

        return tahunBuku;
    }

    public Optional<TahunBuku> findById(String id) {
        return this.tahunBukuRepository.findById(id);
    }

    public Iterable<TahunBuku> getAll() {
        return this.tahunBukuRepository.findAll();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public TahunBuku update(TahunBuku tahunBuku) {
        tahunBuku.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        tahunBukuRepository.update(
                tahunBuku.getKodeTahunBuku(),
                tahunBuku.getNamaTahunBuku(),
                tahunBuku.getTahun(),
                tahunBuku.getStatusAktif(),
                tahunBuku.getUpdatedBy(),
                tahunBuku.getUpdatedDate()
        );

        return tahunBuku;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id) {
        this.tahunBukuRepository.deleteById(id);
    }

    public DataTablesOutput<TahunBuku> findForDataTable(DataTablesInput input) {
        return this.tahunBukuRepository.findAll(input);
    }

    public void resetAllStatusAktif(String id) {
        String query = "UPDATE\n" +
                "   ACC_THNBUKU \n" +
                "SET\n" +
                "   STATUS_AKTIF = '0'\n" +
                "WHERE KODE_THNBUKU <> :id";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", id);

        jdbcTemplate.update(query, map);
    }

    public Optional<TahunBuku> findByStatusAktif() {
        return this.tahunBukuRepository.findByStatusAktif();
    }

    public Optional<TahunBuku> findByTahun(Integer tahun) {
        log.info("{}", tahun);
        return tahunBukuRepository.dataByTahun(tahun.toString());
    }

    public List<TahunBuku> listTahunSort() {
        return tahunBukuRepository.listTahunSort();
    }

    public List<TahunBuku> listByStatusAktif() {
        return tahunBukuRepository.listByStatusAktif();
    }
}
