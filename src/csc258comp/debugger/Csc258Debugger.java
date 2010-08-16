package csc258comp.debugger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;

import javax.swing.JFrame;

import csc258comp.compiler.CompilationException;
import csc258comp.compiler.Csc258Compiler;
import csc258comp.compiler.Csc258Linker;
import csc258comp.compiler.Fragment;
import csc258comp.compiler.SourceCode;
import csc258comp.runner.Program;


public class Csc258Debugger {
	
	public static void main(String[] args) throws IOException {
		Set<Fragment> frags = new HashSet<Fragment>();
		for (String arg : args) {
			try {
				File file = new File(arg);
				SourceCode sc = SourceCode.readFile(file);
				Fragment f = Csc258Compiler.compile(sc);
				frags.add(f);
			} catch (CompilationException e) {
				printCompilerErrors(e.getErrorMessages(), e.getSourceCode());
				System.exit(1);
				return;
			}
		}
		
		Program p = Csc258Linker.link(frags);
		ProbedMachine m = new ProbedMachine(System.in, System.out);
		StatePanel panel = new StatePanel(m);
		m.loadProgram(p);
		
		JFrame frame = new JFrame("CSC258 Computer Debugger");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	private static void printCompilerErrors(SortedMap<Integer,String> msgs, SourceCode sc) {
		String filename = sc.getFile() != null ? sc.getFile().getName() : "(no file)";
		for (int line : msgs.keySet()) {
			System.err.printf("%s:%d: %s%n", filename, line + 1, msgs.get(line));
			System.err.println(sc.getLineAt(line));
			System.err.println();
		}
		System.err.printf("%d error%s%n", msgs.size(), msgs.size() == 1 ? "" : "s");
	}
	
}
