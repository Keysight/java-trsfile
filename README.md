# Inspector Trace Set `.trs` file support for Java

Riscure Inspector uses the `.trs` file format to save and read traces from disk. To better assist reading and writing trace set files from third parties, Riscure published this Java library.

## Quick start
This library supports reading and writing of `.trs` files, but it does not support modifying existing `.trs` files.

### Installation
#### Maven central
This library is available on Maven Central. Use the following information to include it in your pom.xml:

    <dependency>
      <groupId>com.riscure</groupId>
      <artifactId>trsfile</artifactId>
      <version>2.2.5</version>
    </dependency>

#### Basic
Alternatively, simply include the latest release of the library jar in your project.

### General use tips
##### File creation
When not supplied at creation time, the following parameters are defined based on the values of the first added trace:

     NUMBER_OF_SAMPLES: 
        Every next trace needs to have the same number of samples.
     DATA_LENGTH: 
        Every next trace needs to have the same binary data length.
     TITLE_SPACE: 
        Every next trace's title will be at most the length of the title of the first trace. 
     SCALE_X: 
        Defined for the whole set based on the sampling frequency of the first trace.
     SAMPLE_CODING: 
        Defined for the whole set based on the values of the first trace. 
        Since this is dynamically decided based on the values of the first trace, 
        it can be beneficial to force floats by calling Trace.forceFloatCoding() for the first trace. 

### Using the TRS V2 additions
As of release 2.0, two additional provisions were added to the .trs format: Trace Set Parameters and Trace Parameters. Note that TRS V2 is backwards compatible with TRS V1. However, as can be expected, the additional information will not be available when using a pre-V2 reader.
#### Trace Set Parameters
Trace Set Parameters are user-defined key value pairs that can be used to save global information about the trace set. The following types of data can be used (also found in com.riscure.trs.enums.ParameterType):
     
     BYTE: 1 byte integer
     SHORT: 2 byte integer
     INT: 4 byte integer
     FLOAT: 4 byte floating point
     LONG: 8 byte integer
     DOUBLE: 8 byte floating point
     STRING: UTF-8 encoded string value
Each type also supports array creation. Please note that there is no provision for arrays of length 1. An array of length 1 will always be saved and loaded as a single value.
##### Using Trace Set Parameters
Global parameters can be added by creating a `TraceSetParameterMap` object when creating a trace set. The following java code shows an example:
```java
TRSMetaData metaData = new TRSMetaData();
metaData.put(TRSTag.TRS_VERSION, 2);
TraceSetParameterMap parameters = new TraceSetParameterMap();
parameters.put("BYTE", (byte)1);
parameters.put("SHORT", (short)2);
parameters.put("INT", 3);
parameters.put("FLOAT", (float)4);
parameters.put("LONG", (long)5);
parameters.put("DOUBLE", (double)6);
parameters.put("STRING", String.format("%3d", 7));
parameters.put("BYTEARRAY", new byte[]{(byte) 8, (byte) 9, (byte) 0});
parameters.put("SHORTARRAY", new short[]{(short) 1, (short) 2, (short) 3});
parameters.put("INTARRAY", new int[]{4, 5, 6});
parameters.put("FLOATARRAY", new float[]{(float) 7, (float) 8, (float) 9});
parameters.put("LONGARRAY", new long[]{0, 1, 2});
parameters.put("DOUBLEARRAY", new double[]{3, 4, 5});
metaData.put(TRSTag.TRACE_SET_PARAMETERS, parameters);
TraceSet ts = TraceSet.create("PATH_TO_NEW_TRS_FILE", metaData);
//Add traces here
ts.close();
```

#### Trace Parameters
Trace Parameters behave very similar to Trace Set Parameters from a user perspective. They are values that can be added to _every_ trace, describing specific values that can vary between traces. The data types that can be used are the same as for Trace Set Parameters. However, there are several details that are different:

