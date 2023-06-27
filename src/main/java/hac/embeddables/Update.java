package hac.embeddables;

import javax.persistence.Embeddable;

@Embeddable
public class Update {
    private String key;
    private String value;

    public Update() {
    }

    public Update(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
