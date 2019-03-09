
DROP TABLE IF EXISTS user;

CREATE TABLE user (
  id bigint(20) NOT NULL AUTO_INCREMENT,
  first_name varchar(100) NOT NULL,
  last_name varchar(100) NOT NULL,
  email varchar(100) NOT NULL,
  password varchar(200) NOT NULL,
  profile_pic varchar(200) DEFAULT NULL,
  mobile varchar(200) DEFAULT NULL,
  address1 varchar(200) DEFAULT NULL,
  address2 varchar(200) DEFAULT NULL,
  city varchar(50) DEFAULT NULL,
  pin_code varchar(20) DEFAULT NULL,
  user_role tinyint NOT NULL,
  authorised tinyint(1) NOT NULL DEFAULT '0',
  uuid VARCHAR(200) NULL DEFAULT NULL,
  created_date datetime NOT NULL,
  updated_date datetime NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE login_detail (
  id int(11) NOT NULL AUTO_INCREMENT,
  last_login varchar(50) NOT NULL,
  failed_attempt tinyint NOT NULL DEFAULT 0,
  blocked_time bigint(20) NOT NULL DEFAULT 0,
  user_id bigint(20) NOT NULL,
  user_ip VARCHAR(45) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_user (user_id),
  KEY login_detail_user_fk (user_id),
  CONSTRAINT login_detail_user_fk FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE cities (
  id int(11) NOT NULL AUTO_INCREMENT,
  city_name varchar(50) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY unique_user (city_name)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;