package com.horace.coin.tx;

import lombok.SneakyThrows;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class OPTest {

    @SneakyThrows
    @Test
    void encode_num() {
        OP op = new OP();
        assertArrayEquals(new byte[0], op.encode_num(0));
        assertArrayEquals(new byte[]{(byte) 0x01}, op.encode_num(1));
        assertArrayEquals(new byte[]{(byte) 0x81}, op.encode_num(-1));
        assertArrayEquals(new byte[]{(byte) 0x7f}, op.encode_num(127));
        assertArrayEquals(new byte[]{(byte) 0xff}, op.encode_num(-127));
        assertArrayEquals(new byte[]{(byte) 0x80, (byte) 0x00}, op.encode_num(128));
        assertArrayEquals(new byte[]{(byte) 0x80, (byte) 0x80}, op.encode_num(-128));
        assertArrayEquals(new byte[]{(byte) 0xff, (byte) 0x00}, op.encode_num(255));
        assertArrayEquals(new byte[]{(byte) 0xff, (byte) 0x80}, op.encode_num(-255));
    }

    @Test
    void decode_num() {
        OP op = new OP();
        assertEquals(0, op.decode_num(new byte[] {}));
        assertEquals(1, op.decode_num(new byte[] {0x01}));
        assertEquals(-1, op.decode_num(new byte[] {(byte) 0x81}));
        assertEquals(127, op.decode_num(new byte[] {(byte) 0x7f}));
        assertEquals(-127, op.decode_num(new byte[] {(byte) 0xff}));
        assertEquals(128, op.decode_num(new byte[] {(byte) 0x80, (byte) 0x00}));
        assertEquals(-128, op.decode_num(new byte[] {(byte) 0x80, (byte) 0x80}));
        assertEquals(255, op.decode_num(new byte[] {(byte) 0xff, (byte) 0x00}));
        assertEquals(-255, op.decode_num(new byte[] {(byte) 0xff, (byte) 0x80}));
    }

    @SneakyThrows
    @Test
    void op_if_basic_true() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.push(op.encode_num(1));
        List<Integer> items = new ArrayList<>(Arrays.asList(1, 104));
        assertEquals(true, op.op_if(stack, items));
        assertTrue(items.equals(Arrays.asList(1)));
    }

    @SneakyThrows
    @Test
    void op_if_basic_false() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.push(op.encode_num(0));
        List<Integer> items = new ArrayList<>(Arrays.asList(1, 104));
        assertEquals(true, op.op_if(stack, items));
        assertTrue(items.isEmpty());
    }

    @SneakyThrows
    @Test
    void op_if_else_true() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.push(op.encode_num(1));
        List<Integer> items = new ArrayList<>(Arrays.asList(1, 103, 2, 104));
        assertEquals(true, op.op_if(stack, items));
        assertTrue(items.equals(Arrays.asList(1)));
    }

    @SneakyThrows
    @Test
    void op_if_else_false() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.push(op.encode_num(0));
        List<Integer> items = new ArrayList<>(Arrays.asList(1, 103, 2, 104));
        assertEquals(true, op.op_if(stack, items));
        assertTrue(items.equals(Arrays.asList(2)));
    }

    @SneakyThrows
    @Test
    void op_if_nested() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.push(op.encode_num(1));
        List<Integer> items = new ArrayList<>(Arrays.asList(
                1,    // Outer true operation
                99,   // Nested IF
                2,    // Nested true operation
                104,  // Nested ENDIF
                103,  // ELSE
                3,    // Outer false operation
                104   // Outer ENDIF
        ));
        assertEquals(true, op.op_if(stack, items));
        assertTrue(items.equals(Arrays.asList(1, 99, 2, 104)));
    }

    @Test
    public void op_notif_EmptyStack() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        ArrayList<Integer> items = new ArrayList<>();
        items.add(104);  // ENDIF

        boolean result = op.op_notif(stack, items);
        assertFalse(result);
    }

    @SneakyThrows
    @Test
    public void op_notif_SimpleTrueCondition() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(0));  // Condition evaluates to true (0 in this inverted logic)

        ArrayList<Integer> items = new ArrayList<>();
        items.add(1);    // Some operation
        items.add(103);  // ELSE
        items.add(2);    // Some other operation
        items.add(104);  // ENDIF

        boolean result = op.op_notif(stack, items);
        assertTrue(result);
        assertEquals(1, items.size());
        assertEquals(1, items.get(0));
    }

    @SneakyThrows
    @Test
    public void op_notif_SimpleFalseCondition() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));  // Condition evaluates to false (non-zero)

        ArrayList<Integer> items = new ArrayList<>();
        items.add(1);    // True branch operation
        items.add(103);  // ELSE
        items.add(2);    // False branch operation
        items.add(104);  // ENDIF

        boolean result = op.op_notif(stack, items);
        assertTrue(result);
        assertEquals(1, items.size());
        assertEquals(2, items.get(0));
    }

    @SneakyThrows
    @Test
    public void op_notif_NestedIf() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(0));  // Outer condition true

        ArrayList<Integer> items = new ArrayList<>();
        items.add(99);   // Nested IF
        items.add(1);    // Nested operation
        items.add(104);  // Nested ENDIF
        items.add(103);  // ELSE
        items.add(2);    // Else operation
        items.add(104);  // Outer ENDIF

        boolean result = op.op_notif(stack, items);
        assertTrue(result);
        assertEquals(3, items.size());
        assertEquals(99, items.get(0));
        assertEquals(1, items.get(1));
        assertEquals(104, items.get(2));
    }

    @SneakyThrows
    @Test
    public void op_notif_MissingEndif() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(0));  // Condition true

        ArrayList<Integer> items = new ArrayList<>();
        items.add(1);    // Operation
        items.add(103);  // ELSE
        items.add(2);    // Else operation
        // No ENDIF

        boolean result = op.op_notif(stack, items);
        assertFalse(result);
    }

    @SneakyThrows
    @Test
    public void op_notif_MultipleOperations() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(0));  // Condition true

        ArrayList<Integer> items = new ArrayList<>();
        items.add(1);    // Operation 1
        items.add(2);    // Operation 2
        items.add(3);    // Operation 3
        items.add(103);  // ELSE
        items.add(4);    // Else operation
        items.add(104);  // ENDIF

        boolean result = op.op_notif(stack, items);
        assertTrue(result);
        assertEquals(3, items.size());
        assertEquals(1, items.get(0));
        assertEquals(2, items.get(1));
        assertEquals(3, items.get(2));
    }

    @SneakyThrows
    @Test
    public void op_2dup_2() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        boolean result = op.op_2dup(stack);
        assertTrue(result);
        assertEquals(4, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(1, op.decode_num(stack.get(2)));
        assertEquals(2, op.decode_num(stack.get(3)));
    }

    @SneakyThrows
    @Test
    public void op_2dup_4() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        boolean result = op.op_2dup(stack);
        assertTrue(result);
        assertEquals(6, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(3, op.decode_num(stack.get(2)));
        assertEquals(4, op.decode_num(stack.get(3)));
        assertEquals(3, op.decode_num(stack.get(4)));
        assertEquals(4, op.decode_num(stack.get(5)));
    }

    @SneakyThrows
    @Test
    public void op_2over_4() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        boolean result = op.op_2over(stack);
        assertTrue(result);
        assertEquals(6, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(3, op.decode_num(stack.get(2)));
        assertEquals(4, op.decode_num(stack.get(3)));
        assertEquals(1, op.decode_num(stack.get(4)));
        assertEquals(2, op.decode_num(stack.get(5)));
    }

    @SneakyThrows
    @Test
    public void op_2over_6() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        stack.add(op.encode_num(5));
        stack.add(op.encode_num(6));
        boolean result = op.op_2over(stack);
        assertTrue(result);
        assertEquals(8, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(3, op.decode_num(stack.get(2)));
        assertEquals(4, op.decode_num(stack.get(3)));
        assertEquals(5, op.decode_num(stack.get(4)));
        assertEquals(6, op.decode_num(stack.get(5)));
        assertEquals(3, op.decode_num(stack.get(6)));
        assertEquals(4, op.decode_num(stack.get(7)));
    }

    @SneakyThrows
    @Test
    public void op_2rot_6() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        stack.add(op.encode_num(5));
        stack.add(op.encode_num(6));
        boolean result = op.op_2rot(stack);
        assertTrue(result);
        assertEquals(6, stack.size());
        assertEquals(3, op.decode_num(stack.get(0)));
        assertEquals(4, op.decode_num(stack.get(1)));
        assertEquals(5, op.decode_num(stack.get(2)));
        assertEquals(6, op.decode_num(stack.get(3)));
        assertEquals(1, op.decode_num(stack.get(4)));
        assertEquals(2, op.decode_num(stack.get(5)));
    }

    @SneakyThrows
    @Test
    public void op_2rot_8() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        stack.add(op.encode_num(5));
        stack.add(op.encode_num(6));
        stack.add(op.encode_num(7));
        stack.add(op.encode_num(8));
        boolean result = op.op_2rot(stack);
        assertTrue(result);
        assertEquals(8, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(5, op.decode_num(stack.get(2)));
        assertEquals(6, op.decode_num(stack.get(3)));
        assertEquals(7, op.decode_num(stack.get(4)));
        assertEquals(8, op.decode_num(stack.get(5)));
        assertEquals(3, op.decode_num(stack.get(6)));
        assertEquals(4, op.decode_num(stack.get(7)));
    }

    @SneakyThrows
    @Test
    public void op_2swap_4() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        boolean result = op.op_2swap(stack);
        assertTrue(result);
        assertEquals(4, stack.size());
        assertEquals(3, op.decode_num(stack.get(0)));
        assertEquals(4, op.decode_num(stack.get(1)));
        assertEquals(1, op.decode_num(stack.get(2)));
        assertEquals(2, op.decode_num(stack.get(3)));
    }

    @SneakyThrows
    @Test
    public void op_2swap_6() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        stack.add(op.encode_num(5));
        stack.add(op.encode_num(6));
        boolean result = op.op_2swap(stack);
        assertTrue(result);
        assertEquals(6, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(5, op.decode_num(stack.get(2)));
        assertEquals(6, op.decode_num(stack.get(3)));
        assertEquals(3, op.decode_num(stack.get(4)));
        assertEquals(4, op.decode_num(stack.get(5)));
    }

    @SneakyThrows
    @Test
    public void op_pick() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(2));
        boolean result = op.op_pick(stack);
        assertTrue(result);
        assertEquals(4, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(3, op.decode_num(stack.get(2)));
        assertEquals(1, op.decode_num(stack.get(3)));
    }

    @SneakyThrows
    @Test
    public void op_roll() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(2));
        boolean result = op.op_roll(stack);
        assertTrue(result);
        assertEquals(3, stack.size());
        assertEquals(2, op.decode_num(stack.get(0)));
        assertEquals(3, op.decode_num(stack.get(1)));
        assertEquals(1, op.decode_num(stack.get(2)));
    }

    @SneakyThrows
    @Test
    public void op_rot_3() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        boolean result = op.op_rot(stack);
        assertTrue(result);
        assertEquals(3, stack.size());
        assertEquals(2, op.decode_num(stack.get(0)));
        assertEquals(3, op.decode_num(stack.get(1)));
        assertEquals(1, op.decode_num(stack.get(2)));
    }

    @SneakyThrows
    @Test
    public void op_rot_5() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        stack.add(op.encode_num(5));
        boolean result = op.op_rot(stack);
        assertTrue(result);
        assertEquals(5, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(4, op.decode_num(stack.get(2)));
        assertEquals(5, op.decode_num(stack.get(3)));
        assertEquals(3, op.decode_num(stack.get(4)));
    }

    @SneakyThrows
    @Test
    public void op_swap_2() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        boolean result = op.op_swap(stack);
        assertTrue(result);
        assertEquals(2, stack.size());
        assertEquals(2, op.decode_num(stack.get(0)));
        assertEquals(1, op.decode_num(stack.get(1)));
    }

    @SneakyThrows
    @Test
    public void op_swap_4() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        boolean result = op.op_swap(stack);
        assertTrue(result);
        assertEquals(4, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(4, op.decode_num(stack.get(2)));
        assertEquals(3, op.decode_num(stack.get(3)));
    }

    @SneakyThrows
    @Test
    public void op_tuck_2() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        boolean result = op.op_tuck(stack);
        assertTrue(result);
        assertEquals(3, stack.size());
        assertEquals(2, op.decode_num(stack.get(0)));
        assertEquals(1, op.decode_num(stack.get(1)));
        assertEquals(2, op.decode_num(stack.get(2)));
    }

    @SneakyThrows
    @Test
    public void op_tuck_4() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.add(op.encode_num(1));
        stack.add(op.encode_num(2));
        stack.add(op.encode_num(3));
        stack.add(op.encode_num(4));
        boolean result = op.op_tuck(stack);
        assertTrue(result);
        assertEquals(5, stack.size());
        assertEquals(1, op.decode_num(stack.get(0)));
        assertEquals(2, op.decode_num(stack.get(1)));
        assertEquals(4, op.decode_num(stack.get(2)));
        assertEquals(3, op.decode_num(stack.get(3)));
        assertEquals(4, op.decode_num(stack.get(4)));
    }

    @Test
    void op_hash160() {
        OP op = new OP();
        Stack<byte[]> stack = new Stack<>();
        stack.push("hello world".getBytes());
        assertTrue(op.op_hash160(stack));
        assertEquals("d7d5ee7824ff93f94c3055af9382c86c68b5ca92", Hex.toHexString(stack.get(0)));
    }

    @Test
    void op_checksig() {
        OP op = new OP();
        BigInteger z = new BigInteger("7c076ff316692a3d7eb3c3bb0f8b1488cf72e1afcd929e29307032997a838a3d", 16);
        byte[] sec = Hex.decode("04887387e452b8eacc4acfde10d9aaf7f6d9a0f975aabb10d006e4da568744d06c61de6d95231cd89026e286df3b6ae4a894a3378e393e93a0f45b666329a0ae34");
        byte[] sig = Hex.decode("3045022000eff69ef2b1bd93a66ed5219add4fb51e11a840f404876325a1e8ffe0529a2c022100c7207fee197d27c618aea621406f6bf5ef6fca38681d82b2f06fddbdce6feab601");
        Stack<byte[]> stack = new Stack<>();
        stack.push(sig);
        stack.push(sec);
        assertTrue(op.op_checksig(stack, z));
        assertEquals(1, op.decode_num(stack.get(0)));
    }

}