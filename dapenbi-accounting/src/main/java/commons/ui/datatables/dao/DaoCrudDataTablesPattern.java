package commons.ui.datatables.dao;

import commons.data.dao.DaoCrudPattern;

import java.io.Serializable;

/**
 * @param <T>
 * @param <ID>
 */
public interface DaoCrudDataTablesPattern<T, ID extends Serializable> extends
        DaoCrudPattern<T, ID>,
        DaoDataTablesPattern<T> {
}
