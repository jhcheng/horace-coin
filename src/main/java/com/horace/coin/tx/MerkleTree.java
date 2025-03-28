package com.horace.coin.tx;

import com.horace.coin.Helper;
import lombok.Getter;

import java.util.Arrays;

public class MerkleTree {

    private int current_depth = 0;
    private int current_index = 0;
    private int max_depth = 0;
    private int total = 0;
    @Getter
    private byte[][][] nodes;

    public MerkleTree(int total) {
        this.total = total;
        this.max_depth = (int) Math.ceil(Math.log10(total)/Math.log10(2));
        nodes = new byte[max_depth + 1][][];
        for (int depth = 0; depth <= max_depth; depth++) {
            int num_items = (int) Math.ceil(total / Math.pow(2, max_depth - depth));
            nodes[depth] = new byte[num_items][];
        }
    }

    public void up() {
        current_depth--;
        current_index = current_index / 2;
    }

    public void left() {
        current_depth++;
        current_index = current_index * 2;
    }

    public void right() {
        current_depth++;
        current_index = current_index * 2 + 1;
    }

    public byte[] root() {
        return this.nodes[0][0];
    }

    public void set_current_node(byte[] value) {
        this.nodes[current_depth][current_index] = value;
    }

    public byte[] get_current_node() {
        return this.nodes[current_depth][current_index];
    }

    public byte[] get_left_node() {
        return this.nodes[current_depth + 1][current_index * 2];
    }

    public byte[] get_right_node() {
        return this.nodes[current_depth + 1][current_index * 2 + 1];
    }

    public boolean is_leaf() {
        return (current_depth == max_depth);
    }

    public boolean right_exists() {
        return this.nodes[current_depth + 1].length > current_index * 2 + 1;
    }

    public void populate_tree(byte[] flag_bits, byte[][] hashes) {
        int flag_index = 0;
        int hash_index = 0;
        while (root() == null) {
            if (is_leaf()) {
                flag_index++;
                set_current_node(hashes[hash_index]);
                hash_index++;
                up();
            } else {
                byte[] left_hash = get_left_node();
                if (left_hash == null) {
                    int flag = flag_bits[flag_index];
                    flag_index++;
                    if (flag == 0) {
                        set_current_node(hashes[hash_index]);
                        hash_index++;
                        up();
                    } else {
                        left();
                    }
                } else if (right_exists()) {
                    byte[] right_hash = get_right_node();
                    if (right_hash == null) {
                        right();
                    } else {
                        set_current_node(Helper.merkle_parent(left_hash, right_hash));
                        up();
                    }
                } else {
                    set_current_node(Helper.merkle_parent(left_hash, left_hash));
                    up();
                }
            }
        }
        if (hash_index != hashes.length) {
            throw new RuntimeException("hashes not all consumed");
        }
        for (int i = flag_index; i < flag_bits.length; i++) {
            if (flag_bits[i] == 1) {
                throw new RuntimeException("flag bits not all consumed");
            }
        }
    }

}
