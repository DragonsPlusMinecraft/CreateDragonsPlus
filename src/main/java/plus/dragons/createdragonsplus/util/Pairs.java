/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createdragonsplus.util;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Pairs {
    public static <F, S> Predicate<Pair<F, S>> filter(BiPredicate<F, S> predicate) {
        return pair -> predicate.test(pair.getFirst(), pair.getSecond());
    }

    public static <F> Predicate<Pair<F, ?>> filterFirst(Predicate<F> predicate) {
        return pair -> predicate.test(pair.getFirst());
    }

    public static <S> Predicate<Pair<?, S>> filterSecond(Predicate<S> predicate) {
        return pair -> predicate.test(pair.getSecond());
    }

    public static <K, V, F> Function<Map.Entry<K, V>, Pair<F, V>> mapKey(Function<K, F> keyMapper) {
        return entry -> Pair.of(keyMapper.apply(entry.getKey()), entry.getValue());
    }

    public static <K, V, S> Function<Map.Entry<K, V>, Pair<K, S>> mapValue(Function<V, S> valueMapper) {
        return entry -> Pair.of(entry.getKey(), valueMapper.apply(entry.getValue()));
    }

    public static <K, V, F, S> Function<Map.Entry<K, V>, Pair<F, S>> mapEntry(Function<K, F> keyMapper, Function<V, S> valueMapper) {
        return entry -> Pair.of(keyMapper.apply(entry.getKey()), valueMapper.apply(entry.getValue()));
    }

    public static <F, S, R> Function<Pair<F, S>, R> map(BiFunction<F, S, R> mapper) {
        return pair -> mapper.apply(pair.getFirst(), pair.getSecond());
    }

    public static <F1, F2, S1, S2> Function<Pair<F1, S1>, Pair<F2, S2>> map(Function<F1, F2> firstMapper, Function<S1, S2> secondMapper) {
        return pair -> Pair.of(firstMapper.apply(pair.getFirst()), secondMapper.apply(pair.getSecond()));
    }

    public static <F, S> Consumer<Pair<F, S>> accept(BiConsumer<? super F, ? super S> consumer) {
        return pair -> consumer.accept(pair.getFirst(), pair.getSecond());
    }

    public static <E, F extends Iterable<E>, S> Consumer<Pair<F, S>> forEachFirst(BiConsumer<? super E, ? super S> consumer) {
        return pair -> {
            final S second = pair.getSecond();
            pair.getFirst().forEach(first -> consumer.accept(first, second));
        };
    }

    public static <E, F, S extends Iterable<E>> Consumer<Pair<F, S>> forEachSecond(BiConsumer<? super F, ? super E> consumer) {
        return pair -> {
            final F first = pair.getFirst();
            pair.getSecond().forEach(second -> consumer.accept(first, second));
        };
    }
}
