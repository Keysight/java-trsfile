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
    private static final String UNABLE_TO_SET_PARAMETER =
            "Unable to set parameter `%s` to `%s`: This trace set is in read mode and cannot be modified.";
    private static final String REMOVAL_NOT_SUPPORTED_EXCEPTION =
            "Unable to remove parameter `%s`: This trace set is in read mode and cannot be modified.";
    private static final String MODIFICATION_NOT_SUPPORTED_EXCEPTION = "Unable to modify: This trace set is in read mode and cannot be modified.";

    private static final String UNABLE_TO_ADD_ALL_OF_S_THIS_TRACE_SET_IS_IN_READ_MODE_AND_CANNOT_BE_MODIFIED = "Unable to add all of `%s` : This trace set is in read mode and cannot be modified.";

    private UnmodifiableTraceParameterMap(TraceParameterMap delegate) {
        super.putAll(delegate.copy());
    }

    public static TraceParameterMap of(TraceParameterMap delegate) {
        return new UnmodifiableTraceParameterMap(delegate);
    }

    @Override
    public TraceParameter put(String key, TraceParameter value) {
        throw new UnsupportedOperationException(
                String.format(UNABLE_TO_SET_PARAMETER,
                        key,
                        value.toString()
                )
        );
    }

    @Override
    public TraceParameter remove(Object key) {
        throw new UnsupportedOperationException(
                String.format(REMOVAL_NOT_SUPPORTED_EXCEPTION,
                        key
                )
        );
    }

    @Override
    public void putAll(Map<? extends String, ? extends TraceParameter> m) {
        throw new UnsupportedOperationException(
                String.format(
                        UNABLE_TO_ADD_ALL_OF_S_THIS_TRACE_SET_IS_IN_READ_MODE_AND_CANNOT_BE_MODIFIED,
                        m.toString()
                )
        );
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException(
                MODIFICATION_NOT_SUPPORTED_EXCEPTION
        );
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
        throw new UnsupportedOperationException(MODIFICATION_NOT_SUPPORTED_EXCEPTION);
    }

    @Override
    public TraceParameter putIfAbsent(String key, TraceParameter value) {
        throw new UnsupportedOperationException(
                String.format(UNABLE_TO_SET_PARAMETER,
                        key,
                        value.toString()
                )
        );
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException(
                String.format(
                        REMOVAL_NOT_SUPPORTED_EXCEPTION,
                        key
                )
        );
    }

    @Override
    public boolean replace(String key, TraceParameter oldValue, TraceParameter newValue) {
        throw new UnsupportedOperationException(
                String.format(UNABLE_TO_SET_PARAMETER,
                        key,
                        newValue.toString()
                )
        );
    }

    @Override
    public TraceParameter replace(String key, TraceParameter value) {
        throw new UnsupportedOperationException(
                String.format(UNABLE_TO_SET_PARAMETER,
                        key,
                        value.toString()
                )
        );
    }

    @Override
    public TraceParameter merge(String key, TraceParameter value, BiFunction<? super TraceParameter, ? super TraceParameter, ? extends TraceParameter> remappingFunction) {
        throw new UnsupportedOperationException(MODIFICATION_NOT_SUPPORTED_EXCEPTION);
    }
}
