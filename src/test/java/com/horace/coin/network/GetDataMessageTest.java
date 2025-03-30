package com.horace.coin.network;

import org.junit.jupiter.api.Test;

import java.util.HexFormat;

import static org.junit.jupiter.api.Assertions.*;

class GetDataMessageTest {

    @Test
    void test_serialize() {
        GetDataMessage message = new GetDataMessage();
        message.addData(IMessage.FILTERED_BLOCK_DATA_TYPE, HexFormat.of().parseHex("00000000000000cac712b726e4326e596170574c01a16001692510c44025eb30"));
        message.addData(IMessage.FILTERED_BLOCK_DATA_TYPE, HexFormat.of().parseHex("00000000000000beb88910c46f6b442312361c6693a7fb52065b583979844910"));
        assertArrayEquals(HexFormat.of().parseHex("020300000030eb2540c41025690160a1014c577061596e32e426b712c7ca00000000000000030000001049847939585b0652fba793661c361223446b6fc41089b8be00000000000000"),
                message.serialize());
    }

}