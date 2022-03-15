package org.umcn.tml.msea.enrichment;

import org.umcn.tml.msea.datafiles.headers.labels.FeatureTableLabel;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.Header;

public class FeatureTableHeader extends Header {
	private static FeatureTableLabel[] essentialLabels = new FeatureTableLabel[]{
			FeatureTableLabel.Feature_ID,
			FeatureTableLabel.HMP,
			FeatureTableLabel.KEGG_Entry,
			FeatureTableLabel.P_Value,
			FeatureTableLabel.Fold_Change
	};

	public FeatureTableHeader() {
		super(FeatureTableLabel.class, "UDA Sample Feature File", essentialLabels);
	}

}
