function plot_trace()
    %% Add Inspector classes to classpath (change path to the actual location)
    inspector_jar = 'C:/MATLAB/trsfile-${project.version}.jar';
    javaaddpath(inspector_jar);

    %% Open one of the example tracesets (change path if necessary)
    trs_file = 'C:/MATLAB/example.trs';
    traceset = javaMethod('open', 'com.riscure.trs.TraceSet', trs_file);

    %% Get the first trace and extract the plaintext/ciphertext data
    trace = traceset.get(0);
    params = trace.getParameters();
    %% The example trace has two parameters: INPUT and OUTPUT
    %% Calling params.toString() will print the whole structure of names and associated data
    input = params.getByteArray("INPUT");
    output = params.getByteArray("OUTPUT");
    input_as_string = reshape(dec2hex(abs(input)), 1, []);
    output_as_string = reshape(dec2hex(abs(output)), 1, []);

    %% plot the trace, use plain/ciphertext as title
    plot(trace.getSample());
    title(strcat("Input=",input_as_string,", Output=",output_as_string));

    %% close the traceset
    traceset.close();
    %% MATLAB doesn't release the file handle until we call fclose
    fclose(trs_file);
end
