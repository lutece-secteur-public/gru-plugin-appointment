-- liquibase formatted sql
-- changeset appointment:update_db_appointment_2.0.29-2.0.30.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE appointment_appointment ADD date_appointment_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ;

ALTER TABLE appointment_appointment ADD admin_access_code_create VARCHAR(100) ;
