package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.Saldo;
import id.co.dapenbi.accounting.entity.transaksi.SaldoCurrent;
import id.co.dapenbi.accounting.repository.transaksi.SaldoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class SaldoService {

    @Autowired
    private SaldoRepository repository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Saldo save(Saldo saldo) {
        return repository.save(saldo);
    }

    public List<Saldo> findAllUpdatedToday() {
        return repository.findAllUpdatedToday(Timestamp.valueOf(LocalDateTime.now()));
    }

    public Optional<Saldo> findByIdRekeningAndLatestTglSaldo(Integer idRekening) {
        log.info("ID Rekening: {}", idRekening);
        String query = "SELECT \n" +
                "   as2.*\n" +
                "FROM ACC_SALDO as2 \n" +
                "JOIN (\n" +
                "   SELECT\n" +
                "       ID_REKENING,\n" +
                "       MAX(TGL_SALDO)  AS tglSaldo \n" +
                "   FROM ACC_SALDO\n" +
                "   GROUP BY ID_REKENING\n" +
                ") as1 ON as1.ID_REKENING = as2.ID_REKENING AND as1.tglSaldo = as2.TGL_SALDO\n" +
                "WHERE 1 = 1\n" +
                "AND as2.ID_REKENING = :idRekening\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("idRekening", idRekening);

        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, map, new RowMapper<Optional<Saldo>>() {
                @Override
                public Optional<Saldo> mapRow(ResultSet resultSet, int i) throws SQLException {
                    Saldo value = new Saldo();
                    value.setIdRekening(resultSet.getInt("ID_REKENING"));
                    value.setKodeRekening(resultSet.getString("KODE_REKENING"));
                    value.setNamaRekening(resultSet.getString("NAMA_REKENING"));
                    value.setSaldoAkhir(resultSet.getBigDecimal("SALDO_AKHIR"));
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            Saldo saldo = new Saldo();
            saldo.setSaldoAkhir(BigDecimal.ZERO);
            return Optional.of(saldo);
        }
    }

    public Integer countByKodeDRI(String kodeDRI) {
        return null;
    }
}
