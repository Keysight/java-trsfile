%% Add Inspector classes to classpath (change path to the actual location)
inspector_jar = 'C:/MATLAB/trsfile-${project.version}.jar';
javaaddpath(inspector_jar);

%% Open one of the example tracesets (change path if necessary)
trs_file = 'C:/MATLAB/example.trs';
traceset = javaMethod('open', 'com.riscure.trs.TraceSet', trs_file);

%% Get the first trace and extract the plaintext/ciphertext data
trace = traceset.get(0);
data = trace.getData();
data_as_string = reshape(dec2hex(abs(data)), 1, []);

%% plot the trace, use plain/ciphertext as title
plot(trace.getSample());
title(data_as_string);

%% close the traceset
traceset.close();