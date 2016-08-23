
package speciestreebuilder;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: FindCloseGenomesParams</p>
 * <pre>
 * Input data type for find_close_genomes method. Method produces list of refereces to public genomes similar to query.
 * query_genome - (required) query genome reference
 * max_mismatch_percent - (optional) defines maximum mismatch percentage when compare aminoacids from user genome with 
 *     public genomes (defualt value is 5).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "query_genome",
    "max_mismatch_percent"
})
public class FindCloseGenomesParams {

    @JsonProperty("query_genome")
    private String queryGenome;
    @JsonProperty("max_mismatch_percent")
    private Long maxMismatchPercent;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("query_genome")
    public String getQueryGenome() {
        return queryGenome;
    }

    @JsonProperty("query_genome")
    public void setQueryGenome(String queryGenome) {
        this.queryGenome = queryGenome;
    }

    public FindCloseGenomesParams withQueryGenome(String queryGenome) {
        this.queryGenome = queryGenome;
        return this;
    }

    @JsonProperty("max_mismatch_percent")
    public Long getMaxMismatchPercent() {
        return maxMismatchPercent;
    }

    @JsonProperty("max_mismatch_percent")
    public void setMaxMismatchPercent(Long maxMismatchPercent) {
        this.maxMismatchPercent = maxMismatchPercent;
    }

    public FindCloseGenomesParams withMaxMismatchPercent(Long maxMismatchPercent) {
        this.maxMismatchPercent = maxMismatchPercent;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((("FindCloseGenomesParams"+" [queryGenome=")+ queryGenome)+", maxMismatchPercent=")+ maxMismatchPercent)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
