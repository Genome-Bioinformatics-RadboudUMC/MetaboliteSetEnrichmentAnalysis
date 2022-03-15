package org.umcn.tml.msea.enrichment.hyperlink;

import org.umcn.tml.shared.databases.headers.labels.SMPDBMetaboliteIDLabel;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.Header;

public class SMPDBMetaboliteIDHeader extends Header {
	private static SMPDBMetaboliteIDLabel[] essentialLabels = new SMPDBMetaboliteIDLabel[]{
			SMPDBMetaboliteIDLabel.SMPDB_ID,
			SMPDBMetaboliteIDLabel.HMDB_ID
	};

	public SMPDBMetaboliteIDHeader() {
		super(SMPDBMetaboliteIDLabel.class, "SMPDB_Metabolite_IDs", essentialLabels);
	}
}
