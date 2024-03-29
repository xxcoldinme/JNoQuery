package com.itzstonlex.jnq.jdbc;

import com.itzstonlex.jnq.content.JnqContentMeta;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.sql.DatabaseMetaData;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public class JDBCContentMeta implements JnqContentMeta {

    DatabaseMetaData impl;

    @SneakyThrows
    @Override
    public String getURL() {
        return impl.getURL();
    }

    @SneakyThrows
    @Override
    public String getUsername() {
        return impl.getUserName();
    }

    @SneakyThrows
    @Override
    public String getDatabaseProductName() {
        return impl.getDatabaseProductName();
    }

    @SneakyThrows
    @Override
    public String getDatabaseProductVersion() {
        return impl.getDatabaseProductVersion();
    }

    @SneakyThrows
    @Override
    public boolean isReadOnly() {
        return impl.isReadOnly();
    }
}
