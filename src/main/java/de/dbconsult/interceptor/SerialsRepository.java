package de.dbconsult.interceptor;

public class SerialsRepository {

    private SerialDescriptor mill;
    private SerialDescriptor pc;
    private SerialDescriptor extra;

    public SerialDescriptor getSerialById(int id) {
        if (id ==0) {
            return new SerialDescriptor(0,"Swallow", "dev0");
        } else if(id==1) {
            return pc;
        } else if(id==2) {
            return mill;
        }
        return new SerialDescriptor(255,"ERROR", "dev0");
    }


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
