package id.co.dapenbi.accounting.dao.transaksi;

import commons.ui.datatables.DataTablesRequest;
import id.co.dapenbi.accounting.entity.transaksi.ValidasiSerap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ValidasiSerapDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;


}
