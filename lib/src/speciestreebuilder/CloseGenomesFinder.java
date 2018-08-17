package speciestreebuilder;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.Tuple2;
import workspace.ObjectData;
import workspace.SubObjectIdentity;
import workspace.WorkspaceClient;

public class CloseGenomesFinder {
    private final Map<String, String> configParams;
    private final AuthToken token;
    
    public CloseGenomesFinder(Map<String, String> configParams, AuthToken token) {
        this.configParams = configParams;
        this.token = token;
    }

    public List<String> findGenomes(FindCloseGenomesParams params) throws Exception {
        return findGenomes(params, false);
    }
    
	public List<String> findGenomes(FindCloseGenomesParams params, boolean stopOnZeroDist) throws Exception {
		long maxDist = params.getMaxMismatchPercent() == null ? 5L : params.getMaxMismatchPercent();
		SpeciesTreeBuilder stb = new SpeciesTreeBuilder(configParams, token);
		Map<String, String> idLabelMap = new TreeMap<String, String>();
		Map<String, Map<String, List<String>>> idRefMap = 
				new TreeMap<String, Map<String, List<String>>>();
		Set<String> seeds = new HashSet<String>();
		speciestreebuilder.SpeciesTreeBuilder.AlignConcat concat = stb.placeUserGenomesIntoAlignment( 
				Arrays.asList(params.getQueryGenome()), true, idLabelMap, idRefMap, seeds);
		List<Tuple2<String, Integer>> kbIdToMinDist = stb.sortPublicGenomesByMismatches(
				seeds, concat, stopOnZeroDist);
		Map<String, String> kbToRefs = stb.loadGenomeKbToRefs();
		List<String> ret = new ArrayList<String>();
		for (Tuple2<String, Integer> idAndDist : kbIdToMinDist) {
			int dist = idAndDist.getE2();
			if (dist > maxDist)
				break;
			String kbId = idAndDist.getE1();
			String ref = null;
			if (idRefMap.get(kbId) == null) {
				ref = kbToRefs.get(kbId);
				if (ref == null) {
					System.err.println("[WARNING] CloseGenomeFinder: Can't find public genome object for id : " + kbId);
					continue;
				}
			} else {
				ref = idRefMap.get(kbId).get("g").get(0);
			}
			ret.add(ref);
		}
		return ret;
	}
	
	public String guessTaxonomy(GuessTaxonomyPathParams params) throws Exception {
		String ret = guessTaxonomy(params, true);
		if (ret == null)
			ret = guessTaxonomy(params, false);
		return ret;
	}

	public String guessTaxonomy(GuessTaxonomyPathParams params, boolean stopOnZeroDist) throws Exception {
		List<String> genomeRefs = findGenomes(new FindCloseGenomesParams().
				withQueryGenome(params.getQueryGenome()), stopOnZeroDist);
		String ret = null;
		String key = "taxonomy";
		URL wsUrl = new URL(configParams.get("workspace-url"));
		WorkspaceClient ws = new WorkspaceClient(wsUrl, token);
        ws.setIsInsecureHttpConnectionAllowed(true);
		for (String genomeRef : genomeRefs) {
			@SuppressWarnings("deprecation")
            ObjectData od = ws.getObjectSubset(Arrays.asList(
					new SubObjectIdentity().withRef(genomeRef)
					.withIncluded(Arrays.asList(key)))).get(0);
			Map<?, ?> genomeObj = od.getData().asClassInstance(Map.class);
			String taxonomy = (String)genomeObj.get(key);
			if (taxonomy != null) {
				taxonomy = taxonomy.trim();
				if (taxonomy.contains(";")) {
					ret = taxonomy.substring(0, taxonomy.lastIndexOf(';') + 1);
					break;
				}
			}
		}
		return ret;
	}
}
