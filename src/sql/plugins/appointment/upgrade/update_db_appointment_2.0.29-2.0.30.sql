ALTER TABLE appointment_appointment ADD date_appointment_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ;

ALTER TABLE appointment_appointment ADD admin_access_code_create VARCHAR(100) ;
