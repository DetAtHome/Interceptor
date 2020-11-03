package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialsRepository;
import de.dbconsult.interceptor.Workflow;
import de.dbconsult.interceptor.WorkflowResult;

import javax.swing.*;

public class GUILogAndPass implements Workflow {

    LogAndPassFrame logAndPass;

    public GUILogAndPass() {
        /*JFrame mainFrame= new JFrame();//creating instance of JFrame
        mainFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        mainFrame.setContentPane(new LogAndPassFrame().);*/
        String pc1 = SerialsRepository.getInstance().getPc().name;
        String mill = SerialsRepository.getInstance().getMill().name;
        String extra = SerialsRepository.getInstance().getExtra().name;

        logAndPass = new LogAndPassFrame(pc1,mill, extra);
        logAndPass.show();

    }

    @Override
    public synchronized WorkflowResult process(WorkflowResult data) {

        if(!logAndPass.getDisableAll()) {
            if(!(data.getOutput()[0]=='?' && logAndPass.getDisableQuestion())) {
                logAndPass.addData(data);
            } else if(!(new String(data.getOutput()).contains("$G0") && logAndPass.getDisableG())) {
                logAndPass.addData(data);
            }
        }

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
}
