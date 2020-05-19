package ca.magex.crm.amnesia.services;

import java.io.PrintStream;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import ca.magex.crm.amnesia.AmnesiaDB;
import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.common.PersonName;
import ca.magex.crm.api.roles.User;
import ca.magex.crm.api.services.CrmInitializationService;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_CENTRALIZED)
public class AmnesiaInitializationService implements CrmInitializationService {

	private AmnesiaDB db;

	public AmnesiaInitializationService(AmnesiaDB db) {
		this.db = db;
	}

	@Override
	public boolean isInitialized() {
		return db.isInitialized();
	}

	@Override
	public User initializeSystem(String organization, PersonName name, String email, String username, String password) {
		return db.findUser(db.initialize(organization, name, email, username, password));
	}
	
	@Override
	public boolean reset() {
		db.reset();
		return true;
	}
	
	@Override
	public void dump(PrintStream os) {
		db.dump(os);
	}

}