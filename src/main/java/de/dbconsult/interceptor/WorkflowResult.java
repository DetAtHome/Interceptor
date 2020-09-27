package de.dbconsult.interceptor;

public class WorkflowResult {
    private SerialDescriptor formSource;
    private SerialDescriptor toDestination;
    private byte[] output;
    private int len;

    public WorkflowResult(SerialDescriptor in, SerialDescriptor out, byte[] result, int len) {
        this.formSource = in;
        this.toDestination = out;
        this.output = result;
        this.len = len;
    }

    public byte[] getOutput() {
        return output;
    }

    public void setOutput(byte[] output) {
        this.output = output;
    }

    public SerialDescriptor getFormSource() {
        return formSource;
    }

    public SerialDescriptor getToDestination() {
        return toDestination;
    }

    public void setFormSource(SerialDescriptor formSource) {
        this.formSource = formSource;
    }

    public void setToDestination(SerialDescriptor toDestination) {
        this.toDestination = toDestination;
    }

    public int getLen() {
        return len;
    }

    public void setLen(int len) {
        this.len = len;
    }
}
