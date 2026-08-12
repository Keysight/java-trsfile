# TRS File Format Specification

This document describes the `.trs` file format implemented by this library.

## 1. General rules

- **Byte order:** little-endian for all multi-byte numeric values.
- **Strings:** UTF-8 encoded.
- **Container style:** the file starts with a metadata TLV stream, followed by packed trace records.

## 2. File layout

```
[metadata TLVs ...][TRACE_BLOCK][trace 0][trace 1]...[trace N-1]
```

Metadata is read until tag `TRACE_BLOCK` (`0x5F`) is reached.

Each trace record has fixed size:

`TITLE_SPACE + DATA_LENGTH + (NUMBER_OF_SAMPLES * sample_byte_size)`

## 3. Metadata TLV encoding

Each metadata item is encoded as:

1. `T`: 1 byte tag id
2. `L`: variable-length length field
3. `V`: payload bytes

### 3.1 Length encoding

- If `L <= 0x7F`, it is written as one byte.
- If `L > 0x7F`, the first byte is `0x80 + n`, where `n` is the number of following length bytes.
- Those `n` bytes store `L` in little-endian order.

The reader accepts metadata lengths up to `0xFFFFFF`.

## 4. Core metadata tags

| Tag | Id | Payload |
|---|---:|---|
| NUMBER_OF_TRACES | `0x41` | int32 |
| NUMBER_OF_SAMPLES | `0x42` | int32 |
| SAMPLE_CODING | `0x43` | uint8 enum |
| DATA_LENGTH | `0x44` | uint16 |
| TITLE_SPACE | `0x45` | uint8 |
| GLOBAL_TITLE | `0x46` | string |
| DESCRIPTION | `0x47` | string |
| OFFSET_X | `0x48` | int32 |
| LABEL_X | `0x49` | string |
| LABEL_Y | `0x4A` | string |
| SCALE_X | `0x4B` | float32 |
| SCALE_Y | `0x4C` | float32 |
| TRACE_OFFSET | `0x4D` | int32 |
| LOGARITHMIC_SCALE | `0x4E` | boolean |
| TRS_VERSION | `0x4F` | uint8 |
| TRACE_BLOCK | `0x5F` | empty |
| TRACE_SET_PARAMETERS | `0x76` | serialized map |
| TRACE_PARAMETER_DEFINITIONS | `0x77` | serialized map |
| PADDING | `0xFF` | bytes |

Unknown tags are skipped using their declared length.

## 5. Sample coding

`SAMPLE_CODING` uses these values:

| Code | Meaning | Bytes/sample |
|---:|---|---:|
| `0x01` | BYTE | 1 |
| `0x02` | SHORT | 2 |
| `0x04` | INT | 4 |
| `0x14` | FLOAT | 4 |

Samples are stored as raw little-endian values in the chosen encoding.

## 6. Trace record layout

For each trace:

1. **Title**: exactly `TITLE_SPACE` bytes
2. **Data**: exactly `DATA_LENGTH` bytes
3. **Samples**: `NUMBER_OF_SAMPLES * sample_byte_size` bytes

If a title is all whitespace when read, the reader may synthesize:

`<GLOBAL_TITLE> <trace_index>`

## 7. Version behavior

### 7.1 Version 1

- The `DATA_LENGTH` bytes are treated as opaque legacy trace data.
- On read, non-empty legacy data is exposed as a trace parameter named `LEGACY_DATA`.

### 7.2 Version 2+

Version 2 adds:

- `TRACE_SET_PARAMETERS`
- `TRACE_PARAMETER_DEFINITIONS`

Per-trace parameter data is stored only as the packed value bytes in the trace record; the names, types, lengths, and offsets live in the header definition map.

### 7.3 Version 3 in this implementation

- Writers default `TRS_VERSION` to `3`.
- Writers reserve about `1_000_000` bytes for metadata by inserting `PADDING` before `TRACE_BLOCK`.
- This allows later header rewrites without moving the trace data.

## 8. Trace set parameters

`TRACE_SET_PARAMETERS` stores global custom key/value pairs.

Serialized form:

1. `NE`: uint16 entry count
2. Repeated `NE` times:
   - `NL`: uint16 name length
   - `N`: UTF-8 name bytes
   - `TYPE`: uint8 parameter type
   - `LEN`: uint16 element count
   - `VALUE`: serialized value bytes

Supported parameter types:

| Type | Code | Element size |
|---|---:|---:|
| BYTE | `0x01` | 1 |
| SHORT | `0x02` | 2 |
| INT | `0x04` | 4 |
| LONG | `0x08` | 8 |
| FLOAT | `0x14` | 4 |
| DOUBLE | `0x18` | 8 |
| STRING | `0x20` | UTF-8 bytes |
| BOOL | `0x31` | 1 |

Length 1 values are treated as scalars by the API, but they still serialize through the same map structure.

## 9. Trace parameter definitions

`TRACE_PARAMETER_DEFINITIONS` defines the per-trace parameter block layout.

Serialized form:

1. `NE`: uint16 entry count
2. Repeated `NE` times:
   - `NL`: uint16 name length
   - `N`: UTF-8 name bytes
   - `TYPE`: uint8 parameter type
   - `LEN`: uint16 element count
   - `OFFSET`: uint16 byte offset within the trace data block

The order of entries is preserved and used when packing/unpacking trace parameter bytes.

## 10. Per-trace parameter block

For version 2+ files, the trace data block is the concatenation of the defined parameters in header order.

`DATA_LENGTH` must equal:

`sum(parameter_length * type_byte_size)`

## 11. Practical constraints

- All traces in a file must have the same number of samples.
- All traces must have the same data length.
- String fields are truncated or padded to their reserved byte length when writing.
- Unknown metadata tags are tolerated on read.

