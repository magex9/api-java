package ca.magex.crm.api.system.id;

import ca.magex.crm.api.Crm;
import ca.magex.crm.api.system.Type;

/**
 * A Specific Identifier used for Phrase Identification
 * 
 * @author Jonny
 */
public class PhraseIdentifier extends OptionIdentifier {
	
	private static final long serialVersionUID = Crm.SERIAL_UID_VERSION;
	
	public static final String CONTEXT = OptionIdentifier.CONTEXT + "phrases/";
	
	public static final PhraseIdentifier VALIDATION_FIELD_REQUIRED = new PhraseIdentifier("VALIDATION/FIELD/REQUIRED");

	public static final PhraseIdentifier VALIDATION_FIELD_FORBIDDEN = new PhraseIdentifier("VALIDATION/FIELD/FORBIDDEN");

	public static final PhraseIdentifier VALIDATION_FIELD_INVALID = new PhraseIdentifier("VALIDATION/FIELD/INVALID");

	public static final PhraseIdentifier VALIDATION_FIELD_FORMAT = new PhraseIdentifier("VALIDATION/FIELD/FORMAT");

	public static final PhraseIdentifier VALIDATION_FIELD_MINLENGTH = new PhraseIdentifier("VALIDATION/FIELD/MINLENGTH");

	public static final PhraseIdentifier VALIDATION_FIELD_MAXLENGTH = new PhraseIdentifier("VALIDATION/FIELD/MAXLENGTH");

	public static final PhraseIdentifier VALIDATION_FIELD_INACTIVE = new PhraseIdentifier("VALIDATION/FIELD/INACTIVE");

	public static final PhraseIdentifier VALIDATION_STATUS_PENDING = new PhraseIdentifier("VALIDATION/STATUS/PENDING");

	public static final PhraseIdentifier VALIDATION_OPTION_IMMUTABLE = new PhraseIdentifier("VALIDATION/OPTION/IMMUTABLE");

	public static final PhraseIdentifier VALIDATION_OPTION_DUPLICATE = new PhraseIdentifier("VALIDATION/OPTION/DUPLICATE");

	public static final PhraseIdentifier VALIDATION_OPTION_INVALID = new PhraseIdentifier("VALIDATION/OPTION/INVALID");

	public PhraseIdentifier(CharSequence id) {
		super(id);
	}
	
	@Override
	public String getContext() {
		return PhraseIdentifier.CONTEXT;
	}
	
	@Override
	public Type getType() {
		return Type.PHRASE;
	}
	
}
