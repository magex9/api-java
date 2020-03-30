package ca.magex.data.graphql.queries;

import java.util.HashMap;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;

import ca.magex.crm.api.crm.Organization;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.services.OrganizationService;

@Component
public class OrganizationQuery implements GraphQLQueryResolver {
	
	private OrganizationService organizations;

	public OrganizationQuery(OrganizationService organizations) {
		this.organizations = organizations;
	}

	public Iterable<Organization> findAllOrganizations() {
		return organizations.findOrganizations(new OrganizationsFilter(new HashMap<String, Object>(), new Paging(Sort.by("displayname"))));
	}
	
    public long countOrganizations() {
		return organizations.countOrganizations(new OrganizationsFilter(new HashMap<String, Object>(), new Paging(Sort.by("displayname"))));
    }
    
}
