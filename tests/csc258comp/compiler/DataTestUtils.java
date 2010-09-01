package csc258comp.compiler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


final class DataTestUtils {
	
	public static void test(String code, int expectedWord) {
		try {
			List<String> lines = new ArrayList<String>();
			Collections.addAll(lines, code.split("\n"));
			
			SourceCode source = new SourceCode(lines);
			Fragment f = MyCompiler.compile(source);
			
			int addr = f.getLabels().get("d");
			int word = f.getImage()[addr];
			assertEquals(expectedWord, word);
		} catch (CompilerException e) {
			fail("Compiler exception: " + e.getMessage());
		}
	}
	
	
	public static void testInvalid(String code) throws CompilerException {
		List<String> lines = new ArrayList<String>();
		Collections.addAll(lines, code.split("\n"));
		
		SourceCode source = new SourceCode(lines);
		MyCompiler.compile(source);
	}
	
}
