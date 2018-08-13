package speciestreebuilder;

import genomeannotationapi.GenomeAnnotationAPIClient;
import genomeannotationapi.GenomeSelectorV1;
import genomeannotationapi.GetGenomeParamsV1;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import kbasegenomes.Feature;
import kbasegenomes.Genome;

import datafileutil.DataFileUtilClient;

import us.kbase.auth.AuthToken;
import us.kbase.common.service.Tuple11;
import us.kbase.common.service.Tuple2;
import us.kbase.common.service.UObject;
import us.kbase.common.utils.AlignUtil;
import us.kbase.common.utils.CorrectProcess;
import us.kbase.common.utils.FastaReader;
import us.kbase.common.utils.FastaWriter;
import us.kbase.kbasetrees.Tree;
import us.kbase.kbasetrees.util.TreeStructureUtil;
import us.kbase.kbasetrees.util.WorkspaceUtil;
import workspace.CopyObjectParams;
import workspace.GetObjectInfoNewParams;
import workspace.GetObjects2Params;
import workspace.ListObjectsParams;
import workspace.ObjectIdentity;
import workspace.ObjectSpecification;
import workspace.WorkspaceClient;
import workspace.WorkspaceIdentity;

public class SpeciesTreeBuilder {
    private File tempDir;
    private File dataDir;
    private WorkspaceClient storage;
    private AuthToken token;
    private String wsUrl;
	private static final String SPECIES_TREE_TYPE = "SpeciesTree";
	private static final String MAX_EVALUE = "1e-05";
	private static final int MIN_COVERAGE = 50;
	private static final int DEFAULT_NEAREST_GENOME_COUNT = 20;
    private static final int MIN_NEAREST_GENOME_COUNT = 1;
	private static final int MAX_NEAREST_GENOME_COUNT = 200;
	private static final String[] genomeWsTypes = {"KBaseGenomes.Genome", 
	    "KBaseGenomeAnnotations.GenomeAnnotation"};
	
	private Map<String, String> genomeKbToRefMap = null;
	private String genomeWsName = null;
	
	public SpeciesTreeBuilder(Map<String, String> configParams, 
	        AuthToken token) throws Exception {
        this(getDirParam(configParams, "scratch"), 
                getDirParam(configParams, "data.dir"), 
                configParams.get("public.genomes.ws"), 
                new URL(configParams.get("workspace-url")),
                token);
	}

	public SpeciesTreeBuilder(File tempDir, File dataDir, String genomeWsName, URL wsUrl, 
	        AuthToken token) throws Exception {
	    this.tempDir = tempDir;
	    if (!this.tempDir.exists())
	        tempDir.mkdirs();
	    this.dataDir = dataDir;
		this.genomeWsName = genomeWsName;
        this.storage = new WorkspaceClient(wsUrl, token);
        storage.setIsInsecureHttpConnectionAllowed(true);
        this.token = token;
        this.wsUrl = wsUrl.toString();
	}

	public static File getDirParam(Map<String, String> configParams, String param) {
	    String tempDirPath = configParams.get(param);
	    if (tempDirPath == null)
	        throw new IllegalStateException("Parameter " + param + " is not defined in " +
	        		"configuration");
	    return new File(tempDirPath);
	}

