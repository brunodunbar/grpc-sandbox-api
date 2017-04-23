import com.brunodunbar.grpc.sandbox.Services;

import java.util.Objects;
import java.util.stream.Collectors;

public class GRpcWrapper {

    public static Services.Value wrap(Object value) {

        Services.Value.Builder builder = Services.Value.newBuilder();

        if (value == null) {
            return builder.setType(Services.ValueType.NULL).build();
        } else if (value instanceof String) {
            return builder.setType(Services.ValueType.STRING).setStringValue((String) value).build();
        } else if (value instanceof Integer) {
            return builder.setType(Services.ValueType.INTEGER).setIntValue((Integer) value).build();
        } else if (value instanceof Long) {
            return builder.setType(Services.ValueType.LONG).setLongValue((Long) value).build();
        } else if (value instanceof Float) {
            return builder.setType(Services.ValueType.FLOAT).setFloatValue((Float) value).build();
        } else if (value instanceof Double) {
            return builder.setType(Services.ValueType.DOUBLE).setDoubleValue((Double) value).build();
        }

        throw new UnsupportedOperationException("Não é possível converter o valor do tipo " + value.getClass().getName());
    }

    public static Object unwrap(Services.Value value) {
        Objects.requireNonNull(value, "O valor não pode ser nulo");
        switch (value.getType()) {
            case NULL:
                return null;
            case STRING:
                return value.getStringValue();
            case INTEGER:
                return value.getIntValue();
            case LONG:
                return value.getLongValue();
            case FLOAT:
                return value.getFloatValue();
            case DOUBLE:
                return value.getDoubleValue();
            default:
                throw new UnsupportedOperationException("Tipo de valor desconhecido " + value.getType());
        }
    }

    public static Services.ExtracaoRequest wrap(ExtracaoRequest request) {
        Services.ExtracaoRequest.Builder builder = Services.ExtracaoRequest.newBuilder()
                .setId(request.getId());

        int index = 0;
        for (Object value : request.getValues()) {
            builder.setValues(index++, wrap(value));
        }

        return builder.build();
    }

    public static ExtracaoRequest unwrap(Services.ExtracaoRequest request) {
        return new ExtracaoRequest.Builder()
                .id(request.getId())
                .addValues(request.getValuesList().stream()
                        .map(GRpcWrapper::unwrap)
                        .collect(Collectors.toList()))
                .build();
    }


    public static Services.ExtracaoResponse wrap(ExtracaoResponse response) {
        return Services.ExtracaoResponse.newBuilder()
                .setId(response.getId())
                .setSuccess(response.isSuccess())
                .setMessage(response.getMessage())
                .build();
    }

    public static ExtracaoResponse unwrap(Services.ExtracaoResponse response) {
        return new ExtracaoResponse.Builder()
                .id(response.getId())
                .success(response.getSuccess())
                .message(response.getMessage())
                .build();
    }


}
