ALTER TABLE appointment_appointment_response ADD CONSTRAINT fk_app_response_id_resp FOREIGN KEY (id_response)
      REFERENCES genatt_response (id_response) ON DELETE RESTRICT ON UPDATE RESTRICT ;
