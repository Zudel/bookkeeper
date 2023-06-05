package org.apache.bookkeeper.client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.net.BindException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

@RunWith(value= Parameterized.class)
public class LedgerHandleTest extends BookKeeperClusterTestCase{

    byte[] data;
    private LedgerHandle lh;
    private static final int numBookies = 3;
    private static AsyncCallback.AddCallback cb;
    private static Object ctx;

    public LedgerHandleTest(byte[] data, AsyncCallback.AddCallback cb, Object ctx) {
        super(numBookies);
        this.data = data;
        this.cb = cb;
        this.ctx = ctx;

    }
    /**
     * setup the environment for the test.
     *This method is called in LedgerHandleTest.java
     */
    @Before
    public void setupEnv() {

        try {
                this.lh = bkc.createLedger(BookKeeper.DigestType.DUMMY, "testPasswd".getBytes());
            }catch (InterruptedException e){
                LOG.error("InterruptedException on creating ledger", e);
            }catch (Exception e){
                LOG.error("Exception on creating ledger", e);
            }
    }
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                // byte[],  AddCallback, Control Object
                {"test".getBytes(), getMockedReadCb(), new Object()},
                {"test".getBytes(),getMockedReadCb(),null},
                {"test".getBytes(),null,new Object()},
                {"test".getBytes(),null,null},
                {"".getBytes(),getMockedReadCb(), new Object()},
                {"".getBytes(),getMockedReadCb(),null},
                {"".getBytes(),null,new Object()},
                {"".getBytes(),null,null},
                {null,getMockedReadCb(),null},
                {null,null,new Object()},
                {null,getMockedReadCb(),new Object()},
                {null,null,null}

        });
    }

    /**
     * setup and tests the method public void asyncAddEntry(final byte[] data, final AddCallback cb, final Object ctx)
     * The method is called in LedgerHandleTest.java
     */
    @Test
    public void testAsyncAddEntry() {

        try {
            if(data == null) {
                assertEquals(-1, lh.getLastAddPushed());
                return;
            }
            lh.asyncAddEntry(data, cb, ctx);
        }
        catch (NullPointerException e){
            System.out.println("NullPointerException");
            Assert.assertTrue(true);
        }
        catch (IllegalArgumentException e){
            System.out.println("IllegalArgumentException ok");
            Assert.assertTrue(true);
        }
        catch (Exception e){
            System.out.println("Exception");
            Assert.assertFalse(true);
        }
        if (data != null)
            Assert.assertEquals(0, lh.getLastAddPushed()); //the first entry has id equal to 0
    }

/**
 * Tear down the environment for the test.
 * This method is called in LedgerHandleTest.java
 */
    @After
    public void tearDownEnv() throws Exception {
        bkc.close();
    }

    private static AsyncCallback.AddCallback getMockedReadCb() {
        AsyncCallback.AddCallback cb = mock(AsyncCallback.AddCallback.class);
        doNothing().when(cb).addComplete(isA(Integer.class), isA(LedgerHandle.class), isA(Long.class), isA(Object.class));
        return cb;
    }
}
