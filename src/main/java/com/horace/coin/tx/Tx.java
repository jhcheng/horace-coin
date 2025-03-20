package com.horace.coin.tx;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Tx {

    private final int version;
    private final TxIn[] txIns;
    private final TxOut[] txOuts;
    private final long lockTime;
    private final boolean testnet;


}
