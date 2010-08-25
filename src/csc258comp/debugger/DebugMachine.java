package csc258comp.debugger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import csc258comp.runner.Loader;
import csc258comp.runner.Machine;
import csc258comp.runner.Program;
import csc258comp.runner.BasicMachine;


public final class DebugMachine implements Machine {
	
	private final Machine machine;
	
	
	
	public DebugMachine(InputStream in, OutputStream out) {
		machine = new BasicMachine(in, out);
	}
	
	
	
	@Override
	public boolean isHalted() {
		return machine.isHalted();
	}
	
	
	@Override
	public void setHalted(boolean halted) {
		machine.setHalted(halted);
	}
	
	
	@Override
	public int getProgramCounter() {
		return machine.getProgramCounter();
	}
	
	
	@Override
	public void setProgramCounter(int addr) {
		machine.setProgramCounter(addr);
	}
	
	
	@Override
	public int getAccumulator() {
		return machine.getAccumulator();
	}
	
	
	@Override
	public void setAccumulator(int val) {
		machine.setAccumulator(val);
	}
	
	
	@Override
	public boolean getConditionCode() {
		return machine.getConditionCode();
	}
	
	
	@Override
	public void setConditionCode(boolean val) {
		machine.setConditionCode(val);
	}
	
	
	@Override
	public int getMemoryAt(int addr) {
		return machine.getMemoryAt(addr);
	}
	
	
	@Override
	public void setMemoryAt(int addr, int val) {
		machine.setMemoryAt(addr, val);
	}
	
	
	@Override
	public int input() throws IOException {
		return machine.input();
	}
	
	
	@Override
	public boolean output(int b) throws IOException {
		return machine.output(b);
	}
	
	
	public void loadProgram(Program prog) {
		if (prog == null)
			throw new NullPointerException();
		Loader.load(this, prog);
	}
	
}
