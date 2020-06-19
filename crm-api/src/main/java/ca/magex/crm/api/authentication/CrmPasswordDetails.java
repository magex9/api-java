package ca.magex.crm.api.authentication;

import java.util.Date;

public interface CrmPasswordDetails {

	boolean isTemporary();

	Date getExpiration();

	String getCipherText();

}