1. The length of the added information *must* be the same for every trace. This means that the first trace added to the trace set dictates the length of both arrays _and_ strings. If a longer string is added later, it will be truncated.
2. The length of every parameter is saved in the header at creation time, in a structure called `TraceParameterDefinitionMap`. This structure is used when reading out the traces to determine the structure of the included data. This information is _not_ added to the individual traces themselves.
3. Going forward, there will be pre-defined tags used to mark important information:
        SAMPLES: An alternative for saving the samples of a trace. This may in the future replace the predefined trace structure of title-data-samples.
        TITLE: An alternative for saving the title of a trace. This may in the future replace the predefined trace structure of title-data-samples.

Any additional tags can be added based on the application.  

##### Using Trace Parameters
Local parameters can be added by creating a `TraceParameterMap` object when creating a trace. The following java code shows an example:
```java
TraceParameterMap parameters = new TraceParameterMap();
parameters.put("BYTE", (byte)1);
parameters.put("SHORT", (short)2);
parameters.put("INT", 3);
parameters.put("FLOAT", 4.0f);
parameters.put("LONG", 5L);
parameters.put("DOUBLE", 6.0);
parameters.put("STRING", "A string");
parameters.put("BYTEARRAY", new byte[]{(byte) 1, (byte) 2, (byte) 3});
parameters.put("SHORTARRAY", new short[]{(short) 4, (short) 5, (short) 6});
parameters.put("INTARRAY", new int[]{7, 8, 9});
parameters.put("FLOATARRAY", new float[]{0.0f, 1.0f, 2.0f});
parameters.put("LONGARRAY", new long[]{3L, 4L, 5L});
parameters.put("DOUBLEARRAY", new double[]{6.0, 7.0, 8.0});
Trace.create("trace title", new float[0], parameters);
```
Note that the previously mentioned `TraceParameterDefinitionMap` is created automatically when adding the first trace.

### Reading `.trs` files
```java
import com.riscure.trs.Trace;
import com.riscure.trs.TraceSet;
import com.riscure.trs.enums.TRSTag;

public class TRSOpen {
    public static void main(String[] args) {
        TraceSet ts = TraceSet.open("PATH_TO_TRS_FILE");
        int numberOfTraces = ts.getMetaData().getInt(TRSTag.NUMBER_OF_TRACES);
        for (int k = 0; k < numberOfTraces; k++) {
            Trace t = ts.getTrace(k);
            float[] samples = t.getSample();
            //perform operations
        }
        ts.close();
    }
}
```

### Creating `.trs` files
```java
import com.riscure.trs.Trace;
import com.riscure.trs.TraceSet;

public class TRSCreate {
    public static void main(String[] args) {
        TraceSet ts = TraceSet.create("PATH_TO_NEW_FILE");
        //create sampleData and for the trace
        ts.add(new Trace("Title", binaryData, sampleData));
        //add more traces...
        ts.close();
    }   
}
```

## MATLAB/Octave use
The main goal of this library is to allow users to open and create Inspector trace sets in MATLAB/Octave. Below is a short example of how to use this library in a MATLAB environment.
```MATLAB
javaaddpath("PATH_TO_JAR_FILE\trsfile.jar");
traceSet = javaMethod("create", "com.riscure.trs.TraceSet", "PATH_TO_TRACES\TRACESET_NAME.trs");
% adding some extra spaces after the title to leave room for longer titles
traceSet.add(com.riscure.trs.Trace("Trace 0        ", [0x0, 0x0f, 0xA, 0x3C], [0.0, 0.1, 0.2, 0.3]));
% add more traces...
traceSet.close();
```

## Riscure Inspector use

Riscure Inspector has its own definitions that are used to tag specific types of data. Note that many of the type are namespaced; The ':' character is interpreted as a namespace separator for the purpose of grouping values together. 
We will try to keep a list here of standardized known tags as best we can.

