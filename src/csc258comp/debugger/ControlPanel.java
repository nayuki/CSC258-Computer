package csc258comp.debugger;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import csc258comp.runner.MachineException;


@SuppressWarnings("serial")
final class ControlPanel extends JPanel {
	
	private final DebugPanel parent;
	
	
	
	public ControlPanel(DebugPanel parent) {
		super(new FlowLayout(FlowLayout.LEFT));
		if (parent == null)
			throw new NullPointerException();
		this.parent = parent;
		
		JButton step = new JButton("Step");
		step.addActionListener(new StepAction());
		add(step);
		
		JButton run = new JButton("Run");
		run.addActionListener(new RunAction());
		add(run);
	}
	
	
	
	private class StepAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			try {
				DebugMachine m = parent.machine;
				int oldPc = m.getProgramCounter();
				int oldAcc = m.getAccumulator();
				boolean oldCond = m.getConditionCode();
				
				parent.controller.step();
				
				RegisterPanel rp = parent.registerPanel;
				rp.programCounter.setBackground(m.getProgramCounter() == oldPc + 1 ? DebugPanel.unchangedColor : DebugPanel.changedColor);
				rp.accumulator.setBackground(m.getAccumulator() == oldAcc ? DebugPanel.unchangedColor : DebugPanel.changedColor);
				rp.conditionCode.setBackground(m.getConditionCode() == oldCond ? DebugPanel.unchangedColor : DebugPanel.changedColor);
				rp.stepCountField.setText(Long.toString(parent.controller.getStepCount()));
			} catch (MachineException ex) {
				throw new RuntimeException(ex);
			}
		}
		
	}
	
	
	private class RunAction implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			try {
				DebugMachine m = parent.machine;
				RegisterPanel rp = parent.registerPanel;
				MachineTableModel tm = parent.tableModel;
				
				int oldAcc = parent.machine.getAccumulator();
				boolean oldCond = parent.machine.getConditionCode();
				
				m.removeListener(rp);
				m.removeListener(tm);
				parent.controller.run();
				rp.programCounterChanged(parent.machine);
				rp.accumulatorChanged(parent.machine);
				rp.conditionCodeChanged(parent.machine);
				m.addListener(rp);
				m.addListener(tm);
				
				rp.programCounter.setBackground(DebugPanel.unchangedColor);
				rp.accumulator.setBackground(parent.machine.getAccumulator() == oldAcc ? DebugPanel.unchangedColor : DebugPanel.changedColor);
				rp.conditionCode.setBackground(parent.machine.getConditionCode() == oldCond ? DebugPanel.unchangedColor : DebugPanel.changedColor);
				rp.stepCountField.setText(Long.toString(parent.controller.getStepCount()));
				tm.fireTableDataChanged();
			} catch (MachineException ex) {
				throw new RuntimeException(ex);
			}
		}
		
	}
	
}
