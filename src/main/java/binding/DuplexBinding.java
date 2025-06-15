package binding;

public class DuplexBinding<T, Q> {
    private IBinding<T,Q> a;
    private IBinding<Q,T> b;

    public DuplexBinding() {
    }
    public DuplexBinding(IBinding<T,Q> a, IBinding<Q,T> b) {
        this.a = a;
        this.b = b;
    }

    public IBinding<T,Q> getA() {
        return a;
    }
    public void setA(IBinding<T,Q> a) {
        this.a = a;
    }

    public IBinding<Q,T> getB() {
        return b;
    }
    public void setB(IBinding<Q,T> b) {
        this.b = b;
    }
}
