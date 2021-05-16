package de.dbconsult.interceptor;

public class WorkflowResult {
    private TargetDevices formSource;
    private TargetDevices toDestination;
    private byte[] output;
    private int len;
    private long index;

    public WorkflowResult(long index, TargetDevices in, TargetDevices out, byte[] result, int len) {
        this.index = index;
        this.formSource = in;
        this.toDestination = out;
        this.output = result;
        this.len = len;
    }

    public long getIndex() {
        return index;
    }

    public byte[] getOutput() {
        return output;
    }

    public void setOutput(byte[] output) {
        this.output = output;
    }

    public TargetDevices getFormSource() {
        return formSource;
    }

    public TargetDevices getToDestination() {
        return toDestination;
    }

    public void setFormSource(TargetDevices formSource) {
        this.formSource = formSource;
    }

    public void setToDestination(TargetDevices toDestination) {
        this.toDestination = toDestination;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
