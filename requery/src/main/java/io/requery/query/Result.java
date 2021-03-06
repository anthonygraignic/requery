/*
 * Copyright 2016 requery.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.requery.query;

import io.requery.util.CloseableIterable;
import io.requery.util.CloseableIterator;
import io.requery.util.function.Consumer;
import io.requery.util.function.Supplier;

import javax.annotation.CheckReturnValue;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

/**
 * Represents a set of results from a query operation. These are terminal operations on the query
 * which will cause the query to get executed. Note this class is not thread safe and the
 * caller should call {@link #close()} after the result is no longer needed. Entities created from
 * the result are still valid and can still be referenced and used. This result is active that
 * calls to methods return the underlying data e.g. {@link Iterable#iterator()},  {@link #toList()}
 * will cause the query to be executed.
 *
 * @param <E> type of element.
 */
public interface Result<E> extends CloseableIterable<E>, AutoCloseable {

    /**
     * @return A {@link AutoCloseable} {@link java.util.Iterator} over the elements in this result.
     */
    @Override
    CloseableIterator<E> iterator();

    /**
     * Close this result and any resources it holds.
     */
    @Override
    void close();

    /**
     * @return {@link Stream} instance over the result set. Java 8 only.
     */
    @CheckReturnValue
    Stream<E> stream();

    /**
     * Converts the result stream to a {@link rx.Observable}. When the observable terminates this
     * result instance will be closed.
     *
     * @return observable stream of the results of this query.
     */
    @CheckReturnValue
    rx.Observable<E> toObservable();

    /**
     * Creates an observable that emits this result initially and then again whenever commits that
     * may affect the query result are made from within the same {@link io.requery.EntityStore}
     * from where this instance originated.
     *
     * @return observable instance of this result that is triggered whenever changes that may
     * affect the query are made.
     */
    @CheckReturnValue
    rx.Observable<Result<E>> toSelfObservable();

    /**
     * Converts the result stream to a {@link io.reactivex.Flowable}. When the flowable terminates
     * this result instance will be closed.
     *
     * @return flowable stream of the results of this query.
     */
    @CheckReturnValue
    io.reactivex.Flowable<E> flowable();

    /**
     * Converts the result stream to a {@link io.reactivex.Maybe} value, return the first element
     * if present or completes if no results.
     *
     * @return maybe instance of the results of this query.
     */
    @CheckReturnValue
    io.reactivex.Maybe<E> maybe();

    /**
     * Converts the result stream to a {@link io.reactivex.Observable}. When the observable
     * terminates this result instance will be closed.
     *
     * @return observable stream of the results of this query.
     */
    @CheckReturnValue
    io.reactivex.Observable<E> observable();

    /**
     * Creates an {@link io.reactivex.Observable} that emits this result initially and then again
     * whenever commits that may affect the query result are made from within the same
     * {@link io.requery.EntityStore} from where this instance originated.
     *
     * @return {@link io.reactivex.Observable} instance of this result that is triggered whenever
     * changes that may affect the query are made.
     */
    @CheckReturnValue
    io.reactivex.Observable<Result<E>> observableResult();

    /**
     * Fill the given collection with all elements from this result set.
     *
     * @param collection to fill
     * @param <C>        collection to fill
     * @return the filled collection.
     */
    <C extends Collection<E>> C collect(C collection);

    /**
     * Gets the first element of this result set.
     *
     * @return first element of this result set.
     * @throws java.util.NoSuchElementException if there is no first element (empty result).
     */
    @CheckReturnValue
    E first() throws NoSuchElementException;

    /**
     * Gets the first element or a default value.
     *
     * @param defaultElement default value for when there is no first element.
     * @return First element or defaultElement if there is none.
     */
    @CheckReturnValue
    E firstOr(E defaultElement);

    /**
     * Gets the first element or a default value from a {@link Supplier}.
     *
     * @param supplier value supplier for when there is no first element.
     * @return First element or the supplier value if there is none.
     */
    @CheckReturnValue
    E firstOr(Supplier<E> supplier);

    /**
     * Gets the first element or a default null value.
     *
     * @return First element or null if there is none.
     */
    @CheckReturnValue
    E firstOrNull();

    /**
     * Consume the results with the given {@link Consumer}. In Java 8 use {@link Iterable#forEach}
     * instead.
     *
     * @param action to receive the elements in this result.
     */
    void each(Consumer<? super E> action);

    /**
     * Converts this result into a list.
     *
     * @return a new unmodifiable list with the contents of the result.
     */
    @CheckReturnValue
    List<E> toList();

    /**
     * Map the results into a new map using an expression of the result element as a key.
     *
     * @param key expression key, must be present in all result elements
     * @param <K> type of key
     * @return map containing the results
     */
    @CheckReturnValue
    <K> Map<K, E> toMap(Expression<K> key);

    /**
     * Map the results into the given map using an expression of the result element as a key.
     *
     * @param key expression key, must be present in all result elements
     * @param map map instance to be populated
     * @param <K> type of key
     * @return map containing the results.
     */
    @CheckReturnValue
    <K> Map<K, E> toMap(Expression<K> key, Map<K, E> map);
}
