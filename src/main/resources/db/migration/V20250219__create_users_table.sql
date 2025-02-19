CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    user_type VARCHAR(50) NOT NULL,
    oauth2_provider VARCHAR(50),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT unique_username_user_type_oauth2_provider UNIQUE (username, user_type, oauth2_provider),
    CONSTRAINT unique_email_user_type_oauth2_provider UNIQUE (email, user_type, oauth2_provider)
);
