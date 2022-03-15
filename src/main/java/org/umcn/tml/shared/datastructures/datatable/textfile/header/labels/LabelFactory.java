package org.umcn.tml.shared.datastructures.datatable.textfile.header.labels;

public class LabelFactory {
    private LabelFactory() {
    }

    @SuppressWarnings("rawtypes")
	private static Class[] classes = new Class[]{
            //e.g. WorkingListLabel.class
    };

    public static Label map(String labelString) {
        for (@SuppressWarnings("unchecked") Class<Label> clazz : classes) {
            Label inClass = mapInClassWithoutCustom(clazz, labelString);
            if (inClass != null) {
                return inClass;
            }
        }
        return new CustomLabel(labelString);
    }

    public static Label mapInClasses(Class<Label>[] targetClasses, String labelString) {
        for (Class<Label> clazz : targetClasses) {
            Label inClass = mapInClassWithoutCustom(clazz, labelString);
            if (inClass != null) {
                return inClass;
            }
        }
        return new CustomLabel(labelString);
    }

    @SuppressWarnings("unchecked")
    public static Label mapInClass(Class<? extends Label> targetClass, String labelString) {
        Label label = mapInClassWithoutCustom((Class<Label>) targetClass, labelString);
        if (label == null) {
            return new CustomLabel(labelString);

        } else {
            return label;
        }
    }

    private static Label mapInClassWithoutCustom(Class<Label> targetClass, String labelString) {
        Label[] labels = targetClass.getEnumConstants();
        for (Label label: labels) {
            if (label.equals(labelString) || label.isSynonym(labelString)) {
                return label;
            }
        }
        return null;
    }


}
