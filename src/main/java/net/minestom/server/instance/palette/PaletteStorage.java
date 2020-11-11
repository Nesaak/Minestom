package net.minestom.server.instance.palette;

import it.unimi.dsi.fastutil.shorts.Short2ShortLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ShortOpenHashMap;
import net.minestom.server.utils.chunk.ChunkUtils;

import static net.minestom.server.instance.Chunk.*;

public class PaletteStorage {

    private final static int PALETTE_MAXIMUM_BITS = 8;

    private int bitsPerEntry;

    private int valuesPerLong;
    private boolean hasPalette;

    private long[][] sectionBlocks = new long[CHUNK_SECTION_COUNT][0];

    // palette index = block id
    private Short2ShortLinkedOpenHashMap paletteBlockMap = new Short2ShortLinkedOpenHashMap(CHUNK_SECTION_SIZE);
    // block id = palette index
    private Short2ShortOpenHashMap blockPaletteMap = new Short2ShortOpenHashMap(CHUNK_SECTION_SIZE);

    {
        // Default value
        this.paletteBlockMap.put((short) 0, (short) 0);
        this.blockPaletteMap.put((short) 0, (short) 0);
    }

    public PaletteStorage(int bitsPerEntry) {
        this.hasPalette = bitsPerEntry <= PALETTE_MAXIMUM_BITS;
        this.bitsPerEntry = hasPalette ? bitsPerEntry : 15;
        this.valuesPerLong = Long.SIZE / this.bitsPerEntry;
    }

    private synchronized short getPaletteIndex(short blockId) {
        if (!hasPalette) {
            return blockId;
        }

        if (!blockPaletteMap.containsKey(blockId)) {
            final short paletteIndex = (short) (paletteBlockMap.lastShortKey() + 1);
            this.paletteBlockMap.put(paletteIndex, blockId);
            this.blockPaletteMap.put(blockId, paletteIndex);
            return paletteIndex;
        }

        return blockPaletteMap.get(blockId);
    }

    public void setBlockAt(int x, int y, int z, short blockId) {
        x %= 16;
        if (x < 0) {
            x = CHUNK_SIZE_X + x;
        }
        z %= 16;
        if (z < 0) {
            z = CHUNK_SIZE_Z + z;
        }

        final int sectionIndex = getSectionIndex(x, y % CHUNK_SECTION_SIZE, z);

        final int index = sectionIndex / valuesPerLong;
        final int bitIndex = (sectionIndex % valuesPerLong) * bitsPerEntry;

        final int section = ChunkUtils.getSectionAt(y);

        if (sectionBlocks[section].length == 0) {
            if (blockId == 0) {
                return;
            }

            sectionBlocks[section] = new long[getSize()];
        }

        // Change to palette value
        blockId = getPaletteIndex(blockId);

        long[] sectionBlock = sectionBlocks[section];

        long block = sectionBlock[index];
        {

                /*System.out.println("blockId "+blockId);
                System.out.println("bitIndex "+bitIndex);
                System.out.println("block "+binary(block));
                System.out.println("mask "+binary(cacheMask));
                System.out.println("cache "+binary(cache));*/

            /*block = block >> bitIndex << bitIndex;
            //System.out.println("block "+binary(block));
            block = block | blockId;
            //System.out.println("block2 "+binary(block));
            block = (block << bitIndex);
            //System.out.println("block3 "+binary(block));
            block = block | cache;
            //System.out.println("block4 "+binary(block));*/

            long clear = Integer.MAX_VALUE >> (31 - bitsPerEntry);

            block |= clear << bitIndex;
            block ^= clear << bitIndex;
            block |= (long) blockId << bitIndex;

            sectionBlock[index] = block;

        }
    }

    public short getBlockAt(int x, int y, int z) {
        x %= 16;
        if (x < 0) {
            x = CHUNK_SIZE_X + x;
        }
        z %= 16;
        if (z < 0) {
            z = CHUNK_SIZE_Z + z;
        }

        final int sectionIndex = getSectionIndex(x, y % CHUNK_SECTION_SIZE, z);

        final int index = sectionIndex / valuesPerLong;
        final int bitIndex = sectionIndex % valuesPerLong * bitsPerEntry;

        final int section = ChunkUtils.getSectionAt(y);

        long[] blocks = sectionBlocks[section];

        if (blocks.length == 0) {
            return 0;
        }

        long mask = Integer.MAX_VALUE >> (31 - bitsPerEntry);
        long value = blocks[index] >> bitIndex & mask;
        /*System.out.println("index " + index);
        System.out.println("bitIndex " + bitIndex);
        System.out.println("test1 " + binary(value));
        System.out.println("test2 " + binary(mask));*/

        // Change to palette value
        final short blockId = hasPalette ? paletteBlockMap.get((short) value) : (short) value;

        //System.out.println("final " + binary(finalValue));


        /*System.out.println("data " + index + " " + bitIndex + " " + sectionIndex);
        System.out.println("POS " + x + " " + y + " " + z);
        System.out.println("mask " + binary(mask));
        System.out.println("bin " + binary(blocks[index]));
        System.out.println("result " + ((blocks[index] >> bitIndex) & mask));*/
        return blockId;
    }

    private int getSize() {
        final int blockCount = 16 * 16 * 16; // A whole chunk section
        final int arraySize = (blockCount + valuesPerLong - 1) / valuesPerLong;
        return arraySize;
    }

    public int getBitsPerEntry() {
        return bitsPerEntry;
    }

    public short[] getPalette() {
        return paletteBlockMap.values().toShortArray();
    }

    public long[][] getSectionBlocks() {
        return sectionBlocks;
    }

    public PaletteStorage copy() {
        PaletteStorage paletteStorage = new PaletteStorage(bitsPerEntry);
        paletteStorage.sectionBlocks = sectionBlocks.clone();
        paletteStorage.paletteBlockMap.putAll(paletteBlockMap);
        paletteStorage.blockPaletteMap.putAll(blockPaletteMap);

        return paletteStorage;
    }

    private static String binary(long value) {
        return "0b" + Long.toBinaryString(value);
    }

    private int getSectionIndex(int x, int y, int z) {
        //return (((y * CHUNK_SECTION_SIZE) + z) * CHUNK_SECTION_SIZE) + x;
        return y << 8 | z << 4 | x;
    }

}