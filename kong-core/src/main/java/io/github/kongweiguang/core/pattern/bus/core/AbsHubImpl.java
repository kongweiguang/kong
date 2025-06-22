package io.github.kongweiguang.core.pattern.bus.core;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.core.lang.Opt.ofNullable;
import static java.util.Comparator.comparing;

/**
 * hubImpl
 *
 * @param <C> 内容类型
 * @param <R> 返回类型
 * @author kongweiguang
 */
public abstract class AbsHubImpl<C, R> implements Hub<C, R> {
    private final Map<String, List<MergeWarp<C, R>>> repo = new ConcurrentHashMap<>();

    @Override
    public Hub<C, R> push(Oper<C, R> oper, Consumer<R> call) {
        notNull(oper, "action must not be null");

        oper.callback(call);

        ofNullable(repo.get(oper.branch())).ifPresent(ms -> ms.forEach(m -> m.merge(oper)));

        return this;
    }

    @Override
    public Hub<C, R> pull(String branch, int index, Merge<Oper<C, R>> merge) {
        notNull(branch, "branch must not be null");
        notNull(merge, "merge must not be null");

        List<MergeWarp<C, R>> merges = repo.computeIfAbsent(branch, k -> new CopyOnWriteArrayList<>());

        merges.add(new MergeWarp<>(index, merge));

        if (merges.size() > 1) {
            merges.sort(comparing(MergeWarp::index));
        }

        return this;
    }

    @Override
    public Hub<C, R> remove(String branch, String name) {
        notNull(branch, "branch must not be null");
        notNull(name, "name must not be null");

        ofNullable(repo.get(branch)).ifPresent(ms -> ms.removeIf(m -> Objects.equals(m.name(), name)));
        return this;
    }
}
