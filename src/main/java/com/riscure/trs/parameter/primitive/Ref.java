package com.riscure.trs.parameter.primitive;

public class Ref {
    private final String value;
    private final int index;

    Ref(String value, int index) {
        this.value = value;
        this.index = index;
    }

    public Ref(int index) {
        this("", index);
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ref ref = (Ref) o;

        return getIndex() == ref.getIndex();
    }

    @Override
    public int hashCode() {
        int result = getValue().hashCode();
        result = 31 * result + getIndex();
        return result;
    }

    @Override
    public String toString() {
        return "Ref{" +
                "name='" + value + '\'' +
                ", index=" + index +
                '}';
    }
}
