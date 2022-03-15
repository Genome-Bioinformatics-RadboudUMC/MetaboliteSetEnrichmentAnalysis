package org.umcn.tml.shared.datastructures.datatable.textfile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.umcn.tml.shared.datastructures.datatable.DataTable;
import org.umcn.tml.shared.datastructures.datatable.DataTableRow;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@Component
public class TextFileWriter {
	public enum OriginalColumns{
		First, Last, Dont_Include;
	}
	
	private static final String TAB = "\t";
	
	private Logger logger = LoggerFactory.getLogger(TextFileWriter.class);
	
	public void start(File file, boolean overwrite, DataTable dataTable, OriginalColumns originalColumns) throws IOException {
		start(file, overwrite, dataTable, originalColumns, dataTable.getHeaderSet());
	}
	
	public void start(File file, boolean overwrite, DataTable dataTable, OriginalColumns originalColumns, Label...labelArray) throws IOException {
		start(file, overwrite, dataTable, originalColumns, new LinkedHashSet<Label>(Arrays.asList(labelArray)));
	}
	
	public void start(File file, boolean overwrite, DataTable dataTable, OriginalColumns originalColumns, Set<Label> labelSet) throws IOException {
		logger.info("Writing : " + file.getAbsolutePath());
		
		if(overwrite) file.setWritable(true);		
		BufferedWriter bw = new BufferedWriter(new FileWriter(file));
		
		writeHeader(bw, labelSet, originalColumns, dataTable.getOriginalHeader());		
		
		for (DataTableRow dataTableRow : dataTable.getRowList()) {
			writeLine(bw, labelSet, originalColumns, dataTableRow);
		}
		
		bw.close();
		file.setReadOnly();
	}

	public void writeHeader(BufferedWriter bw, Set<Label> labelSet, OriginalColumns originalColumns, String originalHeader) throws IOException {
		if (originalColumns.equals(OriginalColumns.First)) {
			bw.write(originalHeader.replaceAll("\\t$", ""));
			bw.write(TAB);
		}
		
		for (Label label : labelSet) {
			bw.write(label.getLabel());
			bw.write(TAB);				
		}
		
		if (originalColumns.equals(OriginalColumns.Last)) {
			bw.write(originalHeader.replaceAll("\\t$", ""));
			bw.write(TAB);
		}
		
		bw.newLine();
	}

	public void writeLine(BufferedWriter bw, Set<Label> headerSet, OriginalColumns originalColumns, DataTableRow dataTableRow) throws IOException {
		if (originalColumns.equals(OriginalColumns.First)) {
			bw.write(dataTableRow.getOriginalRow().replaceAll("\\t$", ""));
			bw.write(TAB);
		}		
		
		for (Label label : headerSet) {
			writeField(bw, label, dataTableRow);
			bw.write(TAB);				
		}
		
		if (originalColumns.equals(OriginalColumns.Last)) {
			bw.write(dataTableRow.getOriginalRow().replaceAll("\\t$", ""));
			bw.write(TAB);
		}

		bw.newLine();
	}
	
	public void writeField(BufferedWriter bw, Label label, DataTableRow dataTableRow) throws IOException {
		try {
			if (label.getValueClass().equals(Double.class)) {
				String value;
				Double d  = dataTableRow.getDouble(label);
				if (d.isInfinite() || d.isNaN()) {
					value = d.toString();
				} else {
					BigDecimal bd = new BigDecimal(dataTableRow.getDouble(label));
					value = bd.toPlainString();					
				}
				bw.write(value);
			} else if (label.getValueClass().equals(Integer.class)) {
				bw.write(dataTableRow.getInteger(label).toString());
			} else if (label.getValueClass().equals(String.class)) {
				bw.write(dataTableRow.getString(label));
			} else {
				//TODO: Shouldn't happen!
			}
		} catch (NullPointerException e) {
			bw.write("null");
		}
	}
}
