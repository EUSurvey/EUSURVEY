package com.ec.survey.tools;

import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ActivitiesToLog  implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    //Hold Triples of (Left: LogId, Middle: OldValue, Right: NewValue)
    private final List<Triple<Integer, String, String>> activities = new ArrayList<>();

    public void put(int id, String oldVal, String newVal) {
        activities.add(Triple.of(id, oldVal, newVal));
    }

    public void put(int id, String[] oldNew) {
        int counter = 0;
        while (counter < oldNew.length) {
            put(id, oldNew[counter++], oldNew[counter++]);
        }
    }

    public void putAll(ActivitiesToLog other) {
        activities.addAll(other.activities);
    }

    /**
     * @return Sorted List of Activity-Triples, where Left: LogId, Middle: OldValue, Right: NewValue
     */
    public List<Triple<Integer, String, String>> getAll() {
        return activities.stream().sorted(Comparator.comparingInt(Triple::getLeft)).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return activities.isEmpty();
    }
}
