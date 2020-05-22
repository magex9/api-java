package ca.magex.json.model;

import java.io.IOException;
import java.io.OutputStream;

public class FakeOutputStream extends OutputStream {

	@Override
	public void write(int b) throws IOException {
		throw new IOException("JUnit exception");
	}

}
