package com.itzstonlex.jnq.jdbc.request;

import com.itzstonlex.jnq.DataConnection;
import com.itzstonlex.jnq.jdbc.JDBCConnection;
import com.itzstonlex.jnq.jdbc.content.JDBCDataContent;
import com.itzstonlex.jnq.request.Request;
import com.itzstonlex.jnq.request.RequestFactory;
import com.itzstonlex.jnq.request.option.RequestConcurrency;
import com.itzstonlex.jnq.request.option.RequestFetchDirection;
import com.itzstonlex.jnq.request.option.RequestHoldability;
import com.itzstonlex.jnq.request.option.RequestType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class JDBCRequest implements Request {

    @Getter
    JDBCConnection connection;

    @Getter
    JDBCDataContent dataContent;

    @NonFinal
    RequestConcurrency concurrency;

    @NonFinal
    RequestFetchDirection fetchDirection;

    @NonFinal
    RequestHoldability holdability;

    @NonFinal
    RequestType type;

    @Override
    public @NonNull DataConnection connection() {
        return connection;
    }

    @Override
    public RequestConcurrency concurrency() {
        return concurrency;
    }

    @Override
    public RequestFetchDirection fetchDirection() {
        return fetchDirection;
    }

    @Override
    public RequestHoldability holdability() {
        return holdability;
    }

    @Override
    public RequestType type() {
        return type;
    }

    @Override
    public @NonNull Request set(@NonNull RequestConcurrency concurrency) {
        this.concurrency = concurrency;
        return this;
    }

    @Override
    public @NonNull Request set(@NonNull RequestFetchDirection fetchDirection) {
        this.fetchDirection = fetchDirection;
        return this;
    }

    @Override
    public @NonNull Request set(@NonNull RequestHoldability holdability) {
        this.holdability = holdability;
        return this;
    }

    @Override
    public @NonNull Request set(@NonNull RequestType type) {
        this.type = type;
        return this;
    }

    @Override
    public @NonNull RequestFactory toFactory() {
        return new JDBCRequestFactory(this);
    }
}