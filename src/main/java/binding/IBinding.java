package binding;

public interface IBinding<T, Q> {
    default boolean push() {
        if (isInvalidated()) {
            return forcePush();
        }
        return false;
    }
    default T get() {
        if(getSource() instanceof ISource<T> s)
            return s.getRaw();
        else
            return getSource().get();
    }

    void unbind();
    IGetter<T> getSource();
    boolean registerReceiver(ISetter<Q> receiver);
    boolean forcePush();
    boolean isInvalidated();

    void setSource(IGetter<T> source);

    boolean isRecipient(ISetter<?> receiver);

    boolean removeReceiver(ISetter<?> receiver);
}
