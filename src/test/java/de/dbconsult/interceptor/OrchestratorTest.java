package de.dbconsult.interceptor;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class OrchestratorTest {

    Orchestrator orchestrator = null;
    @Mock
    SerialsRepository serialsRepository;
    @Mock
    WorkflowRepository workflowRepository;


    @Before
    public void setup() {
        orchestrator = new Orchestrator(serialsRepository, workflowRepository);
    }


    @Test
    public void itPassesDataThroughWorkflows() {
      //  Mockito.when(serialsRepository.)
    }

}