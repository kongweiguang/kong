package io.github.kongweiguang.bus.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.Assert.notNull;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;

/**
 * hubImpl
 *
 * @param <C> 内容类型
 * @param <R> 返回类型
 * @author kongweiguang
 */
public abstract class AbstractHubImpl<C, R> implements Hub<C, R> {
    private final Map<String, List<MergeWarp<C, R>>> repo = new ConcurrentHashMap<>();

    @Override
    public Hub<C, R> push(final Operation<C, R> operation, final Consumer<R> call) {
        notNull(operation, "action must not be null");

        operation.callback(call);

        ofNullable(repo.get(operation.branch())).ifPresent(ms -> ms.forEach(m -> m.merge(operation)));

        return this;
    }

    @Override
    public Hub<C, R> pull(final String branch, final int index, final Merge<Operation<C, R>> merge) {
        notNull(branch, "branch must not be null");
        notNull(merge, "merge must not be null");

        final List<MergeWarp<C, R>> merges = repo.computeIfAbsent(branch, k -> new CopyOnWriteArrayList<>());

        merges.add(new MergeWarp<>(index, merge));

        if (merges.size() > 1) {
            merges.sort(comparing(MergeWarp::index));
        }

        return this;
    }

    @Override
    public Hub<C, R> remove(final String branch, final String name) {
        notNull(branch, "branch must not be null");
        notNull(name, "name must not be null");

        ofNullable(repo.get(branch)).ifPresent(ms -> ms.removeIf(m -> Objects.equals(m.name(), name)));
        return this;
    }
}
