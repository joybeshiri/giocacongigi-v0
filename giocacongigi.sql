-- 1. Crea la tabella playing_field
CREATE TABLE `playing_field` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(30) NOT NULL,
  `description` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 2. Crea la tabella user
CREATE TABLE `user` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role_id` tinyint unsigned NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  KEY `fk_role_id_idx` (`role_id`),
  CONSTRAINT `fk_role_id` 
    FOREIGN KEY (`role_id`) 
    REFERENCES `role` (`id`) 
    ON UPDATE CASCADE 
    ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE `role` (
  `id` TINYINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(30) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 3. Crea la tabella event
CREATE TABLE `event` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `play_date` date NOT NULL,
  `play_time` time NOT NULL,
  `description` varchar(45) NOT NULL,
  `playing_field_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_playing_field_id_idx` (`playing_field_id`),
  CONSTRAINT `fk_playing_field_id` 
  FOREIGN KEY (`playing_field_id`) 
  REFERENCES `playing_field` (`id`) 
  ON UPDATE CASCADE 
  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- 4. Crea la tabella event_user
CREATE TABLE `event_user` (
  `event_id` int(10) unsigned NOT NULL,
  `user_id` int(10) unsigned NOT NULL,
  PRIMARY KEY (`event_id`,`user_id`),
  KEY `fk_user_id_idx` (`user_id`),
  CONSTRAINT `fk_event_id` 
  FOREIGN KEY (`event_id`) 
  REFERENCES `event` (`id`) 
  ON UPDATE CASCADE
  ON DELETE RESTRICT,
  CONSTRAINT `fk_user_id` 
  FOREIGN KEY (`user_id`) 
  REFERENCES `user` (`id`) 
  ON UPDATE CASCADE
  ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Inserimento dei dati (deve essere eseguito dopo aver creato tutte le tabelle)
INSERT INTO `playing_field` VALUES 
(1,'Campo A','Questo è il campo A'),
(2,'Campo B','Questo è il campo B'),
(3,'Campo C','Questo è il campo C');

INSERT INTO `user` VALUES 
(1,'admin','admin@gmail.com','$2a$10$j8lvRqP5tEVOYsBhh3voFuEuEZ5br03bZ1wvlatKCd26xlHnoMZHC','admin'),
(2,'Mario','mario.rossi@gmail.com','$2a$10$pvJp0oxDu8FXGopPgQtaA.A0w2aF2jcsb4mW1xJEsOIMZNc3hmz0O','user'),
(3,'Luigi','luigi.verdi@gmail.com','$2a$10$kyRW3UjQulydpx5vaABtdeA8026.nt//7IGtVmpUkpllXht.FFuXa','user'),
(4,'Ernesto','ernesto.bianchi@gmail.com','$2a$10$xrPlzdxcwT8XcK2DkENTHO8iEsX8FByLNPGWWQ0TJbaa0vT5vNHL2','user');

INSERT INTO `role` (`id`, `name`) VALUES 
(1, 'admin'),
(2, 'user');

UPDATE `user` SET `role_id` = 1 WHERE `id` = 1;
UPDATE `user` SET `role_id` = 2 WHERE `id` IN (2, 3, 4);

INSERT INTO `event` VALUES 
(1,'2025-02-01','20:00:00','Partita con id = 1',1),
(2,'2025-02-02','21:00:00','Partita con id = 2',2),
(3,'2025-02-03','20:30:00','Partita con id = 3',1),
(4,'2025-04-24','20:00:00','Partita con id = 4',1),
(5,'2025-04-23','21:00:00','Partita con id = 5',2),
(6,'2025-04-28','20:00:00','Partita con id = 6',3),
(7,'2025-04-29','21:00:00','Partita con id = 7',2),
(8,'2025-04-30','20:30:00','Partita con id = 8',3),
(9,'2025-05-01','22:00:00','Partita con id = 9',1),
(10,'2025-05-02','20:30:00','Partita con id = 10',1);

INSERT INTO `event_user` VALUES 
(1,2),
(1,3),
(1,4),
(6,2),
(6,3),
(7,2);