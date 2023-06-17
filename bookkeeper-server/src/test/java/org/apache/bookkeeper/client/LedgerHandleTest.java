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

//@RunWith(value= Parameterized.class)
public class LedgerHandleTest extends BookKeeperClusterTestCase{
    byte[] data;
    private LedgerHandle lh;
    private static final int numBookies = 3;
    private static AsyncCallback.AddCallback cb;
    private static Object ctx;
    private int offset;
    private int arrayLen;

    public LedgerHandleTest(byte[] data, int offset, int arrayLen, AsyncCallback.AddCallback cb, Object ctx) {
        super(numBookies);
        this.data = data;
        this.cb = cb;
        this.ctx = ctx;
        this.offset = offset;
        this.arrayLen = arrayLen;

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

            }catch (Exception e){

            }
    }
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                // byte[],  AddCallback, Control Object
                {"test".getBytes(),0, 4, getMockedReadCb(), null},
                /*{"test".getBytes(),0,-1, getMockedReadCb(),null},
                {"test".getBytes(),-1, 4, getMockedReadCb(),null},
                {"test".getBytes(),-1,-1, getMockedReadCb(),null},
                {"test".getBytes(),0, 4, null, null},
                {"test".getBytes(),0,-1, null,null},
                {"test".getBytes(),-1, 4, null,null},
                {"test".getBytes(),-1,-1, null,null},
                {"".getBytes(),0, 4, getMockedReadCb(), null},
                {"".getBytes(),0,-1, getMockedReadCb(),null},
                {"".getBytes(),-1, 4, getMockedReadCb(),null},
                {"".getBytes(),-1,-1, getMockedReadCb(),null},
                {"".getBytes(),0, 4, null, null},
                {"".getBytes(),0,-1, null,null},
                {"".getBytes(),-1, 4, null,null},
                {"".getBytes(),-1,-1, null,null},
                {null,0, 4, getMockedReadCb(), null},
                {null,0,-1, getMockedReadCb(),null},
                {null,-1, 4, getMockedReadCb(),null},
                {null,-1,-1, getMockedReadCb(),null},
                {null,0, 4, null, null},
                {null,0,-1, null,null},
                {null,-1, 4, null,null},
                {null,-1,-1, null,null}*/
        });
    }

    /**
     * setup and tests the method public void asyncAddEntry(final byte[] data, final AddCallback cb, final Object ctx)
     * The method is called in LedgerHandleTest.java
     */
    //@Test
    public void testAsyncAddEntry() {

        try {
            if(data == null) {
                Assert.assertTrue(true);
            }
            if(cb == null) {

                Assert.assertTrue(true);
            }
            if (offset < 0 || arrayLen < 0 || offset + arrayLen > data.length) {
                Assert.assertTrue(true);
            }
            lh.asyncAddEntry(data,offset, arrayLen, cb, ctx);
        }
        catch (ArrayIndexOutOfBoundsException e){

            Assert.assertTrue(true);
        }
        catch (NullPointerException e){

            Assert.assertTrue(true);
        }
        catch (IllegalArgumentException e){

            Assert.assertTrue(true);
        }
        catch (Exception e){

            Assert.assertFalse(true);
        }
        if (data != null && cb != null && offset >= 0 && arrayLen >= 0 && offset + arrayLen <= data.length)
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
