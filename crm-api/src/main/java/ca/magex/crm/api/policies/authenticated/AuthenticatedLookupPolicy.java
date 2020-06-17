package ca.magex.crm.api.policies.authenticated;

import java.util.Locale;

import ca.magex.crm.api.policies.CrmLookupPolicy;
import ca.magex.crm.api.policies.basic.BasicLookupPolicy;
import ca.magex.crm.api.services.CrmAuthenticationService;
import ca.magex.crm.api.services.CrmLookupService;

public class AuthenticatedLookupPolicy implements CrmLookupPolicy {

	@SuppressWarnings("unused")
	private CrmAuthenticationService auth;
	
	private CrmLookupPolicy delegate;
	
	public AuthenticatedLookupPolicy(
			CrmAuthenticationService auth,
			CrmLookupService lookups) {
		this.auth = auth;
		this.delegate = new BasicLookupPolicy(lookups);
	}

	@Override
	public boolean canViewStatusLookup(String StatusLookup, Locale locale) {
		return delegate.canViewStatusLookup(StatusLookup, locale);
	}

	@Override
	public boolean canViewCountryLookup(String CountryLookup, Locale locale) {
		return delegate.canViewCountryLookup(CountryLookup, locale);
	}

	@Override
	public boolean canViewProvinceLookup(String countryLookup, String provinceLookup, Locale locale) {
		return delegate.canViewProvinceLookup(countryLookup, provinceLookup, locale);
	}

	@Override
	public boolean canViewLanguageLookup(String languageLookup, Locale locale) {
		return delegate.canViewLanguageLookup(languageLookup, locale);
	}

	@Override
	public boolean canViewSalutationLookup(String salutationLookup, Locale locale) {
		return delegate.canViewSalutationLookup(salutationLookup, locale);
	}

	@Override
	public boolean canViewBusinessSectorLookup(String sectorLookup, Locale locale) {
		return delegate.canViewBusinessSectorLookup(sectorLookup, locale);
	}

	@Override
	public boolean canViewBusinessUnitLookup(String unitLookup, Locale locale) {
		return delegate.canViewBusinessUnitLookup(unitLookup, locale);
	}

	@Override
	public boolean canViewBusinessClassificationLookup(String classificationLookup, Locale locale) {
		return delegate.canViewBusinessClassificationLookup(classificationLookup, locale);
	}
}
