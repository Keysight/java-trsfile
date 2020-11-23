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
