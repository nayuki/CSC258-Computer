/* 
 * CSC258 computer
 * 
 * Copyright (c) Project Nayuki
 * https://www.nayuki.io/page/csc258-computer-debugger
 */

package csc258comp.debugger;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import javax.swing.JFrame;
import csc258comp.compiler.CompilerException;
import csc258comp.compiler.Fragment;
import csc258comp.compiler.Linker;
import csc258comp.compiler.LinkerException;
import csc258comp.compiler.MyCompiler;
import csc258comp.compiler.SourceCode;
import csc258comp.compiler.SourceLine;
import csc258comp.runner.Program;


public final class Debugger {
	
	public static void main(String[] args) {
		// Compile a fragment for each file argument
		List<Fragment> frags = new ArrayList<Fragment>();
		try {
			for (String arg : args) {
				File file = new File(arg);
				try {
					SourceCode sc = SourceCode.readFile(file);
					Fragment f = MyCompiler.compile(sc);
					frags.add(f);
				} catch (FileNotFoundException e) {
					System.err.printf("File not found: %s%n", file.getPath());
					System.exit(1);
					return;
				}
			}
		} catch (OutOfMemoryError e) {
			System.err.println("Error: Out of memory during compilation");
			System.exit(1);
			return;
		} catch (CompilerException e) {
			printCompilerErrors(e.getErrorMessages(), e.getSourceCode());
			System.exit(1);
			return;
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}
		
		// Link the fragments together to make a program
		Program p;
		try {
			p = Linker.link(frags);
		} catch (LinkerException e) {
			if (e.getErrorMessages() == null)
				System.err.printf("Linker error: %s%n", e.getMessage());
			else
				printLinkerErrors(e.getErrorMessages());
			System.exit(1);
			return;
		} catch (OutOfMemoryError e) {
			System.err.println("Error: Out of memory during linking");
			System.exit(1);
			return;
		}
		
		// Make the machine and load the program
		DebugMachine m = new DebugMachine(System.in, System.out);
		m.loadProgram(p);
		
		// Build GUI
		final DebugPanel panel = new DebugPanel(m, p);
		final JFrame frame = new JFrame("CSC258 Computer Debugger");
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				panel.controller.suspend();
				frame.dispose();
			}
		});
		frame.add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	
	private static void printCompilerErrors(SortedMap<Integer,String> msgs, SourceCode sc) {
		String filename = formatFileName(sc.getFile());
		for (int line : msgs.keySet()) {
			System.err.printf("%s:%d: %s%n", filename, line + 1, msgs.get(line));
			System.err.println(sc.getLineAt(line));
			System.err.println();
		}
		System.err.printf("%d error%s%n", msgs.size(), msgs.size() == 1 ? "" : "s");
	}
	
	
	private static void printLinkerErrors(Map<SourceLine,String> msgs) {
		for (SourceLine line : msgs.keySet()) {
			System.err.printf("%s:%d: %s%n", formatFileName(line.sourceCode.getFile()), line.lineNumber + 1, msgs.get(line));
			System.err.println(line.sourceCode.getLineAt(line.lineNumber));
			System.err.println();
		}
		System.err.printf("%d error%s%n", msgs.size(), msgs.size() == 1 ? "" : "s");
	}
	
	
	private static String formatFileName(File file) {
		if (file != null)
			return file.getName();
		else
			return "(no file)";
	}
	
}
