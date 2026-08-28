-- liquibase formatted sql
-- changeset appointment:update_db_appointment_3.0.11-3.0.13.sql
-- preconditions onFail:MARK_RAN onError:WARN

-- Enforce one appointment_user row per appointment.
--
-- A user row shared by several appointments made the deletion of an appointment fail on the
-- foreign key appointment_appointment.id_user -> appointment_user.id_user, since the plugin
-- deletes the user row along with the appointment.
--
-- For every appointment that is not the most recent one of its user, a copy of the user row is
-- created and the appointment is repointed to that copy. The most recent appointment of each
-- user keeps the original row.
--
-- tmp_id_appointment carries the target appointment inside the inserted row itself : a set based
-- insert generates all the keys at once and gives no way to tell which generated key belongs to
-- which appointment. Its index is required, the correlated subquery of the update scans on it.

ALTER TABLE appointment_user ADD COLUMN tmp_id_appointment INT;
CREATE INDEX tmp_idx_appointment_user ON appointment_user ( tmp_id_appointment );

INSERT INTO appointment_user ( guid, first_name, last_name, email, phone_number, tmp_id_appointment )
SELECT u.guid, u.first_name, u.last_name, u.email, u.phone_number, a.id_appointment
FROM appointment_appointment a
INNER JOIN appointment_user u ON u.id_user = a.id_user
WHERE a.id_appointment NOT IN ( SELECT MAX( id_appointment ) FROM appointment_appointment GROUP BY id_user );

UPDATE appointment_appointment a
SET id_user = ( SELECT u.id_user FROM appointment_user u WHERE u.tmp_id_appointment = a.id_appointment )
WHERE EXISTS ( SELECT 1 FROM appointment_user u WHERE u.tmp_id_appointment = a.id_appointment );

ALTER TABLE appointment_user DROP COLUMN tmp_id_appointment;
