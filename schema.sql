-- 사용자 테이블
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,

    is_deleted BOOLEAN DEFAULT FALSE,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 매물 테이블
CREATE TABLE properties (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,     -- 매물명 (예: 힐스테이트 101동 1503호)
    address VARCHAR(255) NOT NULL,  -- 주소
    property_type ENUM('APARTMENT', 'VILLA', 'OFFICETEL', 'ONE_ROOM', 'OTHER') NOT NULL,
    floor VARCHAR(20),
    built_year INT,
    area DECIMAL(5,2),              -- 면적 (제곱미터)
    available_date DATE,

    is_deleted BOOLEAN DEFAULT FALSE,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 체크리스트 테이블
CREATE TABLE checklists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    property_id BIGINT NOT NULL,
    description TEXT,               -- 점검 설명

    is_deleted BOOLEAN DEFAULT FALSE,

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (property_id) REFERENCES properties(id)
);

-- 체크리스트 항목 테이블
CREATE TABLE checklist_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    checklist_id BIGINT NOT NULL,
    category VARCHAR(50) NOT NULL,      -- 전기 / 수도 / 창문 등
    content VARCHAR(255) NOT NULL,      -- 점검 내용 (예: 콘센트 이상 여부)
    severity ENUM('NONE', 'NORMAL', 'WARNING', 'DANGER') NOT NULL,
    memo VARCHAR(255),                  -- 사용자 메모
    photo_url VARCHAR(255),             -- 하자 사진 (S3 등 URL)

    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (checklist_id) REFERENCES checklists(id)
);
