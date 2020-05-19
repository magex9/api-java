package ca.magex.crm.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.broker.BrokerService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

import ca.magex.crm.jms.model.CrmDataMutation;

// https://memorynotfound.com/spring-boot-embedded-activemq-configuration-example/
// https://stackoverflow.com/questions/48504265/is-it-possible-to-connect-to-spring-boot-embedded-activemq-instance-from-another

@SpringBootApplication()
@EnableJms
public class CrmJmsApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(CrmJmsApplication.class);
		/* generate a file called application.pid, used to track the running process */
		app.addListeners(new ApplicationPidFileWriter());
		ApplicationContext appCtxt = app.run(args);

		JmsTemplate jmsTemplate = appCtxt.getBean(JmsTemplate.class);
		
		jmsTemplate.convertAndSend("crmDataMutationInbox", new CrmDataMutation("Organization", "ABC"));
	}

	@Bean
	public JmsListenerContainerFactory<?> myFactory(ConnectionFactory connectionFactory, DefaultJmsListenerContainerFactoryConfigurer configurer) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		/*
		 * This provides all boot's default to this factory, including the message
		 * converter
		 */
		configurer.configure(factory, connectionFactory);
		/* You could still override some of Boot's default if necessary */
		return factory;
	}

	@Bean
	public MessageConverter jacksonJmsMessageConverter() {
		/* Serialize message content to json using TextMessage */
		MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
		converter.setTargetType(MessageType.TEXT);
		converter.setTypeIdPropertyName("_type");
		return converter;
	}
	
	@Bean
    public BrokerService broker() throws Exception {
        BrokerService broker = new BrokerService();
        broker.addConnector("tcp://localhost:61616");
        broker.setBrokerName("CRM");
        return broker;
    }
}