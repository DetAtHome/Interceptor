package de.dbconsult.interceptor;

public class SerialsRepository {

    private SerialDescriptor mill;
    private SerialDescriptor pc;
    private SerialDescriptor extra;




    public SerialDescriptor getMill() {
        return mill;
    }

    public void setMill(SerialDescriptor mill) {
        this.mill = mill;
    }

    public SerialDescriptor getExtra() {
        return extra;
    }

    public void setExtra(SerialDescriptor extra) {
        this.extra = extra;
    }

    public SerialDescriptor getPc() {
        return pc;
    }

    public void setPc(SerialDescriptor pc) {
        this.pc = pc;
    }

}
