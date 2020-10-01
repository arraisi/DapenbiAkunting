package commons.ui.datatables.service;

import commons.ui.datatables.DataTablesRequest;
import commons.ui.datatables.DataTablesResponse;

/**
 * @param <T> Model class
 */
public interface ServiceDataTablesPattern<T> {

    /**
     * @param params
     * @return
     */
    DataTablesResponse<T> datatables(DataTablesRequest<T> params);
}
