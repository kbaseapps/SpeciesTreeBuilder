package speciestreebuilder.test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.lang3.StringUtils;
import org.forester.io.parsers.nhx.NHXParser;
import org.forester.phylogeny.Phylogeny;
import org.ini4j.Ini;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import buildtestgenome.BuildTestGenomeClient;
import buildtestgenome.PrepareTestGenomeFromProteinsParams;

import speciestreebuilder.BuildGenomeSetFromTreeParams;
import speciestreebuilder.ConstructSpeciesTreeParams;
import speciestreebuilder.FindCloseGenomesParams;
import speciestreebuilder.GuessTaxonomyPathParams;
import speciestreebuilder.SpeciesTreeBuilderServer;
import us.kbase.auth.AuthConfig;
import us.kbase.auth.AuthToken;
import us.kbase.auth.AuthService;
import us.kbase.auth.ConfigurableAuthService;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.RpcContext;
import us.kbase.common.service.UObject;
import us.kbase.common.utils.FastaReader;
import us.kbase.kbasetrees.Tree;
import us.kbase.workspace.CreateWorkspaceParams;
import us.kbase.workspace.GetObjects2Params;
import us.kbase.workspace.ObjectData;
import us.kbase.workspace.ObjectSaveData;
import us.kbase.workspace.ObjectSpecification;
import us.kbase.workspace.ProvenanceAction;
import us.kbase.workspace.SaveObjectsParams;
import us.kbase.workspace.WorkspaceClient;
import us.kbase.workspace.WorkspaceIdentity;

public class SpeciesTreeBuilderServerTest {
    private static AuthToken token = null;
    private static Map<String, String> config = null;
    private static WorkspaceClient wsClient = null;
    private static String wsName = null;
    private static String publicWsName = null;
    private static SpeciesTreeBuilderServer impl = null;
    
