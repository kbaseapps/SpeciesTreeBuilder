
package buildtestgenome;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: PrepareTestGenomeFromProteinsParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "output_workspace_name",
    "output_object_name",
    "genome_name",
    "protein_id_to_sequence"
})
public class PrepareTestGenomeFromProteinsParams {

    @JsonProperty("output_workspace_name")
    private java.lang.String outputWorkspaceName;
    @JsonProperty("output_object_name")
    private java.lang.String outputObjectName;
    @JsonProperty("genome_name")
    private java.lang.String genomeName;
    @JsonProperty("protein_id_to_sequence")
    private Map<String, String> proteinIdToSequence;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("output_workspace_name")
    public java.lang.String getOutputWorkspaceName() {
        return outputWorkspaceName;
    }

    @JsonProperty("output_workspace_name")
    public void setOutputWorkspaceName(java.lang.String outputWorkspaceName) {
        this.outputWorkspaceName = outputWorkspaceName;
    }

    public PrepareTestGenomeFromProteinsParams withOutputWorkspaceName(java.lang.String outputWorkspaceName) {
        this.outputWorkspaceName = outputWorkspaceName;
        return this;
    }

    @JsonProperty("output_object_name")
    public java.lang.String getOutputObjectName() {
        return outputObjectName;
    }

    @JsonProperty("output_object_name")
    public void setOutputObjectName(java.lang.String outputObjectName) {
        this.outputObjectName = outputObjectName;
    }

    public PrepareTestGenomeFromProteinsParams withOutputObjectName(java.lang.String outputObjectName) {
        this.outputObjectName = outputObjectName;
        return this;
    }

    @JsonProperty("genome_name")
    public java.lang.String getGenomeName() {
        return genomeName;
    }

    @JsonProperty("genome_name")
    public void setGenomeName(java.lang.String genomeName) {
        this.genomeName = genomeName;
    }

    public PrepareTestGenomeFromProteinsParams withGenomeName(java.lang.String genomeName) {
        this.genomeName = genomeName;
        return this;
    }

    @JsonProperty("protein_id_to_sequence")
    public Map<String, String> getProteinIdToSequence() {
        return proteinIdToSequence;
    }

    @JsonProperty("protein_id_to_sequence")
    public void setProteinIdToSequence(Map<String, String> proteinIdToSequence) {
        this.proteinIdToSequence = proteinIdToSequence;
    }

    public PrepareTestGenomeFromProteinsParams withProteinIdToSequence(Map<String, String> proteinIdToSequence) {
        this.proteinIdToSequence = proteinIdToSequence;
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
        return ((((((((((("PrepareTestGenomeFromProteinsParams"+" [outputWorkspaceName=")+ outputWorkspaceName)+", outputObjectName=")+ outputObjectName)+", genomeName=")+ genomeName)+", proteinIdToSequence=")+ proteinIdToSequence)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
