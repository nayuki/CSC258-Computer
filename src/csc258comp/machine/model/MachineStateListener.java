package csc258comp.machine.model;

import csc258comp.compiler.Program;


public interface MachineStateListener {
	
	public void haltedChanged(MachineState m);
	
	public void programCounterChanged(MachineState m);
	
	public void accumulatorChanged(MachineState m);
	
	public void conditionCodeChanged(MachineState m);
	
	public void memoryChanged(MachineState m, int addr);
	
	public void programLoaded(MachineState m, Program p);
	
}