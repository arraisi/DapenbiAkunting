package commons.ui.datatables.dao;


import commons.ui.datatables.DataTablesRequest;

import java.util.List;

/**
 * @param <T> target model
 */
public interface DaoDataTablesPattern<T> {

    /**
     * @param params
     * @return
     */
    List<T> datatables(DataTablesRequest<T> params);

    /**
     * @param params
     * @return
     */
    Long datatables(T params);
}
