package org.umcn.tml.msea.clustering;

import org.umcn.tml.msea.datafiles.headers.labels.FeatureTableLabel;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.Header;

public class MSEASampleFeatureFileHeader extends Header {
	private static FeatureTableLabel[] essentialLabels = new FeatureTableLabel[]{
			FeatureTableLabel.Feature_ID,
			FeatureTableLabel.Fold_Change,
			FeatureTableLabel.MSEA_Metabolite_Set_ID
	};

	public MSEASampleFeatureFileHeader() {
		super(FeatureTableLabel.class, "MSEA Sample Feature File", essentialLabels);
	}

}
