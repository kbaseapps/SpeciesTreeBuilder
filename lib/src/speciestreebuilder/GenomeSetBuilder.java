package speciestreebuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.Tuple11;
import us.kbase.common.service.UObject;
import us.kbase.kbasetrees.Tree;
import us.kbase.workspace.CopyObjectParams;
import us.kbase.workspace.GetObjectInfoNewParams;
import us.kbase.workspace.GetObjects2Params;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.ObjectSpecification;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;

public class GenomeSetBuilder {
    public static String buildGenomeSetFromTree(Map<String, String> config, AuthToken token, 
            String treeRef, String genomeSetRef) throws Exception {
        return buildGenomeSetFromTree(config.get("workspace-url"), token, treeRef, genomeSetRef);
    }
    
    public static String buildGenomeSetFromTree(String wsUrl, AuthToken token, String treeRef, 
	        String genomeSetRef) throws Exception {
        WorkspaceClient ws = new WorkspaceClient(new URL(wsUrl), token);
		ws.setIsInsecureHttpConnectionAllowed(true);
		ObjectData data = ws.getObjects2(new GetObjects2Params().withObjects(Arrays.asList(
		        new ObjectSpecification().withRef(treeRef)))).getData().get(0);
		Tree tree = data.getData().asClassInstance(Tree.class);
		long wsid = data.getInfo().getE7();
		Map<String, Map<String, List<String>>> refs = tree.getWsRefs();
		String[] wsGenomeSetName = genomeSetRef.split("/");
		String workspace = wsGenomeSetName[0];
		String genomeSetName = wsGenomeSetName[1];
		Map<String, Object> genomeSetData = new TreeMap<String, Object>();
		genomeSetData.put("description", "GenomeSet of genome included in species tree " + treeRef);
		Map<String, Map<String, String>> elements = new TreeMap<String, Map<String, String>>();
		genomeSetData.put("elements", elements);
	    int gcount = 0;
	    Set<String> namehash = new TreeSet<String>();
	    List<ObjectSpecification> objectids = new ArrayList<ObjectSpecification>();
		for (String key : refs.keySet()) {
			if (key.length() < 5 || !key.substring(0, 5).equals("kb|g.")) {
	            String param = "param" + gcount;
	            gcount++;
	            String ref = refs.get(key).get("g").get(0);
	            objectids.add(new ObjectSpecification().withRef(ref));
	            Map<String, String> element = new TreeMap<String, String>();
	            element.put("ref", ref);
	            elements.put(param, element);
				//System.out.println("Genome " + key + " (ref=" + ref + ") wasn't copied to " + workspace);
			}
		}
		List<Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>>> infos = 
				ws.getObjectInfoNew(new GetObjectInfoNewParams().withObjects(objectids).withIncludeMetadata(0L));
		for (Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>> item : infos) {
			namehash.add(item.getE2());
		}
	    for (String key : refs.keySet()) {
			if (key.length() < 5 || !key.substring(0, 5).equals("kb|g."))
				continue;
			String ref = refs.get(key).get("g").get(0);
			String newname = tree.getDefaultNodeLabels().get(key);
			newname = cleanName(newname);
			if (namehash.contains(newname)) {
				int count = 0;
				String testname = newname + "_" + count;
				while (namehash.contains(testname)) {
					count++;
					testname = newname + "_" + count;
				}
				newname = testname;
			}
			namehash.add(newname);
			Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>> wsinfo = 
					ws.copyObject(new CopyObjectParams().withTo(new ObjectIdentity().withWorkspace(workspace)
					.withName(newname)).withFrom(new ObjectIdentity().withRef(ref)));
			//System.out.println("Genome " + key + " (ref=" + ref + ") was copied to " + workspace + "/" + newname);
			wsid = wsinfo.getE7();
			long objid = wsinfo.getE1();
			long version = wsinfo.getE5();
			ref = "" + wsid + "/" + objid + "/" + version;
			String param = "param" + gcount;
			gcount++;
            Map<String, String> element = new TreeMap<String, String>();
            element.put("ref", ref);
			elements.put(param, element);
	    }
		ws.saveObjects(new SaveObjectsParams().withWorkspace(workspace).withObjects(Arrays.asList(
				new ObjectSaveData().withType("KBaseSearch.GenomeSet").withName(genomeSetName)
				.withData(new UObject(genomeSetData)))));
		return null;
	}
	
	private static String cleanName(String text) {
		StringBuilder ret = new StringBuilder();
		for (int pos = 0; pos < text.length(); pos++) {
			char ch = text.charAt(pos);
			if ((!Character.isAlphabetic(ch)) && (!Character.isDigit(ch)) && (ch != '_') && (ch != '.'))
				ch = '_';
			ret.append(ch);
		}
		return ret.toString();
	}
}
