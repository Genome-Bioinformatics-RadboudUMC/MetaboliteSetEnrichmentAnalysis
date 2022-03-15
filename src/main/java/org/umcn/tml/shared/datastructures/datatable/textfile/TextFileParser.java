package org.umcn.tml.shared.datastructures.datatable.textfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.umcn.tml.shared.datastructures.datatable.DataTable;
import org.umcn.tml.shared.datastructures.datatable.DataTableRow;
import org.umcn.tml.shared.datastructures.datatable.textfile.enums.FieldSeparator;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.Header;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;

@Component//TODO: split a file into multiple DataTables using multiple headers (So you can for example split Annotations and Statistics of the UDA output file).
public class TextFileParser {
	
	private Logger logger = LoggerFactory.getLogger(TextFileParser.class);
	
	public DataTable parse(File file, Header header, Set<Label> labelSet, FieldSeparator fieldSeparator) throws IOException {
		logger.info("Parsing " + header.getFileType() + ": " + file.getAbsolutePath());

		BufferedReader br = new BufferedReader(new FileReader(file));
		DataTable dataTable;
		try {
			
			String headerLine = br.readLine();
			int headerTabCount = StringUtils.countMatches(headerLine, fieldSeparator.getFieldSeparator());
			header.setHeaderLabels(headerLine.split(fieldSeparator.getFieldSeparator()));
			
			if (labelSet == null) {
				labelSet = new LinkedHashSet<Label>(header.getHeaderLabels());
			}
			
			dataTable = new DataTable(file.getName(), labelSet, headerLine);
			
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] fields = line.split(fieldSeparator.getFieldSeparator());			
				
				if (headerTabCount == StringUtils.countMatches(line, fieldSeparator.getFieldSeparator())) {
					DataTableRow dataTableRow = new DataTableRow(line);
										
					for (Label label : labelSet) {
						String field = header.getFieldFromLine(label, fields);
						dataTableRow.addField(label, field, false);
					}
					
					dataTable.addRow(false, dataTableRow);				
				}
			}
			
		} catch (Exception e) {
			throw e;
		} finally {
			br.close();
		}
		
		logger.info("...parsed " + dataTable.size() + " lines from file.");
		
		return dataTable;
	}
	
	public DataTable parseAllColumns(File file, Header header, FieldSeparator fieldSeparator) throws IOException {
		return parse(file, header, null, fieldSeparator);		
	}
	
	public DataTable parseAllLabelColumns(File file, Header header, FieldSeparator fieldSeparator) throws IOException {		
		return parse(file, header, new LinkedHashSet<Label>(Arrays.asList(header.getLabelClass().getEnumConstants())), fieldSeparator);
	}
	
	public DataTable parseEssentialLabelColumns(File file, Header header, FieldSeparator fieldSeparator) throws IOException {
		return parse(file, header, new LinkedHashSet<Label>(Arrays.asList(header.getEssentialLabels())), fieldSeparator);
	}
}
