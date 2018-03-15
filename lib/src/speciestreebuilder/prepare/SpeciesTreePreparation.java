package speciestreebuilder.prepare;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.ini4j.Ini;

import speciestreebuilder.GenomeToCogsAlignment;
import speciestreebuilder.ProteinToCogAlignemt;
import speciestreebuilder.SpeciesTreeBuilder;
import us.kbase.auth.AuthConfig;
import us.kbase.auth.AuthToken;
import us.kbase.auth.ConfigurableAuthService;
import us.kbase.common.service.ServerException;
import us.kbase.common.service.UObject;

/**
 * Multiple alignments of COG-families are done in gene_families module 
 * (us.kbase.kbasegenefamilies.SpeciesTreePrepation). Here is the code
 * constructing default species tree.
 * @author rsutormin
 */
public class SpeciesTreePreparation {
    /** The key for the environment variable or JVM variable with the value of
     * the server config file location.
     */
    public static final String KB_DEP = "KB_DEPLOYMENT_CONFIG";
    private static final boolean prepareCommonSpeciesTree = false;

	public static void main(String[] args) throws Exception {
	    final File deploy = new File("deploy.cfg");
	    final Ini ini = new Ini(deploy);
	    Map<String, String> config = ini.get("SpeciesTreeBuilder");
		File tempDir = new File(config.get("scratch"));
		File dataDir = new File("data");
        String tokenStr = FileUtils.readFileToString(new File("./work/token"));

        URL authUrl = new URL(config.get("auth-service-url"));
        String authAllowInsecure = config.get("auth-service-url-allow-insecure");
        ConfigurableAuthService authSrv = new ConfigurableAuthService(
                new AuthConfig().withKBaseAuthServerURL(authUrl)
                        .withAllowInsecureURLs("true".equals(authAllowInsecure)));
        AuthToken token = authSrv.validateToken(tokenStr);

		URL wsUrl = new URL(config.get("workspace-url"));
        String genomeWsName = config.get("public.genomes.ws");
		SpeciesTreeBuilder stb = new SpeciesTreeBuilder(tempDir, dataDir, genomeWsName, wsUrl, 
		        token);
		if (checkIfAlignmentPreparationNeeded(stb)) {
	        File prepareDir = new File(dataDir, "prepare");
	        prepareDir.mkdirs();
	        prepareGenomeAlignments(prepareDir, stb);
	        combineCogAlignemnts(prepareDir, stb);
	        if (prepareCommonSpeciesTree)
	            prepareFastTree(stb);
		} else {
		    System.out.println("Preparation of COG alignments is skipped");
            if (prepareCommonSpeciesTree) {
                File spTreeFile = new File(stb.getCogsDir(), "species_tree.txt");
                if (spTreeFile.exists()) {
                    System.out.println("Preparation of common species tree is skipped");
                } else {
                    prepareFastTree(stb);
                }
            }
		}
	}
	
	private static boolean checkIfAlignmentPreparationNeeded( 
	        SpeciesTreeBuilder stb) throws Exception {
	    if (!new File(stb.getCogsDir(), "genome_names.txt").exists())
	        return true;
        for (String cogCode : stb.loadCogsCodes(false)) {
            File alnFile = new File(stb.getCogsDir(), "rps.COG" + cogCode + ".smp");
            if (!alnFile.exists())
                return true;
        }
        return false;
	}
	
	private static void prepareGenomeAlignments(File prepareDir,
	        SpeciesTreeBuilder stb) throws Exception {
        File genomeNamesFile = new File(prepareDir, "genome_names.txt");
        if (genomeNamesFile.exists())
            return;
        List<String> cogCodes = stb.loadCogsCodes(false);
        Map<String, Integer> cogToAlnLength = new LinkedHashMap<>();
        for (String cogCode : cogCodes) {
            File smpFile = new File(stb.getCogsDir(), "rps.COG" + cogCode + ".smp");
            String consensus = ScorematParser.searchConsensus(smpFile);
            cogToAlnLength.put(cogCode, consensus.length());
        }
        Map<String, String> genomeObjNameToRef = stb.loadGenomeKbToRefs();
        Set<String> processedGenomeNames = new TreeSet<String>();
        File processedGenomesFile = new File(prepareDir, "genome_names.temp");
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
        for (Map.Entry<String, String> genomeEntry : genomeObjNameToRef.entrySet()) {
            String genomeObjName = genomeEntry.getKey();
            String genomeRef = genomeEntry.getValue();
            try {
                long time = System.currentTimeMillis();
                if (processedGenomeNames.contains(genomeObjName)) {
                    continue;
                }
                GenomeToCogsAlignment alignment = stb.alignGenomeProteins(genomeRef, false, 
                        cogToAlnLength);
                File alnFile = new File(prepareDir, genomeObjName + ".alignments.json");
                UObject.getMapper().writeValue(alnFile, alignment);
                String genomeScientificName = alignment.getGenomeName();
                putGenomeIntoProcessedList(processedGenomesFile, genomeObjName, 
                        genomeScientificName);
                System.out.println("Genome [" + genomeObjName + "], cogs_found=" + 
                        alignment.getCogToProteins().size() + ", " +
                        "time=" + (System.currentTimeMillis() - time));     
                genomeCount++;
                if (genomeCount % 100 == 0) {
                    System.out.println("Info: " + genomeCount + " genomes were processed in " + 
                (System.currentTimeMillis() - timeCommon) + 
                            " ms (average=" + ((System.currentTimeMillis() - timeCommon) / 
                                    genomeCount) + ")");
                }
            } catch (ServerException ex) {
                System.err.println("Error processing genome " + genomeObjName + 
                        " (" + genomeRef + ")");
                System.err.println(ex.getData());
            } catch (Throwable ex) {
                System.err.println("Error processing genome " + genomeObjName + 
                        " (" + genomeRef + ")");
                ex.printStackTrace();
            }
        }
        FileUtils.copyFile(processedGenomesFile, genomeNamesFile);
	}
	
