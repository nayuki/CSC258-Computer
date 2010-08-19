package csc258comp.compiler;

import java.util.HashMap;
import java.util.Map;

import csc258comp.runner.Executor;
import csc258comp.runner.Program;
import csc258comp.util.IntBuffer;


public final class Csc258Linker {
	
	public static Program link(Iterable<Fragment> frags) {
		if (frags == null)
			throw new NullPointerException();
		return new Csc258Linker(frags).result;
	}
	
	
	
	private Program result;
	
	
	
	private Csc258Linker(Iterable<Fragment> frags) {
		Map<Fragment,Integer> fragmentToOffset = layOutFragments(frags);
		Map<String,Integer> allLabels = unionLabels(fragmentToOffset, frags);
		int[] image = resolveAndBuildImage(frags, fragmentToOffset, allLabels);
		
		// Process debugging info
		Map<SourceLine,Integer> srcLineToAddr = new HashMap<SourceLine,Integer>();
		Map<Integer,SourceLine> addrToSrcLine = new HashMap<Integer,SourceLine>();
		for (Fragment f : frags) {
			SourceCode src = f.getSourceCode();
			int off = fragmentToOffset.get(f);
			
			Map<Integer,Integer> aBySl = f.getAddressBySourceLineMap();
			for (int l : aBySl.keySet()) {
				srcLineToAddr.put(new SourceLine(src, l), aBySl.get(l) + off);
			}
			
			Map<Integer,Integer> slByA = f.getSourceLineByAddressMap();
			for (int a : slByA.keySet()) {
				addrToSrcLine.put(a + off, new SourceLine(src, slByA.get(a)));
			}
		}
		
		result = new Program(image, allLabels.get("main"), srcLineToAddr, addrToSrcLine);
	}
	
	
	private static Map<Fragment,Integer> layOutFragments(Iterable<Fragment> frags) {
		Map<Fragment,Integer> fragToOff = new HashMap<Fragment,Integer>();
		int offset = 0;
		for (Fragment f : frags) {
			fragToOff.put(f, offset);
			offset += f.getImageLength();
		}
		return fragToOff;
	}
	
	
	private static Map<String,Integer> unionLabels(Map<Fragment,Integer> fragToOff, Iterable<Fragment> frags) {
		Map<String,Integer> alllabels = new HashMap<String,Integer>();
		alllabels.put("opsys", Executor.OPSYS_ADDRESS);
		
		for (Fragment f : frags) {
			int off = fragToOff.get(f);
			Map<String,Integer> labels = f.getLabels();
			for (String label : labels.keySet()) {
				if (alllabels.containsKey(label))
					throw new IllegalArgumentException(String.format("Duplicate label \"%s\"", label));
				else
					alllabels.put(label, labels.get(label) + off);  // Does relocation
			}
		}
		
		return alllabels;
	}
	
	
	private static int[] resolveAndBuildImage(Iterable<Fragment> frags, Map<Fragment,Integer> fragToOff, Map<String,Integer> allLabels) {
		IntBuffer allImage = new IntBuffer();
		for (Fragment f : frags) {
			int[] image = getImageAndResolveReferences(f, allLabels);
			allImage.append(image);
		}
		return allImage.toArray();
	}
	
	
	private static int[] getImageAndResolveReferences(Fragment f, Map<String,Integer> alllabels) {
		int[] image = f.getImage();
		Map<Integer,String> refs = f.getReferences();
		for (int addr : refs.keySet()) {
			String label = refs.get(addr);
			if (!alllabels.containsKey(label))
				throw new IllegalArgumentException(String.format("Label \"%s\" not defined", label));
			image[addr] |= alllabels.get(label);  // Bottom 24 bits of instruction/data word are all zeros
		}
		return image;
	}
	
}