    @BeforeClass
    public static void init() throws Exception {
        String configFilePath = System.getenv("KB_DEPLOYMENT_CONFIG");
        File deploy = new File(configFilePath);
        Ini ini = new Ini(deploy);
        config = ini.get("SpeciesTreeBuilder");

        // Token validation
        String authUrl = config.get("auth-service-url");
        String authUrlInsecure = config.get("auth-service-url-allow-insecure");
        ConfigurableAuthService authService = new ConfigurableAuthService(
                new AuthConfig().withKBaseAuthServerURL(new URL(authUrl))
                .withAllowInsecureURLs("true".equals(authUrlInsecure)));
        token = authService.validateToken(System.getenv("KB_AUTH_TOKEN"));
        wsClient = new WorkspaceClient(new URL(config.get("workspace-url")), token);
        wsClient.setIsInsecureHttpConnectionAllowed(true);

        // These lines are necessary because we don't want to start linux syslog bridge service
        JsonServerSyslog.setStaticUseSyslog(false);
        JsonServerSyslog.setStaticMlogFile(new File(config.get("scratch"), "test.log").getAbsolutePath());
        impl = new SpeciesTreeBuilderServer();
        impl.getConfig().put("public.genomes.ws", getPublicWsName());
        /*
        // Store test genome (real data)
        FastaReader fr = new FastaReader(new File("test/data", "Shewanella_ANA_3_uid58347.fasta"));
        Map<String, String> proteinIdToSeq = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : fr.readAll().entrySet())
            proteinIdToSeq.put(entry.getKey(), entry.getValue());
        String genomeName = "Shewanella_ANA_3_uid58347";
        String genomeObjId = genomeName + ".genome";
        BuildTestGenomeClient btg = new BuildTestGenomeClient(new URL(System.getenv("SDK_CALLBACK_URL")), token);
        btg.setIsInsecureHttpConnectionAllowed(true);
        btg.prepareTestGenomeFromProteins(
                new PrepareTestGenomeFromProteinsParams().withGenomeName(genomeName)
                .withProteinIdToSequence(proteinIdToSeq)
                .withOutputWorkspaceName(getWsName()).withOutputObjectName(genomeObjId));

        // Sync genome objects (just fake wrappers with kbase-id as name)
        List<ObjectSaveData> taxonObjects = new ArrayList<ObjectSaveData>();
        Map<String, Object> taxon = new LinkedHashMap<String, Object>(4);
        taxon.put("taxonomy_id", 1);
        taxon.put("scientific_name", "xxx");
        taxon.put("domain", "xxx");
        taxonObjects.add(new ObjectSaveData().withName("fakeTaxon").withType("KBaseGenomeAnnotations.Taxon").withData(new UObject(taxon)));
        wsClient.saveObjects(new SaveObjectsParams().withWorkspace(getWsName()).withObjects(taxonObjects));

        String genomeWsType = "KBaseGenomes.Genome";
        List<ObjectSaveData> objects = new ArrayList<ObjectSaveData>();
        Map<String, String> genomeKbIdToName = new LinkedHashMap<>();
        genomeKbIdToName.put("GCF_000146165.2", "Shewanella oneidensis MR-1");
        genomeKbIdToName.put("GCF_000016585.1", "Shewanella putrefaciens CN-32");
        genomeKbIdToName.put("GCF_000015185.1", "Shewanella sp. W3-18-1");
        genomeKbIdToName.put("GCF_000018765.1", "Shewanella baltica OS195");
        genomeKbIdToName.put("GCF_000017325.1", "Shewanella baltica OS185");
        genomeKbIdToName.put("GCF_000021665.1", "Shewanella baltica OS223");
        genomeKbIdToName.put("GCF_000014685.1", "Shewanella sp. MR-4");
        genomeKbIdToName.put("GCF_000014665.1", "Shewanella sp. MR-7");
        genomeKbIdToName.put("GCF_000215895.1", "Shewanella baltica OS117");
        genomeKbIdToName.put("GCF_000178875.2", "Shewanella baltica OS678");
        genomeKbIdToName.put("GCF_000203935.1", "Shewanella sp. ANA-3");
        genomeKbIdToName.put("GCF_000282755.1", "Shewanella sp. POL2");
        genomeKbIdToName.put("GCF_000169215.2", "Shewanella putrefaciens 200");
        genomeKbIdToName.put("GCF_000015845.1", "Shewanella baltica OS155");
        genomeKbIdToName.put("GCF_000231345.1", "Shewanella baltica OS625");
        genomeKbIdToName.put("GCF_000485795.1", "Shewanella decolorationis S1201");
        // Skipping next genome just to check we can handle lost references
        genomeKbIdToName.put("GCF_000712635.2", "Shewanella xiamenensis");
        genomeKbIdToName.put("GCF_001723195.1", "Shewanella xiamenensis");
        genomeKbIdToName.put("GCF_002074855.1", "Shewanella xiamenensis");
        // And several genomes for 
        genomeKbIdToName.put("GCF_000519065.1", "Shewanella putrefaciens HRCR-6");
        genomeKbIdToName.put("GCF_002157365.1", "Shewanella putrefaciens");
        genomeKbIdToName.put("GCF_001591325.1", "Shewanella putrefaciens JCM 20190 = NBRC 3908");
        genomeKbIdToName.put("GCF_001308045.1", "Shewanella sp. Sh95");
        genomeKbIdToName.put("GCF_000217915.1", "Shewanella sp. HN-41");
        genomeKbIdToName.put("GCF_000798835.1", "Shewanella sp. ZOR0012");
        genomeKbIdToName.put("GCF_001401775.1", "Shewanella sp. P1-14-1");
        genomeKbIdToName.put("GCF_000147735.2", "Shewanella baltica BA175");
        genomeKbIdToName.put("GCF_000013765.1", "Shewanella baltica");
        for (String kbId : genomeKbIdToName.keySet()) {
            Map<String, Object> data = new LinkedHashMap<String, Object>(4);
            data.put("id", kbId);
            String name = genomeKbIdToName.get(kbId);
            data.put("scientific_name", name);
            data.put("domain", "Bacteria");
            data.put("genetic_code", 11L);
            data.put("taxonomy", StringUtils.join(name.split(" "), "; ") + name);
            data.put("genome_tiers", new ArrayList<String>());
            data.put("dna_size", new Integer(1));
            data.put("num_contigs", new Integer(1));
            data.put("molecule_type", "xxx");
            data.put("source", "{}");
            data.put("md5", "xxx");
            data.put("gc_content", new Float(1.0));
            data.put("features", new ArrayList<String>());
            data.put("cdss", new ArrayList<String>());
            data.put("taxon_ref", getWsName() + "/fakeTaxon" );
            data.put("feature_counts", new HashMap<String, Integer>());
            objects.add(new ObjectSaveData().withName(kbId).withType(genomeWsType).withData(new UObject(data)));
        }
        wsClient.saveObjects(new SaveObjectsParams().withWorkspace(getPublicWsName()).withObjects(objects));*/
    }
    
    private static String getWsName() throws Exception {
        if (wsName == null) {
            long suffix = System.currentTimeMillis();
            wsName = "test_SpeciesTreeBuilder_" + suffix;
            wsClient.createWorkspace(new CreateWorkspaceParams().withWorkspace(wsName));
        }
        return wsName;
    }
    
    private static String getPublicWsName() throws Exception {
        if (publicWsName == null) {
            long suffix = System.currentTimeMillis();
            publicWsName = "test_SpeciesTreeBuilder_" + suffix;
            wsClient.createWorkspace(new CreateWorkspaceParams().withWorkspace(publicWsName));
        }
        return publicWsName;
    }
    
