package io.github.kongweiguang.bus.core;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static io.github.kongweiguang.core.Assert.notNull;


/**
 * id生成器
 *
 * @author kongweiguang
 */
public final class IdGen {
    public static final IdGen of = of(Duration.ofSeconds(1));
    private final AtomicLong add = new AtomicLong(System.currentTimeMillis() << 25);

    private IdGen(final Duration period) {
        scheduleTask(period);
    }

    public static IdGen of(final Duration period) {
        notNull(period, "period must not be null");

        return new IdGen(period);
    }

    private void scheduleTask(final Duration period) {
        Executors.newSingleThreadScheduledExecutor(IdGen::newThread)
                .scheduleAtFixedRate(this::updateBaseAndResetAdd, period.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS);
    }

    private void updateBaseAndResetAdd() {
        this.add.set(System.currentTimeMillis() << 25);
    }

    private static Thread newThread(final Runnable r) {
        final Thread t = new Thread(r, "idGen");
        t.setDaemon(true);
        return t;
    }

    /**
     * 生成id
     *
     * @return id
     */
    public long next() {
        return add.incrementAndGet();
    }

}