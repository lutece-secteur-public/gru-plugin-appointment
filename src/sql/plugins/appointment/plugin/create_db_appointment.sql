DROP TABLE IF EXISTS appointment_reservation_rule ;
DROP TABLE IF EXISTS appointment_appointment_response ;
DROP TABLE IF EXISTS appointment_form_message ;
DROP TABLE IF EXISTS appointment_form_portlet ;
DROP TABLE IF EXISTS appointment_time_slot ;
DROP TABLE IF EXISTS appointment_working_day ;
DROP TABLE IF EXISTS appointment_week_definition ;
DROP TABLE IF EXISTS appointment_closing_day ;
DROP TABLE IF EXISTS appointment_form_rule ;
DROP TABLE IF EXISTS appointment_display ;
DROP TABLE IF EXISTS appointment_localization ;
DROP TABLE IF EXISTS appointment_calendar_template ;
DROP TABLE IF EXISTS appointment_appointment ;
DROP TABLE IF EXISTS appointment_user ;
DROP TABLE IF EXISTS appointment_slot ;
DROP TABLE IF EXISTS appointment_form ;
DROP TABLE IF EXISTS appointment_category ;
DROP TABLE IF EXISTS appointment_comment;


-- -----------------------------------------------------
-- Table appointment_category
-- -----------------------------------------------------

CREATE TABLE appointment_category (
  id_category INT AUTO_INCREMENT,
  label VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_category),
  UNIQUE KEY unique_label (label))
ENGINE = InnoDB;

-- -----------------------------------------------------
-- Table appointment_user
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_user (
  id_user INT AUTO_INCREMENT,
  guid VARCHAR(255) NULL,
  first_name VARCHAR(255) BINARY NOT NULL,
  last_name VARCHAR(255) BINARY NOT NULL,
  email VARCHAR(255) NULL,
  phone_number VARCHAR(255) NULL,
  PRIMARY KEY (id_user),
  UNIQUE KEY unique_index_email (first_name, last_name, email))
ENGINE = InnoDB;

CREATE INDEX email_idx ON appointment_user (email ASC);

