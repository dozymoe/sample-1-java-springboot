CREATE DATABASE sample_springboot CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE sample_springboot_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'demo'@'%' IDENTIFIED BY 'demo';

GRANT ALL PRIVILEGES ON sample_springboot.* TO 'demo'@'%';
GRANT ALL PRIVILEGES ON sample_springboot_test.* TO 'demo'@'%';
