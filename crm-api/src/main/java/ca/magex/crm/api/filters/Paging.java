package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.util.ReflectionUtils;

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
		this.offset = pageSize * (pageNumber - 1);
		this.pageSize = pageSize;
		this.pageNumber = pageNumber;
		this.sort = sort;
	}

	public Paging(long offset, int pageSize, Sort sort) {
		super();
		this.offset = offset;
		this.pageSize = pageSize;
		this.pageNumber = (int) Math.floor(offset / pageSize);
		this.sort = sort;
	}

	public Paging(Sort sort) {
		this(1, 10, sort);
	}

	@Override
	public int getPageNumber() {
		return pageNumber;
	}

	@Override
	public int getPageSize() {
		return pageSize;
	}

	@Override
	public long getOffset() {
		return offset;
	}

	@Override
	public Sort getSort() {
		return sort;
	}

	@Override
	public boolean hasPrevious() {
		return getPageNumber() > 1;
	}

	@Override
	public Pageable next() {
		return new Paging(getOffset() + getPageSize(), getPageSize(), getSort());
	}

	@Override
	public Pageable previousOrFirst() {
		return new Paging(getOffset() - getPageSize() < 0 ? 0 : getOffset() - getPageSize(), getPageSize(), getSort());
	}

	@Override
	public Pageable first() {
		return new Paging(getOffset() + getPageSize(), getPageSize(), getSort());
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
			Iterator<Order> iterator = Paging.this.getSort().iterator();
			while (iterator.hasNext()) {
				Order order = iterator.next();
				String propertyName = order.getProperty();
				String propertyKey = null;
				if (propertyName.indexOf(":") > 0) {
					String[] vals = propertyName.split(":");
					propertyName = vals[0];
					propertyKey = vals[1];
				}
				Field field = ReflectionUtils.findField(o1.getClass(), propertyName);
				if (field != null) {
					ReflectionUtils.makeAccessible(field);
					Object val1 = ReflectionUtils.getField(field, o1);
					Object val2 = ReflectionUtils.getField(field, o2);
					/* localized use the key */
					if (Localized.class.isAssignableFrom(val1.getClass())) {
						if (propertyKey == null) {
							throw new ApiException("Cannot sort by Localized without specifying Locale");
						}
						Localized lVal1 = (Localized) val1;
						Localized lVal2 = (Localized) val2;
						Locale locale = Lang.parse(propertyKey);
						int compare = StringUtils.compare(lVal1.get(locale), lVal2.get(locale));
						if (compare != 0) {
							return compare;
						}
					} else if (Comparable.class.isAssignableFrom(val1.getClass())) {
						Comparable<Object> cVal1 = (Comparable<Object>) val1;
						Comparable<Object> cVal2 = (Comparable<Object>) val2;
						int compare = cVal1.compareTo(cVal2);
						if (compare != 0) {
							return compare;
						}
					}
				}
			}
			/* everything matched */
			return 0;
		}
	}
}