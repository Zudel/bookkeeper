import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.apache.bookkeeper.bookie.storage.ldb.ReadCache;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import java.util.Arrays;
import java.util.Collection;

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
    //i due long rappresentano ledgerId e entryID
    private long ledgerId;
    private long entryId;
        private static long maxCacheSize;
        private static int maxSegmentSize;
    private ByteBuf entry;

    //crea un oggetto ReadCache con i parametri allocator, maxCacheSize, maxSegmentSize

    @BeforeClass
    public static void setup() {
        /**
         * public ReadCache(ByteBufAllocator allocator, long maxCacheSize, int maxSegmentSize)
         */
        //
        allocator = ByteBufAllocator.DEFAULT;
        maxCacheSize = 100;
        maxSegmentSize = 10; // must be > 0, otherwise we will get an exception

        readCache = new ReadCache(allocator, maxCacheSize, maxSegmentSize);
    }

    public ReadCacheTest(ByteBuf expectedEntry, ByteBuf entry, long ledgerId, long entryId) {
        this.expectedEntry = expectedEntry;
        this.entry = entry;
        this.ledgerId = ledgerId;
        this.entryId = entryId;
    }

    //write test cases here for ReadCacheTest class using Junit4


    /**
     * public void put(long ledgerId, long entryId, ByteBuf entry)
     * public ByteBuf get(long ledgerId, long entryId)
     */
    @Test
    public void testput() {

        //crea un oggetto ByteBuf di dimensione 10
        entry = ByteBufAllocator.DEFAULT.buffer(10);
        System.out.println("entry: " + entry);
        System.out.println(" expected " + expectedEntry);
        readCache.put(ledgerId, entryId, entry);
        assertEquals(expectedEntry, readCache.get(ledgerId, entryId) );

    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        // Creazione dell'oggetto ByteBuf con le stesse proprietà
        ByteBuf byteBuf = UnpooledByteBufAllocator.DEFAULT.heapBuffer(2);
        byteBuf.readerIndex(0);
        byteBuf.writerIndex(0);
        return Arrays.asList(new Object[][]{
                {byteBuf, byteBuf, 3L, 3L}, // ByteBuf (expected object), ByteBuf, long, long
                {Unpooled.buffer(1), Unpooled.buffer(1), 3L, 3L}, // ByteBuf, ByteBuf, long, long
                {Unpooled.buffer(1), byteBuf, 3L, 3L}, // ByteBuf, ByteBuf, long, long
        }); //Il valore 3L indica un valore di tipo long con il valore numerico 3. Aggiungendo il suffisso "L" a 3, lo dichiari come un valore long invece di un valore int predefinito.
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

