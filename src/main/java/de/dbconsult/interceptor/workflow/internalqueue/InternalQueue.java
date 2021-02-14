package de.dbconsult.interceptor.workflow.internalqueue;

import de.dbconsult.interceptor.WorkflowResult;

import java.util.LinkedList;
import java.util.Queue;

public class InternalQueue {

    Queue<WorkflowResult> commandQueue = new LinkedList<>();


    public WorkflowResult dequeue() {
        if (commandQueue.isEmpty()) throw new RuntimeException("Buffer underrun");
        return commandQueue.poll();
    }

    public void enqueue(WorkflowResult data) {
        if (data.getLen()>0) commandQueue.add(data);
    }

    public long size() {
        return commandQueue.size();
    }

}
