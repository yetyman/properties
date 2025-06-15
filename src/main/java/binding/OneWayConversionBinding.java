package binding;

import java.util.Objects;
import java.util.function.Function;

public class OneWayConversionBinding<T, Q> implements IBinding<T, Q> {
    private final IGetter<T> source;
    private final Function<T, Q> conversion;
    private ISetter<Q> receiver;

    public OneWayConversionBinding(IGetter<T> source, Function<T, Q> conversion) {
        this.source = source;
        this.conversion = conversion;
    }
    public OneWayConversionBinding(IGetter<T> source, Function<T, Q> conversion, ISetter<Q> receiver) {
        this(source, conversion);
        this.receiver = receiver;
    }

    @Override
    public boolean registerReceiver(ISetter<Q> receiver) {
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

    public ISetter<Q> getReceiver() {
        return receiver;
    }

    @Override
    public boolean forcePush() {
        if(receiver != null) {
            receiver.set(conversion.apply(get()));
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

        if(receiver instanceof ISource<?> g) {
            return !Objects.equals(conversion.apply(srcValue), g.getRaw());
        } else
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
