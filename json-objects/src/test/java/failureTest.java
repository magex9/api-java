import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

import ca.magex.json.model.JsonObject;

public class failureTest {

	@Test
	public void testFailure1() throws IOException {
		InputStream is = getClass().getResourceAsStream("/failure.json");
		InputStreamReader isr = new InputStreamReader(is);
		char[] buffer = new char[1024];
		int len = 0;
		StringBuffer text = new StringBuffer();
		while ((len = isr.read(buffer, 0, 1024)) > 0) {
			text.append(buffer, 0, len);
		}
		new JsonObject(text.toString());
	}
}
