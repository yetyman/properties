package binding;

@FunctionalInterface
public interface IChange<T> {
    void notify(Object source, T was, T is);
}
