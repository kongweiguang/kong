package io.github.kongweiguang.bus.core;

import io.github.kongweiguang.bus.Bus;

import java.lang.reflect.Method;
import java.util.List;

import static io.github.kongweiguang.bus.Bus.hub;
import static io.github.kongweiguang.bus.core.InnerUtil.generics;
import static io.github.kongweiguang.core.lang.Assert.isTure;
import static io.github.kongweiguang.core.lang.Assert.notNull;
import static io.github.kongweiguang.core.lang.If.trueSupF1;
import static io.github.kongweiguang.core.util.Strs.defaultIfEmpty;
import static io.github.kongweiguang.core.util.Strs.isEmpty;

/**
 * 默认hub实现
 *
 * @param <C>
 * @param <R>
 * @author kongweiguang
 */
public class DefaultHubImpl<C, R> extends AbstractHubImpl<C, R> {

    @Override
    public Hub<C, R> pullClass(final Object obj) {
        return exc(Type.pull, obj);
    }

    @Override
    public Hub<C, R> removeClass(final Object obj) {
        return exc(Type.remove, obj);
    }

    private enum Type {
        pull,
        remove
    }

    private Hub<C, R> exc(final Type type, final Object obj) {
        notNull(obj, "class must not be null");

        for (Method m : obj.getClass().getDeclaredMethods()) {
            bind(type, obj, m);
        }

        return this;
    }

    private static void bind(final Type type, final Object obj, final Method m) {
        Pull pull = m.getAnnotation(Pull.class);

        if (pull != null) {
            Class<?>[] params = m.getParameterTypes();

            isTure(!(params.length == 0 && pull.value().isEmpty()), "method or branch must have a value ");

            isTure(params.length <= 1, "method params not > 1");

            m.setAccessible(true);

            final Hub<?, ?> condition = trueSupF1(isEmpty(pull.hub()), Bus::hub, () -> hub(pull.name()));
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

            if (Oper.class.isAssignableFrom(params[0])) {

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
    private static <C, R> Merge<Oper<C, R>> mr(final Object obj, final Method m, final Class<?>[] params, final String name) {
        return new Merge<Oper<C, R>>() {
            @Override
            public String name() {
                return defaultIfEmpty(name, Merge.super.name());
            }

            @Override
            public void mr(final Oper<C, R> oper) throws Exception {
                final Object[] args = new Object[params.length];

                if (params.length == 1) {

                    if (Oper.class.isAssignableFrom(params[0])) {
                        args[0] = oper;
                    } else {
                        args[0] = oper.content();
                    }

                }

                final Object fr = m.invoke(obj, args);

                if (oper.hasCallBack()) {
                    oper.res((R) fr);
                }
            }
        };
    }

}
