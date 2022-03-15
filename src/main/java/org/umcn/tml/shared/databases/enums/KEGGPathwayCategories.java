package org.umcn.tml.shared.databases.enums;

public enum KEGGPathwayCategories {
	Cellular_Processes("Cellular Processes", new String[]{
			"Cell growth and death", 
			"Transport and catabolism", 
			"Cellular community - eukaryotes", 
			"Cell motility"
	}),
	Environmental_Information_Processing("Environmental Information Processing", new String[]{
			"Membrane transport",
			"Signal transduction",
			"Signaling molecules and interaction"
	}),
	Genetic_Information_Processing("Genetic Information Processing", new String[]{
			"Translation",
			"Folding, sorting and degradation",
			"Transcription",
			"Replication and repair"	
	}),
	Human_Diseases("Human Diseases", new String[]{
			"Drug resistance: Antineoplastic",
			"Endocrine and metabolic diseases",
			"Neurodegenerative diseases",
			"Substance dependence",
			"Infectious diseases: Bacterial",
			"Infectious diseases: Parasitic",
			"Infectious diseases: Viral",
			"Cancers: Overview",
			"Cancers: Specific types",
			"Immune diseases",
			"Cardiovascular diseases"	
	}),
	Metabolism("Metabolism", new String[]{
			"Carbohydrate metabolism",
			"Lipid metabolism",
			"Metabolism of cofactors and vitamins",
			"Energy metabolism",
			"Amino acid metabolism",
			"Nucleotide metabolism",
			"Biosynthesis of other secondary metabolites",
			"Metabolism of other amino acids",
			"Glycan biosynthesis and metabolism",
			"Metabolism of terpenoids and polyketides",
			"Xenobiotics biodegradation and metabolism",
			"Global and overview maps"
	}),
	Organismal_Systems("Organismal Systems", new String[]{
			"Endocrine system",
			"Immune system",
			"Aging",
			"Circulatory system",
			"Development",
			"Environmental adaptation",
			"Nervous system",
			"Sensory system",
			"Excretory system",
			"Digestive system"
	});
	
	
	private final String name;
	private final String[] subCategories;
	
	KEGGPathwayCategories(String name, String[] subCategories) {
		this.name = name;
		this.subCategories = subCategories;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String[] getSubCategories() {
		return this.subCategories;
	}
}
