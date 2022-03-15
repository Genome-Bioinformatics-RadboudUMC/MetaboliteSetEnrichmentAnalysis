package org.umcn.tml.shared.datastructures.datatable.textfile.header.labels;

//TODO: force check if ValueClass is one of the expected classes. E.g. if your code only processes String, integer, double, make sure the value class cannot be anything else!
@SuppressWarnings("rawtypes")
public interface Label {
    String getLabel();
    String[] getSynonyms();
    Class getValueClass();

    boolean equals(String name);
    boolean isSynonym(String name);

    String toString();
    int hashCode();
}
