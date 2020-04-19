package ca.magex.crm.hazelcast.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.services.CrmPasswordService;
import ca.magex.crm.api.system.Identifier;

@Service
public class HazelcastPasswordService implements CrmPasswordService {

	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public void setPassword(Identifier personId, String password) {
		Map<Identifier, String> passwords = hzInstance.getMap("passwords");
		passwords.put(personId, password);
	}

	@Override
	public String getPassword(Identifier personId) {
		Map<Identifier, String> passwords = hzInstance.getMap("passwords");
		return passwords.get(personId);
	}
}