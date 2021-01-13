package com.riscure.trs.parameter.trace;

import com.riscure.trs.parameter.TraceParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * This is an unmodifiable version of a trace parameter map. This should always be used for a trace read back from a file.
 */
public class UnmodifiableTraceParameterMap extends TraceParameterMap {
    private static final UnsupportedOperationException MODIFICATION_NOT_SUPPORTED_EXCEPTION =
            new UnsupportedOperationException("This trace set is in read mode, and the parameters cannot be modified.");

    private UnmodifiableTraceParameterMap(TraceParameterMap delegate) {
        super.putAll(delegate);
    }

    public static TraceParameterMap of(TraceParameterMap delegate) {
        return new UnmodifiableTraceParameterMap(delegate);
    }

    @Override
    public TraceParameter put(String key, TraceParameter value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameter remove(Object key) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public void putAll(Map<? extends String, ? extends TraceParameter> m) {
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
    public Collection<TraceParameter> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    @Override
    public Set<Map.Entry<String, TraceParameter>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super TraceParameter, ? extends TraceParameter> function) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameter putIfAbsent(String key, TraceParameter value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public boolean replace(String key, TraceParameter oldValue, TraceParameter newValue) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameter replace(String key, TraceParameter value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameter merge(String key, TraceParameter value, BiFunction<? super TraceParameter, ? super TraceParameter, ? extends TraceParameter> remappingFunction) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }
}
