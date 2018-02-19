package bzh.msansm1.trevad.server.db;

import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.RequestScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.jboss.logging.Logger;

@RequestScoped
@TransactionManagement(TransactionManagementType.BEAN)
public class DatabaseMigrationService {
    private static final Logger LOG = Logger.getLogger(DatabaseMigrationService.class);

    public void performDatabaseMigration() {
        DataSource dataSource = getDataSource();

        LOG.info("init DB migration");

        // run database migration scripts
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        flyway.migrate();
    }

    private DataSource getDataSource() {
        try {
            Context context = new InitialContext();
            return (DataSource) context.lookup("java:jboss/datasources/TrevadDS");
        } catch (NamingException e) {
            throw new RuntimeException("Unable to load datasource using name: java:jboss/datasources/TrevadDS", e);
        }
    }
}
