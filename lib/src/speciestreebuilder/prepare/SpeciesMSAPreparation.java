package speciestreebuilder.prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.ini4j.Ini;

import speciestreebuilder.SpeciesTreeBuilder;
import us.kbase.auth.AuthException;
import us.kbase.auth.AuthService;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.Tuple2;
import us.kbase.common.service.UnauthorizedException;
// import us.kbase.kbasegenefamilies.DomainAlignments;
import us.kbase.kbasegenomes.Genome;
import us.kbase.workspace.ObjectIdentity;
import us.kbase.workspace.WorkspaceClient;

public class SpeciesMSAPreparation {
    private static final String genomeWsName = "KBasePublicGenomesLoad";
    private static final String genomeWsType = "KBaseGenomes.Genome";
    private static final String domainWsName = "KBasePublicGeneDomains";
    private static final String domainModelWsType = "KBaseGeneFamilies.DomainModel";
    private static final String domainAnnotationWsType = "KBaseGeneFamilies.DomainAnnotation";
    //     private static final String domainAlignmentsWsType = "KBaseGeneFamilies.DomainAlignments";

    public static void main(String[] args) throws Exception {
        Map<String, String> testCfg = loadTestCfg();
        File tempDir = new File(testCfg.get("scratch"));
        File dataDir = new File(testCfg.get("data.dir"));
        String user = testCfg.get("test.user1");
        String pwd = testCfg.get("test.pwd1");
        File cogsDir = new File(dataDir, "cogs");
        AuthToken token = AuthService.login(user, pwd).getToken();
        URL wsUrl = new URL(testCfg.get("workspace.srv.url"));
        SpeciesTreeBuilder stb = new SpeciesTreeBuilder(tempDir, dataDir, null, wsUrl, token);
        List<String> cogCodes = stb.loadCogsCodes(false);
        for (String cogCode : cogCodes) {
            File smpFile = new File("rps." + cogCode + ".smp");
        }





        /*File annotDir = new File(tempDir, "annotation");
        WorkspaceClient client = client(props);
        Map<Integer, Tuple2<String, DomainModel>> domains = loadCogModelRefsAndObjects(client, cogCodes);
        List<File> smpFiles = new ArrayList<File>();
        Map<String, Integer> modelRefToCogCode = new HashMap<String, Integer>();
        Map<String, Tuple2<String,String>> modelNameToRefConsensus = new TreeMap<String, Tuple2<String,String>>();
        for (Map.Entry<Integer, Tuple2<String, DomainModel>> entry : domains.entrySet()) {
            String modelRef = entry.getValue().getE1();
            DomainModel model = entry.getValue().getE2();
            // File smpFile = new File(cogsDir, "rps.COG" + entry.getKey() + ".smp");
            // DomainSearchTask.saveModelSmpIntoFile(model, smpFile);
            // smpFiles.add(smpFile);
            modelRefToCogCode.put(modelRef, entry.getKey());
            // modelNameToRefConsensus.put(model.getDomainName(), 
            //			new Tuple2<String,String>().withE1(modelRef).withE2(model.getCddConsensusSeq()));
        }
        File dbFile = File.createTempFile("rps", ".db", tempDir);
        DomainSearchTask searchTask = new DomainSearchTask(tempDir, null);
        searchTask.formatRpsDb(smpFiles, dbFile);
        Map<String, String> genomeRefToName = new TreeMap<String, String>();
        for (Tuple2<String, String> refAndName : Utils.listAllObjectsRefAndName(client, genomeWsName, genomeWsType))
            genomeRefToName.put(refAndName.getE1(), refAndName.getE2());
        Map<String, String> annotNameToRefMap = new TreeMap<String, String>();
        for (Tuple2<String, String> refAndName : Utils.listAllObjectsRefAndName(client, domainWsName, domainAnnotationWsType))
            annotNameToRefMap.put(refAndName.getE2(), refAndName.getE1());
        Map<String, String> alignNameToRefMap = new TreeMap<String, String>();
        for (Tuple2<String, String> refAndName : Utils.listAllObjectsRefAndName(client, domainWsName, domainAlignmentsWsType))
            alignNameToRefMap.put(refAndName.getE2(), refAndName.getE1());
        Set<String> processedGenomeNames = new HashSet<String>();
        File processedGenomesFile = new File(cogsDir, "genome_names.txt");
        if (processedGenomesFile.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(processedGenomesFile));
            while (true) {
                String l = br.readLine();
                if (l == null)
                    break;
                if (l.isEmpty())
                    continue;
                processedGenomeNames.add(l.split("\t")[0]);
            }
            br.close();
        }
        int genomeCount = 0;
        long timeCommon = System.currentTimeMillis();
        for (Map.Entry<String, String> genomeEntry : genomeRefToName.entrySet()) 
            try {
                long time = System.currentTimeMillis();
                String genomeRef = genomeEntry.getKey();
                String genomeObjectName = genomeEntry.getValue();
                if (processedGenomeNames.contains(genomeObjectName)) {
                    //System.out.println("Genome [" + genomeObjectName + "] was already processed: " + genomeRef);
                    continue;
                }
                String genomeAnnotationObjectName = genomeObjectName + ".domains";
                String genomeAlignmentsObjectName = genomeObjectName + ".alignments";
                String annotRef = annotNameToRefMap.get(genomeAnnotationObjectName);
                String alignRef = alignNameToRefMap.get(genomeAlignmentsObjectName);
                File f1 = null;
                File f2 = null;
                if (annotRef != null && alignRef != null) {
                    f1 = new File(annotDir, "domains_" + annotRef.replace('/', '_') + ".json.gz");
                    f2 = new File(annotDir, "alignments_" + alignRef.replace('/', '_') + ".json.gz");
                }
                String genomeDomain = null;
                String genomeScientificName = null;
                for (int i = 0; i < 5; i++)
                    try {
                        Map<String, String >genome = client.getObjectSubset(Arrays.asList(new SubObjectIdentity().withRef(genomeRef)
                                .withIncluded(Arrays.asList("scientific_name", "domain")))).get(0).getData().asInstance();
                        genomeDomain = genome.get("domain");
                        genomeScientificName = genome.get("scientific_name");
                    } catch (Exception e) {
                        System.err.println(genomeRef + " (" + i + "): " + e.getMessage());
                    }
                if (genomeDomain == null || !(genomeDomain.equals("Bacteria") || genomeDomain.equals("Archaea"))) {
                    putGenomeIntoProcessedList(processedGenomesFile, genomeObjectName, genomeScientificName);
                    System.out.println("Genome [" + genomeObjectName + "] is skipped cause domain=[" + genomeDomain + "], " +
                            "time=" + (System.currentTimeMillis() - time));
                    continue;
                }
                DomainAnnotation annot = null;
                DomainAlignments align = null;
                if (f1 != null && f2 != null && f1.exists() && f2.exists()) {
                    annot = DomainClusterPreparation.readJsonFromGZip(f1, DomainAnnotation.class);
                    align = DomainClusterPreparation.readJsonFromGZip(f2, DomainAlignments.class);
                } else {
                    Genome genome = getObject(client, genomeRef, Genome.class);
                    Tuple2<DomainAnnotation, DomainAlignments> result = searchTask.runDomainSearch(
                            genome, genomeRef, dbFile, modelNameToRefConsensus);
                    annot = result.getE1();
                    align = result.getE2();
                    System.out.println("Genome [" + genomeObjectName + "] was scanned by rps-blast, " +
                            "time=" + (System.currentTimeMillis() - time));
                }
                if (!annot.getGenomeRef().equals(genomeRef))
                    throw new IllegalStateException();
                if (!align.getGenomeRef().equals(genomeRef))
                    throw new IllegalStateException();
                Map<Integer, Tuple2<Double, String>> cogCodeToEvalueAndSeq = new TreeMap<Integer, Tuple2<Double, String>>();
                for (Map.Entry<String, List<Tuple5<String, Long, Long, Long, Map<String, List<Tuple5<Long, Long, Double, Double, Double>>>>>> contigElements : annot.getData().entrySet()) {
                    for (Tuple5<String, Long, Long, Long, Map<String, List<Tuple5<Long, Long, Double, Double, Double>>>> element : contigElements.getValue()) {
                        String featureId = element.getE1();
                        if (annot.getFeatureToContigAndIndex().get(featureId) == null) {
                            if (element.getE5().isEmpty())
                                continue;
                            System.err.println("No feature index for [" + featureId + "] but domain map has size=" + element.getE5().size());
                            continue;
                        }
                        for (Map.Entry<String, List<Tuple5<Long, Long, Double, Double, Double>>> domainPlaces : element.getE5().entrySet()) {
                            String domainRef = domainPlaces.getKey();
                            if (!modelRefToCogCode.containsKey(domainRef))
                                continue;
                            int cogCode = modelRefToCogCode.get(domainRef);
                            for (Tuple5<Long, Long, Double, Double, Double> domainPlace : domainPlaces.getValue()) {
                                String alignedSeq = align.getAlignments().get(domainRef).get(featureId).get("" + domainPlace.getE1());
                                if (alignedSeq == null)
                                    throw new IllegalStateException("Can't find ailgnment for feature [" + featureId + "] in file: " + f2);
                                double evalue = domainPlace.getE3();
                                Tuple2<Double, String> evalueAndSeq = cogCodeToEvalueAndSeq.get(cogCode);
                                if (evalueAndSeq == null) {
                                    cogCodeToEvalueAndSeq.put(cogCode, new Tuple2<Double, String>().withE1(evalue).withE2(alignedSeq));
                                } else if (evalue < evalueAndSeq.getE1()) {
                                    evalueAndSeq.setE1(evalue);
                                    evalueAndSeq.setE2(alignedSeq);
                                }
                            }
                        }
                    }
                }
                for (Map.Entry<Integer, Tuple2<Double, String>> cogEntry : cogCodeToEvalueAndSeq.entrySet()) {
                    int cogCode = cogEntry.getKey();
                    File clusterFile = new File(cogsDir, "COG" + cogCode + ".trim.faa");
                    PrintWriter clusterPw = new PrintWriter(new FileWriter(clusterFile, true));
                    clusterPw.println(">" + genomeObjectName);
                    clusterPw.println(cogEntry.getValue().getE2());
                    clusterPw.close();
                }
                putGenomeIntoProcessedList(processedGenomesFile, genomeObjectName, genomeScientificName);
                System.out.println("Genome [" + genomeObjectName + "], cogs_found=" + cogCodeToEvalueAndSeq.size() + ", " +
                        "time=" + (System.currentTimeMillis() - time));		
                genomeCount++;
                if (genomeCount % 100 == 0) {
                    System.out.println("Info: " + genomeCount + " genomes were processed in " + (System.currentTimeMillis() - timeCommon) + 
                            " ms (average=" + ((System.currentTimeMillis() - timeCommon) / genomeCount) + ")");
                }
            } catch (Throwable e) {
                String genomeRef = genomeEntry.getKey();
                String genomeObjectName = genomeEntry.getValue();
                System.err.println("Error processing genome " + genomeObjectName + " (" + genomeRef + ")");
                e.printStackTrace();
            }*/
    }

    private static Set<Integer> loadCogsCodes(File cogListFile) throws IOException {
        File inputList = cogListFile;
        Set<Integer> cogCodes = new TreeSet<Integer>();
        BufferedReader br = new BufferedReader(new FileReader(inputList));
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            if (l.trim().length() == 0)
                continue;
            cogCodes.add(Integer.parseInt(l.trim()));
        }
        br.close();
        return cogCodes;
    }

    private static Map<String, String> loadTestCfg() throws Exception {
        return new Ini(new File("test.cfg")).get("SpeciesTreeBuilder");
    }

}
