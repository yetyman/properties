package binding;

import java.util.ArrayList;
import java.util.List;

public class Property<T> implements IProperty<T> {
    private T value;
    private List<IBinding<T, ?>> outward = null;
    private List<IBinding<?, T>> inward = null;

    public Property() {

    }
    public Property(T val) {
        value = val;
    }

    @Override
    public T getRaw() {
        return value;
    }
    @Override
    public void setRaw(T value) {
        this.value = value;
    }

    @Override
    public T get() {
        updateFromInwardBindings();
        return getRaw();
    }

    @Override
    public void set(T value) {
        this.setRaw(value);
        pushOutwardBindings();
    }

    /**
     * This method dictates how inward binding(ones which set this property) are queried on {@link #get()}.
     * This implementation updates from the first inward binding, and ignores any others.
     * This is the default behaviour, and can be overridden by subclasses to change this behaviour.
     */
    protected void updateFromInwardBindings() {
        if(inward != null && !inward.isEmpty())
            for (IBinding<?, T> binding : inward) {
                binding.push();
                break;//we presume that only one value can be held at once, so only update from the first inward binding
            }
    }

    protected void pushOutwardBindings() {
        if(outward != null)
            for (IBinding<T, ?> binding : outward) {
                binding.push();
            }
    }

    @Override
    public List<IBinding<T, ?>> getListeners() {
        return outward;
    }
    @Override
    public List<IBinding<?, T>> getSubscriptions() {
        return inward;
    }

    @Override
    public IBinding<T, T> generateBinding() {
        OneWayBinding<T> binding = new OneWayBinding<T>(this);
        bindOut(binding);
        return binding;
    }

    @Override
    public boolean unbind(IBinding<?,?> binding) {
        if(binding.getSource() == this) {
            binding.setSource(null);
            return outward != null && outward.remove(binding);
        } else if (binding.isRecipient(this)) {
            binding.removeReceiver(this);
            return inward != null && inward.remove(binding);
        } else
            return false;
    }

    @Override
    public <Q> IBinding<Q, T> bind(IBinding<Q, T> binding) {
        if(binding.getSource() == this)
            throw new IllegalArgumentException("Cannot bind a property to itself");

        binding.registerReceiver(this);

        if(inward == null)
            inward = new ArrayList<>();
        inward.add(binding);

        return binding;
    }

    @Override
    public IBinding<T, T> bindOut(ISetter<T> settable) {
        OneWayBinding<T> binding = new OneWayBinding<T>(this, settable);
        bindOut(binding);
        return binding;
    }


    @Override
    public <Q> IBinding<T, Q> bindOut(IBinding<T, Q> binding) {
        if(binding.getSource() != this)
            throw new IllegalArgumentException("Binding source must be self");
        if(outward != null && outward.contains(binding))
            throw new IllegalArgumentException("Binding already registered");

        if(outward == null)
            outward = new ArrayList<>();

        outward.add(binding);
        return binding;
    }

    @Override
    public DuplexBinding<T,T> bindDuplex(IProperty<T> target) {
        if(outward == null)
            outward = new ArrayList<>();

        DuplexBinding<T,T> binding = new DuplexBinding<T,T>();
        binding.setA(bind(target));
        binding.setB(target.bind(this));
        return binding;
    }

}
