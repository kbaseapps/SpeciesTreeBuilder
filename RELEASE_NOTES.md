### Version 0.1.1
- Convert the kb_phylogenomics client to an async client and remove beta tag

### Version 0.1.0
- Update to Java 8 to enable TLS 1.2

### Version 0.0.13
- Documentation updates

### Version 0.0.12
- Tweaks to the display of genomes

### Version 0.0.11
- Use kb_phylogenomics app to create report

### Version 0.0.8
- Updated app to use new refseq genome data. The cog files were regenerated using
the new refseq genome data from public workspace ReferenceDataManager in prod.
The tests were updated to use the new refseq genome ids.

###VERSION:  0.0.7 (Released September 2017)
--
NEW FEATURES:
- none

UPDATED FEATURES / BUG FIXES:
- Combines species tree and genome set building into a single method,
  so that references are consistent between the two objects.
- Reduces default number of genomes in tree to 20, to avoid cluttering
  up user's workspace.
