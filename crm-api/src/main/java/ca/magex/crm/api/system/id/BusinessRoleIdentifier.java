package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Business Role Option Identification
 * 
 * @author Jonny
 */
public class BusinessRoleIdentifier extends OptionIdentifier {

	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "business-roles/";
	
	public static final BusinessRoleIdentifier EXECS_CEO = new BusinessRoleIdentifier("EXECS/CEO");

	public static final BusinessRoleIdentifier EXECS_CIO = new BusinessRoleIdentifier("EXECS/CIO");

	public static final BusinessRoleIdentifier IMIT_DIRECTOR = new BusinessRoleIdentifier("IMIT/DIRECTOR");

	public static final BusinessRoleIdentifier SYS_ADMIN = new BusinessRoleIdentifier("IMIT/OPS/INFRA/ADMIN");

	public static final BusinessRoleIdentifier DEVELOPER = new BusinessRoleIdentifier("IMIT/DEV/APPS/DEV");

	public static final BusinessRoleIdentifier TESTER = new BusinessRoleIdentifier("IMIT/DEV/QA/TESTER");

	public static final BusinessRoleIdentifier EXTERNAL_OWNER = new BusinessRoleIdentifier("EXTERNAL/OWNER");

	public static final BusinessRoleIdentifier EXTERNAL_EMPLOYEE = new BusinessRoleIdentifier("EXTERNAL/EMPLOYEE");

	public static final BusinessRoleIdentifier EXTERNAL_CONTACT = new BusinessRoleIdentifier("EXTERNAL/CONTACT");

	public BusinessRoleIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return BusinessRoleIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.BUSINESS_ROLE;
	}
	
}
