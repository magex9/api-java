package ca.magex.crm.graphql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.authentication.CrmPasswordService;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPermissionService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.resource.CrmDataInitializer;

@Component
public class ContextListener implements ApplicationListener<ApplicationReadyEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(ContextListener.class);

	@Autowired private CrmOrganizationService crmOrganizationService;
	@Autowired private CrmLocationService crmLocationService;
	@Autowired private CrmPersonService crmPersonService;
	@Autowired private CrmUserService crmUserService;
	@Autowired private CrmLookupService crmLookupService;
	@Autowired private CrmPermissionService crmPermissionService;
	@Autowired private CrmPasswordService crmPasswordService;

	@Override
	public void onApplicationEvent(ApplicationReadyEvent event) {
		LOG.info("Initializing Crm Data");
		CrmDataInitializer.initialize(crmOrganizationService, crmLocationService, crmPersonService, crmUserService,
				crmPermissionService, crmPasswordService, crmLookupService);
	}	
}
