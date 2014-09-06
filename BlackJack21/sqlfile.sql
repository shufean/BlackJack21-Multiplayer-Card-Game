-- phpMyAdmin SQL Dump
-- version 3.5.7
-- http://www.phpmyadmin.net
--
-- Host: localhost
-- Generation Time: May 10, 2013 at 07:59 PM
-- Server version: 5.5.29
-- PHP Version: 5.4.10

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";

--
-- Database: `blackjack`
--

-- --------------------------------------------------------

--
-- Table structure for table `cards`
--

CREATE TABLE `cards` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) DEFAULT NULL,
  `card_type` varchar(255) DEFAULT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=85 ;

--
-- Dumping data for table `cards`
--

INSERT INTO `cards` (`id`, `username`, `card_type`, `value`) VALUES
(79, 'akm', '3', '3'),
(80, 'akm', '3', '13'),
(81, 'Sh', '0', '3'),
(82, 'Sh', '3', '9'),
(83, 'dealer', '1', '2'),
(84, 'dealer', '0', '13');

-- --------------------------------------------------------

--
-- Table structure for table `userlist`
--

CREATE TABLE `userlist` (
  `game_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_1` varchar(255) DEFAULT NULL,
  `user_2` varchar(255) DEFAULT NULL,
  `user_3` varchar(255) DEFAULT NULL,
  `user_4` varchar(255) DEFAULT NULL,
  `user_5` varchar(255) DEFAULT NULL,
  `user_6` varchar(255) DEFAULT NULL,
  `user_7` varchar(255) DEFAULT NULL,
  `user_8` varchar(255) DEFAULT NULL,
  `user_9` varchar(255) DEFAULT NULL,
  `user_10` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`game_id`),
  UNIQUE KEY `game_id` (`game_id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=15 ;

--
-- Dumping data for table `userlist`
--

INSERT INTO `userlist` (`game_id`, `user_1`, `user_2`, `user_3`, `user_4`, `user_5`, `user_6`, `user_7`, `user_8`, `user_9`, `user_10`) VALUES
(14, 'akm', 'Sh', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
