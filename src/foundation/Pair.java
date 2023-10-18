package foundation;

import java.util.Objects;

public class Pair<T1, T2> {
    final private T1 first;
    final private T2 sceond;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.sceond = second;
    }

    public T1 first() {
        return first;
    }

    public T2 second() {
        return sceond;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) && Objects.equals(sceond, pair.sceond);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, sceond);
    }
}

