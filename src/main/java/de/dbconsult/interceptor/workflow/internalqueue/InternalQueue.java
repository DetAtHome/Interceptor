package de.dbconsult.interceptor.workflow.internalqueue;

import de.dbconsult.interceptor.WorkflowResult;

import java.util.LinkedList;
import java.util.Queue;

public class InternalQueue {

    private static InternalQueue instance;
    Queue<WorkflowResult> commandQueue = new LinkedList<>();

    private InternalQueue(){};

    public static InternalQueue getInstance() {
        if(instance==null) instance=new InternalQueue();
        return instance;
    }

    public WorkflowResult dequeue() {
        if (commandQueue.isEmpty()) throw new RuntimeException("Buffer underrun");
        return commandQueue.poll();
    }

    public void enqueue(WorkflowResult data) {
        if (data.getLen()<=0) throw new RuntimeException("Cannot enqueue null element");
        commandQueue.add(data);
    }

}
