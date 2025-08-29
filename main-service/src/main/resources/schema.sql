 CREATE TABLE IF NOT EXISTS users (
          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
          name VARCHAR(127),
          email VARCHAR(255)
        );

 CREATE TABLE IF NOT EXISTS categories (
          id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
          name VARCHAR(50)
        );

 CREATE TABLE IF NOT EXISTS locations (
           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
           lat FLOAT,
           lon FLOAT
         );

 CREATE TABLE IF NOT EXISTS events (
           id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
           title VARCHAR(64),
           annotation VARCHAR(2000),
           category_id INTEGER REFERENCES categories ON DELETE CASCADE,
           confirmed_requests INT,
           created_on TIMESTAMP WITHOUT TIME ZONE,
           description VARCHAR(7000),
           location_id INTEGER REFERENCES locations ON DELETE CASCADE,
           event_date TIMESTAMP WITHOUT TIME ZONE,
           initiator_id INTEGER REFERENCES users ON DELETE CASCADE,
           paid BOOLEAN,
           participant_limit INT,
           published_on TIMESTAMP WITHOUT TIME ZONE,
           request_moderation BOOLEAN,
           state VARCHAR(120),
           views INT
         );
 CREATE TABLE IF NOT EXISTS compilations (
            id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            title VARCHAR(100),
            pinned BOOLEAN
          );

  CREATE TABLE IF NOT EXISTS compilation_events (
             id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
             compilation_id INTEGER REFERENCES compilations ON DELETE CASCADE,
             event_id INTEGER REFERENCES events ON DELETE CASCADE
           );

  CREATE TABLE IF NOT EXISTS requests (
               id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
               created TIMESTAMP WITHOUT TIME ZONE,
               event_id INTEGER REFERENCES events ON DELETE CASCADE,
               requester_id INTEGER REFERENCES users ON DELETE CASCADE,
               status VARCHAR(50)
             );