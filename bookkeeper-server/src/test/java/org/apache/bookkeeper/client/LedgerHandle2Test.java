package org.apache.bookkeeper.client;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;


@RunWith(value= Parameterized.class)
public class LedgerHandle2Test extends BookKeeperClusterTestCase{
    private static final int numBookies = 2;
    private LedgerHandle lh;
    private static AsyncCallback.ReadCallback cb;
    private static Object ctx;
    private int lastEntry;
    private int firstEntry;

    /**
     * public void asyncReadEntries(long firstEntry, long lastEntry, ReadCallback cb, Object ctx):
     * */
    public LedgerHandle2Test(int firstEntry, int lastEntry, AsyncCallback.ReadCallback cb, Object ctx) {
        super(numBookies);
        this.cb = cb;
        this.ctx = ctx;
        this.firstEntry = firstEntry;
        this.lastEntry = lastEntry;
    }

    @Before
    public void setupEnv() throws BKException, InterruptedException {
            this.lh = bkc.createLedger(BookKeeper.DigestType.DUMMY, "test".getBytes());
            lh.asyncAddEntry("test".getBytes(), getMockedCb(), null);

    }
    @Test
    public void testAsyncReadEntries()  {
        try {
        if(firstEntry < 0 || lastEntry < 0){
            throw new IllegalArgumentException("firstEntry < 0 or lastEntry < 0");
        }
        if (firstEntry > lastEntry){
            throw new ArrayIndexOutOfBoundsException("firstEntry > lastEntry");
        }

            lh.asyncReadEntries(firstEntry, lastEntry, cb, null);
        }catch (ArrayIndexOutOfBoundsException e){
            Assert.assertTrue( firstEntry > lastEntry);
        }
        catch (IllegalArgumentException e) {
            Assert.assertTrue(firstEntry<0 || lastEntry<0);
        }
        catch (NullPointerException e){
            Assert.assertTrue(cb == null);
        }


    }


    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                // long firstEntry, long lastEntry, ReadCallback cb, control object vtx
                {0, 0, getMockedReadCb(), null},
                {0, 1, getMockedReadCb(), null},
                {-1, 0, getMockedReadCb(), null},
                {-1, -1, getMockedReadCb(), null},
                {0, 0, null, null},
                {0, 1, null, null},
                {-1, 0, null, null},
                {-1, -1, null, null}



        });

    }
    @After
    public void tearDownEnv() throws Exception {
        bkc.close();
    }
    private static AsyncCallback.ReadCallback getMockedReadCb() {
        AsyncCallback.ReadCallback cb = mock(AsyncCallback.ReadCallback.class);
        doNothing().when(cb).readComplete(isA(Integer.class), isA(LedgerHandle.class), isA(Enumeration.class), isA(Object.class));
        return cb;
    }
    private AsyncCallback.AddCallback getMockedCb() {
        AsyncCallback.AddCallback cb = mock(AsyncCallback.AddCallback.class);
        doNothing().when(cb).addComplete(isA(Integer.class), isA(LedgerHandle.class), isA(Long.class), isA(Object.class));
        return cb;
    }


}
