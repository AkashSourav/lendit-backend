CREATE TABLE `item_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(45) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_name` varchar(100) NOT NULL,
  `item_category_id` int(11) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `last_lend_date` datetime DEFAULT NULL,
  `land_status` tinyint(4) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_item_1_idx` (`owner_id`),
  KEY `fk_item_2_idx` (`item_category_id`),
  CONSTRAINT `fk_item_1` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_item_2` FOREIGN KEY (`item_category_id`) REFERENCES `item_category` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `item_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_id` bigint(20) NOT NULL,
  `sold_status` tinyint(4) NOT NULL,
  `sold_price` int(11) NOT NULL,
  `address` varchar(1000) NOT NULL,
  `lend_start_date` datetime NOT NULL,
  `lend_end_date` datetime NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Item_details_1_idx` (`item_id`),
  CONSTRAINT `fk_Item_details_1` FOREIGN KEY (`item_id`) REFERENCES `item` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `item_price_details` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `item_details_id` bigint(20) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `price` int(11) NOT NULL,
  `owner_approval` tinyint(4) NOT NULL,
  `viewed_status` tinyint(4) NOT NULL,
  `created_date` datetime NOT NULL,
  `updated_date` datetime NOT NULL,
  `approval_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_Item_price_details_1_idx` (`user_id`),
  CONSTRAINT `fk_Item_price_details_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_Item_price_details_2` FOREIGN KEY (`id`) REFERENCES `item_details` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
