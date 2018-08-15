package speciestreebuilder.test;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ArrayList;
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

import speciestreebuilder.ConstructSpeciesTreeOutput;
import speciestreebuilder.ConstructSpeciesTreeParams;
import speciestreebuilder.SpeciesTreeBuilderServer;
import us.kbase.auth.AuthConfig;
import us.kbase.auth.AuthToken;
import us.kbase.auth.AuthService;
import us.kbase.auth.ConfigurableAuthService;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.RpcContext;
import us.kbase.common.service.UObject;
import us.kbase.kbasetrees.Tree;
import workspace.CreateWorkspaceParams;
import workspace.GetObjects2Params;
import workspace.ObjectData;
import workspace.ObjectSaveData;
import workspace.ObjectSpecification;
import workspace.ProvenanceAction;
import workspace.SaveObjectsParams;
import workspace.WorkspaceClient;
import workspace.WorkspaceIdentity;

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
        String spTreeId = "sptree.1";
        String genomeRef = "ReferenceDataManager/GCF_000010525.1";
        ConstructSpeciesTreeOutput result = impl.constructSpeciesTree(new ConstructSpeciesTreeParams()
                                                   .withNewGenomes(Arrays.asList(genomeRef))
                                                   .withOutWorkspace(getWsName())
                                                   .withOutTreeId(spTreeId)
                                                   .withUseRibosomalS9Only(0L)
                                                   .withNearestGenomeCount(20L)
                                                   .withOutGenomesetRef(getWsName()+"/genomeset.1")
                                                   .withCopyGenomes(1L)
                                                   , token, getContext());
        String treeRef = result.getOutputResultId();
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
        // int genomesFound = impl.findCloseGenomes(
        //        new FindCloseGenomesParams().withQueryGenome(genomeRef), token, getContext()).size();
        // Assert.assertEquals(4, genomesFound);
        // String taxPath = impl.guessTaxonomyPath(
        //        new GuessTaxonomyPathParams().withQueryGenome(genomeRef), token, getContext());
        // Assert.assertEquals("Shewanella; baltica;", taxPath);
        ///////////////////////////////////////////////////////////////////////////////////////////
    }

    @Test(expected=IllegalArgumentException.class)
    public void testNoSameName() throws Exception {
        String spTreeId = "sptree.1";
        String genomeRef = "ReferenceDataManager/GCF_000010525.1";
        String genomeSetRef = getWsName() + "/" + spTreeId;
        ConstructSpeciesTreeOutput result = impl.constructSpeciesTree(new ConstructSpeciesTreeParams()
                        .withNewGenomes(Arrays.asList(genomeRef))
                        .withOutWorkspace(getWsName())
                        .withOutTreeId(spTreeId)
                        .withUseRibosomalS9Only(0L)
                        .withNearestGenomeCount(20L)
                        .withOutGenomesetRef(genomeSetRef)
                        .withCopyGenomes(1L)
                , token, getContext());
    }
}
