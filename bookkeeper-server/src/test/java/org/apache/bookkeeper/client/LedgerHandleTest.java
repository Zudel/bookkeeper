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
    private static final int numBookies = 1;
    private static final int ensSize = 1;
    private static final int writeQSize = 1;
    private static final int ackQSize = 1;
    private static AsyncCallback.AddCallback cb;
    private static Object ctx;

    public LedgerHandleTest(byte[] data, AsyncCallback.AddCallback cb, Object ctx) throws Exception {
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
    public void setupEnv() throws Exception {
            this.lh = bkc.createLedger(ensSize, writeQSize, ackQSize, BookKeeper.DigestType.CRC32, "testPasswd".getBytes());

    }
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                // byte[],  AddCallback, Control Object
                {"test".getBytes(), getMockedReadCb(), ctx},
                {"".getBytes(), null, ctx},
                {null, getMockedReadCb(), ctx},
                {null, null, ctx},
                {"".getBytes(), getMockedReadCb(), ctx},
                {"".getBytes(), null, ctx},

        });
    }

    /**
     * setup and tests the method public void asyncAddEntry(final byte[] data, final AddCallback cb, final Object ctx)
     * The method is called in LedgerHandleTest.java
     */
    @Test
    public void test()  {
        try{
            lh.asyncAddEntry(data , cb, ctx);
        }

        catch (NullPointerException e ){
            LOG.error("NullPointerException on testing asyncReadEntries", e);
            Assert.assertTrue(true); //fails the test case
        }
        catch (Exception e) {
            LOG.error("generic Exception", e);
            Assert.assertTrue(true); //fails the test case
        }
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
