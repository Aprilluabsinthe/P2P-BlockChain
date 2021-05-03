package TransLib;

import java.io.Serializable;

/**
 * GetStateReply - a serializable packet wrapper for the getState
 * method of transportLayer
 */
public class GetStateReply implements Serializable {
    /**
     * The current term number.
     */
    public int length;
    /**
     * The leader flag.
     */
    public String lastHash;

    private static final long serialVersionUID = 1L;

    /**
     * GetStateReply - Construct a get state packet with given term and leader.
     *
     * @param length the current length
     * @param lastHash The lastHash
     */
    public GetStateReply(int length, String lastHash) {
        this.length = length;
        this.lastHash = lastHash;
    }
}
