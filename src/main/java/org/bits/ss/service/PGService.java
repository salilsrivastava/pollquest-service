package org.bits.ss.service;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bits.ss.Main;
import org.flywaydb.core.Flyway;


import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Logger;

public class PGService {
    private static final Logger logger = Logger.getLogger(PGService.class.getName());
    private final HikariConfig hikariConfig = new HikariConfig();
    private HikariDataSource hikariDataSource;
    private final int dbPort = 54321;
    private final String dbhostName = "172.25.48.1";
    private final String database = "postgres";
    private final String jdbcUrl = "jdbc:postgresql://" + dbhostName + ":" + dbPort + "/" + database;

    public void initFlyway(){
        logger.info("Flyway initializing...");
        var flyway = Flyway.configure()
                .locations("flywayScripts")
                .validateOnMigrate(false)
                .baselineOnMigrate(true)
                .sqlMigrationPrefix("0")
                .dataSource(getDataSource()).load();
        flyway.migrate();
    }


    public void initDataSource(){
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername("postgres");
        hikariConfig.setPassword("");
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariDataSource = new HikariDataSource(hikariConfig);
        logger.info("Connected to DB");
    }

    public DataSource getDataSource(){
        return hikariDataSource;
    }


    public boolean isDBConnected(){
        logger.info("Checking if database is connected...");
        final DataSource ds = getDataSource();
        try(Connection connection = ds.getConnection();
            PreparedStatement ps = connection.prepareStatement("""
                    SELECT 1
                    """)){
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                return true;
            }
        }catch (Exception e){
            logger.severe("Exception Occurred " + e);
        }
        return false;
    }
}
