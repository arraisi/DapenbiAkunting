package commons.ui.datatables.service;


import commons.data.service.ServiceCrudPattern;

import java.io.Serializable;

/**
 * @param <T>
 * @param <ID>
 */
public interface ServiceCrudDataTablesPattern<T, ID extends Serializable> extends
        ServiceCrudPattern<T, ID>,
        ServiceDataTablesPattern<T> {
}
