# Inspector Trace Set `.trs` file support for Java

Riscure Inspector uses the `.trs` file format to save and read traces from disk. To better assist reading and writing trace set files from third parties, Riscure published this Java library.

## Quick start
This library supports reading and writing of `.trs` files, but it does not support modifying existing `.trs` files.

### Installation
Simply include the latest release of the library jar in your project. It is currently not available through any distribution networks (Maven central, etc..). If this changes, it will be stated here.

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
Trace Set Parameters are user-defined key value pairs that can be used to save global information about the trace set. The following types of data can be used (also found in com.riscure.trs.parameter.ParameterTypes):
     
     BYTE: 1 byte integer
     SHORT: 2 byte integer
     INT: 4 byte integer
     FLOAT: 4 byte floating point
     LONG: 8 byte integer
     DOUBLE: 8 byte floating point
     STRING: UTF-8 encoded string value
Each type also supports array creation. Please note that there is no provision for arrays of length 1. An array of length 1 will always be saved and loaded as a single value.
##### Using Trace Set Parameters
Global parameters can be added by creating a `TraceSetParameters` object when creating a trace set. The following java code shows an example:
```java
TRSMetaData metaData = new TRSMetaData();
metaData.put(TRSTag.TRS_VERSION, 2);
TraceSetParameters parameters = new TraceSetParameters();
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
TraceParameters behave very similar to Trace Set Parameters from a user perspective. They are values that can be added to _every_ trace, describing specific values that can vary between traces. The data types that can be used are the same as for Trace Set Parameters. However, there are several details that are different:

    1. The length of the added information *must* be the same for every trace. This means that the first trace added to the trace set dictates the length of both arrays _and_ strings. If a longer string is added later, it will be truncated.
    2. The length of every parameter is saved in the header at creation time, in a structure called `TraceParameterDefinitions`. This structure is used when reading out the traces to determine the structure of the included data. This information is _not_ added to the individual traces themselves.
    3. Going forward, there will be pre-defined tags used to mark important information:
        SAMPLES: An alternative for saving the samples of a trace. This may in the future replace the predefined trace structure of title-data-samples.
        TITLE: An alternative for saving the title of a trace. This may in the future replace the predefined trace structure of title-data-samples.

Any additional tags can be added based on the application. Riscure Inspector will have its own definitions. We will try to keep a list here of standardized known tags as best we can.

    - INPUT:    (BYTE ARRAY): the value used as input for a cryptographic operation
    - OUTPUT:   (BYTE ARRAY): the value returned by a cryptographic operation
    - KEY:      (BYTE ARRAY): the value used as key for a cryptographic operation

##### Using Trace Parameters
Local parameters can be added by creating a `TraceParameters` object when creating a trace. The following java code shows an example:
```java
TraceParameters parameters = new TraceParameters();
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
Note that the previously mentioned `TraceParameterDefinitions` are created automatically when adding the first trace.

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


## Documentation
TODO

## Testing
This is a Maven project. Tests are run automatically when building the Maven project.

## License
[BSD 3-Clause Clear License](https://choosealicense.com/licenses/bsd-3-clause-clear/)
