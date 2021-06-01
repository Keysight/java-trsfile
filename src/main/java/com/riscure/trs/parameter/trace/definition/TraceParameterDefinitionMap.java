package com.riscure.trs.parameter.trace.definition;

import com.riscure.trs.TRSMetaDataUtils;
import com.riscure.trs.io.LittleEndianInputStream;
import com.riscure.trs.io.LittleEndianOutputStream;
import com.riscure.trs.parameter.TraceParameter;
import com.riscure.trs.parameter.trace.TraceParameterMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents the header definitions of all user-added local parameters in the trace format
 * This explicitly implements LinkedHashMap to ensure that the data is retrieved in the same order as it was added
 */
public class TraceParameterDefinitionMap extends LinkedHashMap<String, TraceParameterDefinition<TraceParameter>> {

    public TraceParameterDefinitionMap() {
        super();
    }

    public TraceParameterDefinitionMap(TraceParameterDefinitionMap toCopy) {
        this();
        putAll(toCopy);
    }

    /**
     * @return a new instance of a TraceParameterDefinitionMap containing all the same values as this one
     */
    public TraceParameterDefinitionMap copy() {
        return new TraceParameterDefinitionMap(this);
    }

    public int totalSize() {
        return values().stream().mapToInt(definition -> definition.getLength() * definition.getType().getByteSize()).sum();
    }

    /**
     * @return this map converted to a byte array, serialized according to the TRS V2 standard definition
     * @throws RuntimeException if the map failed to serialize correctly
     */
    public byte[] serialize() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (LittleEndianOutputStream dos = new LittleEndianOutputStream(baos)) {
            //Write NE
            dos.writeShort(size());
            for (Map.Entry<String, TraceParameterDefinition<TraceParameter>> entry : entrySet()) {
                byte[] nameBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                //Write NL
                dos.writeShort(nameBytes.length);
                //Write N
                dos.write(nameBytes);
                TraceParameterDefinition<? extends TraceParameter> value = entry.getValue();
                value.serialize(dos);
            }
            dos.flush();
            return baos.toByteArray();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @param bytes a valid serialized Trace parameter definition map
     * @return a new populated Trace parameter definition map as represented by the provided byte array
     * @throws RuntimeException if the provided byte array does not represent a valid parameter definition map
     */
    public static TraceParameterDefinitionMap deserialize(byte[] bytes) {
        TraceParameterDefinitionMap result = new TraceParameterDefinitionMap();
        if (bytes != null && bytes.length > 0) {
            try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
                LittleEndianInputStream dis = new LittleEndianInputStream(bais);
                //Read NE
                short numberOfEntries = dis.readShort();
                for (int k = 0; k < numberOfEntries; k++) {
                    String name = TRSMetaDataUtils.readName(dis);
                    //Read definition
                    result.put(name, TraceParameterDefinition.deserialize(dis));
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        return UnmodifiableTraceParameterDefinitionMap.of(result);
    }

    /**
     * Create a set of definitions based on the parameters present in a trace.
     *
     * @param parameters the parameters of the trace
     * @return a set of definitions based on the parameters present in a trace
     */
    public static TraceParameterDefinitionMap createFrom(TraceParameterMap parameters) {
        TraceParameterDefinitionMap definitions = new TraceParameterDefinitionMap();
        if (!parameters.isEmpty()) {
            short offset = 0;
            for (Map.Entry<String, TraceParameter> entry : parameters.entrySet()) {
                definitions.put(entry.getKey(), new TraceParameterDefinition<>(entry.getValue(), offset));
                offset += entry.getValue().length() * entry.getValue().getType().getByteSize();
            }
        }
        return definitions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TraceParameterDefinitionMap that = (TraceParameterDefinitionMap) o;
        if (this.size() != that.size()) return false;

        return this.entrySet().stream().allMatch(e -> e.getValue().equals(that.get(e.getKey())));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
