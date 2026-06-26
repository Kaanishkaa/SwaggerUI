CREATE TABLE employees (
    id          UUID                NOT NULL DEFAULT random_uuid(),
    first_name  VARCHAR(100)        NOT NULL,
    last_name   VARCHAR(100)        NOT NULL,
    email       VARCHAR(255)        NOT NULL,
    phone       VARCHAR(30),
    department  VARCHAR(50)         NOT NULL,
    job_title   VARCHAR(150)        NOT NULL,
    manager_id  UUID,
    start_date  DATE                NOT NULL,
    status      VARCHAR(20)         NOT NULL DEFAULT 'ACTIVE',
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT pk_employees         PRIMARY KEY (id),
    CONSTRAINT uq_employees_email   UNIQUE (email),
    CONSTRAINT chk_employees_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_employees_department ON employees (department);
CREATE INDEX idx_employees_status     ON employees (status);
CREATE INDEX idx_employees_last_name  ON employees (last_name);
