delimiter $$

CREATE DATABASE `liga` /*!40100 DEFAULT CHARACTER SET utf8 */$$


CREATE TABLE `match` (
  `idmatch` int(11) NOT NULL AUTO_INCREMENT,
  `year` int(11) NOT NULL,
  `week` int(11) NOT NULL,
  `league` int(11) DEFAULT NULL,
  `home` varchar(45) NOT NULL,
  `away` varchar(45) NOT NULL,
  `home_goals` int(10) unsigned zerofill NOT NULL,
  `away_goals` int(10) unsigned zerofill NOT NULL,
  PRIMARY KEY (`idmatch`),
  KEY `ix_year_week` (`year`,`week`)
) ENGINE=InnoDB AUTO_INCREMENT=10518 DEFAULT CHARSET=utf8$$

