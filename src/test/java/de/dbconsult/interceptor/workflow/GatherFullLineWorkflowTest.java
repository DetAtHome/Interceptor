package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialDescriptor;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowRepository;
import de.dbconsult.interceptor.WorkflowResult;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GatherFullLineWorkflowTest {

    GatherFullLineWorkflow workflow;

    @Before
    public void setup() {
        workflow = new GatherFullLineWorkflow();
    }

    @Test
    public void itPassesCompleteMessages() {
        String[] dta = new String[] {"ok\n"};

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = workflow.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
    }

    @Test
    public void itStoresAFragmentForOKTillNewLine() {
        String[] dta = new String[] {"o","k\n"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("ok\n",new String(out.getOutput()));
    }

    @Test
    public void itStoresAFragmentForSquareBracketTillNewLine() {
        String[] dta = new String[] {"[frissel","foo","frassel]"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("[frisselfoofrassel]",new String(out.getOutput()));

    }

    @Test
    public void itStoresAFragmentForSharpBracketTillNewLine() {
        String[] dta = new String[] {"<frissel","foo","frassel>"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("<frisselfoofrassel>",new String(out.getOutput()));

    }

    @Test
    public void itStoresAFragmentForRoundBracketTillNewLine() {
        String[] dta = new String[] {"(frissel","foo","frassel)"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("(frisselfoofrassel)",new String(out.getOutput()));
    }

    @Test
    public void itStoresAFragmentFor$$TillNewLine() {
        String[] dta = new String[] {"$$102","=","3\n"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("$$102=3\n",new String(out.getOutput()));

    }

    @Test
    public void itStoresAFragmentFor$TillNewLine() {
        String[] dta = new String[] {"$102","=","3 ","(friesel","frasel)\n"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("$102=3 (frieselfrasel)\n",new String(out.getOutput()));
    }


    @Test
    public void itPassesTildeSigns() {
        String[] dta = new String[] {"~"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("~",new String(out.getOutput()));
        assertEquals("pc",new String(out.getToDestination().getName()));
    }


    @Test
    public void itPassesQuestionMarks() {
        String[] dta = new String[] {"?"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("?",new String(out.getOutput()));
        assertEquals("pc",new String(out.getToDestination().getName()));
    }


    @Test
    public void itPassesHoldSigns() {
        String[] dta = new String[] {"!"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("!",new String(out.getOutput()));
        assertEquals("pc",new String(out.getToDestination().getName()));
    }

    @Test
    public void itPassesCtrlX() {
        byte[] dta = new byte[] {24};
        WorkflowResult out = null;
        WorkflowResult data = new WorkflowResult(0,
                new SerialDescriptor(0,"mill","test"),
                new SerialDescriptor(1,"pc","test"),
                dta,1);
        out = workflow.process(data);

        assertEquals(24,out.getOutput()[0]);
        assertEquals("pc",new String(out.getToDestination().getName()));
    }

    @Test
    public void itConsumesUnfinishedLines() {
        String[] dta = new String[] {"[blubber"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("ABORT",new String(out.getToDestination().getName()));

    }

    @Test
    public void itPassesLinesWhenFinished() {
        String[] dta = new String[] {"[blub","ber]"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("pc",new String(out.getToDestination().getName()));
    }


    @Test
    public void itRecognizesAlarmMessages() {
        String[] dta = new String[] {"ALA","RM: locked\n"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("ALARM: locked\n",new String(out.getOutput()));
        assertEquals("pc",new String(out.getToDestination().getName()));
    }

    @Test
    public void itRecognizesErrorMessages() {
        String[] dta = new String[] {"err","or: 9\n"};
        WorkflowResult out = null;
        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            out = workflow.process(data);
        }
        assertEquals("error: 9\n",new String(out.getOutput()));
        assertEquals("pc",new String(out.getToDestination().getName()));
    }
}