
package forex.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class DatabaseConfig {
    private final Properties properties = new Properties();

    public DatabaseConfig() {
        Path configPath = Path.of("config", "database.properties");

        try (InputStream in = Files.newInputStream(configPath)) {
            properties.load(in);
        } catch (IOException e) {
            System.out.println("Could not read config/database.properties: " + e.getMessage());
        }
    }

    public String url() {
        return value("DB_URL", "jdbc:mysql://YOUR_TIDB_HOST:4000/test?sslMode=VERIFY_IDENTITY&connectionTimeZone=UTC");
    }

    public String user() {
        return value("DB_USER", "YOUR_CLUSTER_PREFIX.root");
    }

    public String password() {
        return value("DB_PASSWORD", "YOUR_TIDB_PASSWORD");
    }

    private String value(String key, String fallback) {
        String env = System.getenv(key);

        if (env != null && !env.isBlank()) {
            return env;
        }

        return properties.getProperty(key, fallback);
    }
}
