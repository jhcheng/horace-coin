package com.horace.coin;

import com.horace.coin.tx.EndianUtils;
import lombok.SneakyThrows;
import org.bouncycastle.jcajce.provider.digest.RIPEMD160;
import org.bouncycastle.util.BigIntegers;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;

public class Helper {

    public static int SIGHASH_ALL = 1;
    public static int SIGHASH_NONE = 2;
    public static int SIGHASH_SINGLE = 3;

    private static final String BASE58_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
    private static final BigInteger FIFTY_EIGHT = BigInteger.valueOf(58);
    private static final long TWO_WEEKS = 60 * 60 * 24 * 14;

    @SneakyThrows
    public static byte[] hash256(byte[] input) {
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(digest.digest(input));
    }

    @SneakyThrows
    public static byte[] hash160(byte[] input) {
        final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        final RIPEMD160.Digest digest = new RIPEMD160.Digest();
        return digest.digest(sha256.digest(input));
    }

    public static String encode_base58(final byte[] s) {
        int count = 0;
        for (byte c : s) {
            if (c == 0) count++;
            else break;
        }
        BigInteger num = BigIntegers.fromUnsignedByteArray(s);
        String[] prefix = new String[count];
        Arrays.fill(prefix, "1");
        final StringBuffer sb = new StringBuffer();
        while (num.compareTo(BigInteger.ZERO) > 0) {
            final BigInteger[] divmod = num.divideAndRemainder(BigInteger.valueOf(58));
            sb.insert(0, BASE58_ALPHABET.charAt(divmod[1].intValueExact()));
            num = divmod[0];
        }
        sb.insert(0, String.join("", prefix));
        return sb.toString();
    }

    public static String encode_base58_checksum(final byte[] b) {
        final byte[] temp = new byte[b.length + 4];
        System.arraycopy(b, 0, temp, 0, b.length);
        System.arraycopy(hash256(b), 0, temp, b.length, 4);
        return encode_base58(temp);
    }

    public static byte[] decode_base58(String s) {
        // Convert Base58 string to BigInteger
        BigInteger num = BigInteger.ZERO;
        for (char c : s.toCharArray()) {
            int digit = BASE58_ALPHABET.indexOf(c);
            if (digit == -1) {
                throw new IllegalArgumentException("Invalid character in Base58 string: " + c);
            }
            num = num.multiply(FIFTY_EIGHT).add(BigInteger.valueOf(digit));
        }
        // Convert to 25 bytes
        byte[] combined = num.toByteArray();
        // Pad or trim to exactly 25 bytes
        byte[] result = new byte[25];
        int length = combined.length;
        if (length > 25) {
            System.arraycopy(combined, length - 25, result, 0, 25);
        } else {
            System.arraycopy(combined, 0, result, 25 - length, length);
        }

        // Verify checksum
        byte[] checksum = new byte[4];
        System.arraycopy(result, 21, checksum, 0, 4);
        byte[] payload = new byte[21];
        System.arraycopy(result, 0, payload, 0, 21);

        byte[] calculatedChecksum = hash256(payload);
        for (int i = 0; i < 4; i++) {
            if (checksum[i] != calculatedChecksum[i]) {
                HexFormat hexFormat = HexFormat.of();
                throw new IllegalArgumentException(
                        String.format("Bad address: checksum mismatch %s vs %s",
                                hexFormat.formatHex(checksum), hexFormat.formatHex(calculatedChecksum))
                );
            }
        }
        // Return payload without version byte and checksum
        byte[] output = new byte[20];
        System.arraycopy(result, 1, output, 0, 20);
        return output;
    }

    public static String h160_to_p2pkh_address(byte[] h160) {
        return h160_to_p2pkh_address(h160, false);
    }

    public static String h160_to_p2pkh_address(byte[] h160, boolean testnet) {
        byte[] prefix = testnet ? new byte[]{0x6f} : new byte[]{0x00};
        return encode_base58_checksum(org.bouncycastle.util.Arrays.concatenate(prefix, h160));
    }

    public static String h160_to_p2sh_address(byte[] h160) {
        return h160_to_p2sh_address(h160, false);
    }

    public static String h160_to_p2sh_address(byte[] h160, boolean testnet) {
        byte[] prefix = testnet ? new byte[]{(byte) 0xc4} : new byte[]{0x05};
        return encode_base58_checksum(org.bouncycastle.util.Arrays.concatenate(prefix, h160));
    }

    public static BigInteger bitsToTarget(byte[] bits) {
        int exponent = bits[3] & 0xFF;
        int coefficient = EndianUtils.littleEndianToInt(Arrays.copyOf(bits, 3)).intValueExact();
        return BigInteger.valueOf(coefficient).multiply(BigInteger.valueOf(256).pow(exponent - 3));
    }

    @SneakyThrows
    public static byte[] targetToBits(BigInteger target) {
        byte[] raw_bytes = lstrip(target.toByteArray(), (byte) 0x00);
        int exponent;
        byte[] coefficient;
        if (raw_bytes[0] > 0x7f) {
            exponent = raw_bytes.length + 1;
            coefficient = new byte[]{0x00, raw_bytes[0], raw_bytes[1]};
        } else {
            exponent = raw_bytes.length;
            coefficient = Arrays.copyOf(raw_bytes, Math.min(3, raw_bytes.length));
        }
        // Reverse coefficient for little-endian format
        byte[] newBits = new byte[4];
        for (int i = 0; i < coefficient.length; i++) {
            newBits[2 - i] = coefficient[i]; // Reverse first 3 bytes
        }
        newBits[3] = (byte) exponent; // Append exponent
        return newBits;
    }

    public static byte[] lstrip(byte[] bytes, byte b) {
        int startIdx = 0;
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] != b) {
                startIdx = i;
                break;
            }
        }
        return Arrays.copyOfRange(bytes, startIdx, bytes.length);
    }

    public static byte[] calculate_new_bits(byte[] previous_bits, long time_differential) {
        if (time_differential > TWO_WEEKS * 4) {
            time_differential = TWO_WEEKS * 4;
        }
        if (time_differential < TWO_WEEKS / 4) {
            time_differential = TWO_WEEKS / 4;
        }
        // Convert previous bits to target
        BigInteger previousTarget = bitsToTarget(previous_bits);
        BigInteger newTarget = previousTarget.multiply(BigInteger.valueOf(time_differential))
                .divide(BigInteger.valueOf(TWO_WEEKS));

        // Convert new target back to bits
        return targetToBits(newTarget);
    }

}
