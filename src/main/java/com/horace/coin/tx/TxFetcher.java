package com.horace.coin.tx;

import lombok.SneakyThrows;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bouncycastle.util.Arrays;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

public class TxFetcher {

    private static final Map<String, Tx> cache = new HashMap<>();
    private static final OkHttpClient client = new OkHttpClient();
    private static final HexFormat hexFormat = HexFormat.of();

    public static String getUrl(final boolean testnet) {
        return testnet ? "https://blockstream.info/testnet/api/" : "https://blockstream.info/api/";
    }

    @SneakyThrows
    public static Tx fetch(final String tx_id, final boolean testnet, final boolean fresh) {
        if (fresh || !cache.containsKey(tx_id)) {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(getUrl(testnet)).newBuilder()
                    .addPathSegment("tx")
                    .addPathSegment(tx_id)
                    .addPathSegment("hex");
            Request request = new Request.Builder()
                    .url(urlBuilder.build() )
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String body = response.body().string().trim();
                byte[] raw = hexFormat.parseHex(body);
                if (raw[4] == 0) {
                    raw = Arrays.concatenate(Arrays.copyOf(raw, 4), Arrays.copyOfRange(raw, 6, raw.length));
                }
                Tx tx = Tx.parse(new ByteArrayInputStream(raw), testnet);
                if (!tx.id().equals(tx_id)) {
                    throw new RuntimeException(String.format("tx id mismatch, tx_id: %s vs tx.id: %s", tx_id, tx.id()));
                }
                cache.put(tx_id, tx);
            }
        }
        return cache.get(tx_id);
    }

    public static Tx fetch(final String tx_id, final boolean testnet) {
        return fetch(tx_id, testnet, false);
    }

    public static Tx fetch(final String tx_id) {
        return fetch(tx_id, false, false);
    }

}
