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
    private static final UnsupportedOperationException MODIFICATION_NOT_SUPPORTED_EXCEPTION =
            new UnsupportedOperationException("This trace set is in read mode, and the parameters cannot be modified.");

    private UnmodifiableTraceParameterDefinitionMap(TraceParameterDefinitionMap delegate) {
        super.putAll(delegate.copy());
    }

    public static TraceParameterDefinitionMap of(TraceParameterDefinitionMap delegate) {
        return new UnmodifiableTraceParameterDefinitionMap(delegate);
    }

    @Override
    public TraceParameterDefinition<TraceParameter> put(String key, TraceParameterDefinition<TraceParameter> value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameterDefinition<TraceParameter> remove(Object key) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public void putAll(Map<? extends String, ? extends TraceParameterDefinition<TraceParameter>> m) {
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
    public Collection<TraceParameterDefinition<TraceParameter>> values() {
        return Collections.unmodifiableCollection(super.values());
    }

    @Override
    public Set<Map.Entry<String, TraceParameterDefinition<TraceParameter>>> entrySet() {
        return Collections.unmodifiableSet(super.entrySet());
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super TraceParameterDefinition<TraceParameter>, ? extends TraceParameterDefinition<TraceParameter>> function) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameterDefinition<TraceParameter> putIfAbsent(String key, TraceParameterDefinition<TraceParameter> value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public boolean replace(String key, TraceParameterDefinition<TraceParameter> oldValue, TraceParameterDefinition<TraceParameter> newValue) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameterDefinition<TraceParameter> replace(String key, TraceParameterDefinition<TraceParameter> value) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }

    @Override
    public TraceParameterDefinition<TraceParameter> merge(String key, TraceParameterDefinition<TraceParameter> value, BiFunction<? super TraceParameterDefinition<TraceParameter>, ? super TraceParameterDefinition<TraceParameter>, ? extends TraceParameterDefinition<TraceParameter>> remappingFunction) {
        throw MODIFICATION_NOT_SUPPORTED_EXCEPTION;
    }
}
