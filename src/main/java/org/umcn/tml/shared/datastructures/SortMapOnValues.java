package org.umcn.tml.shared.datastructures;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: Sort null values first / last or keep ignoring them?
// http://stackoverflow.com/questions/8119366/sorting-hashmap-by-values
public class SortMapOnValues {
	public enum Order {
		ASCENDING, DESCENDING
	}
	
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(SortMapOnValues.class);
	
    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValues(Map<K, V> unsortMap, final Order order)
    {
        List<Entry<K, V>> list = new LinkedList<Entry<K, V>>(unsortMap.entrySet());
        
        for (Entry<K, V> e : unsortMap.entrySet()) {
        	if (e.getValue() == null) {
        		list.remove(e);
        	}
        }

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<K, V>>()
        {
            public int compare(Entry<K, V> o1,
                    Entry<K, V> o2)
            {
                if (order.equals(Order.ASCENDING))
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (Entry<K, V> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}
