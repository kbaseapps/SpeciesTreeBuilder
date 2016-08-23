package us.kbase.kbasetrees.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.forester.io.parsers.nhx.NHXFormatException;
import org.forester.io.parsers.nhx.NHXParser;
import org.forester.phylogeny.Phylogeny;
import org.forester.phylogeny.PhylogenyMethods;
import org.forester.phylogeny.PhylogenyNode;
import org.forester.phylogeny.data.Confidence;
import org.forester.phylogeny.iterators.PhylogenyNodeIterator;

/**
 * collection of static methods which use the forester library to perform
 * basic tree structure manipulations.
 * 
 * @author msneddon
 */
public class TreeStructureUtil {
	
	
	static public boolean canBeParsedAsDouble() {
		return false;
	}
	
	/**
	 * 
	 */
	static public String mergeZeroDistanceLeaves(String tree) throws Exception {
		// parse the tree
		NHXParser parser = new NHXParser();
		parser.setSource(tree);
		Phylogeny [] trees = parser.parse();

		// restructure the tree
		StringBuilder relabeledTrees = new StringBuilder();
		for(int k=0; k<trees.length; k++) {
			PhylogenyMethods.transferInternalNamesToBootstrapSupport(trees[k]);
			// first pass over leaf nodes, flag the parents if the distance to the parent is zero
			Map <Long,Integer> parentListTarget = new HashMap<Long,Integer>();
			for( final PhylogenyNodeIterator it = trees[k].iteratorExternalForward(); it.hasNext(); ) {
				PhylogenyNode leaf = it.next();
				if(leaf.getDistanceToParent()==0) {
					if(leaf.getParent().getName().equals("")) {
						long parentId = leaf.getParent().getId();
						Integer zeroCount = parentListTarget.get(parentId);
						if(zeroCount==null) {
							parentListTarget.put(new Long(parentId), new Integer(1));
						} else {
							parentListTarget.put(new Long(parentId), new Integer(zeroCount.intValue()+1));
						}
					}
				}
			}
			// now pass over the marked parents, check if all leaf nodes were distance zero, and if
			// so, we remove the parent and replace it with the first child node
			for (Map.Entry<Long, Integer> pair : parentListTarget.entrySet()) {
				PhylogenyNode parent = trees[k].getNode(pair.getKey());
				System.out.println("got something in this list: "+pair.getKey()+" -> "+pair.getValue());
				if(pair.getValue().intValue() == parent.getNumberOfDescendants()) {
					// remove it.
					for(int c=parent.getNumberOfDescendants()-1; c>0; c--) {
						parent.removeChildNode(c);
					}
					PhylogenyMethods.removeNode(parent, trees[k]);
				}
			}
			TreeStructureUtil.transferConfidenceToLabels(trees[k]);
			relabeledTrees.append(trees[k].toNewHampshire());
		}
		return relabeledTrees.toString();
	}
	
	/**
	 * Internal node labels are often used to store bootstrap values.  This method
	 * saves the internal node labels as a new confidence object IF the label can
	 * be parsed as a double value, and the name is set to an empty string.  This 
	 * should be called first before additional confidence values are added and
	 * before operating on the 
	 * @param tree
	 */
	static void saveInternalNodeLabels(Phylogeny tree) {
		for( final PhylogenyNodeIterator it = tree.iteratorPostorder(); it.hasNext(); ) {
			PhylogenyNode node = it.next();
			if(node.isInternal()) {
				node.getBranchData().addConfidence(new Confidence(0,node.getName()));
				node.setName(Double.toString(node.getBranchData().getConfidence(0).getValue()));
			}
		}
	}
	
	static void transferConfidenceToLabels(Phylogeny tree) {
		for( final PhylogenyNodeIterator it = tree.iteratorPostorder(); it.hasNext(); ) {
			PhylogenyNode node = it.next();
			if(node.isInternal()) {
				node.getBranchData().addConfidence(new Confidence(0,node.getName()));
				node.setName(Double.toString(node.getBranchData().getConfidence(0).getValue()));
			}
		}
	}
	
	public static String rerootTreeToMidpoint(String treeText) throws IOException {
        NHXParser parser = new NHXParser();
        parser.setSource(treeText);
        Phylogeny [] trees = parser.parse();
        StringBuilder changedTrees = new StringBuilder();
        for(Phylogeny tree : trees) {
        	PhylogenyMethods.midpointRoot(tree);
        	changedTrees.append(tree.toNewHampshire());
        }
        return changedTrees.toString();
	}
	
}
