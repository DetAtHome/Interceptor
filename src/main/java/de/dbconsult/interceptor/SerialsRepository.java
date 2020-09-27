package de.dbconsult.interceptor;

import de.dbconsult.interceptor.serial.SerialCommunication;
import de.dbconsult.interceptor.serial.TwoWaySerialComm;

public class SerialsRepository {
    private static SerialsRepository instance;

    private SerialDescriptor mill;
    private SerialDescriptor pc;
    private SerialCommunication millComm;
    private SerialCommunication pcComm;

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

    public SerialCommunication getCommById(int id) {
        if(id==1) {
            return pcComm;
        } else if(id==2) {
            return millComm;
        }
        return null;
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

    public void setPcComm(SerialCommunication pcComm) {
        this.pcComm = pcComm;
    }

    public void setMillComm(SerialCommunication millComm) {
        this.millComm = millComm;
    }
}
