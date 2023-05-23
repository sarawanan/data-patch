package com.example.datapatch;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@SpringBootApplication
public class DataPatchApplication implements ApplicationRunner {
    @Value("${rollback.enabled}")
    private boolean rollback;

    @Value("${encryption.enabled}")
    private boolean encryptionEnabled;

    @Value("${patch.file}")
    private String patchFile;

    @Value("${rollback.file}")
    private String rollbackFile;

    final DataSource dataSource;
    final Decryption decryption;

    public DataPatchApplication(DataSource dataSource, Decryption decryption) {
        this.dataSource = dataSource;
        this.decryption = decryption;
    }

    public static void main(String[] args) {
        SpringApplication.run(DataPatchApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        var resource = new ClassPathResource(rollback ? rollbackFile : patchFile);
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator();
        resourceDatabasePopulator.addScripts(encryptionEnabled ? decryption.decrypt(resource) : resource);
        DatabasePopulatorUtils.execute(resourceDatabasePopulator, dataSource);
    }
}
