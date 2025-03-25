package com.horace.coin.tx;

import com.horace.coin.Helper;
import com.horace.coin.ecc.S256Point;
import com.horace.coin.ecc.Signature;
import lombok.SneakyThrows;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * https://github.com/bitcoin/bitcoin/blob/master/src/script/interpreter.cpp
 */
public class OP {

    public byte[] encode_num(long num) throws IOException {
        if (num == 0) return new byte[0];
        long abs_num = Math.abs(num);
        boolean negative = num < 0;
        try (ByteArrayOutputStream result = new ByteArrayOutputStream();) {
            while (abs_num != 0) {
                result.write((byte) (abs_num & 0xff));
                abs_num >>>= 8;  // Use unsigned right shift
            }
            byte[] bytes = result.toByteArray();
            // Check if last byte's MSB is set
            if ((bytes[bytes.length - 1] & 0x80) != 0) {
                if (negative) {
                    result.write((byte) 0x80);
                } else {
                    result.write((byte) 0);
                }
            } else if (negative) {
                bytes[bytes.length - 1] |= (byte) 0x80;
                return bytes;
            }
            return result.toByteArray();
        }
    }

    public long decode_num(byte[] element) {
        if (element == null || element.length == 0) return 0;
        // In Java, we'll process from end to start directly
        // (Python's [::-1] reverses, we'll just iterate backwards)
        boolean negative = (element[element.length - 1] & 0x80) != 0;
        long result;

        if (negative) {
            result = element[element.length - 1] & 0x7f;
        } else {
            result = element[element.length - 1] & 0xff;  // Ensure unsigned byte
        }

        // Process remaining bytes from right to left
        for (int i = element.length - 2; i >= 0; i--) {
            result <<= 8;
            result += (element[i] & 0xff);  // Convert byte to unsigned int
        }
        return negative ? -result : result;
    }

