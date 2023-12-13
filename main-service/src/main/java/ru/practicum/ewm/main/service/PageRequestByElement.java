package ru.practicum.ewm.main.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class PageRequestByElement extends PageRequest {
    private Integer from;

    protected PageRequestByElement(int from, int size, Sort sort) {
        super(from / size, size, sort);
        this.from = from;
    }

    public static PageRequest of(int from, int size, Sort sort) {
        return new PageRequestByElement(from, size, sort);
    }

    public static PageRequest of(int from, int size) {
        return of(from, size, Sort.unsorted());
    }

    @Override
    public long getOffset() {
        return from;
    }
}
