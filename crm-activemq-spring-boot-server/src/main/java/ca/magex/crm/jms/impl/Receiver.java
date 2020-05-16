package ca.magex.crm.jms.impl;

import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import ca.magex.crm.jms.model.CrmDataMutation;

@Component
public class Receiver {
	
	@JmsListener(destination="crmDataMutationInbox", containerFactory = "myFactory")
	public void receiveMessage(CrmDataMutation mutation) {
		LoggerFactory.getLogger(getClass()).info("Received Mutation: " + mutation);
	}
}