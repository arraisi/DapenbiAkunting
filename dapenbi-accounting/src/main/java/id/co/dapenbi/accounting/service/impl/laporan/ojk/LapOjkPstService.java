package id.co.dapenbi.accounting.service.impl.laporan.ojk;

import id.co.dapenbi.accounting.dto.laporan.LaporanOjkDTO;
import id.co.dapenbi.accounting.dto.laporan.TableColumn;
import id.co.dapenbi.accounting.dto.laporan.TableLapOjk;
import id.co.dapenbi.accounting.entity.laporan.ojk.OjkPst;
import id.co.dapenbi.accounting.entity.parameter.TahunBuku;
import id.co.dapenbi.accounting.mapper.LaporanOjkMapper;
import id.co.dapenbi.accounting.repository.laporan.ojk.OjkPstRepository;
import id.co.dapenbi.accounting.service.impl.parameter.TahunBukuService;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class LapOjkPstService {

    @Autowired
    private OjkPstRepository repository;

    @Autowired
    private LaporanOjkMapper mapper;

    @Autowired
    private TahunBukuService tahunBukuService;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public TableLapOjk<OjkPst> getDataTable(String kodePeriode, String kodeTahunBuku) {
        DataTablesInput input = new DataTablesInput();
        input.addOrder("idLaporan", true);
        input.setLength(-1);
        Specification<OjkPst> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPst> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        DataTablesOutput<OjkPst> output = repository.findAll(input, Specification.where(byKodePeriode).and(byKodeTahunBuku));
        Iterable<OjkPst> ojkPstIterable = output.getData();
        return new TableLapOjk<>(
                IterableUtils.toList(ojkPstIterable),
                this.getColumns()
        );
    }

    private List<TableColumn> getColumns() {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("id", "idLaporan", false));
        columns.add(new TableColumn("Uraian", "uraian"));
        columns.add(new TableColumn("Program Pensiun", "programPensiun"));
        columns.add(new TableColumn("Manfaat Lain", "manfaatLain"));
        columns.add(new TableColumn("Gabungan", "gabungan"));
        return columns;
    }

    public List<OjkPst> findAll(String kodePeriode, String kodeTahunBuku) {
        Specification<OjkPst> byKodePeriode = this.byKodePeriode(kodePeriode);
        Specification<OjkPst> byKodeTahunBuku = this.byKodeTahunBuku(kodeTahunBuku);
        return repository.findAll(Specification.where(byKodePeriode).and(byKodeTahunBuku));
    }

    private Specification<OjkPst> byKodePeriode(String kodePeriode) {
        return (book, cq, cb) -> cb.equal(book.get("kodePeriode"), kodePeriode);
    }

    private Specification<OjkPst> byKodeTahunBuku(String kodeTahunBuku) {
        return (book, cq, cb) -> cb.equal(book.get("kodeTahunBuku"), kodeTahunBuku);
    }

    public int proses(List<LaporanOjkDTO.Manual> list, String kodeTahunBuku, String kodePeriode, Principal principal) throws IncorrectResultSizeDataAccessException {
        Optional<TahunBuku> tahunBukuOptional = tahunBukuService.findByTahun(Integer.valueOf(kodeTahunBuku));
        Stream<LaporanOjkDTO.Manual> laporanOjkDTOStream =
                list.stream().map(laporanOjkInputManual -> {
                    laporanOjkInputManual.setKodeTahunBuku(tahunBukuOptional.get().getKodeTahunBuku());
                    laporanOjkInputManual.setKodePeriode(kodePeriode);
                    return laporanOjkInputManual;
                });

        int count = 0;
        if (list.size() > 0) {
            List<LaporanOjkDTO.Manual> laporanOjks = laporanOjkDTOStream.collect(Collectors.toList());
            for (LaporanOjkDTO.Manual inputManual : laporanOjks) {
                OjkPst ojkPst = mapper.dtoToEntity(inputManual);
                String query = "MERGE INTO OJK_PST \n" +
                        "USING DUAL\n" +
                        "ON (KODE_PERIODE = :kodePeriode AND KODE_THNBUKU = :kodeThnbuku AND URUTAN = :urutan)\n" +
                        "WHEN MATCHED THEN\n" +
                        "    UPDATE\n" +
                        "    SET URAIAN         = :uraian,\n" +
                        "        UPDATED_BY     = :createdBy,\n" +
                        "        UPDATED_DATE   = :createdDate\n" +
                        "WHEN NOT MATCHED THEN\n" +
                        "    INSERT (ID_LAPORAN, URAIAN, KODE_THNBUKU, URUTAN, KODE_PERIODE, CREATED_BY, CREATED_DATE)\n" +
                        "    VALUES (:idLaporan, :uraian, :kodeThnbuku, :urutan, :kodePeriode, :createdBy, :createdDate)\n";
                MapSqlParameterSource params = new MapSqlParameterSource();
                params.addValue("idLaporan", ojkPst.getIdLaporan());
                params.addValue("uraian", ojkPst.getUraian());
                params.addValue("urutan", ojkPst.getUrutan());
                params.addValue("kodeThnbuku", ojkPst.getKodeTahunBuku());
                params.addValue("kodePeriode", ojkPst.getKodePeriode());
                params.addValue("createdBy", principal.getName());
                params.addValue("createdDate", Timestamp.valueOf(LocalDateTime.now()));
                count+= jdbcTemplate.update(query, params);
            }
        }
        return count;
    }
}