	public String run(ConstructSpeciesTreeParams inputData) throws Exception {
		System.out.println("====== Running SpeciesTreeBuilder ======");
		if (inputData.getOutGenomesetRef().split("/")[1].equals(inputData.getOutTreeId())) {
			throw new IllegalArgumentException("Output Genome Set and Output Tree must have different names");
		}
		boolean useCog103Only = inputData.getUseRibosomalS9Only() != null && 
				inputData.getUseRibosomalS9Only() == 1L;
		System.out.println(">>> useCog103Only: "+useCog103Only);
		long nearestGenomeCount = inputData.getNearestGenomeCount() != null ? 
				inputData.getNearestGenomeCount() : DEFAULT_NEAREST_GENOME_COUNT;
        boolean copyGenomes = inputData.getCopyGenomes() == null || (inputData.getCopyGenomes() == 1L);

		if (nearestGenomeCount < MIN_NEAREST_GENOME_COUNT)
		    throw new IllegalStateException("Neighbor public genome count can not be less than " +
		            MIN_NEAREST_GENOME_COUNT);
		if (nearestGenomeCount > MAX_NEAREST_GENOME_COUNT)
		    throw new IllegalStateException("Neighbor public genome count can not be more than " +
		            MAX_NEAREST_GENOME_COUNT);
		List<String> genomeRefs = inputData.getNewGenomes();
		if (genomeRefs == null) {
			String genomeSetRef = inputData.getGenomesetRef();
			if (genomeSetRef == null)
				throw new IllegalStateException("Either new_genomes or genomeset_ref field " +
						"should be defined");
			@SuppressWarnings("unchecked")
			Map<String, Object> genomeSet = storage.getObjects2(
			        new GetObjects2Params().withObjects(Arrays.asList(new ObjectSpecification()
			        .withRef(genomeSetRef)))).getData().get(0).getData().asClassInstance(
			                Map.class);
			@SuppressWarnings("unchecked")
			Map<String, Object> genomeSetElements = (Map<String, Object>)genomeSet.get("elements");
			genomeRefs = new ArrayList<String>();
		    for (String key : genomeSetElements.keySet()) {
				@SuppressWarnings("unchecked")
		    	Map<String, Object> genomeSetElem = (Map<String, Object>)genomeSetElements.get(
		    	        key);
		    	genomeRefs.add((String)genomeSetElem.get("ref"));
		    }
		}
		Tree tree = placeUserGenomes(inputData.getOutWorkspace(),
                                     genomeRefs,
                                     useCog103Only,
                                     false,
                                     copyGenomes,
                                     (int)nearestGenomeCount);
		System.out.println(">>> Built species tree ...");
        String treeId = inputData.getOutTreeId();
        if (treeId == null)
            treeId = "tree" + System.currentTimeMillis();
		String treeRef = saveResult(inputData.getOutWorkspace(), treeId, tree, inputData);
        String outGenomesetRef = inputData.getOutGenomesetRef();
        GenomeSetBuilder.buildGenomeSetFromTree
            (wsUrl,token,treeRef,outGenomesetRef,copyGenomes);
		System.out.println(">>> Built genome set from tree ...");
        return treeRef;
	}
	
	private String saveResult(String ws, String id, Tree res,
			ConstructSpeciesTreeParams inputData) throws Exception {
	    URL callbackUrl = new URL(System.getenv("SDK_CALLBACK_URL"));
        DataFileUtilClient dfu = new DataFileUtilClient(callbackUrl, token);
        dfu.setIsInsecureHttpConnectionAllowed(true);
		Map<String, String> meta = new LinkedHashMap<String, String>();
		meta.put("type", SPECIES_TREE_TYPE);
		Long wsId = dfu.wsNameToId(ws);
		return WorkspaceUtil.getRefFromObjectInfo(dfu.saveObjects(
                new datafileutil.SaveObjectsParams().withId(wsId)
                .withObjects(Arrays.asList(new datafileutil.ObjectSaveData()
                .withType("KBaseTrees.Tree").withName(id)
                .withData(new UObject(res)).withMeta(meta)))).get(0));
	}

	
	public String makeTreeForBasicCogs(boolean useCog103Only) throws Exception {
		return makeTree(concatCogAlignments(useCog103Only).toMap());
	}
	
	public String makeTree(Map<String, String> aln) throws Exception {
		File tempFile = File.createTempFile("aln", ".faa", tempDir);
		try {
			FastaWriter fw = new FastaWriter(tempFile);
			for (Map.Entry<String, String> entry : aln.entrySet())
				fw.write(entry.getKey(), entry.getValue());
			fw.close();
			return runFastTree(tempFile);
		} finally {
			try { tempFile.delete(); } catch (Exception ignore) {}
		}
	}
	
	private File getBinDir() {
	    return new File("bin");
	}

	private String getOsSuffix() {
        String osName = System.getProperty("os.name").toLowerCase();
        String suffix;
        if (osName.contains("linux")) {
            suffix = "linux";
        } else if (osName.contains("mac os x")) {
            suffix = "macosx";
        } else {
            throw new IllegalStateException("Unsupported OS type: " + osName);
        }
        return suffix;
	}
	
	private File getFastTreeBin() {
		return new File(getBinDir(), "FastTree." + getOsSuffix());
	}

	private File getFormatRpsDbBin() {
		return new File(getBinDir(), "makeprofiledb." + getOsSuffix());
	}

	private File getRpsBlastBin() {
		return new File(getBinDir(), "rpsblast." + getOsSuffix());
	}

