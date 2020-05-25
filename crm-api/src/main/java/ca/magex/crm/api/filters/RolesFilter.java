package ca.magex.crm.api.filters;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

import ca.magex.crm.api.exceptions.ApiException;
import ca.magex.crm.api.roles.Role;
import ca.magex.crm.api.services.Crm;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Status;

public class RolesFilter implements Serializable {
	
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
	
	private Identifier groupId;
	
	private String englishName;
	
	private String frenchName;
	
	private String code;
	
	private Status status;

	public RolesFilter() {
		this(null, null, null, null, null);
	}
	
	public RolesFilter(Identifier groupId, String englishName, String frenchName, String code, Status status) {
		this.groupId = groupId;
		this.englishName = englishName;
		this.frenchName = frenchName;
		this.code = code;
		this.status = status;
	}
	
	public RolesFilter(Map<String, Object> filterCriteria) {
		this.groupId = filterCriteria.containsKey("groupId") ? new Identifier((String) filterCriteria.get("groupId")) : null;
		this.englishName = (String) filterCriteria.get("englishName");
		this.frenchName = (String) filterCriteria.get("frenchName");
		this.code = (String) filterCriteria.get("code");
		this.status = null;
		if (filterCriteria.containsKey("status") && StringUtils.isNotBlank((String) filterCriteria.get("status"))) {
			try {
				this.status = Status.valueOf(StringUtils.upperCase((String) filterCriteria.get("status")));
			} catch (IllegalArgumentException e) {
				throw new ApiException("Invalid status value '" + filterCriteria.get("status") + "' expected one of {" + StringUtils.join(Status.values(), ",") + "}");
			}
		}
	}
	
	public Identifier getGroupId() {
		return groupId;
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
	
	public RolesFilter withGroupId(Identifier groupId) {
		return new RolesFilter(groupId, englishName, frenchName, code, status);
	}

	public RolesFilter withEnglishName(String englishName) {
		return new RolesFilter(groupId, englishName, frenchName, code, status);
	}
	
	public RolesFilter withFrenchName(String frenchName) {
		return new RolesFilter(groupId, englishName, frenchName, code, status);
	}
		
	public RolesFilter withCode(String code) {
		return new RolesFilter(groupId, englishName, frenchName, code, status);
	}
	
	public RolesFilter withStatus(Status status) {
		return new RolesFilter(groupId, englishName, frenchName, code, status);
	}

	public static List<Sort> getSortOptions() {
		return SORT_OPTIONS;
	}
	
	public static Sort getDefaultSort() {
		return Sort.by(Direction.ASC, "code");
	}

	public static Paging getDefaultPaging() {
		return new Paging(getDefaultSort());
	}

	public Comparator<Role> getComparator(Paging paging) {
		return paging.new PagingComparator<Role>();
	}
}