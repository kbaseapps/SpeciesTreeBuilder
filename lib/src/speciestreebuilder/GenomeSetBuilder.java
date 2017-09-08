package speciestreebuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import datafileutil.DataFileUtilClient;
import datafileutil.GetObjectsParams;
import datafileutil.ObjectData;
import datafileutil.ObjectSaveData;
import datafileutil.SaveObjectsParams;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.Tuple11;
import us.kbase.common.service.UObject;
import us.kbase.kbasetrees.Tree;
import us.kbase.kbasetrees.util.WorkspaceUtil;
import us.kbase.workspace.CopyObjectParams;
import us.kbase.workspace.GetObjectInfoNewParams;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.ObjectSpecification;
import us.kbase.workspace.WorkspaceClient;

public class GenomeSetBuilder {
    public static String buildGenomeSetFromTree(Map<String, String> config, AuthToken token, 
                                                String treeRef, String genomeSetRef, boolean copyGenomes) throws Exception {
        return buildGenomeSetFromTree(config.get("workspace-url"), token, treeRef, genomeSetRef, copyGenomes);
    }
    
    public static String buildGenomeSetFromTree(String wsUrl, AuthToken token, String treeRef, 
                                                String genomeSetRef, boolean copyGenomes) throws Exception {
        URL callbackUrl = new URL(System.getenv("SDK_CALLBACK_URL"));
        DataFileUtilClient dfu = new DataFileUtilClient(callbackUrl, token);
        dfu.setIsInsecureHttpConnectionAllowed(true);

        WorkspaceClient ws = new WorkspaceClient(new URL(wsUrl), token);
		ws.setIsInsecureHttpConnectionAllowed(true);
		ObjectData data = dfu.getObjects(new GetObjectsParams().withObjectRefs(Arrays.asList(
		        treeRef))).getData().get(0);
		Tree tree = data.getData().asClassInstance(Tree.class);
		Map<String, Map<String, List<String>>> refs = tree.getWsRefs();
		String[] wsGenomeSetName = genomeSetRef.split("/");
		String workspace = wsGenomeSetName[0];
        Long wsId = dfu.wsNameToId(workspace);
		String genomeSetName = wsGenomeSetName[1];
		Map<String, Object> genomeSetData = new TreeMap<String, Object>();
		genomeSetData.put("description", "GenomeSet of genome included in species tree " + treeRef);
		Map<String, Map<String, String>> elements = new TreeMap<String, Map<String, String>>();
		genomeSetData.put("elements", elements);
	    int gcount = 0;
	    List<String> genomesToCopy = new ArrayList<String>();
		for (String key : refs.keySet()) {
		    if (refs.get(key).containsKey("g") && refs.get(key).get("g").size() > 0) {
		        String ref = refs.get(key).get("g").get(0);
		        long refWsId = Long.parseLong(ref.split("/")[0]);
		        if ((wsId.equals(refWsId)) || (!copyGenomes)) {
		            String param = "param" + gcount;
		            gcount++;
		            Map<String, String> element = new TreeMap<String, String>();
		            element.put("ref", ref);
		            elements.put(param, element);
		            //System.out.println("Genome " + key + " (ref=" + ref + ") wasn't copied to " + workspace);
		        } else {
		            genomesToCopy.add(key);
		        }
			} else {
			    System.err.println("[WARNING] GenomeSetBuilder: Can't find genome object " +
			    		"reference for id: " + key);
			}
		}
		if (genomesToCopy.size() > 0) {
		    List<ObjectSpecification> objectids = new ArrayList<ObjectSpecification>();
		    for (String key : genomesToCopy) {
                String ref = refs.get(key).get("g").get(0);
		        objectids.add(new ObjectSpecification().withRef(ref));
		    }
		    Set<String> namehash = new TreeSet<String>();
		    List<Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>>> infos = 
		            ws.getObjectInfoNew(new GetObjectInfoNewParams().withObjects(objectids).withIncludeMetadata(0L));
		    for (Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>> item : infos) {
		        namehash.add(item.getE2());
		    }
		    for (String key : genomesToCopy) {
		        String origRef = refs.get(key).get("g").get(0);
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
		        		.withName(newname)).withFrom(new ObjectIdentity().withRef(origRef)));
		        //System.out.println("Genome " + key + " (ref=" + origRef + ") was copied to " + workspace + "/" + newname);
		        String ref = WorkspaceUtil.getRefFromObjectInfo(wsinfo);
		        String param = "param" + gcount;
		        gcount++;
		        Map<String, String> element = new TreeMap<String, String>();
		        element.put("ref", ref);
		        elements.put(param, element);
		    }
		}
		return WorkspaceUtil.getRefFromObjectInfo(dfu.saveObjects(new SaveObjectsParams().withId(wsId)
		        .withObjects(Arrays.asList(new ObjectSaveData().withType("KBaseSearch.GenomeSet")
		                .withName(genomeSetName).withData(new UObject(genomeSetData))))).get(0));
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
