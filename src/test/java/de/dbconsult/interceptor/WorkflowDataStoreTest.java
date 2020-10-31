package de.dbconsult.interceptor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class WorkflowDataStoreTest {

    WorkflowDataStore dataStore;

    @Before
    public void setUp() {
        dataStore = WorkflowDataStore.getInstance();
        dataStore.reset();
    }

    @Test
    public void testReadNull() {
        Object read = dataStore.read("foo");
        assertNull(read);
    }

    @Test
    public void testStoreNotThereBefore() {
        assertTrue(dataStore.update("foo","bar"));
        assertEquals("bar", dataStore.read("foo"));
    }

    @Test
    public void testUpdate() {
        dataStore.update("foo", "bar");
        dataStore.update("foo", "baz");
        assertEquals("baz", dataStore.read("foo"));
    }

    @Test
    public void testClear() {
        dataStore.update("foo", "bar");
        dataStore.update("foo", null);
        assertNull(dataStore.read("foo"));
    }
}