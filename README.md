# MetaboliteSetEnrichmentAnalysis
A re-implementation of metabolite set enrichment analysis (MSEA) for the in-house analysis of untargeted metabolomics data from patients with rare inherited metabolic disorders. 

**A Manuscript with more details on the code is currently under review.

## Requirements
JDK: 8u121
Apache Maven: 3.8.2

## Installation (i.e. Build the java project)
```bash
cd .../MetaboliteSetEnrichmentAnalysis
mvn package
```

## Usage
Demo data is included in the repository and is placed in the correct locations during building of the MetaboliteSetEnrichmentAnalysis project.

To run the code on the demo data, open the command line and navigate to the following location:

```bash
cd .../MetaboliteSetEnrichmentAnalysis/target/MetaboliteSetEnrichmentAnalysis-1.0.0-RELEASE-package
```

### 1. Run enrichment
```bash
java -ea -cp "lib/*" org.umcn.tml.msea.enrichment.PathwayBasedMSEAApp -metaboliteSetDatabase="data/Metabolite_Sets.tsv" -smpdbMetaboliteIDs="data/SMPDB_metabolite_IDs.tsv" -inputFeatureFile="data/MSEA_Demo_Feature_Table.tsv" -outputFeatureFile="data/MSEA_Demo_Feature_Table_MSEA.tsv" -outputMetaboliteSetFile="data/MSEA_Demo_Metabolite_Set_Table_MSEA.tsv"
```

### 2. Run clustering
```bash
java -ea -cp "lib/*" org.umcn.tml.msea.clustering.MSEAClusteringApp -inputMetaboliteSetFile="data/MSEA_Demo_Metabolite_Set_Table_MSEA.tsv" -inputFeatureFile="data/MSEA_Demo_Feature_Table_MSEA.tsv" -outputClusterFile="data/MSEA_Demo_Cluster_Table_Clustering.tsv" -outputMetaboliteSetFile="data/MSEA_Demo_Metabolite_Set_Table_Clustering.tsv" -outputFeatureFile="data/MSEA_Demo_Feature_Table_Clustering.tsv"
```

## Credits
### Metabolite Set information was gathered from
SMPDB: https://smpdb.ca/ 
Jewison T, Su Y, Disfany FM, et al. SMPDB 2.0: Big Improvements to the Small Molecule Pathway Database Nucleic Acids Res. 2014 Jan;42(Database issue):D478-84. https://doi.org/10.1093/nar/gkp1002

KEGG: https://www.genome.jp/kegg/
Kanehisa, M. and Goto, S.; KEGG: Kyoto Encyclopedia of Genes and Genomes. Nucleic Acids Res. 28, 27-30 (2000). https://doi.org/10.1093/nar/28.1.27

### Pre-existing MSEA implementations
Jianguo Xia, David S. Wishart, MSEA: a web-based tool to identify biologically meaningful patterns in quantitative metabolomic data, Nucleic Acids Research, Volume 38, Issue suppl_2, 1 July 2010, Pages W71–W77, https://doi.org/10.1093/nar/gkq329

Marco-Ramell, A., Palau-Rodriguez, M., Alay, A. et al. Evaluation and comparison of bioinformatic tools for the enrichment analysis of metabolomics data. BMC Bioinformatics 19, 1 (2018). https://doi.org/10.1186/s12859-017-2006-0