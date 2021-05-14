package de.dbconsult.interceptor;

public interface Communication {

    WorkflowResult readFully(String channel);
    void write(WorkflowResult data);
    void write(String data);
}
