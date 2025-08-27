-- liquibase formatted sql
-- changeset appointment:update_db_appointment_1.1.5-1.1.6.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE appointment_form ADD COLUMN workgroup varchar(255) default 'all';