	private String runFastTree(File input) throws Exception {
		CorrectProcess cp = null;
		ByteArrayOutputStream errBaos = null;
		Exception err = null;
		String binPath = getFastTreeBin().getAbsolutePath();
		int procExitValue = -1;
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		try {
			Process p = Runtime.getRuntime().exec(CorrectProcess.arr(binPath,
					"-fastest", input.getAbsolutePath()));
			errBaos = new ByteArrayOutputStream();
			cp = new CorrectProcess(p, result, "", errBaos, "");
			cp.waitFor();
			errBaos.close();
			procExitValue = p.exitValue();
		} catch(Exception ex) {
			try{ 
				errBaos.close(); 
			} catch (Exception ignore) {}
			try{ 
				if(cp!=null) 
					cp.destroy(); 
			} catch (Exception ignore) {}
			err = ex;
		}
		if (errBaos != null || result.size() == 0) {
			String err_text = new String(errBaos.toByteArray());
			if (err_text.length() > 0)
				err = new Exception("FastTree: " + err_text, err);
		}
		if (procExitValue != 0) {
			if (err == null)
				err = new IllegalStateException("FastTree exit code: " + procExitValue);
			throw err;
		}
		return new String(result.toByteArray(), Charset.forName("UTF-8")).trim();
	}
	
	public File getCogsDir() {
		return new File(dataDir, "cogs");
	}
	
