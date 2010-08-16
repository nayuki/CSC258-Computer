package csc258comp.debugger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import csc258comp.compiler.Program;
import csc258comp.machine.impl.SimpleMachine;
import csc258comp.machine.model.Machine;
import csc258comp.machine.model.MachineStateListener;


public final class ProbedMachine implements Machine {
	
	private Machine state;
	
	private Set<MachineStateListener> listeners;
	
	
	public ProbedMachine(InputStream in, OutputStream out) {
		state = new SimpleMachine(in, out);
		listeners = new HashSet<MachineStateListener>();
	}
	
	
	
	@Override
	public boolean isHalted() {
		return state.isHalted();
	}
	
	
	@Override
	public void setHalted(boolean halted) {
		state.setHalted(halted);
		for (MachineStateListener listener : listeners)
			listener.haltedChanged(this);
	}
	
	
	@Override
	public int getProgramCounter() {
		return state.getProgramCounter();
	}
	
	
	@Override
	public void setProgramCounter(int addr) {
		state.setProgramCounter(addr);
		for (MachineStateListener listener : listeners)
			listener.programCounterChanged(this);
	}
	
	
	@Override
	public int getAccumulator() {
		return state.getAccumulator();
	}
	
	
	@Override
	public void setAccumulator(int val) {
		state.setAccumulator(val);
		for (MachineStateListener listener : listeners)
			listener.accumulatorChanged(this);
	}
	
	
	@Override
	public boolean getConditionCode() {
		return state.getConditionCode();
	}
	
	
	@Override
	public void setConditionCode(boolean val) {
		state.setConditionCode(val);
		for (MachineStateListener listener : listeners)
			listener.conditionCodeChanged(this);
	}
	
	
	@Override
	public int getMemoryAt(int addr) {
		return state.getMemoryAt(addr);
	}
	
	
	@Override
	public void setMemoryAt(int addr, int val) {
		state.setMemoryAt(addr, val);
		for (MachineStateListener listener : listeners)
			listener.memoryChanged(this, addr);
	}
	
	
	@Override
	public int input() throws IOException {
		return state.input();
	}
	
	
	@Override
	public boolean output(int b) throws IOException {
		return state.output(b);
	}
	
	
	public void loadProgram(Program prog) {
		int[] image = prog.getImage();
		setHalted(false);
		setProgramCounter(prog.getMainAddress());
		setAccumulator(0);
		setConditionCode(false);
		for (int i = 0; i < image.length; i++)
			setMemoryAt(i, image[i]);
		for (MachineStateListener listener : listeners)
			listener.programLoaded(this, prog);
	}
	
	
	public void addListener(MachineStateListener listener) {
		listeners.add(listener);
	}
	
	
	public void removeListener(MachineStateListener listener) {
		listeners.remove(listener);
	}
	
}
