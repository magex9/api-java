package ca.magex.crm.test;

import static org.junit.Assert.assertEquals;

import org.springframework.data.domain.Page;

import ca.magex.crm.api.exceptions.BadRequestException;
import ca.magex.crm.api.system.Identifier;
import ca.magex.crm.api.system.Lang;

public class CrmAsserts {
	
	public static <T> void assertSinglePage(Page<T> page, int totalElements) {
		assertPage(page, totalElements, totalElements, 1, false, false, false, false);
	}
	
	public static <T> void assertPage(Page<T> page, int totalElements, int pageSize, int pageNumber, boolean first, boolean previous, boolean next, boolean last) {
		assertEquals(totalElements, page.getTotalElements());
		assertEquals(pageNumber, page.getNumber());
		assertEquals(pageSize, page.getContent().size());
		assertEquals(first, page.isFirst());
		assertEquals(previous, page.hasPrevious());
		assertEquals(next, page.hasNext());
		assertEquals(last, page.isLast());
	}

	public static void assertBadRequestMessage(BadRequestException e, Identifier identifier, String type, String path, String message) {
		assertEquals(1, e.getMessages().size());
		if (identifier != null)
			assertEquals(identifier, e.getMessages().get(0).getIdentifier());
		assertEquals(type, e.getMessages().get(0).getType());
		assertEquals(path, e.getMessages().get(0).getPath());
		assertEquals(message, e.getMessages().get(0).getReason().get(Lang.ENGLISH));
	}
	
}
