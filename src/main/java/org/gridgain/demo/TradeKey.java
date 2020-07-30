package org.gridgain.demo;

public class TradeKey {
    private long ID;

    private int BUYER_ID;

    public TradeKey(long id, int buyerId) {
        this.ID = id;
        this.BUYER_ID = buyerId;
    }

    public long getId() {
        return ID;
    }

    public int getBuyerId() {
        return BUYER_ID;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TradeKey key = (TradeKey)o;

        if (ID != key.ID)
            return false;
        return BUYER_ID == key.BUYER_ID;
    }

    @Override public int hashCode() {
        int result = (int)(ID ^ (ID >>> 32));
        result = 31 * result + BUYER_ID;
        return result;
    }
}
