package ca.magex.crm.api.system;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.SerializationUtils;
import org.springframework.data.domain.PageImpl;

import ca.magex.crm.api.exceptions.DuplicateItemFoundException;
import ca.magex.crm.api.exceptions.ItemNotFoundException;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.Crm;

public class FilteredPage<T> extends PageImpl<T> {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private Serializable filter;
	
	private Paging paging;
	
	public FilteredPage(Serializable filter, Paging paging, List<T> content, long total) {
		super(content, paging, total);
		this.filter = SerializationUtils.clone(filter);
		this.paging = SerializationUtils.clone(paging);
	}
	
	public Serializable getFilter() {
		return filter;
	}
	
	public Paging getPaging() {
		return paging;
	}
	
	public T getSingleItem() {
		if (getTotalElements() < 1)
			throw new ItemNotFoundException("No items found: " + filter);
		if (getTotalElements() > 1)
			throw new DuplicateItemFoundException("Duplicate items found: " + filter);
		return getContent().get(0);
	}

}
