package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Lang;
import ca.magex.crm.api.system.Localized;

public class Paging implements Pageable, Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;

	private long offset;

	private int pageSize;

	private int pageNumber;

	private Sort sort;

	public static Paging singleInstance() {
		return new Paging(1, 1, Sort.unsorted());
	}

	public Paging(int pageNumber, int pageSize, Sort sort) {
		super();
		this.pageNumber = (pageNumber < 1 ? 1 : pageNumber); // if we ask for a page before 1, return the first page
		this.pageSize = pageSize;
		this.sort = sort;
		/* calculate the offset */
		this.offset = this.pageSize * (this.pageNumber - 1);
	}

//	public Paging(long offset, int pageSize, Sort sort) {
//		super();
//		this.offset = offset;
//		this.pageSize = pageSize;
//		this.pageNumber = (int) Math.floor(offset / pageSize);
//		this.sort = sort;
//	}

	public Paging(Sort sort) {
		this(1, 10, sort);
	}

	@Override
	public int getPageNumber() {
		return pageNumber;
	}
	
	public Paging withPageNumber(int pageNumber) {
		return new Paging(pageNumber, pageSize, sort);
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	public Paging withPageSize(int pageSize) {
		return new Paging(pageNumber, pageSize, sort);
	}
	
	public Paging allItems() {
		return new Paging(1, Integer.MAX_VALUE, sort);
	}

	@Override
	public long getOffset() {
		return offset;
	}

//	public Paging withOffset(long offset) {
//		return new Paging(offset, pageSize, sort);
//	}

	@Override
	public Sort getSort() {
		return sort;
	}
	
	public Paging withSort(Sort sort) {
		return new Paging(pageNumber, pageSize, sort);
	}

	@Override
	public boolean hasPrevious() {
		return getPageNumber() > 1;
	}

	@Override
	public Paging next() {
		return new Paging(getPageNumber() + 1, getPageSize(), getSort());
	}

	@Override
	public Paging previousOrFirst() {
		return new Paging(getPageNumber() - 1, getPageSize(), getSort());
	}

	@Override
	public Paging first() {
		return new Paging(1, getPageSize(), getSort());
	}

	/**
	 * Creates a new Comparator based on this Paging Instance
	 * 
	 * @author Jonny
	 *
	 * @param <T>
	 */
	public class PagingComparator<T> implements Comparator<T> {

		@SuppressWarnings("unchecked")
		@Override
		public int compare(T o1, T o2) {
			PropertyUtilsBean pub = new PropertyUtilsBean();
			Iterator<Order> iterator = Paging.this.getSort().iterator();
			while (iterator.hasNext()) {
				Order order = iterator.next();
				String propertyName = order.getProperty();
				if ("englishName".contentEquals(propertyName)) {
					propertyName = "name:" + Lang.ENGLISH;
				}
				else if ("frenchName".contentEquals(propertyName)) {
					propertyName = "name:" + Lang.FRENCH;
				}
				else if ("code".contentEquals(propertyName)) {					
					propertyName = "name:" + Lang.ROOT;
				}
				String propertyKey = null;
				if (propertyName.indexOf(":") > -1) {
					String[] vals = propertyName.split(":");
					propertyName = vals[0];
					propertyKey = (vals.length == 1 ? "" : vals[1]); // ROOT has no property key
				}					
				try {					
					Object val1 = propertyName.equals("") ? o1 : pub.getProperty(o1, propertyName);
					Object val2 = propertyName.equals("") ? o2 : pub.getProperty(o2, propertyName);
					/* localized use the key */
					if (Localized.class.isAssignableFrom(val1.getClass())) {
						if (propertyKey == null) {
							throw new ApiException("Cannot sort by Localized without specifying Locale");
						}
						Localized lVal1 = (Localized) val1;
						Localized lVal2 = (Localized) val2;
						Locale locale = Lang.parse(propertyKey);
						Collator collator = Collator.getInstance();
						collator.setStrength(Collator.NO_DECOMPOSITION);
						int compare = collator.compare(lVal1.get(locale), lVal2.get(locale));
						if (compare == 0) {
							compare = StringUtils.compare(lVal1.get(locale), lVal2.get(locale));
						}
						if (compare != 0) {
							return order.isAscending() ? compare : -compare;
						}
					} else if (String.class.isAssignableFrom(val1.getClass())) {
						Collator collator = Collator.getInstance();
						collator.setStrength(Collator.NO_DECOMPOSITION);
						int compare = collator.compare((String) val1, (String) val2);
						if (compare == 0) {
							compare = StringUtils.compare((String) val1, (String) val2);
						}
						if (compare != 0) {
							return order.isAscending() ? compare : -compare;
						}
					}
					else if (Comparable.class.isAssignableFrom(val1.getClass())) {
						Comparable<Object> cVal1 = (Comparable<Object>) val1;
						Comparable<Object> cVal2 = (Comparable<Object>) val2;
						int compare = cVal1.compareTo(cVal2);
						if (compare != 0) {
							return order.isAscending() ? compare : -compare;
						}
					}
				}
				catch(ReflectiveOperationException e) {
					LoggerFactory.getLogger(getClass()).warn("Unable to sort by property '" + propertyName + "', " + e.getMessage());					
				}				
			}
			/* everything matched */
			return 0;
		}
	}
}