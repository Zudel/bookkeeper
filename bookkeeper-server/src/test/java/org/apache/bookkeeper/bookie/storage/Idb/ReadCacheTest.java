package org.apache.bookkeeper.bookie.storage.Idb;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.ReadCache;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.InputMismatchException;

import static org.junit.Assert.assertEquals;

/**
 * Read cache implementation.
 *
 * <p>Uses the specified amount of memory and pairs it with a hashmap.
 *
 * <p>The memory is splitted in multiple segments that are used in a
 * ring-buffer fashion. When the read cache is full, the oldest segment
 * is cleared and rotated to make space for new entries to be added to
 * the read cache.
 *
 * TRADUZIONE
 * Implementazione della cache di lettura.
 *
 * Utilizza la quantità di memoria specificata e la accoppia con una hashmap.
 *
 * La memoria è divisa in più segmenti che vengono utilizzati in un
 * anello-buffer. Quando la cache di lettura è piena, il segmento più vecchio
 * viene cancellato e ruotato per fare spazio alle nuove voci da aggiungere alla
 * cache di lettura.
 *
 * NOTE
 * l’adeguatezza/bontà dell’insieme dei test di unità può essere stabilita in
 * funzione:
 * ● numero di funzionalità controllate, numero di requisiti controllati, numero di aspetti
 * rilevabili da specifiche
 * ● metriche di “copertura” del codice sorgente a disposizione
 *
 * ANNOTAZIONE @BeforeClass
 * La notazione "@BeforeClass" viene utilizzata per indicare un metodo di inizializzazione che deve essere eseguito
 * una volta prima di tutti i metodi di test nella classe. Questo metodo viene eseguito quando la classe viene
 * caricata e può essere utilizzato per preparare lo stato iniziale per i test. È spesso utilizzato per
 * inizializzare variabili statiche o per eseguire altre attività di setup che devono essere eseguite solo una volta.
 *
 */

@RunWith(value= Parameterized.class)
public class ReadCacheTest {
    private static ReadCache readCache;
    private static ByteBufAllocator allocator;
    private  ByteBuf expectedEntry;
    private long ledgerId;
    private long entryId;
    private static final ByteBuf byteBuf = Unpooled.buffer(10);
        private static long maxCacheSize;
        private static int maxSegmentSize;
    private ByteBuf entry;
    private ByteBuf byteBufRes;

    /**
     * public ReadCache(ByteBufAllocator allocator, long maxCacheSize, int maxSegmentSize)
     */
    @Before
    public void setup()  {

        allocator = ByteBufAllocator.DEFAULT;
        maxCacheSize = 100;
        maxSegmentSize = 10;

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
            if(ledgerId < 0 || entryId < 0)
                throw new IllegalArgumentException("ledgerId and entryId must be >=0");

            readCache.put(ledgerId, entryId, entry);
            byteBufRes = readCache.get(ledgerId, entryId);

        } catch (IllegalArgumentException e) {
                Assert.assertTrue(ledgerId < 0 || entryId < 0);
        }catch (NullPointerException e){
            Assert.assertNull(entry);
        }

        if (expectedEntry == null)
            Assert.assertNull(byteBufRes);
        else
            Assert.assertEquals(expectedEntry, byteBufRes); // expected, actual
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
                {null, null, 3L, 3L},
                {null, null, 3L, -1L},
                {null, null, -1L, 3L},
                {null, null, -1L, -1L},
        });
    }
}


/*
* La classe contiene un metodo chiamato put che viene utilizzato per inserire un'entry (rappresentata come ByteBuf)
* in una cache di segmenti.

Ecco una spiegazione delle operazioni svolte dal metodo:
Calcola la dimensione dell'entry utilizzando il metodo readableBytes() dell'oggetto entry di tipo ByteBuf.
Calcola la dimensione allineata (alignedSize) alla dimensione più vicina multipla di 64 della dimensione dell'entry.
Acquisisce una lettura del lock per la sezione di codice successiva.
Verifica se la dimensione dell'entry supera la dimensione del segmento (segmentSize). In caso affermativo, viene emesso un avviso e il metodo termina.
Se la dimensione dell'entry non supera la dimensione del segmento, calcola l'offset corrente all'interno del segmento e controlla se l'entry può essere inserita completamente all'interno del segmento corrente. Se sì, copia l'entry nel segmento della cache (cacheSegments), aggiorna l'indice della cache (cacheIndexes) e termina il metodo.
Se l'entry non può essere inserita completamente nel segmento corrente, è necessario acquisire un blocco di scrittura (write lock) e passare al segmento successivo. Questo viene gestito nella sezione di codice successiva, che acquisisce il blocco di scrittura, esegue l'operazione di roll-over se necessario e copia l'entry nel segmento della cache corrente.
In sostanza, il metodo put viene utilizzato per inserire un'entry all'interno di una cache segmentata, assicurandosi che l'entry venga copiata correttamente nel segmento appropriato e gestendo il roll-over dei segmenti quando necessario.*/

