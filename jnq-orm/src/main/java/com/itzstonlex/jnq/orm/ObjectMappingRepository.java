package com.itzstonlex.jnq.orm;

import com.itzstonlex.jnq.orm.exception.JnqObjectMappingException;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;

public interface ObjectMappingRepository {

    @NonNull
    CompletableFuture<Integer> save(@NonNull Object object) throws JnqObjectMappingException;

    @NonNull
    <T> LinkedList<T> fetchAll(@NonNull Class<T> cls) throws JnqObjectMappingException;

    @NonNull
    <T> T fetchFirst(@NonNull Class<T> cls) throws JnqObjectMappingException;

    @NonNull
    <T> T fetchLast(@NonNull Class<T> cls) throws JnqObjectMappingException;
}
