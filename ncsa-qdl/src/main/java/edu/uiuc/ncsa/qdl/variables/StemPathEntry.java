package edu.uiuc.ncsa.qdl.variables;

/**
 * <p>Created by Jeff Gaynor<br>
 * on 6/7/21 at  8:47 AM
 */
public class StemPathEntry {

    String int_regex = "[+-]?[1-9][0-9]*";

    /**
     * constructor that does not vencode the entry. The assumption is that this entry is
     * already vencoded and does not need anything done to it. See {@link #StemPathEntry(String, boolean)}
     * @param key
     */
    public StemPathEntry(String key) {
    this(key, false);
    }

    public StemPathEntry(String key, boolean encode) {
        if (key.equals("0") || key.matches(int_regex)) {
            this.index = Long.parseLong(key);
        } else {
            this.key = encode ? codec.encode(key) : key;
        }
    }

    public StemPathEntry(Long index) {
        this.index = index;
    }

    String key;
    Long index;

    public String getKey() {
        return key;
    }
     public String getKey(boolean decode){
        return decode?codec.decode(key):key;
     }
    public void setKey(String key) {
        this.key = key;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public boolean isString() {
        return key != null;
    }

    QDLCodec codec = new QDLCodec();

    public String decodePath() {
        if (key == null) {
            return null;
        }
        return codec.decode(key);
    }


}
