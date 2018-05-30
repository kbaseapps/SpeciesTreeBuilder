
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
 * <p>Original spec-file type: ExportParams</p>
 * <pre>
 * Exporter for trees in Newick format
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "tree_ref"
})
public class ExportParams {

    @JsonProperty("tree_ref")
    private String treeRef;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("tree_ref")
    public String getTreeRef() {
        return treeRef;
    }

    @JsonProperty("tree_ref")
    public void setTreeRef(String treeRef) {
        this.treeRef = treeRef;
    }

    public ExportParams withTreeRef(String treeRef) {
        this.treeRef = treeRef;
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
        return ((((("ExportParams"+" [treeRef=")+ treeRef)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
