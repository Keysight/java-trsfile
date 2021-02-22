package com.riscure.trs.parameter.traceset;

import java.util.*;
import java.util.function.BiFunction;

/**
 * This class represents the header definitions of all user-added global parameters of the trace set
 *
 * This map is read from the file, and is therefore unmodifiable.
 */
public class UnmodifiableTraceSetParameterMap extends TraceSetParameterMap {
    private static final UnsupportedOperationException MODIFICATION_NOT_SUPPORTED_EXCEPTION =
            new UnsupportedOperationException("This trace set is in read mode, and the parameters cannot be modified.");

    private UnmodifiableTraceSetParameterMap(TraceSetParameterMap delegate) {
        super.putAll(delegate.copy());
    }

    public static TraceSetParameterMap of(TraceSetParameterMap delegate) {
        return new UnmodifiableTraceSetParameterMap(delegate);
    }

    @Override
    public TraceSetParameter put(String key, TraceSetParameter value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceSetParameter remove(Object key) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public void putAll(Map<? extends String, ? extends TraceSetParameter> m) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public void clear() {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public Set<String> keySet() {
        return Collections.unmodifiableSet(super.keySet());
    }

    @Override
    public Collection<TraceSetParameter> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    @Override
    public Set<Map.Entry<String, TraceSetParameter>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super TraceSetParameter, ? extends TraceSetParameter> function) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceSetParameter putIfAbsent(String key, TraceSetParameter value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public boolean replace(String key, TraceSetParameter oldValue, TraceSetParameter newValue) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceSetParameter replace(String key, TraceSetParameter value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceSetParameter merge(String key, TraceSetParameter value, BiFunction<? super TraceSetParameter, ? super TraceSetParameter, ? extends TraceSetParameter> remappingFunction) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }
}
