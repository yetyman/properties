package binding;

public interface IProperty<T> extends ISource<T>, IReceiver<T> {
    DuplexBinding<T,T> bindDuplex(IProperty<T> target);
    default IBinding<T, T> generateBinding() {
        return new OneWayBinding<T>(this, this);
    };
}
