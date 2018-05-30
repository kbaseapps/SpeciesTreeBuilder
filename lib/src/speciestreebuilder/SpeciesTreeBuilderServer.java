package speciestreebuilder;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import us.kbase.auth.AuthToken;
import us.kbase.common.service.JsonServerMethod;
import us.kbase.common.service.JsonServerServlet;
import us.kbase.common.service.JsonServerSyslog;
import us.kbase.common.service.RpcContext;

//BEGIN_HEADER
//END_HEADER

/**
 * <p>Original spec-file module name: SpeciesTreeBuilder</p>
 * <pre>
 * A KBase module: SpeciesTreeBuilder
 * </pre>
 */
public class SpeciesTreeBuilderServer extends JsonServerServlet {
    private static final long serialVersionUID = 1L;
    private static final String version = "1.0.0";
    private static final String gitUrl = "git@github.com:jmchandonia/SpeciesTreeBuilder.git";
    private static final String gitCommitHash = "89b78ba87ceb2b29e7850d6ff1c469c69f043852";

    //BEGIN_CLASS_HEADER
    public Map<String, String> getConfig() throws Exception {
        return config;
    }
    //END_CLASS_HEADER

    public SpeciesTreeBuilderServer() throws Exception {
        super("SpeciesTreeBuilder");
        //BEGIN_CONSTRUCTOR
        //END_CONSTRUCTOR
    }

    /**
     * <p>Original spec-file function name: construct_species_tree</p>
     * <pre>
     * Build a species tree out of a set of given genome references.
     * </pre>
     * @param   input   instance of type {@link speciestreebuilder.ConstructSpeciesTreeParams ConstructSpeciesTreeParams}
     * @return   parameter "tree_ref" of original type "ws_tree_id" (A workspace ID that references a Tree data object. @id ws KBaseTrees.Tree)
     */
    @JsonServerMethod(rpc = "SpeciesTreeBuilder.construct_species_tree", async=true)
    public String constructSpeciesTree(ConstructSpeciesTreeParams input, AuthToken authPart, RpcContext jsonRpcContext) throws Exception {
        String returnVal = null;
        //BEGIN construct_species_tree
        SpeciesTreeBuilder runner = new SpeciesTreeBuilder(config, authPart);
        returnVal = runner.run(input);
        //END construct_species_tree
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: find_close_genomes</p>
     * <pre>
     * Find closely related public genomes based on COG of ribosomal s9 subunits.
     * </pre>
     * @param   params   instance of type {@link speciestreebuilder.FindCloseGenomesParams FindCloseGenomesParams}
     * @return   instance of list of original type "ws_genome_id" (A workspace ID that references a Genome data object. @id ws KBaseGenomes.Genome KBaseGenomeAnnotations.GenomeAnnotation)
     */
    @JsonServerMethod(rpc = "SpeciesTreeBuilder.find_close_genomes", async=true)
    public List<String> findCloseGenomes(FindCloseGenomesParams params, AuthToken authPart, RpcContext jsonRpcContext) throws Exception {
        List<String> returnVal = null;
        //BEGIN find_close_genomes
        returnVal = new CloseGenomesFinder(config, authPart).findGenomes(params);
        //END find_close_genomes
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: guess_taxonomy_path</p>
     * <pre>
     * Search for taxonomy path from closely related public genomes (approach similar to find_close_genomes).
     * </pre>
     * @param   params   instance of type {@link speciestreebuilder.GuessTaxonomyPathParams GuessTaxonomyPathParams}
     * @return   instance of String
     */
    @JsonServerMethod(rpc = "SpeciesTreeBuilder.guess_taxonomy_path", async=true)
    public String guessTaxonomyPath(GuessTaxonomyPathParams params, AuthToken authPart, RpcContext jsonRpcContext) throws Exception {
        String returnVal = null;
        //BEGIN guess_taxonomy_path
        returnVal = new CloseGenomesFinder(config, authPart).guessTaxonomy(params);
        //END guess_taxonomy_path
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: build_genome_set_from_tree</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link speciestreebuilder.BuildGenomeSetFromTreeParams BuildGenomeSetFromTreeParams}
     * @return   parameter "genomeset_ref" of original type "ws_genomeset_id" (A workspace ID that references a GenomeSet data object. @id ws KBaseSearch.GenomeSet)
     */
    @JsonServerMethod(rpc = "SpeciesTreeBuilder.build_genome_set_from_tree", async=true)
    public String buildGenomeSetFromTree(BuildGenomeSetFromTreeParams params, AuthToken authPart, RpcContext jsonRpcContext) throws Exception {
        String returnVal = null;
        //BEGIN build_genome_set_from_tree
        returnVal = GenomeSetBuilder.buildGenomeSetFromTree(config, authPart, params.getTreeRef(), params.getGenomesetRef(), true);
        //END build_genome_set_from_tree
        return returnVal;
    }

    /**
     * <p>Original spec-file function name: export_tree_newick</p>
     * <pre>
     * </pre>
     * @param   params   instance of type {@link speciestreebuilder.ExportParams ExportParams}
     * @return   parameter "result" of type {@link speciestreebuilder.ExportResult ExportResult}
     */
    @JsonServerMethod(rpc = "SpeciesTreeBuilder.export_tree_newick", async=true)
    public ExportResult exportTreeNewick(ExportParams params, AuthToken authPart, RpcContext jsonRpcContext) throws Exception {
        ExportResult returnVal = null;
        //BEGIN export_tree_newick
        returnVal = SpeciesTreeBuilder.exportTreeNewick(config.get("workspace-url"),config.get("shock-url"),config.get("scratch"),authPart,params);
        //END export_tree_newick
        return returnVal;
    }
    @JsonServerMethod(rpc = "SpeciesTreeBuilder.status")
    public Map<String, Object> status() {
        Map<String, Object> returnVal = null;
        //BEGIN_STATUS
        returnVal = new LinkedHashMap<String, Object>();
        returnVal.put("state", "OK");
        returnVal.put("message", "");
        returnVal.put("version", version);
        returnVal.put("git_url", gitUrl);
        returnVal.put("git_commit_hash", gitCommitHash);
        //END_STATUS
        return returnVal;
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1) {
            new SpeciesTreeBuilderServer().startupServer(Integer.parseInt(args[0]));
        } else if (args.length == 3) {
            JsonServerSyslog.setStaticUseSyslog(false);
            JsonServerSyslog.setStaticMlogFile(args[1] + ".log");
            new SpeciesTreeBuilderServer().processRpcCall(new File(args[0]), new File(args[1]), args[2]);
        } else {
            System.out.println("Usage: <program> <server_port>");
            System.out.println("   or: <program> <context_json_file> <output_json_file> <token>");
            return;
        }
    }
}
