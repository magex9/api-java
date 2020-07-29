package ca.magex.crm.mongodb.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;

import ca.magex.crm.api.crm.OrganizationDetails;
import ca.magex.crm.api.crm.OrganizationSummary;
import ca.magex.crm.api.filters.OrganizationsFilter;
import ca.magex.crm.api.filters.Paging;
import ca.magex.crm.api.observer.CrmUpdateNotifier;
import ca.magex.crm.api.repositories.CrmOrganizationRepository;
import ca.magex.crm.api.system.FilteredPage;
import ca.magex.crm.api.system.Status;
import ca.magex.crm.api.system.id.AuthenticationGroupIdentifier;
import ca.magex.crm.api.system.id.BusinessGroupIdentifier;
import ca.magex.crm.api.system.id.IdentifierFactory;
import ca.magex.crm.api.system.id.OrganizationIdentifier;
import ca.magex.crm.mongodb.util.TextUtils;
import ca.magex.json.model.JsonObject;

/**
 * Implementation of the Crm Organization Repository backed by a MongoDB
 * 
 * @author Jonny
 */
public class MongoOrganizationRepository extends AbstractMongoRepository implements CrmOrganizationRepository {

	/**
	 * Creates our new MongoDB Backed Organization Repository
	 * @param mongoCrm
	 * @param notifier
	 */
	public MongoOrganizationRepository(MongoDatabase mongoCrm, CrmUpdateNotifier notifier) {
		super(mongoCrm, notifier);
	}

	@Override
	public OrganizationDetails saveOrganizationDetails(OrganizationDetails organization) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");

		return null;
	}

	@Override
	public OrganizationSummary findOrganizationSummary(OrganizationIdentifier organizationId) {
		Document doc = getMongoCrm()
				.getCollection("organizations")
				.find(Filters.eq("organizationId", organizationId.getFullIdentifier()))
				.projection(Projections.fields(
						Projections.include("status", "displayName")))
				.first();
		if (doc == null) {
			return null;
		}
		return new OrganizationSummary(
				organizationId,
				Status.of(doc.getString("status")),
				doc.getString("displayName"));
	}

	@Override
	public OrganizationDetails findOrganizationDetails(OrganizationIdentifier organizationId) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		Document doc = collection
				.find(Filters.eq("organizationId", organizationId.getFullIdentifier()))
				.projection(Projections.fields(
						Projections.include("status", "displayName", "mainLocationId", "mainContactId", "authenticationGroupIds", "businessGroupIds")))
				.first();
		if (doc == null) {
			return null;
		}
		return new OrganizationDetails(
				organizationId,
				Status.of(doc.getString("status")),
				doc.getString("displayName"),
				IdentifierFactory.forId(doc.getString("mainLocationId")),
				IdentifierFactory.forId(doc.getString("mainContactId")),
				doc.getList("authenticationGroupIds", String.class).stream().map(AuthenticationGroupIdentifier::new).collect(Collectors.toList()),
				doc.getList("businessGroupIds", String.class).stream().map(BusinessGroupIdentifier::new).collect(Collectors.toList()));
	}

	@Override
	public long countOrganizations(OrganizationsFilter filter) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		ArrayList<Bson> pipeline = new ArrayList<>();
		/* create our matcher based on the filter */
		Bson fieldMatcher = toMatcher(filter);
		if (fieldMatcher != null) {
			pipeline.add(Aggregates.match(fieldMatcher));
		}
		pipeline.add(Aggregates.project(Projections.include("organizationId")));
		pipeline.add(Aggregates.count("count"));
		Document doc = collection.aggregate(pipeline).first();
		if (doc == null) {
			return 0L;
		}
		return new JsonObject(doc.toJson()).getLong("count");
	}

	@Override
	public FilteredPage<OrganizationSummary> findOrganizationSummary(OrganizationsFilter filter, Paging paging) {
		MongoCollection<Document> collection = getMongoCrm().getCollection("organizations");
		Bson filtering = toMatcher(filter);
		FindIterable<Document> find = null;
		if (filtering == null) {
			find = collection.find();
		} else {
			find = collection.find(filtering);
		}

		List<OrganizationSummary> content = StreamSupport.stream(
				find
						.sort(sorting(paging))
						.skip((int) paging.getOffset())
						.limit(paging.getPageSize())
						.projection(Projections.fields(
								Projections.include("organizationId", "status", "displayName")))
						.spliterator(),
				false)
				.map((doc) -> new OrganizationSummary(
						new OrganizationIdentifier(doc.getString("organizationId")),
						Status.of(doc.getString("status")),
						doc.getString("displayName")))
				.collect(Collectors.toList());
		
		return new FilteredPage<>(filter, paging, content, 1);
	}

	@Override
	public FilteredPage<OrganizationDetails> findOrganizationDetails(OrganizationsFilter filter, Paging paging) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Constructs a Bson based on the given organizations filter or null if no filtering provided
	 * 
	 * @param filter
	 * @return
	 */
	private Bson toMatcher(OrganizationsFilter filter) {
		List<Bson> filters = new ArrayList<>();
		if (filter.getStatus() != null) {
			filters.add(Filters.eq("status", filter.getStatusCode()));
		}
		if (StringUtils.isNotBlank(filter.getDisplayName())) {
			filters.add(Filters.eq("displayName_searchable", TextUtils.toSearchable(filter.getDisplayName())));
		}
		if (filter.getAuthenticationGroupId() != null) {
			filters.add(Filters.eq("authenticationGroupIds", filter.getAuthenticationGroupId().getFullIdentifier()));
		}
		if (filter.getBusinessGroupId() != null) {
			filters.add(Filters.eq("businessGroupIds", filter.getBusinessGroupId().getFullIdentifier()));
		}

		return conjunction(filters);
	}
}
