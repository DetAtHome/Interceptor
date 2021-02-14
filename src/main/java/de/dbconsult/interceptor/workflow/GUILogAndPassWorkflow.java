package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import de.dbconsult.interceptor.internal.UIController;

import javax.swing.*;

public class GUILogAndPassWorkflow  extends AbstractWorkflow {

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
        UIController controller = new UIController(workflowDataStore);
        String pc1 = "mill"; //(String) data[0]; //serialsRepository.getInstance().getPc().name;
        String mill = "pc"; //(String) data[1]; //SerialsRepository.getInstance().getMill().name;
        String extra = "extra"; // (String) data[2]; //SerialsRepository.getInstance().getExtra().name;

        logAndPass = new LogAndPassFrame(pc1,mill, extra, workflowDataStore, controller);
        logAndPass.show();
        workflowDataStore.update("UIInstance", logAndPass);
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

        logAndPass.showPCQSize((long)workflowDataStore.read("pcQSize"));
        logAndPass.showMillQSize((long)workflowDataStore.read("millQSize"));



        String message = new String(data.getOutput());
        if (data.getFormSource().getName().contains("mill")) {
            if(data.getLen()==1 && data.getOutput()[0]==10) {
                setDoLog(false);
            }
            if(isG0RequestPending()) {
                setDoLog(false);
                if(message.contains("]")) {
                    clearG0RequestPending();
                }
            }
            if(isQuestionRequestPending()) {
                setDoLog(false);
                if(message.contains(">")) {
                    clearQuestionRequestPending();
                }
            }
        }
        if (isDoLog()) logAndPass.addData(data);
        setDoLog(true);
        logAndPass.showAbsCmdNumber(workflowDataStore.getCommandFound());
        logAndPass.showAbsOkNumber(workflowDataStore.getOkFound());
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
