package ggc.exceptions;

public class DuplicatePartnerException extends Exception {
    /** invalid key */
    private String _key;

    /**
     * @param key
     */
    public DuplicatePartnerException(String key) {
        _key = key;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return _key;
    }
}