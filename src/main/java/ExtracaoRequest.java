import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ExtracaoRequest {

    private Long id;
    private List<Object> values;

    private ExtracaoRequest() {
        values = new ArrayList<>();
    }

    public Long getId() {
        return id;
    }

    public List<Object> getValues() {
        return values;
    }

    public static class Builder {

        private ExtracaoRequest request = new ExtracaoRequest();

        public Builder id(Long id) {
            this.request.id = id;
            return this;
        }

        public Builder addValue(Object value) {
            this.request.values.add(value);
            return this;
        }

        public Builder addValues(Collection<Object> value) {
            this.request.values.addAll(value);
            return this;
        }

        public ExtracaoRequest build() {
            return request;
        }
    }
}
