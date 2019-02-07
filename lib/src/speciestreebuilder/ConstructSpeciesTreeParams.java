
package speciestreebuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: ConstructSpeciesTreeParams</p>
 * <pre>
 * Input data type for construct_species_tree method. Method produces object of Tree type.
 * new_genomes - (optional) the list of genome references to use in constructing a tree; either
 *     new_genomes or genomeset_ref field should be defined.
 * genomeset_ref - (optional) reference to genomeset object; either new_genomes or genomeset_ref
 *     field should be defined.
 * out_workspace - (required) the workspace to deposit the completed tree
 * out_tree_id - (optional) the name of the newly constructed tree (will be random if not present or null)
 * use_ribosomal_s9_only - (optional) 1 means only one protein family (Ribosomal S9) is used for 
 *     tree construction rather than all 49 improtant families, default value is 0.
 * nearest_genome_count - (optional) defines maximum number of public genomes nearest to
 *     requested genomes that will show in output tree.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "new_genomes",
    "genomeset_ref",
    "out_workspace",
    "out_tree_id",
    "use_ribosomal_s9_only",
    "nearest_genome_count",
    "prioritize_sisters"
})
public class ConstructSpeciesTreeParams {

    @JsonProperty("new_genomes")
    private List<String> newGenomes;
    @JsonProperty("genomeset_ref")
    private java.lang.String genomesetRef;
    @JsonProperty("out_workspace")
    private java.lang.String outWorkspace;
    @JsonProperty("out_tree_id")
    private java.lang.String outTreeId;
    @JsonProperty("use_ribosomal_s9_only")
    private Long useRibosomalS9Only;
    @JsonProperty("nearest_genome_count")
    private Long nearestGenomeCount;
    @JsonProperty("prioritize_sisters")
    private boolean prioritizeSisters;

    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("new_genomes")
    public List<String> getNewGenomes() {
        return newGenomes;
    }

    @JsonProperty("new_genomes")
    public void setNewGenomes(List<String> newGenomes) {
        this.newGenomes = newGenomes;
    }

    public ConstructSpeciesTreeParams withNewGenomes(List<String> newGenomes) {
        this.newGenomes = newGenomes;
        return this;
    }

    @JsonProperty("genomeset_ref")
    public java.lang.String getGenomesetRef() {
        return genomesetRef;
    }

    @JsonProperty("genomeset_ref")
    public void setGenomesetRef(java.lang.String genomesetRef) {
        this.genomesetRef = genomesetRef;
    }

    public ConstructSpeciesTreeParams withGenomesetRef(java.lang.String genomesetRef) {
        this.genomesetRef = genomesetRef;
        return this;
    }

    @JsonProperty("out_workspace")
    public java.lang.String getOutWorkspace() {
        return outWorkspace;
    }

    @JsonProperty("out_workspace")
    public void setOutWorkspace(java.lang.String outWorkspace) {
        this.outWorkspace = outWorkspace;
    }

    public ConstructSpeciesTreeParams withOutWorkspace(java.lang.String outWorkspace) {
        this.outWorkspace = outWorkspace;
        return this;
    }

    @JsonProperty("out_tree_id")
    public java.lang.String getOutTreeId() {
        return outTreeId;
    }

    @JsonProperty("out_tree_id")
    public void setOutTreeId(java.lang.String outTreeId) {
        this.outTreeId = outTreeId;
    }

    public ConstructSpeciesTreeParams withOutTreeId(java.lang.String outTreeId) {
        this.outTreeId = outTreeId;
        return this;
    }

    @JsonProperty("use_ribosomal_s9_only")
    public Long getUseRibosomalS9Only() {
        return useRibosomalS9Only;
    }

    @JsonProperty("use_ribosomal_s9_only")
    public void setUseRibosomalS9Only(Long useRibosomalS9Only) {
        this.useRibosomalS9Only = useRibosomalS9Only;
    }

    public ConstructSpeciesTreeParams withUseRibosomalS9Only(Long useRibosomalS9Only) {
        this.useRibosomalS9Only = useRibosomalS9Only;
        return this;
    }

    @JsonProperty("nearest_genome_count")
    public Long getNearestGenomeCount() {
        return nearestGenomeCount;
    }

    @JsonProperty("nearest_genome_count")
    public void setNearestGenomeCount(Long nearestGenomeCount) {
        this.nearestGenomeCount = nearestGenomeCount;
    }

    @JsonProperty("nearest_genome_count")
    public boolean getPrioritizeSisters() {
        return prioritizeSisters;
    }

    @JsonProperty("nearest_genome_count")
    public void setPrioritizeSisters(boolean prioritizeSisters) {
        this.prioritizeSisters = prioritizeSisters;
    }

    public ConstructSpeciesTreeParams withNearestGenomeCount(Long nearestGenomeCount) {
        this.nearestGenomeCount = nearestGenomeCount;
        return this;
    }

    @JsonAnyGetter
    public Map<java.lang.String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(java.lang.String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public java.lang.String toString() {
        return ((((((((((((((((("ConstructSpeciesTreeParams"+" [newGenomes=")+ newGenomes)+", genomesetRef=")+ genomesetRef)+", outWorkspace=")+ outWorkspace)+", outTreeId=")+ outTreeId)+", useRibosomalS9Only=")+ useRibosomalS9Only)+", nearestGenomeCount=")+ nearestGenomeCount)+", prioritizeSisters=")+ prioritizeSisters)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
