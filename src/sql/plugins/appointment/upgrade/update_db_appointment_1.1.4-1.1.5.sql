-- liquibase formatted sql
-- changeset appointment:update_db_appointment_1.1.4-1.1.5.sql
-- preconditions onFail:MARK_RAN onError:WARN
ALTER TABLE appointment_form ADD COLUMN active_mylutece_authentification smallint default NULL;
