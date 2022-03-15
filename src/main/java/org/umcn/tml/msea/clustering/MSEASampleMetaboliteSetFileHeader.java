package org.umcn.tml.msea.clustering;

import org.umcn.tml.msea.datafiles.headers.labels.MSEAMetaboliteSetLabel;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.Header;

public class MSEASampleMetaboliteSetFileHeader extends Header {
	private static MSEAMetaboliteSetLabel[] essentialLabels = new MSEAMetaboliteSetLabel[]{
			MSEAMetaboliteSetLabel.Metabolite_Set_ID,
			MSEAMetaboliteSetLabel.Metabolite_Set_Name,
			MSEAMetaboliteSetLabel.Metabolite_Set_Category,
			MSEAMetaboliteSetLabel.Metabolite_Set_Metabolites,
			MSEAMetaboliteSetLabel.Metabolite_Set_Genes,	
			MSEAMetaboliteSetLabel.Associated_Metabolite_Set,
			MSEAMetaboliteSetLabel.Aberrant_Features,
			MSEAMetaboliteSetLabel.Normal_Features,
			MSEAMetaboliteSetLabel.Aberrant_Metabolites,
			MSEAMetaboliteSetLabel.Normal_Metabolites,
			MSEAMetaboliteSetLabel.Highest_Feature_FC_Increase,
			MSEAMetaboliteSetLabel.Highest_Feature_FC_Decrease,
			MSEAMetaboliteSetLabel.Feature_FC_NaNs,
			MSEAMetaboliteSetLabel.Hypergeometric_Test_n11,
			MSEAMetaboliteSetLabel.Hypergeometric_Test_n12,
			MSEAMetaboliteSetLabel.Hypergeometric_Test_n21,
			MSEAMetaboliteSetLabel.Hypergeometric_Test_n22,
			MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value,
			MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Benjamini_Hochberg,
			MSEAMetaboliteSetLabel.Hypergeometric_Test_P_Value_Bonferoni_Holm,
			MSEAMetaboliteSetLabel.Visualization_Hyperlink
	};

	public MSEASampleMetaboliteSetFileHeader() {
		super(MSEAMetaboliteSetLabel.class, "MSEA Sample Metabolite Set File", essentialLabels);
	}

}
