package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.SerapDetail;
import id.co.dapenbi.accounting.repository.transaksi.SerapDetailRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SerapDetailService {

    @Autowired
    private SerapDetailRepository serapDetailRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public SerapDetail save(SerapDetail serapDetail) {
//        serapDetail.setIdSerapDetail(UUID.randomUUID().toString());
        Long lastId = idChecker().isPresent() ? idChecker().get().getIdSerapDetail() : 0;
        serapDetail.setIdSerapDetail(lastId + 1);
        serapDetail.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        serapDetail = serapDetailRepository.save(serapDetail);

        return serapDetail;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public SerapDetail update(SerapDetail serapDetail) {
        Long lastId = idChecker().isPresent() ? idChecker().get().getIdSerapDetail() : 0;
        if(serapDetail.getIdSerapDetail() == null) {
            serapDetail.setIdSerapDetail(lastId + 1);
        }
        serapDetail.setUpdatedDate(Timestamp.valueOf(LocalDateTime.now()));
        serapDetail = serapDetailRepository.save(serapDetail);

        return serapDetail;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void updateSaldoAnggaran(Integer id, BigDecimal saldoAnggaran) {
        String query = "UPDATE \n" +
                "(SELECT\n" +
                "\tasd.SALDO_ANGGARAN \n" +
                " FROM ACC_SERAP_DTL asd \n" +
                " INNER JOIN ACC_SERAP as2\n" +
                " ON as2.NO_SERAP = asd.NO_SERAP \n" +
                " WHERE asd.ID_REKENING = :id AND as2.STATUS_DATA <> 'APPROVE'\n" +
                ") t\n" +
                "SET t.SALDO_ANGGARAN = :saldoAnggaran";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("saldoAnggaran", saldoAnggaran);
        map.addValue("id", id);
        log.info("{}", id);

        this.namedParameterJdbcTemplate.update(query, map);
    }

    public Optional<SerapDetail> idChecker() {
        String query = "SELECT \n" +
                "   *\n" +
                "FROM (\n" +
                "   SELECT \n" +
                "       ROW_NUMBER() over (order by asd.ID_SERAP_DTL DESC) as NO,\n" +
                "       asd.ID_SERAP_DTL    as idSerapDetail\n" +
                "   FROM ACC_SERAP_DTL asd \n" +
                ")WHERE no BETWEEN 1 AND 1\n";
        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Optional<SerapDetail>>() {
                @Override
                public Optional<SerapDetail> mapRow(ResultSet resultSet, int i) throws SQLException {
                    SerapDetail serapDetail = new SerapDetail();
                    serapDetail.setIdSerapDetail(resultSet.getLong("idSerapDetail"));
                    return Optional.of(serapDetail);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void delete(String id) {
        serapDetailRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteByNoSerap(String idNoSerap) {
        String query = "Delete from ACC_SERAP_DTL where NO_SERAP = :id\n";

        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("id", idNoSerap);

        this.namedParameterJdbcTemplate.update(query, map);
    }
}
