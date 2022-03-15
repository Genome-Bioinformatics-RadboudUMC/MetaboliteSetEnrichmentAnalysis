package org.umcn.tml.shared.databases.headers;

import org.umcn.tml.shared.databases.headers.labels.MetaboliteSetDBLabel;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.Header;

public class MetaboliteSetDBHeader extends Header {
	private static MetaboliteSetDBLabel[] essentialLabels = new MetaboliteSetDBLabel[]{
			MetaboliteSetDBLabel.Database,
			MetaboliteSetDBLabel.ID,
			MetaboliteSetDBLabel.Name,
			MetaboliteSetDBLabel.Category,
			MetaboliteSetDBLabel.Metabolites,
			MetaboliteSetDBLabel.Genes,
			MetaboliteSetDBLabel.Associated_Metabolite_Sets
	};

	public MetaboliteSetDBHeader() {
		super(MetaboliteSetDBLabel.class, "Metabolite_Sets", essentialLabels);
	}
}
