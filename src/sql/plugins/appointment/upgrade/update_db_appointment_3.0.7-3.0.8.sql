CREATE INDEX idx_appointment_slot ON appointment_appointment_slot (id_appointment, id_slot);

-- Update the value of the users' phone numbers, if they previously entered a
-- value in a generic attribute of type "entryTypePhone"
UPDATE appointment_user user
LEFT JOIN appointment_appointment appointment ON appointment.id_user = user.id_user
LEFT JOIN appointment_appointment_response app_resp ON app_resp.id_appointment = appointment.id_appointment
LEFT JOIN genatt_response gen_resp ON gen_resp.id_response = app_resp.id_response
LEFT JOIN genatt_entry entry ON entry.id_entry = gen_resp.id_entry
LEFT JOIN genatt_entry_type entry_type ON entry_type.id_type = entry.id_type
SET user.phone_number = gen_resp.response_value
WHERE entry_type.class_name = "appointment.entryTypePhone";