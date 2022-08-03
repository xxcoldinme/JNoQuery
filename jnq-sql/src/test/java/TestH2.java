import com.itzstonlex.jnq.DataConnection;
import com.itzstonlex.jnq.exception.JnqException;
import com.itzstonlex.jnq.field.FieldOperator;
import com.itzstonlex.jnq.field.FieldType;
import com.itzstonlex.jnq.impl.content.SchemaContent;
import com.itzstonlex.jnq.impl.content.TableContent;
import com.itzstonlex.jnq.impl.field.IndexDataField;
import com.itzstonlex.jnq.impl.field.ValueDataField;
import com.itzstonlex.jnq.sql.SQLConnection;
import com.itzstonlex.jnq.sql.SQLHelper;

public class TestH2 {

    public static void main(String[] args)
    throws JnqException {

        DataConnection connection = new SQLConnection(SQLHelper.toH2JDBC(), "root", "password");
        SchemaContent schemaContent = connection.getSchemaContent(SQLHelper.H2_DEFAULT_SCHEMA_NAME);

        if (schemaContent == null) {
            System.out.println("wtf ?");
            return;
        }
        
        connection.createRequest(schemaContent)
                .toFactory()
                .<IndexDataField>fromQuery("CREATE TABLE IF NOT EXISTS `reg_users` ({0}, {1}, {2}, {3})")

                .sessionAppender()
                    .append(IndexDataField.createPrimaryNotNullAutoIncrement("id"))
                    .append(IndexDataField.createNotNull(FieldType.VARCHAR, "name"))
                    .append(IndexDataField.createNotNull(FieldType.LONG, "register_date"))
                    .append(IndexDataField.createNotNull(FieldType.LONG, "last_update_date"))
                    .endpoint()

                .compile()
                .updateTransaction();

        // update schemes & tables content caches.
        connection.updateContents();

        // getting created table now.
        TableContent usersTable = schemaContent.getTableContent("reg_users");

        connection.createRequest(schemaContent)
                .toFactory()
                .<ValueDataField>fromQuery("INSERT INTO `{content}`.`reg_users` ({name0}, {name1}, {name2}) VALUES (?, ?, ?)")

                .sessionAppender()
                    .append(ValueDataField.create("name", "itzstonlex"))
                    .append(ValueDataField.create("register_date", System.currentTimeMillis()))
                    .append(ValueDataField.create("last_update_date", System.currentTimeMillis()))
                    .endpoint()

                .compile()
                .updateTransactionAsync()

                .whenComplete((updateResponse, error) ->  {

                    System.out.println("INSERT RESPONSE:");
                    System.out.println(" Inserted User ID: " + updateResponse.getGeneratedKey());
                    System.out.println(" - " + updateResponse.getAffectedRows() + " affected rows");
                    System.out.println();
                });

        connection.createRequest(usersTable)
                .toFactory()
                .newFinder()

                .withLimit(50)

                .sessionSelector()
                    .withUpperCase("name").as("upper_name")
                    .withAll()

                .sessionFilter()
                    .and(ValueDataField.create("name", "itzstonlex"))
                    .endpoint()

                .sessionGroup()
                    .by(ValueDataField.create("id"))
                    .endpoint()

                .sessionSort()
                    .byDesc(ValueDataField.create("id"))
                    .byAsc(ValueDataField.create("name"))
                    .endpoint()

                .compile()
                .fetchFirstLineAsync()

                .whenComplete((response, throwable) -> {

                    System.out.println("USER FETCH RESPONSE:");

                    System.out.println(" ID: " + response.getNullableInt("id"));
                    System.out.println(" Name (in upper-case): " + response.getNullableString("upper_name"));
                    System.out.println(" Name (without case): " + response.getNullableString("name"));
                    System.out.println(" Register Time Millis: " + response.getNullableTimestamp("register_date").toGMTString());
                    System.out.println(" Last Update Time Millis: " + response.getNullableTimestamp("register_date").toGMTString());
                    System.out.println();

                    System.out.println(" Response indexes: " + response.getIndexes());
                    System.out.println(" Response labels names: " + response.getLabels());
                    System.out.println();
                });

        connection.createRequest(usersTable)
                .toFactory()
                .newUpdate()

                .sessionUpdater()
                    .and(ValueDataField.create("last_update_date", System.currentTimeMillis()))
                    .endpoint()

                .sessionFilter()
                    .and(FieldOperator.MORE_WITH_EQUAL, ValueDataField.create("id", 1))
                    .and(ValueDataField.create("name", "itzstonlex"))
                    .endpoint()

                .compile()
                .updateTransactionAsync()

                .whenComplete((updateResponse, throwable) -> {

                    System.out.println("UPDATE RESPONSE:");
                    System.out.println(" - " + updateResponse.getAffectedRows() + " affected rows");
                    System.out.println();
                });
    }

}
