### OVERVIEW
--
The Species Tree Builder creates Species Trees showing relationships
between user-provided and reference Genomes.  It also builds GenomeSet
objects containing the genomes in the tree.

### Version 1.0.0
--
__NEW FEATURES:__
- Can export trees in Newick format
__UPDATED FEATURES / BUG FIXES:__
- none

### Version 0.0.8
--
__NEW FEATURES:__
- none

__UPDATED FEATURES / BUG FIXES:__
- Updated app to use new refseq genome data. The cog files were
  regenerated using the new refseq genome data from public workspace
  ReferenceDataManager in prod.  The tests were updated to use the new
  refseq genome ids.

### VERSION 0.0.7
--
__NEW FEATURES:__
- none

__UPDATED FEATURES / BUG FIXES:__
- Combines species tree and genome set building into a single method,
  so that references are consistent between the two objects.
- Reduces default number of genomes in tree to 20, to avoid cluttering
  up user's workspace.
