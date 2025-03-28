package com.horace.coin.tx;

import com.horace.coin.Helper;
import lombok.Getter;
import org.bouncycastle.util.Arrays;

import java.nio.ByteBuffer;

@Getter
public class MerkleBlock extends Block {

    private int total;
    private byte[][] hashes;
    private byte[] flags;

    public MerkleBlock(int version, byte[] prevBlock, byte[] merkleRoot, int timestamp, byte[] bits, byte[] nonce, int total, byte[][] hashes, byte[] flags) {
        super(version, prevBlock, merkleRoot, timestamp, bits, nonce);
        this.total = total;
        this.hashes = hashes;
        this.flags = flags;
    }

    public static MerkleBlock parse(ByteBuffer buffer) {
        Block block = Block.parse(buffer);
        byte[] t = new byte[4];
        buffer.get(t);
        int total = EndianUtils.littleEndianToInt(t).intValue();
        int num_tx_hashes = (int) EndianUtils.readVarInt(buffer);
        byte[][] hashes = new byte[num_tx_hashes][];
        for (int i = 0; i < num_tx_hashes; i++) {
            byte[] tx_hash = new byte[32];
            buffer.get(tx_hash);
            hashes[i] = Arrays.reverse(tx_hash);
        }
        int flags_len = (int) EndianUtils.readVarInt(buffer);
        byte[] flags = new byte[flags_len];
        buffer.get(flags);
        return new MerkleBlock(block.getVersion(), block.getPrevBlock(), block.getMerkleRoot(), block.getTimestamp(), block.getBits(), block.getNonce(), total, hashes, flags);
    }

    public static MerkleBlock parse(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return parse(buffer);
    }


    public boolean is_valid() {
        byte[] flag_bits = Helper.bytes_to_bit_field(flags);
        byte[][] h = java.util.Arrays.stream(hashes).map(Arrays::reverse).toArray(byte[][]::new);
        MerkleTree tree = new MerkleTree(total);
        tree.populate_tree(flag_bits, h);
        return Arrays.areEqual(Arrays.reverse(tree.root()), getMerkleRoot());
    }

}
