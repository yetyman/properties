package binding;

public class OneWayBinding<T> implements IBinding<T, T> {
    private final IGetter<T> source;
    private ISetter<T> receiver;

    public OneWayBinding(IGetter<T> source) {
        this.source = source;
    }
    public OneWayBinding(IGetter<T> source, ISetter<T> receiver) {
        this(source);
        this.receiver = receiver;
    }

    @Override
    public boolean registerReceiver(ISetter<T> receiver) {
        if(this.receiver != null)
            return false;

        this.receiver = receiver;
        return true;
    }

    @Override
    public void unbind() {
        if(source instanceof ISource<?> s)
            s.unbind(this);
    }

    @Override
    public IGetter<T> getSource() {
        return source;
    }

    public ISetter<T> getReceiver() {
        return receiver;
    }

    @Override
    public boolean forcePush() {
        if(receiver != null) {
            receiver.set(get());
            return true;
        }
        return false;
    }

    @Override
    public boolean isInvalidated() {
        T srcValue;
        if(getSource() instanceof ISource<T> s)
            srcValue = s.getRaw();
        else
            srcValue = getSource().get();

        if(receiver instanceof ISource<?> g)
            return srcValue != g.getRaw();
        else
            return true;
    }

    @Override
    public void setSource(IGetter<T> source) {
        throw new UnsupportedOperationException("Cannot change source of OneWayBinding");
    }

    @Override
    public boolean isRecipient(ISetter<?> receiver) {
        return this.receiver == receiver;
    }

    @Override
    public boolean removeReceiver(ISetter<?> receiver) {
        if(receiver == this.receiver) {
            this.receiver = null;
            return true;
        } else
        {
            return false;
        }
    }
}
