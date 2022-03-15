package org.umcn.tml.shared.datastructures.datatable.textfile.header;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.Label;
import org.umcn.tml.shared.datastructures.datatable.textfile.header.labels.LabelFactory;

public class Header {
	private Class<? extends Label> label;
    private String filetype;
    private Label[] essentialLabels;
    private List<Label> headerLabels;
	
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(Header.class);
	
	public Header(Class<? extends Label> label, String fileType) {
		this.label = label;
		this.filetype = fileType;
		this.essentialLabels = new Label[]{};	
	}
	
	public Header (Class<? extends Label> label, String fileType, Label[] essentialLabels) {
		this.label = label;
		this.filetype = fileType;
		this.essentialLabels = essentialLabels;		
	}
	
	public Header (Class<? extends Label> label, String fileType, Label[] essentialLabels, String[] splittedLine) throws IOException {	
		this.label = label;
		this.filetype = fileType;
		this.essentialLabels = essentialLabels;
		setHeaderLabels(splittedLine);
	}

	public void setHeaderLabels(String[] splittedLine) throws IOException {
        headerLabels = new LinkedList<>();
        for (String value: splittedLine) {
            addHeaderValue(value);
        }
		validateHeader();
    }

    private void addHeaderValue(String value) {
        headerLabels.add(LabelFactory.mapInClass(label, value));
    }

    private void validateHeader() throws IOException {
        String missingColumns = "";
    	for (Label label: essentialLabels) {
            if (getIndex(label) < 0) {
            	missingColumns = missingColumns + ", " + label;
            }
        }
    	if (!missingColumns.equals("")){
    		missingColumns = missingColumns.replaceFirst(", ", "");
    		throw new IOException(filetype + " header is incomplete, cannot identify the following mandatory columns: " + missingColumns + ".");
    	}
    }

    public int getIndex(Label label) {
        return headerLabels.indexOf(label);
    }
    
    public List<Label> getHeaderLabels () {
    	return headerLabels;
    }
    
    public Label[] getEssentialLabels () {
    	return essentialLabels;
    }
    
    public String getFileType () {
    	return this.filetype;
    }
    
    public Class<? extends Label> getLabelClass() {
    	return this.label;
    }
    
    //TODO: FIX!
    public String getFieldFromLine(Label label, String[] splittedLine) {
    	try {
    		return splittedLine[getIndex(label)];
    	} catch (Exception e) {
    		return "";
    	}
    }
    
    public String[] getStringArrayFromLine(List<Label> labelList, String[] splittedLine) {
    	String[] stringArray = new String[labelList.size()];
    	
    	for (int i = 0; i < labelList.size(); i++) {
    		stringArray[i] = getFieldFromLine(labelList.get(i), splittedLine);
    	}
    	
    	return stringArray;
    }
    
    public String[] getEssentialFieldsFromLine(String[] splittedLine ) {
    	String[] essentialFields = new String[essentialLabels.length];
    	
    	for (int i = 0; i < essentialFields.length; i++) {
    		Label label = essentialLabels[i];
    		essentialFields[i] = splittedLine[getIndex(label)];	
    	}
    	
    	return essentialFields;
    }
}
