package io.github.kongweiguang.core.lang;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * 融合了Java的Optional、Rust风格的Option和Result功能的实用工具类
 *
 * @param <T> 包含的值的类型
 * @param <E> 错误的类型
 * @author kongweiguang
 */
public class Opt<T, E> {

    // 值 - 可为null
    private final T value;

    // 错误 - 可为null
    private final E error;

    // 内部状态
    private final State state;

    // 表示Opt的三种状态
    public enum State {
        PRESENT,    // 有值
        EMPTY,      // 空值
        ERROR       // 错误
    }

    // 私有构造函数 - 直接指定所有字段
    private Opt(T value, E error, State state) {
        this.value = value;
        this.error = error;
        this.state = state;
    }

    /**
     * 创建一个包含指定非空值的Opt
     *
     * @param value 非null值
     * @return 包含指定值的Opt
     * @throws NullPointerException 如果值为null
     */
    public static <T, E> Opt<T, E> of(T value) {
        return new Opt<>(Objects.requireNonNull(value), null, State.PRESENT);
    }

    /**
     * 创建一个包含指定值的Opt，如果值为null则返回空Opt
     *
     * @param value 可能为null的值
     * @return 包含指定值的Opt，或空Opt
     */
    public static <T, E> Opt<T, E> ofNullable(T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * 创建一个空的Opt
     *
     * @return 空Opt
     */
    public static <T, E> Opt<T, E> empty() {
        return new Opt<>(null, null, State.EMPTY);
    }

    /**
     * 创建一个包含错误的Opt
     *
     * @param error 错误值(不能为null)
     * @return 包含错误的Opt
     * @throws NullPointerException 如果错误为null
     */
    public static <T, E> Opt<T, E> error(E error) {
        return new Opt<>(null, Objects.requireNonNull(error), State.ERROR);
    }

    /**
     * 从Optional创建Opt
     */
    public static <T, E> Opt<T, E> ofOptional(Optional<T> optional) {
        return optional.map(Opt::<T, E>of).orElseGet(Opt::empty);
    }

    /**
     * 从异常中创建错误Opt
     */
    public static <T, E extends Throwable> Opt<T, E> ofException(E exception) {
        return error(exception);
    }

    /**
     * 执行可能抛出异常的操作，并将结果转换为Opt
     */
    public static <T, E extends Throwable> Opt<T, E> ofTry(ThrowingSupplier<T, E> supplier) {
        try {
            return Opt.ofNullable(supplier.get());
        } catch (Throwable e) {
            @SuppressWarnings("unchecked")
            E error = (E) e;
            return Opt.error(error);
        }
    }

    /**
     * 检查Opt是否包含值
     */
    public boolean isPresent() {
        return state == State.PRESENT;
    }

    /**
     * 检查Opt是否为空
     */
    public boolean isEmpty() {
        return state == State.EMPTY;
    }

    /**
     * 检查Opt是否包含错误
     */
    public boolean isError() {
        return state == State.ERROR;
    }

    /**
     * 如果Opt包含值，则返回该值，否则抛出NoSuchElementException
     */
    public T get() {
        if (state == State.PRESENT) {
            return value;
        }

        throw new NoSuchElementException(state == State.ERROR ? "Unable to get value, Opt has error : " + error : "Unable to get value, Opt is empty");
    }

    /**
     * 不论状态直接返回值
     */
    public T value() {
        return value;
    }

    /**
     * 获取错误，如果Opt不包含错误则抛出异常
     */
    public E getError() {
        if (state == State.ERROR) {
            return error;
        }

        throw new NoSuchElementException("Opt does not contain an error");
    }

    /**
     * 如果Opt包含值并且值满足断言，则返回true，否则返回false
     */
    public boolean isPresentAnd(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        return isPresent() && predicate.test(value);
    }

    /**
     * 如果Opt包含值，则对值执行指定操作
     */
    public Opt<T, E> ifPresent(Consumer<? super T> consumer) {
        if (state == State.PRESENT) {
            consumer.accept(value);
        }
        return this;
    }

    /**
     * 如果Opt为空，则执行指定操作
     */
    public Opt<T, E> ifEmpty(Runnable action) {
        if (state == State.EMPTY) {
            action.run();
        }
        return this;
    }

    /**
     * 如果Opt包含错误，则对错误执行指定操作
     */
    public Opt<T, E> ifError(Consumer<? super E> errorConsumer) {
        if (state == State.ERROR) {
            errorConsumer.accept(error);
        }
        return this;
    }

    /**
     * 根据Opt状态执行不同的操作
     */
    public Opt<T, E> match(
            Consumer<? super T> valueConsumer,
            Runnable emptyAction,
            Consumer<? super E> errorConsumer) {
        switch (state) {
            case PRESENT -> valueConsumer.accept(value);
            case EMPTY -> emptyAction.run();
            case ERROR -> errorConsumer.accept(error);
        }
        return this;
    }

    /**
     * 根据Opt状态返回不同的结果
     */
    public <R> R matchResult(
            Function<? super T, ? extends R> valueFunction,
            Supplier<? extends R> emptySupplier,
            Function<? super E, ? extends R> errorFunction) {
        return switch (state) {
            case PRESENT -> valueFunction.apply(value);
            case EMPTY -> emptySupplier.get();
            case ERROR -> errorFunction.apply(error);
        };
    }

    /**
     * 如果Opt为空或包含错误，则返回指定的其他值，否则返回Opt中的值
     */
    public T orElse(T other) {
        return state == State.PRESENT ? value : other;
    }

    /**
     * 如果Opt为空或包含错误，则调用指定的Supplier获取结果，否则返回Opt中的值
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return state == State.PRESENT ? value : supplier.get();
    }

    /**
     * 如果Opt为空，则抛出指定的异常，否则返回Opt中的值
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (state == State.PRESENT) {
            return value;
        }

        if (state == State.ERROR && error instanceof Throwable) {
            @SuppressWarnings("unchecked")
            X throwable = (X) error;
            throw throwable;
        }

        throw exceptionSupplier.get();
    }

    /**
     * 如果Opt包含错误，则将错误转换为异常并抛出，如果为空则抛出指定的异常，否则返回值
     */
    public <X extends Throwable> T orElseThrowError(Function<? super E, ? extends X> errorMapper,
                                                    Supplier<? extends X> emptyExceptionSupplier) throws X {
        if (state == State.PRESENT) {
            return value;
        }

        if (state == State.ERROR) {
            throw errorMapper.apply(error);
        }

        throw emptyExceptionSupplier.get();
    }

    /**
     * 如果Opt包含值，则将提供的映射函数应用于该值，否则返回空Opt
     */
    public <U> Opt<U, E> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (state != State.PRESENT) {
            @SuppressWarnings("unchecked")
            Opt<U, E> result = (Opt<U, E>) this;
            return result;
        }

        U newValue = mapper.apply(value);
        return newValue == null ? Opt.empty() : Opt.of(newValue);
    }

