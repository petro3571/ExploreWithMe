 CREATE TABLE IF NOT EXISTS hits (
          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
          app VARCHAR(64),
          uri VARCHAR(255),
          ip VARCHAR(40),
          timestamp TIMESTAMP WITHOUT TIME ZONE
        );