package csc258comp.compiler;

import org.junit.Test;


public class IntegerTest {
	
	@Test
	public void testBasic() {
		DataTestUtils.test("d: I 0", 0);
		DataTestUtils.test("d: I 1", 1);
		DataTestUtils.test("d: I 10", 10);
		DataTestUtils.test("d: I -1", -1);
	}
	
	
	@Test
	public void testZeroPadding() {
		DataTestUtils.test("d: I 000000000000000000001", 1);
	}
	
	
	@Test
	public void testUpperLimit() {
		DataTestUtils.test("d: I 2147483639", 2147483639);
		DataTestUtils.test("d: I 2147483646", 2147483646);
		DataTestUtils.test("d: I 2147483647", 2147483647);
	}
	
	
	@Test
	public void testLowerLimit() {
		DataTestUtils.test("d: I -2147483639", -2147483639);
		DataTestUtils.test("d: I -2147483647", -2147483647);
		DataTestUtils.test("d: I -2147483648", -2147483648);
	}
	
	
	@Test(expected=CompilationException.class)
	public void testInvalid() throws CompilationException {
		DataTestUtils.testInvalid("I abcd");
	}
	
	
	@Test(expected=CompilationException.class)
	public void testPlusSign() throws CompilationException {
		DataTestUtils.testInvalid("I +0");
	}
	
	
	@Test(expected=CompilationException.class)
	public void testPositiveOverflow() throws CompilationException {
		DataTestUtils.testInvalid("I 2147483648");
	}
	
	
	@Test(expected=CompilationException.class)
	public void testNegativeOverflow() throws CompilationException {
		DataTestUtils.testInvalid("I -2147483649");
	}
	
	
	@Test(expected=CompilationException.class)
	public void testBigPositiveOverflow() throws CompilationException {
		DataTestUtils.testInvalid("I 99999999999999999999999999999999999999999999999999");
	}
	
}
