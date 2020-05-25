package ca.magex.crm.amnesia.generator;

import static org.junit.Assert.*;

import org.junit.Test;

public class LoremIpsumGeneratorTests {
	
	@Test
	public void testCreatingClass() throws Exception {
		assertNotNull(new LoremIpsumGenerator());
	}
	
	@Test
	public void testBuildWords() throws Exception {
		assertEquals(1, LoremIpsumGenerator.buildWords(0).split(" ").length);
		assertEquals("", LoremIpsumGenerator.buildWords(0));
		assertEquals(5, LoremIpsumGenerator.buildWords(5).split(" ").length);
		assertEquals("Lorem ipsum dolor sit amet,", LoremIpsumGenerator.buildWords(5));
		assertEquals(1000, LoremIpsumGenerator.buildWords(1000).split(" ").length);
	}

	@Test
	public void testRandomWords() throws Exception {
		assertEquals(1, LoremIpsumGenerator.randomWords(0).split(" ").length);
		assertEquals("", LoremIpsumGenerator.randomWords(0));
		assertEquals(5, LoremIpsumGenerator.randomWords(5).split(" ").length);
		assertEquals(1000, LoremIpsumGenerator.randomWords(1000).split(" ").length);
	}
	
	@Test
	public void testBuildParagraphs() throws Exception {
		assertEquals(1, LoremIpsumGenerator.buildParagraphs(0).split("\n").length);
		assertEquals("", LoremIpsumGenerator.buildParagraphs(0));
		assertEquals(5, LoremIpsumGenerator.buildParagraphs(5).split("\n").length);
		assertEquals(1000, LoremIpsumGenerator.buildParagraphs(1000).split("\n").length);
	}

	@Test
	public void testRandomParagraphs() throws Exception {
		assertEquals(1, LoremIpsumGenerator.randomParagraphs(0).split("\n").length);
		assertEquals("", LoremIpsumGenerator.randomParagraphs(0));
		assertEquals(5, LoremIpsumGenerator.randomParagraphs(5).split("\n").length);
		assertEquals(1000, LoremIpsumGenerator.randomParagraphs(1000).split("\n").length);
	}

}
