package ca.magex.crm.api.filters;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Paging implements Pageable {
	
	private long offset;
	
	private int pageSize;
	
	private int pageNumber;
	
	private Sort sort;
	
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
		this.pageNumber = (int)Math.floor(offset / pageSize);
		this.sort = sort;
	}

	public Paging(Sort sort) {
		this(0, 10, sort);
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
	
}
