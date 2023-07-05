package org.apache.bookkeeper.bookie.storage.Idb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.ReadCache;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.InputMismatchException;
import static org.junit.Assert.assertEquals;



@RunWith(value= Parameterized.class)
public class ReadCacheTest {
    private static ReadCache readCache;
    private static ByteBufAllocator allocator= ByteBufAllocator.DEFAULT;
    private  ByteBuf expectedEntry;
    private long ledgerId;
    private long entryId;
    private static final ByteBuf byteBuf = Unpooled.buffer(10);
    private static final ByteBuf byteBuf2 = Unpooled.buffer(2000);


        private  long maxCacheSize = 100;
        private int maxSegmentSize = 10;
    private ByteBuf entry;
    private ByteBuf byteBufRes;

    /**
     * public ReadCache(ByteBufAllocator allocator, long maxCacheSize, int maxSegmentSize)
     */
    @Before
    public void setup()  {
        byteBuf2.setIndex(0, 200);
        readCache = new ReadCache(allocator, maxCacheSize, maxSegmentSize);
    }

    public ReadCacheTest(ByteBuf expectedEntry, ByteBuf entry, long ledgerId, long entryId) {
            this.expectedEntry = expectedEntry;
            this.entry = entry;
            this.ledgerId = ledgerId;
            this.entryId = entryId;
    }

    @Test
    public void testput() {

        try {
            readCache.put(ledgerId, entryId, entry);
            byteBufRes = readCache.get(ledgerId, entryId);


        } catch (IllegalArgumentException e) {
                Assert.assertTrue(ledgerId < 0 || entryId < 0);
        }catch (NullPointerException e){
            Assert.assertNull(entry);
        }
        if (byteBufRes == null)
            Assert.assertNull(expectedEntry);
    }
    @After
    public void closeCache() {
        readCache.close();
    }

    /**
     * public void put(long ledgerId, long entryId, ByteBuf entry)
     * public ByteBuf get(long ledgerId, long entryId)
     */
    @Parameters
    public static Collection<Object[]> getParameters() {
        // Creazione dell'oggetto ByteBuf con le stesse proprietà
        return Arrays.asList(new Object[][]{
                {byteBuf, byteBuf, 3L, 3L}, // ByteBuf (expected object), ByteBuf, long ledgeId, long EntryId
                {null, byteBuf, 3L,-1L},
                {null, byteBuf, -1L, 3L},
                {null, byteBuf, -1L, -1L},
                {null, null , 3L, 3L},
                {null, null, 3L, -1L},
                {null, null, -1L, 3L},
                {null, null, -1L, -1L},
                //seconda iterazione
                //(entrySize > segmentSize)
                {null,byteBuf2,3L, 3L},

        });
    }
}
