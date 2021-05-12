package LabBlockChain.BlockChain.p2p;

/**
 * the wrapped message type in p2p message exchanging and serlization process
 */
public enum MessageType {
    QUERY_LATEST_BLOCK(0),
    QUERY_BLOCKCHAIN(1),
    QUERY_TRANSACTION(2),
    QUERY_PACKED_TRANSACTION(3),
    QUERY_WALLET(4),
    RESPONSE_BLOCKCHAIN(5),
    RESPONSE_TRANSACTION(6),
    RESPONSE_PACKED_TRANSACTION(7),
    RESPONSE_WALLET(8),
    QUERY_UNPACKED_TRANSACTION(9),
    QUERY_ALL_TRANSACTION(10),
    QUERY_PEER(11),
    RESPONSE_UNPACKED_TRANSACTION(12),
    RESPONSE_ALL_TRANSACTION(13),
    RESPONSE_PEER(14);


    public int value;

    /**
     * set value for each type
     * @param value
     */
    MessageType(int value) {
        this.value = value;
    }

    int getValue(){
        return value;
    }
}
