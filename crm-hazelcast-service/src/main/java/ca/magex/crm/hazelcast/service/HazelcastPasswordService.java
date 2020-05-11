package ca.magex.crm.hazelcast.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.hazelcast.core.HazelcastInstance;

import ca.magex.crm.api.MagexCrmProfiles;
import ca.magex.crm.api.authentication.CrmPasswordService;

@Service
@Primary
@Profile(MagexCrmProfiles.CRM_DATASTORE_DECENTRALIZED)
public class HazelcastPasswordService implements CrmPasswordService {

	@Autowired private HazelcastInstance hzInstance;
	
	@Override
	public String getEncodedPassword(String username) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public boolean isExpiredPassword(String username) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean isTempPassword(String username) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean updatePassword(String username, String encodedPassword) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean verifyPassword(String username, String encodedPassword) {
		// TODO Auto-generated method stub
		return false;
	}
}