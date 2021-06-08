function create_sine_trace()
    %% Constants
    fs = 10e3;                              % Sampling frequency
    f_base = 10;                            % Base frequency. Other signals will be a harmonic of this.
    tmax = 0.5;                             % produce `tmax` seconds of signal
    trs_file = 'C:/MATLAB/sine_wave.trs';   % The trs filename
    global_title = "Matlab export";
    local_title_template = "Hz sine wave";
    title_space = 64;

    %% Create time base
    t = linspace(0,tmax,fs*tmax);

    %% Add Inspector classes to classpath (change path to the actual location)
    inspector_jar = 'C:/MATLAB/trsfile-${project.version}.jar';
    javaaddpath(inspector_jar);

    %% Create metadata and fill relevant fields
    metadata = javaObject('com.riscure.trs.TRSMetaData');
    gt = javaMethod('valueOf', 'com.riscure.trs.enums.TRSTag', 'GLOBAL_TITLE');
    ts = javaMethod('valueOf', 'com.riscure.trs.enums.TRSTag', 'TITLE_SPACE');
    sx = javaMethod('valueOf', 'com.riscure.trs.enums.TRSTag', 'SCALE_X');
    metadata.put(gt, global_title);
    metadata.put(ts, uint32(title_space));
    metadata.put(sx, single(1/fs));

    %% Create the new file
    traceset = javaMethod('create', 'com.riscure.trs.TraceSet', trs_file, metadata);

    %% Add 10 traces with sine wave of 100..10 Hz
    %% Note that a warning will be displayed here for the first trace because we reserved more title space than needed
    for n = [1:10]
        y = sin(2*pi*f_base*n*t);
        trace_title = strcat(num2str(f_base*n), local_title_template);
        params = javaObject('com.riscure.trs.parameter.trace.TraceParameterMap');   % Empty TRS2 parameter map
        trace = javaObject('com.riscure.trs.Trace', trace_title, y, params);
        traceset.add(trace);
    end

    %% close file to store to disk.
    traceset.close();
    %% MATLAB doesn't release the file handle until we call fclose
    fclose(trs_file);
end