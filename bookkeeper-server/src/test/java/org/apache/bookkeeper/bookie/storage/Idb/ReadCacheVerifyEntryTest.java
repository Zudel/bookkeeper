package org.apache.bookkeeper.bookie.storage.Idb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.apache.bookkeeper.bookie.storage.ldb.ReadCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;


@RunWith(value= Parameterized.class)
    public class ReadCacheVerifyEntryTest {
    private boolean flag;
    private ReadCache readCache;
        private static ByteBufAllocator allocator;
        private long ledgerId;
        private long entryId;
        private static final ByteBuf byteBuf = Unpooled.buffer(10);
        private static long maxCacheSize;
        private static int maxSegmentSize;
        private boolean res;

        /**
         * public ReadCache(ByteBufAllocator allocator, long maxCacheSize, int maxSegmentSize)
         */
        @Before
        public void setup()  {

            allocator = ByteBufAllocator.DEFAULT;
            maxCacheSize = 100;
            maxSegmentSize = 10;
            readCache = new ReadCache(allocator, maxCacheSize, maxSegmentSize);
            if (flag) {
                try {
                    readCache.put(ledgerId, entryId, byteBuf);
                } catch (IllegalArgumentException e) {
                    Assert.assertNotNull(e);
                }
            }
        }

        public ReadCacheVerifyEntryTest(long ledgerId, long entryId, boolean flag) {
            this.ledgerId = ledgerId;
            this.entryId = entryId;
            this.flag = flag;
        }

        @Test
        public void hasEntryTest(){
            try{
                if(ledgerId < 0 || entryId < 0)
                    throw new IllegalArgumentException("ledgerId and entryId must be >=0");

               res = readCache.hasEntry(ledgerId, entryId);
            } catch (IllegalArgumentException e) {
                Assert.assertNotNull(e);
                return;
            }
            if (flag)
                Assert.assertEquals(true, res);
            else
                Assert.assertEquals(false, res);

        }

        /**
         * public void put(long ledgerId, long entryId, ByteBuf entry)
         * public ByteBuf get(long ledgerId, long entryId)
         */
        @Parameterized.Parameters
        public static Collection<Object[]> getParameters() {
            // Creazione dell'oggetto ByteBuf con le stesse proprietà
            return Arrays.asList(new Object[][]{
                    {3L, 3L, true}, // long ledgeId, long EntryId
                    {3L, -1L, true},
                    {-1L,3L,true},
                    {-1L,-1L, true},
                    //seconda iterazione
                    //{3L, 3L, false}
            });
        }
}