-- -----------------------------------------------------
-- Table appointment_form
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_form (
  id_form INT AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  reference VARCHAR(255) NULL,
  id_category INT NULL,
  starting_validity_date DATE NULL,
  ending_validity_date DATE NULL,
  is_active BOOLEAN NOT NULL DEFAULT FALSE,
  id_workflow INT NULL,
  workgroup varchar(255) NULL,
  is_multislot_appointment BOOLEAN NOT NULL DEFAULT FALSE,
  PRIMARY KEY (id_form),
   CONSTRAINT fk_appointment_form_appointment_category
    FOREIGN KEY (id_category)
    REFERENCES appointment_category (id_category)
    ON DELETE SET NULL
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX starting_validity_date_idx ON appointment_form (starting_validity_date ASC);

CREATE INDEX ending_validity_date_idx ON appointment_form (ending_validity_date ASC);

CREATE INDEX fk_appointment_form_appointment_category_idx ON appointment_form (id_category ASC);

-- -----------------------------------------------------
-- Table appointment_slot
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_slot (
  id_slot INT AUTO_INCREMENT,
  starting_date_time TIMESTAMP NULL,
  ending_date_time TIMESTAMP NULL,
  is_open BOOLEAN NOT NULL DEFAULT TRUE,
  is_specific BOOLEAN NOT NULL DEFAULT FALSE,
  max_capacity INT NOT NULL DEFAULT 0,
  nb_remaining_places INT NOT NULL DEFAULT 0,
  nb_potential_remaining_places INT NOT NULL DEFAULT 0,
  nb_places_taken INT NOT NULL DEFAULT 0,
  id_form INT NOT NULL,
  PRIMARY KEY (id_slot, id_form),
  UNIQUE KEY unique_index_starting_date_time (id_form,starting_date_time),
  UNIQUE KEY unique_index_ending_date_time (id_form,ending_date_time),
  CONSTRAINT fk_appointment_slot_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_slot_appointment_form_idx ON appointment_slot (id_form ASC);

CREATE INDEX starting_date_time_idx ON appointment_slot (starting_date_time ASC);

CREATE INDEX ending_date_time_idx ON appointment_slot (ending_date_time ASC);

-- -----------------------------------------------------
-- Table appointment_appointment
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_appointment (
  id_appointment INT AUTO_INCREMENT,
  reference VARCHAR(45) NULL,
  nb_places INT NOT NULL DEFAULT 0,
  is_cancelled BOOLEAN NOT NULL DEFAULT FALSE,
  id_action_cancelled INT,
  notification INT NOT NULL DEFAULT 0,
  id_admin_user INT NULL DEFAULT 0,
  date_appointment_create TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  admin_access_code_create VARCHAR(100) ,
  id_user INT NOT NULL,
  PRIMARY KEY (id_appointment, id_user ),  
  CONSTRAINT fk_appointment_appointment_appointment_user
    FOREIGN KEY (id_user)
    REFERENCES appointment_user (id_user)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_appointment_appointment_user_idx ON appointment_appointment (id_user ASC);

CREATE INDEX reference_idx ON appointment_appointment (reference ASC);


-- -----------------------------------------------------
-- Table appointment_appointment_slot
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_appointment_slot (

	id_appointment INT NOT NULL, 
	id_slot INT NOT NULL,
	nb_places INT NOT NULL,
	PRIMARY KEY (id_appointment, id_slot ),
	CONSTRAINT fk_appointment_appointment_slot_appointment
    FOREIGN KEY (id_appointment)
    REFERENCES appointment_appointment (id_appointment),
    CONSTRAINT fk_appointment_appointment_slot_slot
    FOREIGN KEY (id_slot)
    REFERENCES appointment_slot (id_slot)

);
-- -----------------------------------------------------
-- Table appointment_appointment_response
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_appointment_response (
  id_appointment_response INT AUTO_INCREMENT,
  id_response INT NOT NULL,
  id_appointment INT NOT NULL,
  PRIMARY KEY (id_appointment_response, id_response, id_appointment),
  UNIQUE KEY unique_index (id_appointment,id_response),
  CONSTRAINT fk_appointment_appointment_response_appointment_appointment
    FOREIGN KEY (id_appointment)
    REFERENCES appointment_appointment (id_appointment)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX fk_appointment_appointment_response_appointment_appointment_idx ON appointment_appointment_response (id_appointment ASC);

-- -----------------------------------------------------
-- Table appointment_calendar_template
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_calendar_template (
  id_calendar_template INT AUTO_INCREMENT,
  title VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  template_path VARCHAR(255) NOT NULL,
  PRIMARY KEY (id_calendar_template))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

-- -----------------------------------------------------
-- Table appointment_form_message
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_form_message (
  id_form_message INT AUTO_INCREMENT,
  calendar_title VARCHAR(255) NOT NULL,
  field_firstname_title VARCHAR(255) NOT NULL,
  field_firstname_help VARCHAR(255) NOT NULL,
  field_lastname_title VARCHAR(255) NOT NULL,
  field_lastname_help VARCHAR(255) NOT NULL,
  field_email_title VARCHAR(255) NOT NULL,
  field_email_help VARCHAR(255) NOT NULL,
  field_confirmationEmail_title VARCHAR(255) NOT NULL,
  field_confirmationEmail_help VARCHAR(255) NOT NULL,
  text_appointment_created TEXT NOT NULL,
  url_redirect_after_creation VARCHAR(255) NOT NULL,
  text_appointment_canceled TEXT NOT NULL,
  label_button_redirection VARCHAR(255) NOT NULL,
  no_available_slot VARCHAR(255) NOT NULL,
  calendar_description TEXT NOT NULL,
  calendar_reserve_label VARCHAR(255) NOT NULL,
  calendar_full_label VARCHAR(255) NOT NULL,
  id_form INT NOT NULL,
  PRIMARY KEY (id_form_message, id_form),
  CONSTRAINT fk_appointment_form_message_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX fk_appointment_form_message_appointment_form_idx ON appointment_form_message (id_form ASC);


-- -----------------------------------------------------
-- Table appointment_form_portlet
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_form_portlet (
  id_portlet INT NOT NULL,
  id_form INT NOT NULL,
  PRIMARY KEY (id_portlet, id_form),
  CONSTRAINT fk_appointment_form_portlet_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8
COLLATE = utf8_unicode_ci;

CREATE INDEX fk_appointment_form_portlet_appointment_form_idx ON appointment_form_portlet (id_form ASC);


-- -----------------------------------------------------
-- Table appointment_week_definition
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_week_definition (
  id_week_definition INT AUTO_INCREMENT,
  date_of_apply DATE NOT NULL,
  id_form INT NOT NULL,
  PRIMARY KEY (id_week_definition, id_form),
  UNIQUE KEY unique_index_date_of_apply (id_form,date_of_apply),
  CONSTRAINT fk_appointment_week_definition_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_week_type_appointment_form_idx ON appointment_week_definition (id_form ASC);

CREATE INDEX date_of_apply_idx ON appointment_week_definition (date_of_apply ASC);


-- -----------------------------------------------------
-- Table appointment_working_day
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_working_day (
  id_working_day INT AUTO_INCREMENT,
  day_of_week INT NOT NULL,
  id_week_definition INT NOT NULL,
  PRIMARY KEY (id_working_day, id_week_definition),
  UNIQUE KEY unique_index (id_week_definition,day_of_week),
  CONSTRAINT fk_appointment_working_day_appointment_week_definition
    FOREIGN KEY (id_week_definition)
    REFERENCES appointment_week_definition (id_week_definition)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_working_day_appointment_week_definition_idx ON appointment_working_day (id_week_definition ASC);


-- -----------------------------------------------------
-- Table appointment_time_slot
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_time_slot (
  id_time_slot INT AUTO_INCREMENT,
  starting_time TIME NOT NULL,
  ending_time TIME NOT NULL,
  is_open BOOLEAN NOT NULL DEFAULT TRUE,
  max_capacity INT NOT NULL DEFAULT 0,
  id_working_day INT NOT NULL,
  PRIMARY KEY (id_time_slot, id_working_day),
  UNIQUE KEY unique_index_starting_time (id_working_day,starting_time),
  UNIQUE KEY unique_index_ending_time (id_working_day,ending_time),
  CONSTRAINT fk_appointment_time_slot_appointment_working_day
    FOREIGN KEY (id_working_day)
    REFERENCES appointment_working_day (id_working_day)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_time_slot_appointment_working_day_idx ON appointment_time_slot (id_working_day ASC);

CREATE INDEX starting_time_idx ON appointment_time_slot (starting_time ASC);

CREATE INDEX ending_time_idx ON appointment_time_slot (ending_time ASC);


-- -----------------------------------------------------
-- Table appointment_localization
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_localization (
  id_localization INT AUTO_INCREMENT,
  longitude FLOAT NULL,
  latitude FLOAT NULL,
  address VARCHAR(255) NULL,
  id_form INT NOT NULL,
  PRIMARY KEY (id_localization, id_form),
  CONSTRAINT fk_appointment_localization_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_localization_appointment_form_idx ON appointment_localization (id_form ASC);


-- -----------------------------------------------------
-- Table appointment_display
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_display (
  id_display INT AUTO_INCREMENT,
  display_title_fo BOOLEAN NOT NULL DEFAULT FALSE,
  icon_form_content MEDIUMBLOB NULL,
  icon_form_mime_type VARCHAR(255) NULL,
  nb_weeks_to_display INT NOT NULL DEFAULT 0,
  is_displayed_on_portlet BOOLEAN NOT NULL DEFAULT TRUE,
  id_calendar_template INT(11) NOT NULL,
  id_form INT NOT NULL,
  PRIMARY KEY (id_display, id_calendar_template, id_form),
  UNIQUE KEY unique_index (id_form),
  CONSTRAINT fk_appointment_display_appointment_calendar_template
    FOREIGN KEY (id_calendar_template)
    REFERENCES appointment_calendar_template (id_calendar_template)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT fk_appointment_display_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_display_appointment_calendar_template_idx ON appointment_display (id_calendar_template ASC);

CREATE INDEX fk_appointment_display_appointment_form_idx ON appointment_display (id_form ASC);


-- -----------------------------------------------------
-- Table appointment_form_rule
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_form_rule (
  id_form_rule INT AUTO_INCREMENT,
  is_captcha_enabled BOOLEAN NOT NULL DEFAULT FALSE,
  is_mandatory_email_enabled BOOLEAN NOT NULL DEFAULT FALSE,
  is_active_authentication BOOLEAN NOT NULL DEFAULT FALSE,
  nb_days_before_new_appointment INT NOT NULL DEFAULT 0,
  min_time_before_appointment INT NOT NULL DEFAULT 0,
  nb_max_appointments_per_user INT NOT NULL DEFAULT 0,
  nb_days_for_max_appointments_per_user INT NOT NULL DEFAULT 0,
  bo_overbooking BOOLEAN NOT NULL DEFAULT FALSE,

  id_form INT NOT NULL,
  PRIMARY KEY (id_form_rule, id_form),
  UNIQUE KEY unique_index (id_form),
  CONSTRAINT fk_appointment_form_rule_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_form_rule_appointment_form_idx ON appointment_form_rule (id_form ASC);


-- -----------------------------------------------------
-- Table appointment_closing_day
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_closing_day (
  id_closing_day INT AUTO_INCREMENT,
  date_of_closing_day DATE NOT NULL,
  id_form INT NOT NULL,
  PRIMARY KEY (id_closing_day, id_form),
  UNIQUE KEY unique_index (id_form,date_of_closing_day),
  CONSTRAINT fk_appointment_closing_day_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_closing_day_appointment_form_idx ON appointment_closing_day (id_form ASC);

CREATE INDEX date_of_closing_day ON appointment_closing_day (date_of_closing_day ASC);


-- -----------------------------------------------------
-- Table appointment_reservation_rule
-- -----------------------------------------------------

CREATE TABLE IF NOT EXISTS appointment_reservation_rule (
  id_reservation_rule INT AUTO_INCREMENT,
  date_of_apply DATE NOT NULL,
  max_capacity_per_slot INT NOT NULL DEFAULT 0,
  max_people_per_appointment INT NOT NULL DEFAULT 0,
  id_form INT NOT NULL,
  PRIMARY KEY (id_reservation_rule, id_form),
  UNIQUE KEY unique_index_date_of_apply (id_form,date_of_apply),
  CONSTRAINT fk_appointment_reservation_rule_appointment_form
    FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX fk_appointment_reservation_rule_appointment_form_idx ON appointment_reservation_rule (id_form ASC);

CREATE INDEX date_of_apply_idx ON appointment_reservation_rule (date_of_apply ASC);



--
-- Structure for table appointment_comment
--

CREATE TABLE appointment_comment (
id_comment int AUTO_INCREMENT,
id_form int default '0' NOT NULL,
starting_validity_date date NOT NULL,
ending_validity_date date NOT NULL,
comment long varchar NOT NULL,
comment_creation_date date NOT NULL, comment_user_creator VARCHAR(255) NOT NULL,
PRIMARY KEY (id_comment),
CONSTRAINT fk_appointment_comment FOREIGN KEY (id_form)
    REFERENCES appointment_form (id_form)
);
