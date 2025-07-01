package binding;

import java.util.concurrent.atomic.AtomicReference;

//OneWayBinding and Property already handle invalidation logic, all we're doing here is making
// sure the change invocation gets the last value it got before. not necessarily the same as the getter's value.
// There is also no reason the getter can't be changes in process.
public class Listener<T> extends OneWayBinding<T> {
    private IChange<T> change;
    AtomicReference<T> lastSet = new AtomicReference<>(null);

    public Listener(IGetter<T> getter, IChange<T> change) {
        super(getter, null);
        super.registerReceiver(this::setter);
    }

    public void setter(T s) {
        change.notify(this.getSource(), lastSet.getAndSet(s), lastSet.get());
    }

    //registerReceiver is not valid on a listener. use registerChange instead
    @Override
    public boolean registerReceiver(ISetter<T> receiver) {
        return false;
    }

    //removeReceiver is not valid on a listener. use registerChange instead
    @Override
    public boolean removeReceiver(ISetter<?> receiver) {
        return false;
    }

    public IChange<T> getChange() {
        return change;
    }

    public void setChange(IChange<T> change) {
        this.change = change;
    }

    public boolean isChange(IChange<?> change) {
        return change == this.change;
    }

    @Override
    public boolean isInvalidated() {
        return lastSet.get() != getSource().get();
    }

}
