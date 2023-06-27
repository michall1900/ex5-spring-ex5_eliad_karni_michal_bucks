package hac.embeddables;

import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class UpdateMap {

    @ElementCollection
    private List<Update> entries = new ArrayList<>();

    public UpdateMap(List<Update> entries) {
        this.entries = entries;
    }

    public UpdateMap() {
    }

    public List<Update> getEntries() {
        return entries;
    }

    public void setEntries(List<Update> entries) {
        this.entries = entries;
    }
}
