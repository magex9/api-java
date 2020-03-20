package ca.magex.crm.api.system;

import java.time.LocalDateTime;

public class Enabled {
	
	private Identifier identifier;
	
	private LocalDateTime enabled;
	
	private LocalDateTime disabled;
	
	public Enabled(Identifier identifier, LocalDateTime enabled, LocalDateTime disabled) {
		this.identifier = identifier;
		this.enabled = enabled;
		this.disabled = disabled;
	}

	public Identifier getIdentifier() {
		return identifier;
	}

	public LocalDateTime getEnabled() {
		return enabled;
	}

	public LocalDateTime getDisabled() {
		return disabled;
	}

}