	public List<String> loadCogsCodes(boolean useCog103Only) throws IOException {
		if (useCog103Only)
			return Arrays.asList("103");
		File inputList = new File(getCogsDir(), "cog_list.txt");
		List<String> cogCodes = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(inputList));
		while (true) {
			String l = br.readLine();
			if (l == null)
				break;
			if (l.trim().length() == 0)
				continue;
			cogCodes.add(l.trim());
		}
		br.close();
		return cogCodes;
	}
	
	public Map<String, String> loadCogAlignment(String cogCode) throws IOException {
		GZIPInputStream is = new GZIPInputStream(new FileInputStream(
				new File(getCogsDir(), "COG" + cogCode + ".trim.faa.gz")));
		FastaReader fr = new FastaReader(new InputStreamReader(is));
		Map<String, String> aln = fr.readAll();
		fr.close();
		return aln;
	}
	
	public AlignConcat concatCogAlignments(boolean useCog103Only) throws IOException {
		Map<String, Map<String, String>> cogAlignments = 
		        new LinkedHashMap<String, Map<String, String>>();
		for (String cogCode : loadCogsCodes(useCog103Only)) 
			cogAlignments.put(cogCode, loadCogAlignment(cogCode));
		return concatCogAlignments(cogAlignments);
	}
	
	private AlignConcat concatCogAlignments(Map<String, Map<String, String>> alignments) 
	        throws IOException {
		Set<String> commonIdSet = new HashSet<String>();
		for (String cogCode : alignments.keySet()) {
			Map<String, String> aln = alignments.get(cogCode);
			commonIdSet.addAll(aln.keySet());
		}
		List<String> cogCodes = new ArrayList<String>();
		Map<String, List<Integer>> cog2positions = new LinkedHashMap<String, List<Integer>>();
		for (String cogCode : alignments.keySet()) {
		    cogCodes.add(cogCode);
			List<Integer> positions = trimAlignment(alignments.get(cogCode));
			cog2positions.put(cogCode, positions);
		}
		return new AlignConcat(alignments, commonIdSet, cogCodes, cog2positions);
	}

	public File formatRpsDb(List<File> scorematFiles) throws Exception {
        File tempInputFile = File.createTempFile("rps", ".db", tempDir);
        return formatRpsDb(scorematFiles, tempInputFile);
	}

	public File formatRpsDb(List<File> scorematFiles, File dbFile) throws Exception {
		PrintWriter pw = new PrintWriter(dbFile);
		for (File f : scorematFiles) {
			pw.println(f.getAbsolutePath());
		}
		pw.close();
		CorrectProcess cp = null;
		ByteArrayOutputStream errBaos = null;
		Exception err = null;
		String binPath = getFormatRpsDbBin().getAbsolutePath();
		int procExitValue = -1;
		try {
			Process p = Runtime.getRuntime().exec(CorrectProcess.arr(binPath,
					"-in", dbFile.getAbsolutePath(), "-threshold", "9.82", 
					"-scale", "100.0", "-dbtype", "rps", "-index", "true"));
			errBaos = new ByteArrayOutputStream();
			cp = new CorrectProcess(p, null, "formatrpsdb", errBaos, "");
			cp.waitFor();
			errBaos.close();
			procExitValue = p.exitValue();
		} catch(Exception ex) {
			try{ 
				errBaos.close(); 
			} catch (Exception ignore) {}
			try{ 
				if(cp!=null) 
					cp.destroy(); 
			} catch (Exception ignore) {}
			err = ex;
		}
		if (errBaos != null) {
			String err_text = new String(errBaos.toByteArray());
			if (err_text.length() > 0)
				err = new Exception("FastTree: " + err_text, err);
		}
		if (procExitValue != 0) {
			if (err == null)
				err = new IllegalStateException("FastTree exit code: " + procExitValue);
			throw err;
		}
		return dbFile;
	}

	public File runRpsBlast(File dbFile, File fastaQuery) throws Exception {
        File tempOutputFile = File.createTempFile("rps", ".tab", tempDir);
        return runRpsBlast(dbFile, fastaQuery, tempOutputFile);
	}

	public File runRpsBlast(File dbFile, File fastaQuery, File outputFile) throws Exception {
		CorrectProcess cp = null;
		ByteArrayOutputStream errBaos = null;
		Exception err = null;
		String binPath = getRpsBlastBin().getAbsolutePath();
		int procExitValue = -1;
		FileOutputStream fos = new FileOutputStream(outputFile);
		try {
			Process p = Runtime.getRuntime().exec(CorrectProcess.arr(binPath,
					"-db", dbFile.getAbsolutePath(), "-query", fastaQuery.getAbsolutePath(), 
					"-outfmt", "6 qseqid stitle qstart qseq sstart sseq evalue bitscore pident",
					"-evalue", MAX_EVALUE));
			errBaos = new ByteArrayOutputStream();
			cp = new CorrectProcess(p, fos, "", errBaos, "");
			cp.waitFor();
			errBaos.close();
			procExitValue = p.exitValue();
		} catch(Exception ex) {
			try{ 
				errBaos.close(); 
			} catch (Exception ignore) {}
			try{ 
				if(cp!=null) 
					cp.destroy(); 
			} catch (Exception ignore) {}
			err = ex;
		} finally {
			try { fos.close(); } catch (Exception ignore) {}
		}
		if (errBaos != null) {
			String err_text = new String(errBaos.toByteArray());
			if (err_text.length() > 0)
				err = new Exception("FastTree: " + err_text, err);
		}
		if (procExitValue != 0) {
			if (err == null)
				err = new IllegalStateException("FastTree exit code: " + procExitValue);
			throw err;
		}
		return outputFile;
	}
	
	public void processRpsOutput(File results, RpsBlastCallback callback) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(results));
		Pattern tabSep = Pattern.compile(Pattern.quote("\t"));
		try {
			while (true) {
				String l = br.readLine();
				if (l == null)
					break;
				if (l.trim().length() == 0)
					continue;
				String[] parts = tabSep.split(l);
				String subj = parts[1].substring(0, parts[1].indexOf(','));
				callback.next(parts[0], subj, Integer.parseInt(parts[2]), parts[3], 
						Integer.parseInt(parts[4]), parts[5], parts[6], 
						Double.parseDouble(parts[7]), Double.parseDouble(parts[8]));
			}
		} finally {
			br.close();
		}
	}
	
	public void cleanDbFiles(File dbListFile) {
		for (File f : dbListFile.getParentFile().listFiles()) {
			if (f.getName().startsWith(dbListFile.getName()))
				f.delete();
		}
	}

	public List<Integer> trimAlignment(Map<String, String> aln) {
		return trimAlignment(aln, 0.95);
	}

	public static List<Integer> trimAlignment(Map<String, String> aln, double minNonGapPart) {
	    List<String> tmp = new ArrayList<String>();
	    for (String key : aln.keySet())
	        tmp.add(aln.get(key));
	    String[] arr = tmp.toArray(new String[tmp.size()]);
	    List<Integer> posList = new ArrayList<Integer>();
	    for (int pos = 0; pos < arr[0].length(); pos++) {
	        int nonGaps = 0;
	        for (int n = 0; n < arr.length; n++) 
	            if (arr[n].charAt(pos) != '-')
	                nonGaps++;
	        if (nonGaps >= minNonGapPart * arr.length)
	            posList.add(pos);
	    }
	    return posList;
	}

	private List<File> listScoreMatrixFiles(boolean useCog103Only) throws IOException {
		List<File> ret = new ArrayList<File>();
		for (String cog : loadCogsCodes(useCog103Only))
			ret.add(new File(getCogsDir(), "rps.COG" + cog + ".smp"));
		return ret;
	}
	
	public Map<String, String> loadGenomeKbToNames() throws IOException {
		return loadGenomeKbToNames(getCogsDir());
	}
	
	public static Map<String, String> loadGenomeKbToNames(File cogsDir) throws IOException {	
		File genomeNamesFile = new File(cogsDir, "genome_names.txt");
		BufferedReader br = new BufferedReader(new FileReader(genomeNamesFile));
		Map<String, String> ret = new LinkedHashMap<String, String>();
		try {
			while (true) {
				String l = br.readLine();
				if (l == null)
					break;
				if (l.isEmpty())
					continue;
				String[] parts = l.split("\t");
				ret.put(parts[0], parts[1]);
			}
		} finally {
			br.close();
		}
		return ret;
	}
	
	public Map<String, String> loadGenomeKbToRefs() throws Exception {
		if (genomeKbToRefMap != null)
			return genomeKbToRefMap;
		Map<String, String> ret = new TreeMap<String, String>();
		String wsName = genomeWsName;
		int objectCount = (int)(long)storage.getWorkspaceInfo(
		        new WorkspaceIdentity().withWorkspace(wsName)).getE5();
		int partSize = 10000;
		int partCount = (objectCount + partSize - 1) / partSize;
		for (String wsType : genomeWsTypes) {
		    for (int partNum = 0; partNum < partCount; partNum++) {
		        long minObjectID = partNum * partSize;
		        long maxObjectID = (partNum + 1) * partSize - 1;
		        for (Tuple11<Long, String, String, String, Long, String, Long, String, String, 
		                Long, Map<String,String>> info : 
		            storage.listObjects(new ListObjectsParams().withWorkspaces(
		                    Arrays.asList(wsName)).withType(wsType).withMinObjectID(minObjectID)
		                    .withMaxObjectID(maxObjectID))) {
		            String ref = WorkspaceUtil.getRefFromObjectInfo(info);
		            String objectName = info.getE2();
		            ret.put(objectName, ref);
		        }
		    }
		}
		genomeKbToRefMap = ret;
		return ret;
	}

	public Tree placeUserGenomes(String workspace,
                                 List<String> genomeRefList,
                                 boolean useCog103Only, 
                                 boolean userGenomesOnly,
                                 boolean copyGenomes,
                                 int nearestGenomeCount) throws Exception {
		System.out.println(">>> Placing user genomes into alignment...");

	    URL callbackUrl = new URL(System.getenv("SDK_CALLBACK_URL"));
        DataFileUtilClient dfu = new DataFileUtilClient(callbackUrl, token);
        dfu.setIsInsecureHttpConnectionAllowed(true);
		Long wsId = dfu.wsNameToId(workspace);
		System.out.println(">>> Got workspace Id "+wsId+"...");
        WorkspaceClient ws = new WorkspaceClient(new URL(wsUrl), token);
		ws.setIsInsecureHttpConnectionAllowed(true);
		System.out.println(">>> Got workspace client ...");
        
		Map<String, String> idLabelMap = new TreeMap<String, String>();
		Map<String, Map<String, List<String>>> idRefMap = 
		        new TreeMap<String, Map<String, List<String>>>();
		Set<String> seeds = new HashSet<String>();

		AlignConcat alnConcat = placeUserGenomesIntoAlignment(genomeRefList, useCog103Only, 
		        idLabelMap, idRefMap, seeds);
		
		// Filtering
		System.out.println(">>> filtering...");
		Set<String> nearestNodes = new HashSet<String>();
		if (!userGenomesOnly) {
			List<Tuple2<String, Integer>> kbIdToMinDist = sortPublicGenomesByMismatches(
					seeds, alnConcat, false);
			if (kbIdToMinDist.size() > nearestGenomeCount)
				kbIdToMinDist = kbIdToMinDist.subList(0, nearestGenomeCount);
            for (Tuple2<String, Integer> entry : kbIdToMinDist)
                nearestNodes.add(entry.getE1());
		}
		Map<String, String> concat = new TreeMap<String, String>();
		for (String kbId : alnConcat.getGenomeIds())
			if (seeds.contains(kbId) || nearestNodes.contains(kbId))
				concat.put(kbId, alnConcat.getSequence(kbId));
		Map<String, String> kbToNames = loadGenomeKbToNames();
		Map<String, String> kbToRefs = loadGenomeKbToRefs();
		Map<String, Map<String, List<String>>> idKbMap = 
            new TreeMap<String, Map<String, List<String>>>();
        Set<String> namehash = new TreeSet<String>();
		for (String genomeKb : new ArrayList<String>(concat.keySet())) {
			Map<String, List<String>> refMap = new TreeMap<String, List<String>>();
			refMap.put("g", Arrays.asList(genomeKb));
			idKbMap.put(genomeKb, refMap);
			if (seeds.contains(genomeKb))
				continue;
			String ref = kbToRefs.get(genomeKb);
			if (ref == null) {
				System.err.println("[WARNING] SpeciesTreeBuilder: Can't find public genome object " +
                                   "for id: " + genomeKb);
				//concat.remove(genomeKb);
				//continue;
			}
			String name = kbToNames.get(genomeKb);
			idLabelMap.put(genomeKb, name + " (" + genomeKb + ")");
            System.out.println("debug: "+genomeKb+" ref:"+ref+" name:"+name);
            if (ref != null) {
                long refWsId = Long.parseLong(ref.split("/")[0]);
                if (copyGenomes && (!wsId.equals(refWsId))) {
                    // copy it here, make new genome ref
                    String origRef = new String(ref);
                    String newname = idLabelMap.get(genomeKb);
                    newname = GenomeSetBuilder.cleanName(newname);
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
                        ws.copyObject(new CopyObjectParams()
                                      .withTo(new ObjectIdentity()
                                              .withWorkspace(workspace)
                                              .withName(newname))
                                      .withFrom(new ObjectIdentity()
                                                .withRef(origRef)));
                    System.out.println("Genome " + genomeKb + " (ref=" + origRef + ") was copied to " + workspace + "/" + newname);
                    ref = WorkspaceUtil.getRefFromObjectInfo(wsinfo);
                }
            }
			refMap = new TreeMap<String, List<String>>();
			refMap.put("g", ref == null ? Collections.<String>emptyList() : Arrays.asList(ref));
			idRefMap.put(genomeKb, refMap);
		}
		String treeText = makeTree(concat);
		// Rerooting
		System.out.println(">>> Rerooting ...");
        treeText = TreeStructureUtil.rerootTreeToMidpoint(treeText);
		Map<String, String> props = new TreeMap<String, String>();
		props.put("cog_codes", UObject.getMapper().writeValueAsString(loadCogsCodes(
		        useCog103Only)));
		return new Tree().withTree(treeText).withDefaultNodeLabels(idLabelMap)
				.withLeafList(new ArrayList<String>(idLabelMap.keySet()))
				.withWsRefs(idRefMap).withKbRefs(idKbMap)
				.withTreeAttributes(props).withType(SPECIES_TREE_TYPE);
	}

	public List<Tuple2<String, Integer>> sortPublicGenomesByMismatches(
			Set<String> seeds, AlignConcat concat, boolean stopOnZeroDist) {
		List<Tuple2<String, Integer>> kbIdToMinDist = new ArrayList<Tuple2<String, Integer>>();
		Set<String> genomeIds = concat.getGenomeIds();
		for (String kbId : genomeIds) {
			if (seeds.contains(kbId))
				continue;
			char[] kbSeq = concat.getSequence(kbId).toCharArray();
			int minDist = -1;
			for (String userId : genomeIds) {
				if (!seeds.contains(userId))
					continue;
				char[] userSeq = concat.getSequence(userId).toCharArray();
				int dist = getDistance(kbSeq, userSeq);
				minDist = (minDist < 0) ? dist : Math.min(dist, minDist);
				if (stopOnZeroDist && minDist == 0)
					break;
			}
			kbIdToMinDist.add(new Tuple2<String, Integer>().withE1(kbId).withE2(minDist));
			if (stopOnZeroDist && minDist == 0)
				break;
		}
		Collections.sort(kbIdToMinDist, new Comparator<Tuple2<String, Integer>>() {
			@Override
			public int compare(Tuple2<String, Integer> o1, Tuple2<String, Integer> o2) {
				return o1.getE2().compareTo(o2.getE2());
			}
		});
		return kbIdToMinDist;
	}

	public AlignConcat placeUserGenomesIntoAlignment(List<String> genomeRefList, 
	        boolean useCog103Only, Map<String, String> idLabelMap,
			Map<String, Map<String, List<String>>> idRefMap, Set<String> seeds) throws Exception {
		Map<String, Map<String, String>> cogAlignments = 
		        new LinkedHashMap<String, Map<String, String>>();
		Map<String, Integer> cogToAlnLength = new LinkedHashMap<String, Integer>();
		for (String cogCode : loadCogsCodes(useCog103Only)) {
			try {
				System.out.println(">>> loading cog " + cogCode + "...");
				Map<String, String> alignment = loadCogAlignment(cogCode);
				cogAlignments.put(cogCode, alignment);
				int alnLength = alignment.get(alignment.keySet().iterator().next()).length();
				cogToAlnLength.put(cogCode, alnLength);
				System.out.println(">>> loaded cog " + cogCode + "...");
			} catch (Exception ex) {
				System.out.println("Error: Exception thrown while loading cogs: " +ex.getMessage());
				ex.printStackTrace();
			}
		}
		System.out.println(">>> Cog codes loaded ...");
        List<ObjectSpecification> objectids = new ArrayList<ObjectSpecification>();
        for (String genomeRef : genomeRefList)
            objectids.add(new ObjectSpecification().withRef(genomeRef));
        Map<String, String> genomeRefToObjName = new TreeMap<String, String>();
        List<Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>>> infos = 
                storage.getObjectInfoNew(new GetObjectInfoNewParams().withObjects(objectids).withIncludeMetadata(0L));
        for (Tuple11<Long, String, String, String, Long, String, Long, String, String, Long, Map<String,String>> info : infos) {
            genomeRefToObjName.put(WorkspaceUtil.getRefFromObjectInfo(info), info.getE2());
        }
        List<GenomeToCogsAlignment> userData = new ArrayList<GenomeToCogsAlignment>();
		for (String genomeRef : genomeRefToObjName.keySet()) {
			try {
	            objectids.add(new ObjectSpecification().withRef(genomeRef));
				userData.add(alignGenomeProteins(genomeRef, useCog103Only, cogToAlnLength));
			} catch (Exception ex) {
				throw new IllegalStateException("Error processing genome [" + genomeRef + "] " +
				        "(" + ex.getMessage() + ")", ex);
			}
		}
		System.out.println(">>> Genome proteins aligned ...");
		for (String cogCode : cogAlignments.keySet()) {
			for (int genomePos = 0; genomePos < userData.size(); genomePos++) {
				GenomeToCogsAlignment genomeRes = userData.get(genomePos);
				List<ProteinToCogAlignemt> alns = genomeRes.getCogToProteins().get(cogCode);
				if (alns == null || alns.isEmpty())
					continue;
				String alignedSeq = alns.get(0).getTrimmedFeatureSeq();
				String genomeRef = genomeRes.getGenomeRef();
				String nodeName = "user" + (genomePos + 1);
				cogAlignments.get(cogCode).put(nodeName, alignedSeq);
				seeds.add(nodeName);
				if (!idLabelMap.containsKey(nodeName)) {
					idLabelMap.put(nodeName, genomeRes.getGenomeName() + " (" + 
					        genomeRefToObjName.get(genomeRef) + ")");
					Map<String, List<String>> refMap = new TreeMap<String, List<String>>();
					refMap.put("g", Arrays.<String>asList(genomeRef));
					idRefMap.put(nodeName, refMap);
				}
			}
		}
		System.out.println(">>> Concatenated cog alignments ...");
		return concatCogAlignments(cogAlignments);
	}
	
	private int getDistance(char[] s1, char[] s2) {
		int ret = 0;
		int len = s1.length;
		if (len != s2.length)
			throw new IllegalStateException();
		for (int i = 0; i < len; i++)
			if (s1[i] != s2[i])
				ret++;
		return ret;
	}
	
	public GenomeToCogsAlignment alignGenomeProteins(String genomeRef, 
			boolean useCog103Only, final Map<String, Integer> cogToAlnLength) 
			        throws Exception {
        URL callbackUrl = new URL(System.getenv("SDK_CALLBACK_URL"));
        GenomeAnnotationAPIClient gaapi = new GenomeAnnotationAPIClient(callbackUrl, token);
        gaapi.setIsInsecureHttpConnectionAllowed(true);
        Genome genome = gaapi.getGenomeV1(
                new GetGenomeParamsV1().withGenomes(
                        Arrays.asList(new GenomeSelectorV1().withRef(genomeRef)))
                        .withIncludedFeatureFields(Arrays.asList("id", "location",
                                "protein_translation", "type"))).getGenomes().get(0).getData();
        String genomeName = genome.getScientificName();
        if (genomeName == null)
            genomeName = "Genome " + genomeRef;
        final List<Feature> cdss = genome.getFeatures();
		File fastaFile = File.createTempFile("proteome", ".fasta", tempDir);
		File dbFile = null;
		File tabFile = null;
		try {
			FastaWriter fw = new FastaWriter(fastaFile);
			int protCount = 0;
			try {
				for (int pos = 0; pos < cdss.size(); pos++) {
				    Feature cds = cdss.get(pos);
					String seq = cds.getProteinTranslation();
					if (seq == null || seq.isEmpty())
						continue;
					fw.write("" + pos, seq);
					protCount++;
				}
			} finally {
				try { fw.close(); } catch (Exception ignore) {}
			}
			if (protCount == 0)
				throw new IllegalStateException("No protein translations");
			dbFile = formatRpsDb(listScoreMatrixFiles(useCog103Only));
			tabFile = runRpsBlast(dbFile, fastaFile);
			final Map<String, List<ProteinToCogAlignemt>> cog2proteins = 
					new LinkedHashMap<String, List<ProteinToCogAlignemt>>();
			processRpsOutput(tabFile, new SpeciesTreeBuilder.RpsBlastCallback() {
				@Override
				public void next(String query, String subject, int qstart, String qseq,
						int sstart, String sseq, String evalue, double bitscore,
						double ident) throws Exception {
					if (!subject.startsWith("COG"))
						throw new IllegalStateException("Unexpected subject name in prs blast " +
								"result: " + subject);
					String cogCode = "" + Integer.parseInt(subject.substring(3));
					int alnLen = cogToAlnLength.get(cogCode);
					String alignedSeq = AlignUtil.removeGapsFromSubject(alnLen, qseq, sstart - 1, 
					        sseq);
					int coverage = 100 - AlignUtil.getGapPercent(alignedSeq);
					if (coverage < MIN_COVERAGE)
						return;
					List<ProteinToCogAlignemt> protList = cog2proteins.get(cogCode);
					if (protList == null) {
						protList = new ArrayList<ProteinToCogAlignemt>();
						cog2proteins.put(cogCode, protList);
					}
					ProteinToCogAlignemt result = new ProteinToCogAlignemt();
					result.setTrimmedFeatureSeq(alignedSeq);
					result.setBitscore(bitscore);
					result.setCogCode(cogCode);
					result.setAlignedCogConsensus(sseq);
					result.setCoverage(coverage);
					result.setEvalue(Double.parseDouble(evalue));
					int featurePos = Integer.parseInt(query);
					result.setFeatureId(cdss.get(featurePos).getId());
					result.setAlignedFeatureSeq(qseq);
					result.setIdentity(ident);
					protList.add(result);
				}
			});
			if (cog2proteins.isEmpty())
				throw new IllegalStateException("Not one protein family member found");
			for (List<ProteinToCogAlignemt> results : cog2proteins.values())
				if (results.size() > 1)
					Collections.sort(results, new Comparator<ProteinToCogAlignemt>() {
						@Override
						public int compare(ProteinToCogAlignemt o1, ProteinToCogAlignemt o2) {
							return Double.valueOf(o1.getEvalue()).compareTo(o2.getEvalue());
						}
					});
			GenomeToCogsAlignment ret = new GenomeToCogsAlignment();
			ret.setGenomeRef(genomeRef);
			ret.setGenomeName(genomeName);
			ret.setCogToProteins(cog2proteins);
			return ret;
		} finally {
			try { fastaFile.delete(); } catch (Exception ignore) {}
			if (dbFile != null)
				try { cleanDbFiles(dbFile); } catch (Exception ignore) {}
			if (tabFile != null)
				try { tabFile.delete(); } catch (Exception ignore) {}
		}
	}
	
	public static interface RpsBlastCallback {
		public void next(String query, String subj, int qstart, String qseq, int sstart, 
		        String sseq, String evalue, double bitscore, double ident) throws Exception;
	}
	
	public static class AlignConcat {
	    private Map<String, Map<String, String>> alignments;
	    private Set<String> commonIdSet;
        private List<String> cogCodes;
        private Map<String, List<Integer>> cog2positions;
	    
	    public AlignConcat(Map<String, Map<String, String>> alignments, Set<String> commonIdSet,
	            List<String> cogCodes, Map<String, List<Integer>> cog2positions) {
	        this.alignments = alignments;
	        this.commonIdSet = Collections.unmodifiableSet(commonIdSet);
	        this.cogCodes = cogCodes;
	        this.cog2positions = cog2positions;
	    }

	    public Set<String> getGenomeIds() {
            return commonIdSet;
        }
	    
	    public String getSequence(String genomeId) {
	        StringBuilder ret = new StringBuilder();
	        for (String cogCode : cogCodes) {
	            String part = alignments.get(cogCode).get(genomeId);
	            List<Integer> positions = cog2positions.get(cogCode);
	            for (int pos : positions) {
	                ret.append(part == null ? '-' : part.charAt(pos));
	            }
	        }
	        return ret.toString();
	    }
	    
	    public Map<String, String> toMap() {
	        Map<String, String> ret = new TreeMap<String, String>();
	        for (String genomeId : commonIdSet)
	            ret.put(genomeId, getSequence(genomeId));
	        return ret;
	    }
	}
}
