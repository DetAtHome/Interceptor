package de.dbconsult.interceptor.workflow;

import de.dbconsult.interceptor.SerialDescriptor;
import de.dbconsult.interceptor.WorkflowDataStore;
import de.dbconsult.interceptor.WorkflowResult;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MonitorSpindleSpeedWorkflowTest {

    MonitorSpindleSpeedWorkflow spindleSpeed;
    double speedSet = -1;
    int interactions = 0;

    @Before
    public void setup() {
        interactions = 0;
        spindleSpeed =  new MonitorSpindleSpeedWorkflow();
    }

    @Test
    public void processNonMessage() {
        String[] dta = new String[] {"ok"};

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                new SerialDescriptor(0,"mill","test"),
                new SerialDescriptor(1,"pc","test"),
                foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(0, interactions);
    }

    @Test
    public void processFullMessageButSpindleStopped() {
        String[] dta = new String[] {"<Idle|MPos:0.000,0.000,0.000|FS:0,0|WCO:0.000,0.000,0.000>",
                "ok\n<Idle|MPos:0.000,0.000,0.000|FS:111.111,222.222|WCO:0.000,0.000,0.000>"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(0,interactions);
    }

    @Test
    public void processSplitAlarmMessage() {
        String[] dta = new String[] {
                "<Alarm|MPos:0.000,0.000,",
                "0.000|FS:0,222.333|WCO:0.000,0.000,0.000>",

        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(1,interactions);
        assertEquals(0, speedSet,0);
    }

    @Test
    public void ignoresComments() {
        String[] dta = new String[] {
                "(<Idle|MPos:0.000,0.000,",
                "0.000|FS:0,222.333|WCO:0.000,0.000,0.000|A:S>)",
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S333.444]",
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(1,interactions);
        assertEquals(333.444, speedSet,0);

    }

    @Test
    public void ignoresStartupMessage() {


        String[] dta = new String[] {
                "Grbl 1.1f ['$' for help]",
                "[MSG:'$H'|'$X' to unlock]"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(0,interactions);
        assertEquals(-1, speedSet,0);
    }

    @Test
    public void findSpeedinM3G0Command() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S10","00]",
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(1,interactions);
        assertEquals(1000, speedSet,0);

    }

    @Test
    public void okAndIdleMixed() {
        String[] dta = new String[] {
                "ok\n[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.333]ok",
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(1,interactions);
        assertEquals(222.333, speedSet,0);

    }
    @Test
    public void splitInSpeed() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S22","2.333]"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(1,interactions);
        assertEquals(222.333, speedSet,0);

    }

    @Test
    public void setSpeedOnFullMessage() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.222]ok"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(222.222, speedSet,0);
        }

    }

    @Test
    public void stopsOnAlarm() {
        String[] dta = new String[] {
                "<Alarm|MPos:0.000,0.000,",
                "0.000|FS:0,22","2.333|WCO:0.000,0.000,0.000>",

        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(1,interactions);
        assertEquals(0, speedSet,0);

    }

    @Test
    public void doesStopWhenM5ed() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M5 M9 T0 F0 S10","00]",
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(1,interactions);
        assertEquals(0, speedSet,0);


    }

    @Test
    public void doesNotWriteSpeedIfNotModified() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.333]",
                "<Idle|MPos:0.000,0.000,0.000|FS:111.111,222.222|WCO:0.000,0.000,0.000|A:S>",
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.222]",
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.222]"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(2,interactions);
        assertEquals(222.222, speedSet,0);
    }
    @Test
    public void picksUpModifiedSpeedWhenRestarting() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.333]",
                "[GC:G0 G54 G17 G21 G90 G94 M5 M9 T0 F0 S222.333]",
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.222]",
                "<Idle|MPos:0.000,0.000,0.000|FS:111.111,222.222|WCO:0.000,0.000,0.000|A:S>",
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.222]"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(3,interactions);
        assertEquals(222.222, speedSet,0);
    }

    @Test
    public void keepsTrackOfTheDesiredSpeed() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.333]",
                "<Idle|MPos:0.000,0.000,0.000|FS:111.111,222.222|WCO:0.000,0.000,0.000|A:S>",
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.222]",
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S222.222]"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals(222.222D, WorkflowDataStore.getInstance().read("SpindleSpeed"));
    }

    @Test
    public void keepsTrackOfSpindleStateWhenOn() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M3 M9 T0 F0 S1000]",
                "<Idle|MPos:0.000,0.000,0.000|FS:111.111,222.222|WCO:0.000,0.000,0.000|A:S>",
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals("ON", WorkflowDataStore.getInstance().read("SpindleState"));
    }

    @Test
    public void keepsTrackOfSpindleStateWhenOf() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M5 M9 T0 F0 S1000]",
                "<Idle|MPos:0.000,0.000,0.000|FS:111.111,222.222|WCO:0.000,0.000,0.000>",
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals("OFF", WorkflowDataStore.getInstance().read("SpindleState"));
    }

    @Test
    public void keepsTrackOfSpindleStateWhenM5ed() {
        String[] dta = new String[] {
                "[GC:G0 G54 G17 G21 G90 G94 M5 M9 T0 F0 S10","00]"
        };

        for(String foo:dta) {
            WorkflowResult data = new WorkflowResult(0,
                    new SerialDescriptor(0,"mill","test"),
                    new SerialDescriptor(1,"pc","test"),
                    foo.getBytes(),foo.length()
            );
            WorkflowResult out = spindleSpeed.process(data);
            assertEquals(out.getOutput(), data.getOutput());
        }
        assertEquals("OFF", WorkflowDataStore.getInstance().read("SpindleState"));
    }


}
