package com.ubb.repository;

import com.ubb.domain.TipRata;
import com.ubb.util.paging.Page;
import com.ubb.util.paging.Pageable;

public interface PagingRepository<E> extends Repository<E> {
    Page<E> findAllOnPage(Pageable pageable, TipRata filter);
}
