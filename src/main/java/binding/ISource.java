package binding;


import java.util.List;

public interface ISource<T> extends IGetter<T>{
    T getRaw();
    default T get(){
        return getRaw();
    }
    default IBinding<T, T> bindOut(ISetter<T> settable) {
        IBinding<T, T> binding = generateBinding();

        if (settable instanceof IReceiver<T> r)
            r.bind(binding);

        return binding;
    }
    default Listener<T> bindOut(IChange<T> change) {
        Listener<T> binding = generateListener();

        binding.setChange(change);

        return binding;
    }

    <Q> IBinding<T, Q> bindOut(IBinding<T, Q> binding);

    List<IBinding<T, ?>> getListeners();
    default IBinding<T, T> generateBinding() {
        return new OneWayBinding<T>(this, null);
    };
    default Listener<T> generateListener() {
        return new Listener<>(this, null);
    };
    boolean unbind(IBinding<?,?> binding);

}