### Trace Set Parameters

Name                                            | Type          | Description |
----                                            | ----          | ----------- |
KEY                                             | BYTE ARRAY    | The value used as key for a cryptographic operation (applicable to the entire set)
SETUP:OSCILLOSCOPE:RANGE                        | FLOAT         | The range (V) of the oscilloscope used for this measurement
SETUP:OSCILLOSCOPE:COUPLING                     | INTEGER       | The coupling of the oscilloscope used for this measurement. Currently encoded as 0 for AC and 1 for DC
SETUP:OSCILLOSCOPE:OFFSET                       | FLOAT         | The offset (V) of the oscilloscope used for this measurement
SETUP:OSCILLOSCOPE:INPUT_IMPEDANCE              | FLOAT         | The input impedance (Ω) of the oscilloscope used for this measurement
SETUP:OSCILLOSCOPE:DEVICE_ID                    | STRING        | The device ID of the oscilloscope used for this measurement. This is commonly the name, type, and/or serial number
SETUP:OSCILLOSCOPE:ACTIVE_CHANNEL_COUNT         | INTEGER       | The number of active channels of the oscilloscope used for this measurement
SETUP:OSCILLOSCOPE:COUNT                        | INTEGER       | The number of oscilloscopes used for this measurement
SETUP:ICWAVES:FILTER:TYPE                       | INTEGER       | The type of filter used by the icWaves during this measurement: 0 = None, 1 = Lowpass, 2 = Highpass, 3 = Narrow Baseband
SETUP:ICWAVES:FILTER:FREQUENCY                  | INTEGER       | The center frequency of the filter used by the icWaves during this measurement
SETUP:ICWAVES:FILTER:RANGE                      | INTEGER       | The range of the filter used by the icWaves during this measurement
SETUP:ICWAVES:EXT_CLK:ENABLED                   | BOOLEAN       | Whether the external clock input of the icWaves was used during this measurement
SETUP:ICWAVES:EXT_CLK:THRESHOLD                 | FLOAT         | The threshold of the external clock used by the icWaves during this measurement. This dictates the minimum voltage level that will be recognized as a logical '1'
SETUP:ICWAVES:EXT_CLK:FREQUENCY                 | FLOAT         | The input frequency of the external clock used by the icWaves during this measurement
SETUP:ICWAVES:EXT_CLK:TIMEBASE                  | INTEGER       | The divider applied to the external clock before multiplication. The value (n) should be in the range [0, 15], indicating a division by 2^n. i.e. '0' divides by 1, '1' divides by 2, '2' divides by 4, etc
SETUP:ICWAVES:EXT_CLK:MULTIPLIER                | INTEGER       | The multiplier applied to the external clock used by the icWaves during this measurement. The multiplier must fall in the range [1, 20]. After multiplication, the resulting clock signal must fall in the range [10MHz, 200MHz]
SETUP:ICWAVES:EXT_CLK:PHASE_SHIFT               | INTEGER       | The phase shift applied to the multiplied external clock used by the icWaves during this measurement
SETUP:ICWAVES:EXT_CLK:RESAMPLER_MASK_ENABLED    | BOOLEAN       | Whether the external clock resampler mask of the icWaves was used during this measurement
SETUP:ICWAVES:EXT_CLK:RESAMPLER_MASK            | INTEGER       | How the resampling was applied. '1' means 'multiple samples were combined into one', '0' means 'first sample was picked, the rest was discarded'
SETUP:XYZ:GRID_COUNT_X                          | INTEGER       | The number of steps in the X direction in the XYZ scanning grid
SETUP:XYZ:GRID_COUNT_Y                          | INTEGER       | The number of steps in the Y direction in the XYZ scanning grid
SETUP:XYZ:MEASUREMENTS_PER_SPOT                 | INTEGER       | The number of measurements performed in every location before proceeding to the next location
SETUP:XYZ:REFERENCE_POINTS                      | DOUBLE ARRAY  | The set of locations that define the scanning grid. This is formatted as a set of locations [NE, NW, SW], where each location is represented by 3 doubles [X, Y, Z]
X_OFFSET                                        | INTEGER       | The offset of the x-axis relative to the origin
X_SCALE                                         | FLOAT         | The value of the x-axis unit represented by the distance between two consecutive sample points. The sampling frequency can be calculated by computing '1 / X_SCALE' 
Y_SCALE                                         | FLOAT         | The value of the y-axis unit represented by the distance between two consecutive sample values. This is normally only used if the encoding of a sample is an integer type. For float and double encoding, this value is set to 1. 
TRACE_OFFSET                                    | INTEGER       | The index of the first trace to appear in this set
DISPLAY_HINT:X_LABEL                            | STRING        | The label of the x-axis (commonly used to denote the axis unit)
DISPLAY_HINT:Y_LABEL                            | STRING        | The label of the y-axis (commonly used to denote the axis unit)
DISPLAY_HINT:USE_LOG_SCALE                      | BOOLEAN       | Whether the traces in this trace set are best displayed using a logarithmic Y scale
DISPLAY_HINT:NUM_TRACES_SHOWN                   | INTEGER       | An indication of how many traces should be displayed at the same time to best represent the information in this trace set
DISPLAY_HINT:TRACES_OVERLAP                     | BOOLEAN       | Whether traces should be overlapped in the same window if more than 1 is displayed
TVLA:SETn                                       | STRING        | Used to save the names of the TVLA sets represented in this trace set, where 'n' should be replaced by the set index. e.g. a trace set containing two sets 'RANDOM' and 'FIXED' would define 'TVLA:SET0 = RANDOM' and TVLA:SET1 = FIXED'. The value stored per trace in 'TVLA_SET_INDEX' should correspond to the value of n.
TVLA:CIPHER                                     | STRING        | The string definition of the target cipher of this TVLA set

