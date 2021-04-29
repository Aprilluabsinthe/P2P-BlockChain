package basic;

import java.util.Objects;

public class Block {
    private int index;
    private String timestamp;
    private int asset;
    private String Hash;
    private String preHash;
    private int difficulty;
    private String nouce;


    public Block(int index, String timestamp, int asset, String hash, String preHash, int difficulty, String nouce) {
        this.index = index;
        this.timestamp = timestamp;
        this.asset = asset;
        Hash = hash;
        this.preHash = preHash;
        this.difficulty = difficulty;
        this.nouce = nouce;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getAsset() {
        return asset;
    }

    public void setAsset(int asset) {
        this.asset = asset;
    }

    public String getHash() {
        return Hash;
    }

    public void setHash(String hash) {
        Hash = hash;
    }

    public String getPreHash() {
        return preHash;
    }

    public void setPreHash(String preHash) {
        this.preHash = preHash;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getNouce() {
        return nouce;
    }

    public void setNouce(String nouce) {
        this.nouce = nouce;
    }

    @Override
    public String toString() {
        return "Block{" +
                "index=" + index +
                ", timestamp='" + timestamp + '\'' +
                ", asset=" + asset +
                ", Hash='" + Hash + '\'' +
                ", preHash='" + preHash + '\'' +
                ", difficulty=" + difficulty +
                ", nouce='" + nouce + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Block)) return false;
        Block block = (Block) o;
        return index == block.index && asset == block.asset && difficulty == block.difficulty && timestamp.equals(block.timestamp) && Hash.equals(block.Hash) && preHash.equals(block.preHash) && nouce.equals(block.nouce);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, timestamp, asset, Hash, preHash, difficulty, nouce);
    }
}
