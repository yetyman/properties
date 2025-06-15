package binding;

import java.util.List;

public interface IReceiver<T> extends ISetter<T> {
    void setRaw(T value);
    default void set(T value){
        setRaw(value);
    }

    default IBinding<T, T> bind(IGetter<T> target){
        IBinding<T, T> binding = null;
        if(target instanceof ISource<T> s)
            binding = s.generateBinding();
        else
            binding = new OneWayBinding<T>(target, this);

        this.bind(binding);

        return binding;
    }

    <Q> IBinding<Q, T> bind(IBinding<Q, T> target);
    List<IBinding<?, T>> getSubscriptions();
    default IBinding<T, T> generateBinding() {
        return new OneWayBinding<T>(null, this);
    };
    boolean unbind(IBinding<?,?> binding);
}