	private static void combineCogAlignemnts(File prepareDir, 
	        SpeciesTreeBuilder stb) throws Exception {
        List<String> cogCodes = stb.loadCogsCodes(false);
        FileCache fileCache = new FileCache();
        File genomeNamesFile = new File(prepareDir, "genome_names.txt");
        BufferedReader br = new BufferedReader(new FileReader(genomeNamesFile));
        int genomesProcessed = 0;
        while (true) {
            String l = br.readLine();
            if (l == null)
                break;
            if (l.isEmpty())
                continue;
            String genomeKbId = l.split("\t")[0];
            File genomeAlnFile = new File(prepareDir, genomeKbId + ".alignments.json");
            GenomeToCogsAlignment alignment = UObject.getMapper().readValue(genomeAlnFile, 
                    GenomeToCogsAlignment.class);
            for (String cogCode : alignment.getCogToProteins().keySet()) {
                ProteinToCogAlignemt alnLine = alignment.getCogToProteins().get(cogCode).get(0);
                String sequence = alnLine.getTrimmedFeatureSeq();
                File cogAlnFile = new File(prepareDir, "COG" + cogCode + ".trim.faa");
                fileCache.addLine(cogAlnFile, ">" + genomeKbId);
                fileCache.addLine(cogAlnFile, sequence);
            }
            genomesProcessed++;
            if (genomesProcessed % 50 == 0)
                fileCache.flush();
        }
        fileCache.flush();
        br.close();
        for (String cogCode : cogCodes) {
            File outputFile = new File(stb.getCogsDir(), "COG" + cogCode + ".trim.faa.gz");
            if (outputFile.exists())
                continue;
            File inputFile = new File(prepareDir, "COG" + cogCode + ".trim.faa");
            InputStream is = new FileInputStream(inputFile);
            OutputStream os = new GZIPOutputStream(new FileOutputStream(outputFile));
            IOUtils.copy(is, os);
            is.close();
            os.close();
            inputFile.delete();
        }
        FileUtils.copyFile(genomeNamesFile, new File(stb.getCogsDir(), 
                "genome_names.txt"));
	}
	
	private static void prepareFastTree(SpeciesTreeBuilder stb) throws Exception {
		Map<String, String> aln = stb.concatCogAlignments(false).toMap();
		System.out.println("Genomes in common alignment: " + aln.size());
		System.out.println("Sequence length in common alignment: " + aln.get(aln.keySet().iterator().next()).length());
		String tree = stb.makeTree(aln);
		PrintWriter pw2 = new PrintWriter(new File(stb.getCogsDir(), "species_tree.txt"));
		pw2.print(tree);
		pw2.close();
	}

    private static void putGenomeIntoProcessedList(File processedGenomesFile,
            String genomeKbName, String genomeSciName) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(processedGenomesFile, true));
        pw.println(genomeKbName + "\t" + genomeSciName);
        pw.close();
    }
    
    private static class FileCache {
        Map<File, List<String>> file2lines = new LinkedHashMap<File, List<String>>();

        public void addLine(File f, String l) {
            List<String> lines = file2lines.get(f);
            if (lines == null) {
                lines = new ArrayList<String>();
                file2lines.put(f, lines);
            }
            lines.add(l);
        }

        public void flush() throws IOException {
            for (File f : file2lines.keySet()) {
                List<String> lines = file2lines.get(f);
                if (lines.isEmpty())
                    continue;
                PrintWriter pw = new PrintWriter(new FileWriter(f, true));
                try {
                    for (String l : lines)
                        pw.println(l);
                    file2lines.get(f).clear();
                } finally {
                    pw.close();
                }
            }
        }
    }
}
