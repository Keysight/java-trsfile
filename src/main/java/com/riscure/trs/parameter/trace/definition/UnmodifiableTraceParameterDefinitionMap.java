package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.parameter.TraceParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * This class represents the header definitions of all user-added local parameters of the trace set
 *
 * This map is read from the file, and is therefore unmodifiable.
 */
public class UnmodifiableTraceParameterDefinitionMap extends TraceParameterDefinitionMap {
    private static final String UNABLE_TO_SET_PARAMETER =
            "Unable to set parameter `%s` to `%s`: This trace set is in read mode and cannot be modified.";
    private static final String REMOVAL_NOT_SUPPORTED_EXCEPTION =
            "Unable to remove parameter `%s`: This trace set is in read mode and cannot be modified.";
    private static final String MODIFICATION_NOT_SUPPORTED_EXCEPTION = "Unable to modify: This trace set is in read mode and cannot be modified.";

    private static final String UNABLE_TO_ADD_ALL_OF_S_THIS_TRACE_SET_IS_IN_READ_MODE_AND_CANNOT_BE_MODIFIED = "Unable to add all of `%s` : This trace set is in read mode and cannot be modified.";


    private UnmodifiableTraceParameterDefinitionMap(TraceParameterDefinitionMap delegate) {
        super.putAll(delegate.copy());
    }

    public static TraceParameterDefinitionMap of(TraceParameterDefinitionMap delegate) {
        return new UnmodifiableTraceParameterDefinitionMap(delegate);
    }

    @Override
    public TraceParameterDefinition<TraceParameter> put(String key, TraceParameterDefinition<TraceParameter> value) {
        throw new UnsupportedOperationException(
                String.format(UNABLE_TO_SET_PARAMETER,
                        key,
                        value.toString()
                )
        );
    }

    @Override
    public TraceParameterDefinition<TraceParameter> remove(Object key) {
        throw new UnsupportedOperationException(
                String.format(REMOVAL_NOT_SUPPORTED_EXCEPTION,
                        key
                )
        );
    }

    @Override
    public void putAll(Map<? extends String, ? extends TraceParameterDefinition<TraceParameter>> m) {
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
    public Collection<TraceParameterDefinition<TraceParameter>> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    @Override
    public Set<Map.Entry<String, TraceParameterDefinition<TraceParameter>>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super TraceParameterDefinition<TraceParameter>, ? extends TraceParameterDefinition<TraceParameter>> function) {
        throw new UnsupportedOperationException(MODIFICATION_NOT_SUPPORTED_EXCEPTION);
    }

    @Override
    public TraceParameterDefinition<TraceParameter> putIfAbsent(String key, TraceParameterDefinition<TraceParameter> value) {
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
    public boolean replace(String key, TraceParameterDefinition<TraceParameter> oldValue, TraceParameterDefinition<TraceParameter> newValue) {
        throw new UnsupportedOperationException(
                String.format(UNABLE_TO_SET_PARAMETER,
                        key,
                        newValue.toString()
                )
        );
    }

    @Override
    public TraceParameterDefinition<TraceParameter> replace(String key, TraceParameterDefinition<TraceParameter> value) {
        throw new UnsupportedOperationException(
                String.format(UNABLE_TO_SET_PARAMETER,
                        key,
                        value.toString()
                )
        );
    }

    @Override
    public TraceParameterDefinition<TraceParameter> merge(String key, TraceParameterDefinition<TraceParameter> value, BiFunction<? super TraceParameterDefinition<TraceParameter>, ? super TraceParameterDefinition<TraceParameter>, ? extends TraceParameterDefinition<TraceParameter>> remappingFunction) {
        throw new UnsupportedOperationException(MODIFICATION_NOT_SUPPORTED_EXCEPTION);
    }
}
