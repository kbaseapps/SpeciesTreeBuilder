/*
A KBase module: SpeciesTreeBuilder
*/

module SpeciesTreeBuilder {

    /* 
        A workspace ID that references a Tree data object.
        @id ws KBaseTrees.Tree
    */
    typedef string ws_tree_id;
        
    /* 
        A workspace ID that references a Genome data object.
        @id ws KBaseGenomes.Genome KBaseGenomeAnnotations.GenomeAnnotation
    */
    typedef string ws_genome_id;
    
    /* 
        A workspace ID that references a GenomeSet data object.
        @id ws KBaseSearch.GenomeSet
    */
    typedef string ws_genomeset_id;


    /* 
        Input data type for construct_species_tree method. Method produces object of Tree type.

        new_genomes - (optional) the list of genome references to use in constructing a tree; either
            new_genomes or genomeset_ref field should be defined.
        genomeset_ref - (optional) reference to genomeset object; either new_genomes or genomeset_ref
            field should be defined.
        out_workspace - (required) the workspace to deposit the completed tree
        out_tree_id - (optional) the name of the newly constructed tree (will be random if not present or null)
	out_genomeset_ref - the name of the output genomeset object
	copy_genomes - 1 means copy genomes to user workspace; 0 means refer only to the public genomes
        use_ribosomal_s9_only - (optional) 1 means only one protein family (Ribosomal S9) is used for 
            tree construction rather than all 49 improtant families, default value is 0.
        nearest_genome_count - (optional) defines maximum number of public genomes nearest to
            requested genomes that will show in output tree.
    */
    typedef structure {
        list<ws_genome_id> new_genomes;
        ws_genomeset_id genomeset_ref;
        string out_workspace;
        string out_tree_id;
        ws_genomeset_id out_genomeset_ref;
	int copy_genomes;
        int use_ribosomal_s9_only;
        int nearest_genome_count;
    } ConstructSpeciesTreeParams;

    /*
        Build a species tree out of a set of given genome references.
    */
    funcdef construct_species_tree(ConstructSpeciesTreeParams input) returns (ws_tree_id tree_ref) authentication required;


    /* 
        Input data type for find_close_genomes method. Method produces list of refereces to public genomes similar to query.

        query_genome - (required) query genome reference
        max_mismatch_percent - (optional) defines maximum mismatch percentage when compare aminoacids from user genome with 
            public genomes (defualt value is 5).
    */
    typedef structure {
        ws_genome_id query_genome;
        int max_mismatch_percent;
    } FindCloseGenomesParams;

    /*
        Find closely related public genomes based on COG of ribosomal s9 subunits. 
    */
    funcdef find_close_genomes(FindCloseGenomesParams params) returns (list<ws_genome_id>) authentication required;


    /* 
        Input data type for guess_taxonomy_path method. Method produces taxonomy path string.

        query_genome - (required) query genome reference
    */
    typedef structure {
        ws_genome_id query_genome;
    } GuessTaxonomyPathParams;
	
    /*
        Search for taxonomy path from closely related public genomes (approach similar to find_close_genomes). 
    */
    funcdef guess_taxonomy_path(GuessTaxonomyPathParams params) returns (string) authentication required;

		
    typedef structure {
        ws_tree_id tree_ref;
        ws_genomeset_id genomeset_ref;
    } BuildGenomeSetFromTreeParams;
	
    funcdef build_genome_set_from_tree(BuildGenomeSetFromTreeParams params) returns (ws_genomeset_id genomeset_ref) authentication required;
};
