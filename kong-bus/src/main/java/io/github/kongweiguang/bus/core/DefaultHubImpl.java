package io.github.kongweiguang.bus.core;

import io.github.kongweiguang.bus.Bus;

import java.lang.reflect.Method;
import java.util.List;

import static io.github.kongweiguang.bus.Bus.hub;
import static io.github.kongweiguang.bus.core.InnerUtil.generics;
import static io.github.kongweiguang.core.Assert.isTure;
import static io.github.kongweiguang.core.Assert.notNull;
import static io.github.kongweiguang.core.If.trueOrSup;
import static io.github.kongweiguang.core.Strs.defaultIfEmpty;
import static io.github.kongweiguang.core.Strs.isEmpty;

/**
 * 默认hub实现
 *
 * @param <C>
 * @param <R>
 * @author kongweiguang
 */
public class DefaultHubImpl<C, R> extends AbstractHubImpl<C, R> {

    @Override
    public Hub<C, R> pullClass(Object obj) {
        return exc(Type.pull, obj);
    }

    @Override
    public Hub<C, R> removeClass(Object obj) {
        return exc(Type.remove, obj);
    }

    private enum Type {
        pull,
        remove
    }

    private Hub<C, R> exc(final Type type, final Object obj) {
        notNull(obj, "class must not be null");

        final Class<?> clazz = obj.getClass();

        for (Method m : clazz.getDeclaredMethods()) {
            bind(type, clazz, m);
        }

        return this;
    }

    private static void bind(Type type, Object obj, Method m) {
        Pull pull = m.getAnnotation(Pull.class);

        if (pull != null) {
            Class<?>[] params = m.getParameterTypes();

            isTure(!(params.length == 0 && pull.value().isEmpty()), "method or branch must have a value ");

            isTure(params.length <= 1, "method params not > 1");

            m.setAccessible(true);

            final Hub<?, ?> condition = trueOrSup(isEmpty(pull.hub()), Bus::hub, () -> hub(pull.name()));
            final String branch = branch(m, pull, params);

            switch (type) {
                case pull: {
                    condition.pull(branch, pull.index(), mr(obj, m, params, pull.name()));
                    break;
                }
                case remove: {
                    condition.remove(branch, pull.name());
                    break;
                }
            }

        }
    }

    private static String branch(final Method m, final Pull pull, final Class<?>[] params) {
        String branch;

        if (pull.value().isEmpty()) {

            if (Operation.class.isAssignableFrom(params[0])) {

                final List<String> generics = generics(m);

                isTure(!generics.isEmpty(), "action generics must not be null");

                branch = generics.get(0);
            } else {
                branch = params[0].getName();
            }

        } else {
            branch = pull.value();
        }

        return branch;
    }

    @SuppressWarnings("unchecked")
    private static <C, R> Merge<Operation<C, R>> mr(final Object obj, final Method m, final Class<?>[] params, final String name) {
        return new Merge<Operation<C, R>>() {
            @Override
            public String name() {
                return defaultIfEmpty(name, Merge.super.name());
            }

            @Override
            public void mr(final Operation<C, R> operation) throws Exception {
                final Object[] args = new Object[params.length];

                if (params.length == 1) {

                    if (Operation.class.isAssignableFrom(params[0])) {
                        args[0] = operation;
                    } else {
                        args[0] = operation.content();
                    }

                }

                final Object fr = m.invoke(obj, args);

                if (operation.hasCallBack()) {
                    operation.res((R) fr);
                }
            }
        };
    }

}
