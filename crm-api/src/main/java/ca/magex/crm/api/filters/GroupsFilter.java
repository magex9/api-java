package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.roles.Group;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Status;

public class GroupsFilter implements Serializable {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final List<Sort> SORT_OPTIONS = List.of(
		Sort.by(Order.asc("englishName")),
		Sort.by(Order.desc("englishName")),
		Sort.by(Order.asc("frenchName")),
		Sort.by(Order.desc("frenchName")),
		Sort.by(Order.asc("code")),
		Sort.by(Order.desc("code")),
		Sort.by(Order.asc("status")),
		Sort.by(Order.desc("status"))
	);

	private String englishName;
	
	private String frenchName;
	
	private String code;
	
	private Status status;

	public GroupsFilter(String englishName, String frenchName, String code, Status status) {
		this.englishName = englishName;
		this.frenchName = frenchName;
		this.code = code;
		this.status = status;
	}

	public GroupsFilter() {
		this(null, null, null, null);
	}
	
	public String getEnglishName() {
		return englishName;
	}
	
	public String getFrenchName() {
		return frenchName;
	}
	
	public String getCode() {
		return code;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public GroupsFilter withEnglishName(String englishName) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public GroupsFilter withFrenchName(String frenchName) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public GroupsFilter withCode(String code) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public GroupsFilter withStatus(Status status) {
		return new GroupsFilter(englishName, frenchName, code, status);
	}
	
	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "name");
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	public Comparator<Group> getComparator(Paging paging) {
		return paging.new PagingComparator<Group>();
	}
}