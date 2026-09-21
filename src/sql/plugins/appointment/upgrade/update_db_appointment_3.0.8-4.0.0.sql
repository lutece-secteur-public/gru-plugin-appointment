-- liquibase formatted sql
-- changeset appointment:update_db_appointment_3.0.8-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN

-- -----------------------------------------------------
-- Soft-hold of slot places during a booking in progress
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS appointment_slot_hold (
  id_slot INT NOT NULL,
  hold_token VARCHAR(50) NOT NULL,
  nb_places INT DEFAULT 0 NOT NULL,
  expired_date TIMESTAMP NOT NULL,
  PRIMARY KEY (id_slot, hold_token),
  CONSTRAINT fk_appointment_slot_hold_slot
    FOREIGN KEY (id_slot)
    REFERENCES appointment_slot (id_slot)
    );

CREATE INDEX appointment_slot_hold_expired_idx ON appointment_slot_hold (expired_date ASC);

-- -----------------------------------------------------
-- Capacity invariant CHECK : nb_remaining + nb_taken = max_capacity (survives overbooking).
-- Reconcile-before-enforce : restore the equality on any legacy drifted row
-- (trust nb_places_taken + max_capacity, recompute nb_remaining_places) BEFORE adding
-- the constraint, otherwise the ALTER fails on drifted rows.
-- -----------------------------------------------------
UPDATE appointment_slot
SET nb_remaining_places = max_capacity - nb_places_taken
WHERE nb_remaining_places + nb_places_taken <> max_capacity;

ALTER TABLE appointment_slot
  ADD CONSTRAINT chk_appointment_slot_capacity
  CHECK (nb_remaining_places + nb_places_taken = max_capacity);
