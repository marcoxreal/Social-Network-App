package com.ubb.util.paging;

public class Page<E> {
    private final Iterable<E> elementsOnPage;
    private final int totalNumberOfElements;

    public Page(Iterable<E> elements, int totalNumberOfElements) {
        this.elementsOnPage = elements;
        this.totalNumberOfElements = totalNumberOfElements;
    }

    public Iterable<E> getElementsOnPage() {
        return elementsOnPage;
    }

    public int getTotalNumberOfElements() {
        return totalNumberOfElements;
    }
}
