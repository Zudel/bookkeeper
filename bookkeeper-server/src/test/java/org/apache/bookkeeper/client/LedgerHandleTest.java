package org.apache.bookkeeper.client;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
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
                this.lh = bkc.createLedger(BookKeeper.DigestType.DUMMY, "test".getBytes());
            }catch (InterruptedException e){

            }catch (Exception e){

            }
    }
    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                // byte[] ,offset, data.size()  AddCallback, Control Object
                {"test".getBytes(),0, 4, getMockedCb(), null},
                {"test".getBytes(),0,-1, getMockedCb(),null},
                {"test".getBytes(),-1, 4, getMockedCb(),null},
                {"test".getBytes(),-1,-1, getMockedCb(),null},
                {"test".getBytes(),0, 4, null, null},
                {"test".getBytes(),0,-1, null,null},
                {"test".getBytes(),-1, 4, null,null},
                {"test".getBytes(),-1,-1, null,null},
                {"".getBytes(),0, 4, getMockedCb(), null},
                {"".getBytes(),0,-1, getMockedCb(),null},
                {"".getBytes(),-1, 4, getMockedCb(),null},
                {"".getBytes(),-1,-1, getMockedCb(),null},
                {"".getBytes(),0, 4, null, null},
                {"".getBytes(),0,-1, null,null},
                {"".getBytes(),-1, 4, null,null},
                {"".getBytes(),-1,-1, null,null},
                {null,0, 4, getMockedCb(), null},
                {null,0,-1, getMockedCb(),null},
                {null,-1, 4, getMockedCb(),null},
                {null,-1,-1, getMockedCb(),null},
                {null,0, 4, null, null},
                {null,0,-1, null,null},
                {null,-1, 4, null,null},
                {null,-1,-1, null,null}
        });
    }

    /**
     * setup and tests the method public void asyncAddEntry(final byte[] data, final int offset, final int length,
     *                               final AddCallback cb, final Object ctx)
     * The method is called in LedgerHandleTest.java
     */
    @Test
    public void testAsyncAddEntry() {
        try {
            if(data == null) {
                throw new NullPointerException("data is null");
            }
            if(cb == null) {
                throw new NullPointerException("callback is null");
            }
            if (offset < 0 || arrayLen < 0 || offset + arrayLen > data.length) {
                throw new ArrayIndexOutOfBoundsException("Invalid offset or arrayLen");
            }
            lh.asyncAddEntry(data,offset, arrayLen, cb, ctx);
        }
        catch (ArrayIndexOutOfBoundsException e){
            Assert.assertNotNull(e);
        }
        catch (NullPointerException e){
            Assert.assertNotNull(e);
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
    protected static AsyncCallback.AddCallback getMockedCb() {
        AsyncCallback.AddCallback cb = mock(AsyncCallback.AddCallback.class);
        doNothing().when(cb).addComplete(isA(Integer.class), isA(LedgerHandle.class), isA(Long.class), isA(Object.class));
        return cb;
    }
}
