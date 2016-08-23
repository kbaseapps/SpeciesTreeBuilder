
package us.kbase.kbasetrees;

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
 * <p>Original spec-file type: Tree</p>
 * <pre>
 * Data type for phylogenetic trees.
 *     @optional name description type tree_attributes
 *     @optional default_node_labels ws_refs kb_refs leaf_list
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "name",
    "description",
    "type",
    "tree",
    "tree_attributes",
    "default_node_labels",
    "ws_refs",
    "kb_refs",
    "leaf_list"
})
public class Tree {

    @JsonProperty("name")
    private java.lang.String name;
    @JsonProperty("description")
    private java.lang.String description;
    @JsonProperty("type")
    private java.lang.String type;
    @JsonProperty("tree")
    private java.lang.String tree;
    @JsonProperty("tree_attributes")
    private Map<String, String> treeAttributes;
    @JsonProperty("default_node_labels")
    private Map<String, String> defaultNodeLabels;
    @JsonProperty("ws_refs")
    private Map<String, Map<String, List<String>>> wsRefs;
    @JsonProperty("kb_refs")
    private Map<String, Map<String, List<String>>> kbRefs;
    @JsonProperty("leaf_list")
    private List<String> leafList;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("name")
    public java.lang.String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(java.lang.String name) {
        this.name = name;
    }

    public Tree withName(java.lang.String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("description")
    public java.lang.String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(java.lang.String description) {
        this.description = description;
    }

    public Tree withDescription(java.lang.String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("type")
    public java.lang.String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(java.lang.String type) {
        this.type = type;
    }

    public Tree withType(java.lang.String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("tree")
    public java.lang.String getTree() {
        return tree;
    }

    @JsonProperty("tree")
    public void setTree(java.lang.String tree) {
        this.tree = tree;
    }

    public Tree withTree(java.lang.String tree) {
        this.tree = tree;
        return this;
    }

    @JsonProperty("tree_attributes")
    public Map<String, String> getTreeAttributes() {
        return treeAttributes;
    }

    @JsonProperty("tree_attributes")
    public void setTreeAttributes(Map<String, String> treeAttributes) {
        this.treeAttributes = treeAttributes;
    }

    public Tree withTreeAttributes(Map<String, String> treeAttributes) {
        this.treeAttributes = treeAttributes;
        return this;
    }

    @JsonProperty("default_node_labels")
    public Map<String, String> getDefaultNodeLabels() {
        return defaultNodeLabels;
    }

    @JsonProperty("default_node_labels")
    public void setDefaultNodeLabels(Map<String, String> defaultNodeLabels) {
        this.defaultNodeLabels = defaultNodeLabels;
    }

    public Tree withDefaultNodeLabels(Map<String, String> defaultNodeLabels) {
        this.defaultNodeLabels = defaultNodeLabels;
        return this;
    }

    @JsonProperty("ws_refs")
    public Map<String, Map<String, List<String>>> getWsRefs() {
        return wsRefs;
    }

    @JsonProperty("ws_refs")
    public void setWsRefs(Map<String, Map<String, List<String>>> wsRefs) {
        this.wsRefs = wsRefs;
    }

    public Tree withWsRefs(Map<String, Map<String, List<String>>> wsRefs) {
        this.wsRefs = wsRefs;
        return this;
    }

    @JsonProperty("kb_refs")
    public Map<String, Map<String, List<String>>> getKbRefs() {
        return kbRefs;
    }

    @JsonProperty("kb_refs")
    public void setKbRefs(Map<String, Map<String, List<String>>> kbRefs) {
        this.kbRefs = kbRefs;
    }

    public Tree withKbRefs(Map<String, Map<String, List<String>>> kbRefs) {
        this.kbRefs = kbRefs;
        return this;
    }

    @JsonProperty("leaf_list")
    public List<String> getLeafList() {
        return leafList;
    }

    @JsonProperty("leaf_list")
    public void setLeafList(List<String> leafList) {
        this.leafList = leafList;
    }

    public Tree withLeafList(List<String> leafList) {
        this.leafList = leafList;
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
        return ((((((((((((((((((((("Tree"+" [name=")+ name)+", description=")+ description)+", type=")+ type)+", tree=")+ tree)+", treeAttributes=")+ treeAttributes)+", defaultNodeLabels=")+ defaultNodeLabels)+", wsRefs=")+ wsRefs)+", kbRefs=")+ kbRefs)+", leafList=")+ leafList)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
