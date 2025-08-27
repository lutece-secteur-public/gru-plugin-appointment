-- liquibase formatted sql
-- changeset appointment:update_db_appointment_3.0.0-3.0.7.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE appointment_appointment DROP FOREIGN KEY fk_appointment_appointment_appointment_slot;
ALTER  TABLE appointment_form  ADD nb_consecutive_slots INT DEFAULT 1 NOT NULL ;
ALTER TABLE appointment_form ADD COLUMN is_anonymizable BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE appointment_form ADD COLUMN anonymization_pattern VARCHAR(3) DEFAULT NULL;
