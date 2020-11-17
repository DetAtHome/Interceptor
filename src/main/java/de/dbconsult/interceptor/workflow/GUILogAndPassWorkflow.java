package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;

import javax.swing.*;

public class GUILogAndPassWorkflow  extends AbstractWorkflow implements Workflow {

    LogAndPassFrame logAndPass;
    private boolean questionRequestPending = false;
    private boolean g0RequestPending = false;
    private WorkflowDataStore workflowDataStore = null;

    public GUILogAndPassWorkflow() {
        /*JFrame mainFrame= new JFrame();//creating instance of JFrame
        mainFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        mainFrame.setContentPane(new LogAndPassFrame().);*/

    }

    @Override
    public void initialize(WorkflowDataStore workflowDataStore) {
        this.workflowDataStore = workflowDataStore;
        String pc1 = "mill"; //(String) data[0]; //serialsRepository.getInstance().getPc().name;
        String mill = "pc"; //(String) data[1]; //SerialsRepository.getInstance().getMill().name;
        String extra = "extra"; // (String) data[2]; //SerialsRepository.getInstance().getExtra().name;

        logAndPass = new LogAndPassFrame(pc1,mill, extra);
        logAndPass.show();

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {

        if(logAndPass.getDisableAll()) {
            setDoLog(false);
        }

        if(logAndPass.getDisableQuestion()) {
            if (data.getOutput()[0] == '?') {
                setQuestionRequestPending();
                setDoLog(false);
            }
        }

        if(logAndPass.getDisableG()) {
            if (new String(data.getOutput()).startsWith("$G")) {
                setG0RequestPending();
                setDoLog(false);
            }
        }
        String message = new String(data.getOutput());
        if (data.getFormSource().getName().contains("mill")) {
            if(isG0RequestPending()) {
                setDoLog(false);
                if (messageComplete(message)) {
                    if(message.contains("]")) {
                        clearG0RequestPending();
                    } else {
           //             logAndPass.addData(data);
                    }
                }
            }
            if(isQuestionRequestPending()) {
                setDoLog(false);
                if (messageComplete(message)) {
                    if(message.contains(">")) {
                        clearQuestionRequestPending();
                    } else {
            //            logAndPass.addData(data);
                    }
                }
            }
        }
        if (isDoLog()) logAndPass.addData(data);
        setDoLog(true);

        byte[] cmd = data.getOutput();
        if(data.getFormSource().getName().contains("pc")) {
            int commandsFound = 0;
            for (int i = 0; i < data.getLen(); i++) {
                if (cmd[i] == 13) commandsFound++;
            }
            logAndPass.incrementCmd(commandsFound);
        } else {
            int oksFound = 0;
            String dataString = new String(data.getOutput());
            String a[] = dataString.split(" ");
            for (int i = 0; i < a.length; i++)
            {
                if ((a[i]).contains("ok")) oksFound++;
            }

            logAndPass.incrementOk(oksFound);

        }
        return data;
    }

    private void setDoLog(boolean onOff) {
        workflowDataStore.update("DoLog", onOff);
    }

    private boolean isDoLog() {
        Object data =  workflowDataStore.read("DoLog");
        if (data==null) return true;
        return (boolean) data;
    }

    private void setQuestionRequestPending() {
        workflowDataStore.update("QuestionRequestPending", true);

    }

    private void clearQuestionRequestPending() {
        workflowDataStore.update("QuestionRequestPending", false);

    }

    private boolean isQuestionRequestPending() {
        Object data =  workflowDataStore.read("QuestionRequestPending");
        if (data==null) return false;
        return (boolean) data;
    }

    private void setG0RequestPending() {
        workflowDataStore.update("G0RequestPending", true);
    }

    private void clearG0RequestPending() {
        workflowDataStore.update("G0RequestPending", false);
    }

    private boolean isG0RequestPending() {
        Object data =  workflowDataStore.read("G0RequestPending");
        if (data==null) return false;
        return (boolean) data;
    }

}
