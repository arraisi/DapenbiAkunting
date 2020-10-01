package id.co.dapenbi.accounting.service.impl.transaksi;

import id.co.dapenbi.accounting.entity.transaksi.ValidasiSerap;
import id.co.dapenbi.accounting.repository.transaksi.ValidasiSerapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ValidasiSerapService {

    @Autowired
    private ValidasiSerapRepository validasiSerapRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public ValidasiSerap save(ValidasiSerap validasiSerap) {
        Long lastId = idChecker().isPresent() ? idChecker().get().getIdSerapLog() : 0;
        validasiSerap.setIdSerapLog(lastId + 1);
        validasiSerap.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
        return validasiSerapRepository.save(validasiSerap);
    }

    public Optional<ValidasiSerap> idChecker() {
        String query = "SELECT \n" +
                "   *\n" +
                "FROM (\n" +
                "   SELECT \n" +
                "       ROW_NUMBER() over (order by asl.ID_SERAP_LOG DESC) as NO,\n" +
                "       asl.ID_SERAP_LOG    as idSerapLog\n" +
                "   FROM ACC_SERAP_LOG asl \n" +
                ")WHERE no BETWEEN 1 AND 1\n";

        try {
            return this.namedParameterJdbcTemplate.queryForObject(query, new HashMap<>(), new RowMapper<Optional<ValidasiSerap>>() {
                @Override
                public Optional<ValidasiSerap> mapRow(ResultSet resultSet, int i) throws SQLException {
                    ValidasiSerap value = new ValidasiSerap();
                    value.setIdSerapLog(resultSet.getLong("idSerapLog"));
                    return Optional.of(value);
                }
            });
        } catch (EmptyResultDataAccessException erdae) {
            return Optional.empty();
        }
    }
}
