package ca.magex.crm.amnesia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.services.CrmLocationService;
import ca.magex.crm.api.services.CrmLookupService;
import ca.magex.crm.api.services.CrmOrganizationService;
import ca.magex.crm.api.services.CrmPersonService;
import ca.magex.crm.api.services.CrmUserService;
import ca.magex.crm.resource.CrmDataInitializer;

@Component
@Profile(MagexCrmProfiles.CRM_CENTRALIZED)
public class AmnesiaDBContextListener implements ApplicationListener<ContextRefreshedEvent> {

	private static final Logger LOG = LoggerFactory.getLogger(AmnesiaDBContextListener.class);

	@Autowired private CrmOrganizationService crmOrganizationService;
	@Autowired private CrmLocationService crmLocationService;
	@Autowired private CrmPersonService crmPersonService;
	@Autowired private CrmUserService crmUserService;
	@Autowired private CrmLookupService crmlookupService;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		LOG.info("Initializing Crm Data");
		CrmDataInitializer.initialize(crmOrganizationService, crmLocationService, crmPersonService, crmUserService, crmlookupService);		
	}	
}
