
public class ExtracaoResponse {

    private Long id;
    private boolean success;
    private String message;

    private ExtracaoResponse() {

    }

    public Long getId() {
        return id;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public static class Builder {

        private ExtracaoResponse response = new ExtracaoResponse();

        public Builder id(Long id) {
            response.id = id;
            return this;
        }

        public Builder success(boolean success) {
            response.success = success;
            return this;
        }

        public Builder message(String message) {
            response.message = message;
            return this;
        }

        public ExtracaoResponse build() {
            return response;
        }
    }
}