    /**
     * 如果Opt包含错误，则将提供的映射函数应用于该错误，否则返回原始Opt
     */
    public <F> Opt<T, F> mapError(Function<? super E, ? extends F> errorMapper) {
        Objects.requireNonNull(errorMapper);
        if (state != State.ERROR) {
            @SuppressWarnings("unchecked")
            Opt<T, F> result = (Opt<T, F>) this;
            return result;
        }

        F newError = errorMapper.apply(error);
        return Opt.error(newError);
    }

    /**
     * 如果Opt包含值，对值应用返回Opt的映射函数，否则返回空Opt或错误Opt
     */
    public <U> Opt<U, E> flatMap(Function<? super T, ? extends Opt<U, E>> mapper) {
        Objects.requireNonNull(mapper);
        if (state != State.PRESENT) {
            @SuppressWarnings("unchecked")
            Opt<U, E> result = (Opt<U, E>) this;
            return result;
        }
        return Objects.requireNonNull(mapper.apply(value));
    }

    /**
     * 如果Opt包含值，对值应用断言过滤，如果断言返回false则返回空Opt
     */
    public Opt<T, E> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (state != State.PRESENT) {
            return this;
        }
        return predicate.test(value) ? this : empty();
    }

    /**
     * 如果此Opt为空，则返回备用Opt，否则返回此Opt
     */
    public Opt<T, E> or(Supplier<? extends Opt<T, E>> supplier) {
        if (state == State.PRESENT) {
            return this;
        }
        return Objects.requireNonNull(supplier.get());
    }

    /**
     * 返回包含此Opt值的单元素Stream，如果此Opt为空或包含错误，则返回空Stream
     */
    public Stream<T> stream() {
        if (state != State.PRESENT) {
            return Stream.empty();
        }
        return Stream.of(value);
    }

    /**
     * 将Opt转换为普通的Java Optional
     */
    public Optional<T> toOptional() {
        return state == State.PRESENT ? Optional.of(value) : Optional.empty();
    }

    /**
     * 将错误转换为Optional
     */
    public Optional<E> errorToOptional() {
        return state == State.ERROR ? Optional.of(error) : Optional.empty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Opt<?, ?> other)) {
            return false;
        }

        if (state != other.state) {
            return false;
        }

        return switch (state) {
            case PRESENT -> Objects.equals(value, other.value);
            case ERROR -> Objects.equals(error, other.error);
            case EMPTY -> true;
        };
    }

    @Override
    public int hashCode() {
        return switch (state) {
            case PRESENT -> Objects.hashCode(value);
            case ERROR -> ~Objects.hashCode(error); // 反转位以区分错误和值
            case EMPTY -> 0;
        };
    }

    @Override
    public String toString() {
        return switch (state) {
            case PRESENT -> "Opt[" + value + "]";
            case ERROR -> "Opt.Error[" + error + "]";
            case EMPTY -> "Opt.empty";
        };
    }

    /**
     * 用于tryGet方法的函数式接口，表示可能抛出异常的操作
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T, E extends Throwable> {
        T get() throws E;
    }
}
