package com.itzstonlex.jnq.jdbc.request.type;

import com.itzstonlex.jnq.content.request.type.RequestCreateSchema;
import com.itzstonlex.jnq.jdbc.request.JDBCRequest;
import com.itzstonlex.jnq.jdbc.request.JDBCRequestQuery;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JDBCRequestCreateSchema extends JDBCRequestQuery implements RequestCreateSchema {

    private static final String QUERY = "CREATE SCHEMA {checker} `{content}`";

    @NonFinal
    boolean existsChecking;

    public JDBCRequestCreateSchema(@NonNull JDBCRequest request) {
        super(request);
    }

    @Override
    public @NonNull RequestCreateSchema checkAvailability() {
        this.existsChecking = true;
        return this;
    }

    @Override
    protected String toSQL() {
        String query = QUERY.replace("{content}", request.getContent().getName());
        query = query.replace("{checker}", existsChecking ? "IF NOT EXISTS" : "");

        return query;
    }

    @Override
    protected Object[] toFieldValues() {
        return new Object[0];
    }
}
