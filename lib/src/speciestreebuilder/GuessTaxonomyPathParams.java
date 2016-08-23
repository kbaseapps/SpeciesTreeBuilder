
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
 * <p>Original spec-file type: GuessTaxonomyPathParams</p>
 * <pre>
 * Input data type for guess_taxonomy_path method. Method produces taxonomy path string.
 * query_genome - (required) query genome reference
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "query_genome"
})
public class GuessTaxonomyPathParams {

    @JsonProperty("query_genome")
    private String queryGenome;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("query_genome")
    public String getQueryGenome() {
        return queryGenome;
    }

    @JsonProperty("query_genome")
    public void setQueryGenome(String queryGenome) {
        this.queryGenome = queryGenome;
    }

    public GuessTaxonomyPathParams withQueryGenome(String queryGenome) {
        this.queryGenome = queryGenome;
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
        return ((((("GuessTaxonomyPathParams"+" [queryGenome=")+ queryGenome)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