    public boolean op_0(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(0));
        return true;
    }

    public boolean op_1negate(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(-1));
        return true;
    }

    public boolean op_1(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(1));
        return true;
    }

    public boolean op_2(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(2));
        return true;
    }

    public boolean op_3(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(3));
        return true;
    }

    public boolean op_4(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(4));
        return true;
    }

    public boolean op_5(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(5));
        return true;
    }

    public boolean op_6(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(6));
        return true;
    }

    public boolean op_7(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(7));
        return true;
    }

    public boolean op_8(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(8));
        return true;
    }

    public boolean op_9(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(9));
        return true;
    }

    public boolean op_10(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(10));
        return true;
    }

    public boolean op_11(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(11));
        return true;
    }

    public boolean op_12(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(12));
        return true;
    }

    public boolean op_13(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(13));
        return true;
    }

    public boolean op_14(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(14));
        return true;
    }

    public boolean op_15(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(15));
        return true;
    }

    public boolean op_16(Stack<byte[]> stack) throws IOException {
        stack.push(encode_num(16));
        return true;
    }

    public boolean op_nop(Stack<byte[]> stack) {
        return true;
    }

    public boolean op_if(Stack<byte[]> stack, List<Integer> items) {
        if (stack.isEmpty()) return false;
        final List<Integer> trueItems = new ArrayList<>();
        final List<Integer> falseItems = new ArrayList<>();
        List<Integer> currentArray = trueItems;
        boolean found = false;
        int numEndifsNeeded = 1;
        while (!items.isEmpty()) {
            final Integer item = items.remove(0); // Pop first element
            if (item == 99 || item == 100) {  // Nested IF/ELSEIF
                numEndifsNeeded++;
                currentArray.add(item);
            } else if (numEndifsNeeded == 1 && item == 103) {  // ELSE
                currentArray = falseItems;
            } else if (item == 104) {  // ENDIF
                if (numEndifsNeeded == 1) {
                    found = true;
                    break;
                } else {
                    numEndifsNeeded--;
                    currentArray.add(item);
                }
            } else {
                currentArray.add(item);
            }
        }
        if (!found) {
            return false;
        }

        final byte[] element = stack.pop();  // Pop from stack
        if (decode_num(element) == 0) {
            items.addAll(0, falseItems);  // Insert falseItems at beginning
        } else {
            items.addAll(0, trueItems);   // Insert trueItems at beginning
        }

        return true;
    }

    public boolean op_notif(Stack<byte[]> stack, List<Integer> items) {
        if (stack.isEmpty()) return false;
        final List<Integer> trueItems = new ArrayList<>();
        final List<Integer> falseItems = new ArrayList<>();
        List<Integer> currentArray = trueItems;
        boolean found = false;
        int numEndifsNeeded = 1;
        while (!items.isEmpty()) {
            final Integer item = items.remove(0);
            if (item == 99 || item == 100) {
                numEndifsNeeded++;
                currentArray.add(item);
            } else if (numEndifsNeeded == 1 && item == 103) {
                currentArray = falseItems;
            } else if (item == 104) {
                if (numEndifsNeeded == 1) {
                    found = true;
                    break;
                } else {
                    numEndifsNeeded--;
                    currentArray.add(item);
                }
            } else {
                currentArray.add(item);
            }
        }
        if (!found) {
            return false;
        }
        final byte[] element = stack.pop();
        if (decode_num(element) == 0) {
            items.addAll(0, trueItems);
        } else {
            items.addAll(0, falseItems);
        }
        return true;
    }

    public boolean op_verify(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        final byte[] element = stack.pop();
        if (decode_num(element) == 0) {
            return false;
        }
        return true;
    }

    public boolean op_return(Stack<byte[]> stack) {
        return false;
    }

    public boolean op_toaltstack(Stack<byte[]> stack, Stack<byte[]> altstack) {
        if (stack.isEmpty()) return false;
        altstack.push(stack.pop());
        return true;
    }

    public boolean op_fromaltstack(Stack<byte[]> stack, Stack<byte[]> altstack) {
        if (altstack.isEmpty()) return false;
        stack.push(altstack.pop());
        return true;
    }

    // (x1 x2 -- )
    public boolean op_2drop(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        stack.pop();
        stack.pop();
        return true;
    }

    // (x1 x2 -- x1 x2 x1 x2)
    public boolean op_2dup(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        byte[] element_last1 = stack.get(stack.size() - 1);
        byte[] element_last2 = stack.get(stack.size() - 2);
        stack.push(Arrays.copyOf(element_last2, element_last2.length));
        stack.push(Arrays.copyOf(element_last1, element_last1.length));
        return true;
    }

    // (x1 x2 x3 -- x1 x2 x3 x1 x2 x3)
    public boolean op_3dup(Stack<byte[]> stack) {
        if (stack.size() < 3) return false;
        byte[] element_last1 = stack.get(stack.size() - 1);
        byte[] element_last2 = stack.get(stack.size() - 2);
        byte[] element_last3 = stack.get(stack.size() - 3);
        stack.push(Arrays.copyOf(element_last3, element_last3.length));
        stack.push(Arrays.copyOf(element_last2, element_last2.length));
        stack.push(Arrays.copyOf(element_last1, element_last1.length));
        return true;
    }

    // (x1 x2 x3 x4 -- x1 x2 x3 x4 x1 x2)
    public boolean op_2over(Stack<byte[]> stack) {
        if (stack.size() < 4) return false;
        byte[] element_over1 = stack.get(stack.size() - 4);
        byte[] element_over2 = stack.get(stack.size() - 3);
        stack.push(Arrays.copyOf(element_over1, element_over1.length));
        stack.push(Arrays.copyOf(element_over2, element_over2.length));
        return true;
    }

    // (x1 x2 x3 x4 x5 x6 -- x3 x4 x5 x6 x1 x2)
    public boolean op_2rot(Stack<byte[]> stack) {
        if (stack.size() < 6) return false;
        byte[] sixthFromTop = stack.remove(stack.size() - 5);
        byte[] fifthFromTop = stack.remove(stack.size() - 5);
        stack.push(fifthFromTop);
        stack.push(sixthFromTop);
        return true;
    }

    // (x1 x2 x3 x4 -- x3 x4 x1 x2)
    public boolean op_2swap(Stack<byte[]> stack) {
        if (stack.size() < 4) return false;
        // Get the top 4 elements
        byte[] top1 = stack.pop();           // -1 from top
        byte[] top2 = stack.pop();           // -2 from top
        byte[] third = stack.pop();          // -3 from top
        byte[] fourth = stack.pop();         // -4 from top
        // Push them back in swapped order
        stack.push(top2);                    // -2 moves to -4
        stack.push(top1);                    // -1 moves to -3
        stack.push(fourth);                  // -4 moves to -2
        stack.push(third);                   // -3 moves to -1
        return true;
    }

    // (x - 0 | x x)
    public boolean op_ifdup(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        byte[] element = stack.peek();
        if (decode_num(element) != 0) {
            stack.push(Arrays.copyOf(element, element.length));
        }
        return true;
    }

    @SneakyThrows
    public boolean op_depth(Stack<byte[]> stack) {
        stack.push(encode_num(stack.size()));
        return true;
    }

    public boolean op_drop(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        stack.pop();
        return true;
    }

    // (x -- x x)
    public boolean op_dup(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        byte[] element = stack.peek();
        stack.push(Arrays.copyOf(element, element.length));
        return true;
    }

    // (x1 x2 -- x2)
    public boolean op_nip(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        stack.removeElementAt(stack.size() - 2);
        return true;
    }

    // (x1 x2 -- x1 x2 x1)
    public boolean op_over(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        byte[] element = stack.get(stack.size() - 2);
        stack.push(Arrays.copyOf(element, element.length));
        return true;
    }

    // (xn ... x2 x1 x0 n --> xn ... x2 x1 x0 xn)
    public boolean op_pick(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        if (stack.size() < n + 1) return false;
        byte[] element = stack.get(Math.toIntExact(stack.size() - n - 1));
        stack.push(Arrays.copyOf(element, element.length));
        return true;
    }

    // (xn ... x2 x1 x0 n --> ... x2 x1 x0 xn)
    public boolean op_roll(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        if (stack.size() < n + 1) return false;
        if (n == 0) return true;
        byte[] element = stack.remove(Math.toIntExact(stack.size() - n - 1));
        stack.push(element);
        return true;
    }

    // (x1 x2 x3 -- x2 x3 x1)
    //  x2 x1 x3  after first swap
    //  x2 x3 x1  after second swap
    public boolean op_rot(Stack<byte[]> stack) {
        if (stack.size() < 3) return false;
        byte[] element = stack.remove(stack.size() - 3);
        stack.push(element);
        return true;
    }

    // (x1 x2 -- x2 x1)
    public boolean op_swap(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        byte[] xn = stack.pop();
        byte[] xn_1 = stack.pop();
        stack.push(xn);
        stack.push(xn_1);
        return true;
    }

    // (x1 x2 -- x2 x1 x2)
    public boolean op_tuck(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        byte[] xn = stack.peek();
        stack.insertElementAt(Arrays.copyOf(xn, xn.length), stack.size() - 2);
        return true;
    }

    @SneakyThrows
    public boolean op_size(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        stack.push(encode_num(stack.peek().length));
        return true;
    }

    @SneakyThrows
    public boolean op_equal(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        byte[] xn = stack.pop();
        byte[] xn_1 = stack.pop();
        if (Arrays.equals(xn, xn_1)) {
            stack.push(encode_num(1));
        } else {
            stack.push(encode_num(0));
        }
        return true;
    }

    public boolean op_equalverify(Stack<byte[]> stack) {
        return op_equal(stack) && op_verify(stack);
    }

    @SneakyThrows
    public boolean op_1add(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        stack.push(encode_num(n + 1));
        return true;
    }

    @SneakyThrows
    public boolean op_1sub(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        stack.push(encode_num(n - 1));
        return true;
    }

    @SneakyThrows
    public boolean op_negate(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        stack.push(encode_num(-n));
        return true;
    }

    @SneakyThrows
    public boolean op_abs(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        if (n < 0) stack.push(encode_num(-n));
        else stack.push(encode_num(n));
        return true;
    }

    @SneakyThrows
    public boolean op_not(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        if (n == 0) stack.push(encode_num(1));
        else stack.push(encode_num(0));
        return true;
    }

    @SneakyThrows
    public boolean op_0notequal(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        long n = decode_num(stack.pop());
        if (n == 0) stack.push(encode_num(0));
        else stack.push(encode_num(1));
        return true;
    }

    @SneakyThrows
    public boolean op_add(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        stack.push(encode_num(a + b));
        return true;
    }

    @SneakyThrows
    public boolean op_sub(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        stack.push(encode_num(b - a));
        return true;
    }

    @SneakyThrows
    public boolean op_mul(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        stack.push(encode_num(b * a));
        return true;
    }

    @SneakyThrows
    public boolean op_booland(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (a != 0 && b != 0) ? 1 : 0;
        stack.push(encode_num(result));
        return true;
    }

    @SneakyThrows
    public boolean op_boolor(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (a != 0 || b != 0) ? 1 : 0;
        stack.push(encode_num(result));
        return true;
    }

    @SneakyThrows
    public boolean op_numequal(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (a == b) ? 1 : 0;
        stack.push(encode_num(result));
        return true;
    }

    public boolean op_numequalverify(Stack<byte[]> stack) {
        return op_numequal(stack) && op_verify(stack);
    }

    @SneakyThrows
    public boolean op_numnotequal(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (a == b) ? 0 : 1;
        stack.push(encode_num(result));
        return true;
    }

    @SneakyThrows
    public boolean op_lessthan(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (b < a) ? 1 : 0;
        stack.push(encode_num(result));
        return true;
    }

    @SneakyThrows
    public boolean op_greaterthan(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (b > a) ? 1 : 0;
        stack.push(encode_num(result));
        return true;
    }

    @SneakyThrows
    public boolean op_lessthanorequal(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (b <= a) ? 1 : 0;
        stack.push(encode_num(result));
        return true;
    }

    @SneakyThrows
    public boolean op_greaterthanorequal(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        int result = (b >= a) ? 1 : 0;
        stack.push(encode_num(result));
        return true;
    }

    @SneakyThrows
    public boolean op_min(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        if (a < b) stack.push(encode_num(a));
        else stack.push(encode_num(b));
        return true;
    }

    @SneakyThrows
    public boolean op_max(Stack<byte[]> stack) {
        if (stack.size() < 2) return false;
        long a = decode_num(stack.pop());
        long b = decode_num(stack.pop());
        if (a > b) stack.push(encode_num(a));
        else stack.push(encode_num(b));
        return true;
    }

    @SneakyThrows
    public boolean op_within(Stack<byte[]> stack) {
        if (stack.size() < 3) return false;
        long maximum = decode_num(stack.pop());
        long minimum = decode_num(stack.pop());
        long element = decode_num(stack.pop());
        if (element >= minimum && element < maximum) stack.push(encode_num(1));
        else stack.push(encode_num(0));
        return true;
    }

    public boolean op_ripemd160(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        byte[] element = stack.pop();
        final RIPEMD160.Digest digest = new RIPEMD160.Digest();
        stack.push(digest.digest(element));
        return true;
    }

    @SneakyThrows
    public boolean op_sha1(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        byte[] element = stack.pop();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        stack.push(messageDigest.digest(element));
        return true;
    }

    @SneakyThrows
    public boolean op_sha256(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        byte[] element = stack.pop();
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        stack.push(messageDigest.digest(element));
        return true;
    }

    public boolean op_hash160(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        byte[] element = stack.pop();
        stack.push(Helper.hash160(element));
        return true;
    }

    public boolean op_hash256(Stack<byte[]> stack) {
        if (stack.isEmpty()) return false;
        byte[] element = stack.pop();
        stack.push(Helper.hash256(element));
        return true;
    }

    @SneakyThrows
    public boolean op_checksig(Stack<byte[]> stack, BigInteger z) {
        if (stack.size() < 2) return false;
        byte[] sec_pubkey = stack.pop();
        byte[] der_signature = removeLastByte(stack.pop());  // remove the last element
        S256Point point = S256Point.parse(sec_pubkey);
        Signature sig = Signature.parse(der_signature);
        if (point.verify(z, sig)) stack.push(encode_num(1));
        else stack.push(encode_num(0));
        return true;
    }

    private static byte[] removeLastByte(byte[] arr) {
        if (arr.length == 0) {
            return arr;
        }
        byte[] newArr = new byte[arr.length - 1];
        System.arraycopy(arr, 0, newArr, 0, arr.length - 1);
        return newArr;
    }

    public boolean op_checksigverify(Stack<byte[]> stack, BigInteger z) {
        return op_checksig(stack, z) || op_verify(stack);
    }

    @SneakyThrows
    public boolean op_checkmultisig(Stack<byte[]> stack, BigInteger z) {
        if (stack.isEmpty()) return false;
        int n = (int) decode_num(stack.pop());
        if (stack.size() < n + 1) return false;
        List<byte[]> sec_pubkeys = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            sec_pubkeys.add(stack.pop());
        }
        int m = (int) decode_num(stack.pop());
        if (stack.size() < m + 1) return false;
        List<byte[]> der_signatures = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            byte[] element = stack.pop();
            der_signatures.add(removeLastByte(element));
        }
        stack.pop();
        List<S256Point> points = sec_pubkeys.stream().map(S256Point::parse).collect(Collectors.toList());
        List<Signature> sigs = der_signatures.stream().map(Signature::parse).collect(Collectors.toList());
        for (Signature sig : sigs) {
            if (points.size() == 0) return false;
            while (points.size() > 0) {
                S256Point point = points.remove(0);  // pop(0)
                if (point.verify(z, sig)) break;
            }
        }
        stack.push(encode_num(1));
        return true;
    }

    public boolean op_checkmultisigverify(Stack<byte[]> stack, BigInteger z) {
        return op_checkmultisig(stack, z) || op_verify(stack);
    }

    public boolean op_checklocktimeverify(Stack<byte[]> stack, int locktime, int sequence) {
        if (sequence == 0xFFFFFFFF) { // Max sequence value, means disabled
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }

        long element = decode_num(stack.peek()); // Peek at the top element (do not pop)
        if (element < 0) {
            return false;
        }
        if (element < 500000000 && locktime > 500000000) {
            return false;
        }
        if (locktime < element) {
            return false;
        }
        return true;
    }

    public boolean op_checksequenceverify(Stack<byte[]> stack, int version, int sequence) {
        if ((sequence & (1 << 31)) != 0) { // If the sequence has the disable flag set
            return false;
        }
        if (stack.isEmpty()) {
            return false;
        }

        long element = decode_num(stack.peek()); // Peek at the top element (do not pop)
        if (element < 0) {
            return false;
        }

        if ((element & (1 << 31)) != 0) { // Check if the element has the disable flag
            if (version < 2) {
                return false;
            }
            if ((sequence & (1 << 31)) != 0) {
                return false;
            }
            if ((element & (1 << 22)) != (sequence & (1 << 22))) {
                return false;
            }
            if ((element & 0xFFFF) > (sequence & 0xFFFF)) { // Compare lower 16 bits
                return false;
            }
        }
        return true;
    }


    public static final String[] OP_CODE_NAMES = new String[]{
            "OP_0", "OP_PUSHBYTES_1", "OP_PUSHBYTES_2", "OP_PUSHBYTES_3", "OP_PUSHBYTES_4", "OP_PUSHBYTES_5", "OP_PUSHBYTES_6", "OP_PUSHBYTES_7", "OP_PUSHBYTES_8", "OP_PUSHBYTES_9",
            "OP_PUSHBYTES_10", "OP_PUSHBYTES_11", "OP_PUSHBYTES_12", "OP_PUSHBYTES_13", "OP_PUSHBYTES_14", "OP_PUSHBYTES_15", "OP_PUSHBYTES_16", "OP_PUSHBYTES_17", "OP_PUSHBYTES_18", "OP_PUSHBYTES_19",
            "OP_PUSHBYTES_20", "OP_PUSHBYTES_21", "OP_PUSHBYTES_22", "OP_PUSHBYTES_23", "OP_PUSHBYTES_24", "OP_PUSHBYTES_25", "OP_PUSHBYTES_26", "OP_PUSHBYTES_27", "OP_PUSHBYTES_28", "OP_PUSHBYTES_29",
            "OP_PUSHBYTES_30", "OP_PUSHBYTES_31", "OP_PUSHBYTES_32", "OP_PUSHBYTES_33", "OP_PUSHBYTES_34", "OP_PUSHBYTES_35", "OP_PUSHBYTES_36", "OP_PUSHBYTES_37", "OP_PUSHBYTES_38", "OP_PUSHBYTES_39",
            "OP_PUSHBYTES_40", "OP_PUSHBYTES_41", "OP_PUSHBYTES_42", "OP_PUSHBYTES_43", "OP_PUSHBYTES_44", "OP_PUSHBYTES_45", "OP_PUSHBYTES_46", "OP_PUSHBYTES_47", "OP_PUSHBYTES_48", "OP_PUSHBYTES_49",
            "OP_PUSHBYTES_50", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "OP_PUSHBYTES_75", "OP_PUSHDATA1", "OP_PUSHDATA2", "OP_PUSHDATA4", "OP_1NEGATE",
            "", "OP_1", "OP_2", "OP_3", "OP_4", "OP_5", "OP_6", "OP_7", "OP_8", "OP_9",
            "OP_10", "OP_11", "OP_12", "OP_13", "OP_14", "OP_15", "OP_16", "OP_NOP", "", "OP_IF",
            "OP_NOTIF", "", "", "OP_ELSE", "OP_ENDIF", "OP_VERIFY", "OP_RETURN", "OP_TOALTSTACK", "OP_FROMALTSTACK", "OP_2DROP",
            "OP_2DUP", "OP_3DUP", "OP_2OVER", "OP_2ROT", "OP_2SWAP", "OP_IFDUP", "OP_DEPTH", "OP_DROP", "OP_DUP", "OP_NIP",
            "OP_OVER", "OP_PICK", "OP_ROLL", "OP_ROT", "OP_SWAP", "OP_TUCK", "", "", "", "",
            "OP_SIZE", "", "", "", "", "OP_EQUAL", "OP_EQUALVERIFY", "", "", "OP_1ADD",
            "OP_1SUB", "", "", "OP_NEGATE", "OP_ABS", "OP_NOT", "OP_0NOTEQUAL", "OP_ADD", "OP_SUB", "OP_MUL",
            "", "", "", "", "OP_BOOLAND", "OP_BOOLOR", "OP_NUMEQUAL", "OP_NUMEQUALVERIFY", "OP_NUMNOTEQUAL", "OP_LESSTHAN",
            "OP_GREATERTHAN", "OP_LESSTHANOREQUAL", "OP_GREATERTHANOREQUAL", "OP_MIN", "OP_MAX", "OP_WITHIN", "OP_RIPEMD160", "OP_SHA1", "OP_SHA256", "OP_HASH160",
            "OP_HASH256", "OP_CODESEPARATOR", "OP_CHECKSIG", "OP_CHECKSIGVERIFY", "OP_CHECKMULTISIG", "OP_CHECKMULTISIGVERIFY", "OP_NOP1", "OP_CHECKLOCKTIMEVERIFY", "OP_CHECKSEQUENCEVERIFY", "OP_NOP4",
            "OP_NOP5", "OP_NOP6", "OP_NOP7", "OP_NOP8", "OP_NOP9", "OP_NOP10", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
    };

}
