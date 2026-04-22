-- liquibase formatted sql
-- changeset appointment:update_db_appointment_3.0.8-4.0.0.sql
-- preconditions onFail:MARK_RAN onError:WARN

-- Add flag to display the mini calendar navigation widget in front office
ALTER TABLE appointment_display ADD COLUMN IF NOT EXISTS is_display_mini_calendar BOOLEAN DEFAULT FALSE NOT NULL;

-- Add flag to display the "Today" navigation button in front office
ALTER TABLE appointment_display ADD COLUMN IF NOT EXISTS is_display_today_button BOOLEAN DEFAULT TRUE NOT NULL;