    private static RpcContext getContext() {
        return new RpcContext().withProvenance(Arrays.asList(new ProvenanceAction()
            .withService("SpeciesTreeBuilder").withMethod("please_never_use_it_in_production")
            .withMethodParams(new ArrayList<UObject>())));
    }
    
    @AfterClass
    public static void cleanup() {
        if (wsName != null) {
            try {
                wsClient.deleteWorkspace(new WorkspaceIdentity().withWorkspace(wsName));
                System.out.println("Test workspace was deleted");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (publicWsName != null) {
            try {
                wsClient.deleteWorkspace(new WorkspaceIdentity().withWorkspace(publicWsName));
                System.out.println("Test public workspace was deleted");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
    
    protected static void saveWsObject(String wsName, String type, String objName, Object data) throws Exception {
        wsClient.saveObjects(new SaveObjectsParams().withWorkspace(wsName)
                .withObjects(Arrays.asList(new ObjectSaveData()
                .withType(type).withName(objName).withData(new UObject(data)))));
    }
    
    private static List<String> extractLeafNodeNames(String tree) throws Exception {
        NHXParser parser = new NHXParser();
        parser.setSource(tree);
        List<String> leafNodeNames = new ArrayList<String>();
        for (Phylogeny ph : parser.parse())
            leafNodeNames.addAll(Arrays.asList(ph.getAllExternalNodeNames()));
        return leafNodeNames;
    }

    @Test
    public void testConstructSpeciesTree() throws Exception {
        String genomeName = "Shewanella_ANA_3_uid58347";
        String genomeId = genomeName + ".genome";
        String spTreeId = "sptree.1";
        String genomeRef = "7601/4/1";
        String treeRef = impl.constructSpeciesTree(new ConstructSpeciesTreeParams()
                                                   .withNewGenomes(Arrays.asList(genomeRef))
                                                   .withOutWorkspace(getWsName())
                                                   .withOutTreeId(spTreeId)
                                                   .withUseRibosomalS9Only(0L)
                                                   .withNearestGenomeCount(20L)
                                                   .withOutGenomesetRef(getWsName()+"/genomeset.1")
                                                   .withCopyGenomes(1L)
                                                   , token, getContext());
        ObjectData od = wsClient.getObjects2(new GetObjects2Params().withObjects(Arrays.asList(
                new ObjectSpecification().withRef(treeRef)))).getData().get(0);
        Tree tree = od.getData().asClassInstance(Tree.class);
        try {
            List<String> nodeIds = extractLeafNodeNames(tree.getTree());
            System.out.println("leaf nodeIds: "+nodeIds);
            Assert.assertEquals(21, nodeIds.size());
            for (String nodeId : nodeIds) {
                String label = tree.getDefaultNodeLabels().get(nodeId);
                Assert.assertNotNull(label);
                Map<String, List<String>> refMap = tree.getWsRefs().get(nodeId);
                Assert.assertTrue(refMap.containsKey("g"));
                if(nodeId.equals("GCF_000178875.2")) {
                    List<String> refs = refMap.get("g");
                    Assert.assertEquals(refs.size(), 1);
                }
            }

        } catch (Exception ex) {
            System.err.println(tree.getTree());
            throw ex;
        }
        //////////////////////////// GenomeSet type should be updated /////////////////////////////
        // String genomeSetRef = impl.buildGenomeSetFromTree(
        // new BuildGenomeSetFromTreeParams().withTreeRef(treeRef)
        // .withGenomesetRef(getWsName() + "/genomeset.1"), token, getContext());
        // Assert.assertNotNull(genomeSetRef);
        ///////////////////////////////////////////////////////////////////////////////////////////
        int genomesFound = impl.findCloseGenomes(
                new FindCloseGenomesParams().withQueryGenome(genomeRef), token, getContext()).size();
        Assert.assertEquals(4, genomesFound);
        String taxPath = impl.guessTaxonomyPath(
                new GuessTaxonomyPathParams().withQueryGenome(genomeRef), token, getContext());
        Assert.assertEquals("Shewanella; baltica;", taxPath);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoSameName() throws Exception {
        String genomeName = "Shewanella_ANA_3_uid58347";
        String spTreeId = "sptree.1";
        String genomeRef = getWsName() + "/" + spTreeId;
        String treeRef = impl.constructSpeciesTree(new ConstructSpeciesTreeParams()
                        .withNewGenomes(Arrays.asList(genomeRef))
                        .withOutWorkspace(getWsName())
                        .withOutTreeId(spTreeId)
                        .withUseRibosomalS9Only(0L)
                        .withNearestGenomeCount(20L)
                        .withOutGenomesetRef(genomeRef)
                        .withCopyGenomes(1L)
                , token, getContext());
    }
}