### Trace Parameters

Name                        | Type          | Description |
----                        | ----          | ----------- |
INPUT                       | BYTE ARRAY    | The value used as input for a cryptographic operation
OUTPUT                      | BYTE ARRAY    | The value returned by a cryptographic operation
KEY                         | BYTE ARRAY    | The value used as key for a cryptographic operation (in case different keys are used during the acquisition campaign)
TIMEOUT                     | BOOLEAN ARRAY | Whether the measurement encountered a time out
OSCILLOSCOPE:CHANNEL_INDEX  | BYTE          | The index of the channel used to measure this trace
PATTERN_TYPE                | BYTE          | The id of the attacked pattern of a public key attack
TVLA_SET_INDEX              | SHORT         | The index of TVLA set (defined in the set parameters) that this trace belongs to
SOURCE_TRACE_INDEX          | INTEGER       | The index of the corresponding trace in the source trace set. This can be used in cases where the trace set is re-ordered in any way to keep track of the original location.
SOURCE_TRACE_SAMPLE_INDEX   | INTEGER       | The index of the corresponding sample in the source trace. This can be used in cases where the samples are shifted or re-ordered in any way to keep track of the original sample index.
FILTER:LOW_BOUND            | FLOAT         | The lower bound of the frequency filter applied to this trace
FILTER:HIGH_BOUND           | FLOAT         | The upper bound of the frequency filter applied to this trace
FILTER:SEGMENTS:VALUES      | FLOAT ARRAY   | The values describing the filter segments
FILTER:SEGMENTS:COUNT       | INTEGER       | The number of value segments in the filter
XYZ:RELATIVE_POSITION       | DOUBLE ARRAY  | The XYZ position where this trace was measured relative to the origin specified in the set parameters

## Testing
This is a Maven project. Tests are run automatically when building the Maven project.

## License
[BSD 3-Clause Clear License](https://choosealicense.com/licenses/bsd-3-clause-clear/)
