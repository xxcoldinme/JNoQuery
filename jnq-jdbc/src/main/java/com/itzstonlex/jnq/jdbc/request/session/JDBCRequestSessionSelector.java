package com.itzstonlex.jnq.jdbc.request.session;

import com.itzstonlex.jnq.content.request.RequestQuery;
import com.itzstonlex.jnq.content.request.session.RequestSessionCast;
import com.itzstonlex.jnq.content.request.session.RequestSessionSelector;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class JDBCRequestSessionSelector<Query extends RequestQuery>
        extends JDBCRequestSession<Query>
        implements RequestSessionSelector<Query> {

    String generatedSql = "";

    public JDBCRequestSessionSelector(@NonNull Query parent) {
        super(parent);
    }

    private void _append(String value, String generatedSql) {
        this.generatedSql += (!this.generatedSql.isEmpty() ? ", " : "") + String.format(generatedSql, value);
    }

    public String getGeneratedSql() {
        return generatedSql.isEmpty() ? "*" : generatedSql;
    }

    @Override
    public @NonNull Query withAll() {
        this._append("", "*");
        return endpoint();
    }

    @Override
    public @NonNull RequestSessionSelector<Query> with(@NonNull String field) {
        return withCasted(field).uncheck();
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withCasted(@NonNull String field) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(field, "`%s`" + generatedSql));
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withQuery(@NonNull RequestQuery query) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(query.toString(), "(%s)" + generatedSql));
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withCount(@NonNull String field) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(field, "COUNT(`%s`)" + generatedSql));
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withLowerCase(@NonNull String field) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(field, "LOWER(`%s`)" + generatedSql));
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withUpperCase(@NonNull String field) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(field, "UPPER(`%s`)" + generatedSql));
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withMax(@NonNull String field) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(field, "MAX(`%s`)" + generatedSql));
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withMin(@NonNull String field) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(field, "MIN(`%s`)" + generatedSql));
    }

    @Override
    public @NonNull RequestSessionCast<RequestSessionSelector<Query>, Query> withAvg(@NonNull String field) {
        return new JDBCRequestSessionCast<>(this, endpoint(), (generatedSql) -> _append(field, "AVG(`%s`)" + generatedSql));
    }
}
