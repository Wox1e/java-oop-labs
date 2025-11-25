package com.oop.labs.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Service {
    protected <T> List<T> getCommonElements(List<T> list1, List<T> list2) {
        if (list1 == null || list2 == null) {
            return new ArrayList<>();
        }
        Set<Object> set2 = new HashSet<>(list2);
        return list1.stream()
                .filter(set2::contains)
                .collect(Collectors.toList());
    }
}
