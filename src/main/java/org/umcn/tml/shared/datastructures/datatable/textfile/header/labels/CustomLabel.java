package org.umcn.tml.shared.datastructures.datatable.textfile.header.labels;

@SuppressWarnings("rawtypes")
public class CustomLabel implements Label {
    private String label;
    private Class valueClass;

    public CustomLabel(String labelString) {
        this.label = labelString;
        this.valueClass = String.class;
    }
    
    public CustomLabel(String labelString, Class valueClass) {
        this.label = labelString;
        this.valueClass = valueClass;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String[] getSynonyms() {
        return new String[0];
    }

    @Override
    public Class getValueClass() {
        return valueClass;
    }

    @Override
    public boolean equals(String name) {
        return label.equals(name);
    }

    @Override
    public boolean isSynonym(String name) {
        return false;
    }

    @Override
    public boolean equals(Object that) {
        if (that instanceof Label) {
            return this.getLabel().equals(((Label) that).getLabel());
        } else
            return that instanceof String && this.getLabel().equals(that);
    }

    @Override
    public int hashCode() {
        return label.hashCode();
    }

    @Override
    public String toString() {
        return label;
    }
}
