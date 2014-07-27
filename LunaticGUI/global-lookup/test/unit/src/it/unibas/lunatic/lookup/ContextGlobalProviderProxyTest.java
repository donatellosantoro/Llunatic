/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.unibas.lunatic.lookup;

import it.unibas.lunatic.lookup.impl.ContextGlobalProviderProxy;
import static org.junit.Assert.*;

/**
 *
 * @author Tony
 */
public class ContextGlobalProviderProxyTest {

    public ContextGlobalProviderProxyTest() {
    }

    @org.junit.Test
    public void testPutSameClassMultipleKey() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        try {
            instance.put("KeyA", new MockObject());
            instance.put("KeyB", new MockObject());
        } catch (UnsupportedOperationException e) {
            return;
        }
        fail();
    }

    @org.junit.Test
    public void testPutSubclassMultipleKey() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        try {
            instance.put("KeyA", new MockObject());
            instance.put("KeyB", new Object());
        } catch (UnsupportedOperationException e) {
            fail(e.getMessage());
        }
    }

    @org.junit.Test
    public void testPutSubclass() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        try {
            instance.put("KeyA", new Object());
            instance.put("KeyA", new MockObject());
        } catch (UnsupportedOperationException e) {
            fail(e.getMessage());
        }
    }

    @org.junit.Test
    public void testPutSameObjectMultipleKey() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        MockObject mock = new MockObject();
        try {
            instance.put("KeyA", mock);
            instance.put("KeyB", mock);
        } catch (UnsupportedOperationException e) {
            return;
        }
        fail();
    }

    @org.junit.Test
    public void testPutSameClassMultipleTimes() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        MockObject mock = new MockObject();
        instance.put("KeyA", new MockObject());
        instance.put("KeyA", mock);
        assertEquals(mock, instance.get("KeyA", mock.getClass()));
    }

    @org.junit.Test
    public void testGetNull() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        Object result = instance.get("Null", MockObject.class);
        assertNull(result);
    }

    @org.junit.Test
    public void testGet() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        MockObject m1 = new MockObject();
        MockObject2 m2 = new MockObject2();
        instance.put("M1", m1);
        instance.put("M2", m2);
        assertEquals(m1, instance.get("M1", m1.getClass()));
        assertEquals(m2, instance.get("M2", m2.getClass()));
    }
    
    @org.junit.Test
    public void testGetSubclass() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        MockObject m1 = new MockObject();
        instance.put("M1", m1);
        assertEquals(m1, instance.get("M1", Object.class));
    }

    @org.junit.Test
    public void testRemove() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        MockObject m1 = new MockObject();
        MockObject2 m2 = new MockObject2();
        instance.put("M1", m1);
        instance.put("M2", m2);
        instance.remove("M1");
        MockObject result = instance.get("M1", m1.getClass());
        assertNull(result);
        assertEquals(m2, instance.get("M2", m2.getClass()));
    }

    @org.junit.Test
    public void testRemoveInstance() {
        ContextGlobalProviderProxy instance = new ContextGlobalProviderProxy();
        MockObject m1 = new MockObject();
        MockObject2 m2 = new MockObject2();
        instance.put("M1", m1);
        instance.put("M2", m2);
        try {
            instance.remove("M1", m2);
        } catch (UnsupportedOperationException e) {
            return;
        }
        fail();
    }
}