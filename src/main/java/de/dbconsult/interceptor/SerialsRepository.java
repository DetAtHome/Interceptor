package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialCommunication;

public class SerialsRepository {
    private static SerialsRepository instance;

    private SerialDescriptor mill;
    private SerialDescriptor pc;
    private SerialDescriptor extra;
    private SerialCommunication millComm;
    private SerialCommunication pcComm;
    private SerialCommunication extraComm;

    private SerialsRepository() {

    }

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


    public static SerialsRepository getInstance() {
        if (instance==null) instance = new SerialsRepository();
        return instance;
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

    public SerialCommunication getMillComm() {
        return millComm;
    }

    public SerialCommunication getPcComm() {
        return pcComm;
    }

    public SerialCommunication getExtraComm() {
        return extraComm;
    }

    public void setExtraComm(SerialCommunication extraComm) {
        this.extraComm = extraComm;
    }

    public void setPcComm(SerialCommunication pcComm) {
        this.pcComm = pcComm;
    }

    public void setMillComm(SerialCommunication millComm) {
        this.millComm = millComm;
    }
}
