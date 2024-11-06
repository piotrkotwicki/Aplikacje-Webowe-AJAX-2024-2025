-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Maj 05, 2024 at 08:10 PM
-- Wersja serwera: 10.4.32-MariaDB
-- Wersja PHP: 8.0.30

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `projektibfdb`
--

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `authorities`
--

CREATE TABLE `authorities` (
  `id` int(11) NOT NULL,
  `username` varchar(50) NOT NULL,
  `authority` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `authorities`
--

INSERT INTO `authorities` (`id`, `username`, `authority`) VALUES
(8, 'string', 'USER'),
(10, 'test', 'ADMIN');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `director`
--

CREATE TABLE `director` (
  `iddirector` int(11) NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `birthdate` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `director`
--

INSERT INTO `director` (`iddirector`, `name`, `surname`, `birthdate`) VALUES
(3, 'David', 'Fincher', '1962-08-28'),
(4, 'Peter ', 'Jackson', '1961-10-31'),
(17, 'Quantin', 'Tarantino', '1962-08-28');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `movie`
--

CREATE TABLE `movie` (
  `idmovie` int(11) NOT NULL,
  `title` varchar(45) NOT NULL,
  `genre` varchar(45) NOT NULL,
  `premieredate` date NOT NULL,
  `iduser` int(11) NOT NULL,
  `iddirector` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `movie`
--

INSERT INTO `movie` (`idmovie`, `title`, `genre`, `premieredate`, `iduser`, `iddirector`) VALUES
(6, 'Siedem', 'kryminał', '1995-01-05', 1, 3),
(7, 'Podziemny krąg', 'psychologiczny', '1999-07-09', 1, 3),
(8, 'Władca Pierścieni: Drużyna Pierścienia', 'fantasy', '2001-12-04', 1, 4),
(9, 'Władca Pierścieni: Dwie wieże ', 'fantasy', '2002-12-05', 1, 4),
(10, 'Władca Pierścieni: Powrót Króla', 'fantasy', '2003-12-07', 1, 4),
(19, 'Władca Pierścieni: Drużyna Pierścienia 2', 'fantasy', '2001-12-04', 1, 4),
(22, 'string', 'string', '2024-05-05', 17, 3),
(23, 'ewgf', 'gwe', '2024-05-24', 13, 3);

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `user`
--

CREATE TABLE `user` (
  `iduser` int(11) NOT NULL,
  `login` varchar(45) NOT NULL,
  `pass` varchar(45) NOT NULL,
  `role` varchar(45) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`iduser`, `login`, `pass`, `role`) VALUES
(1, 'admin', 'admin', 'admin'),
(2, 'user', 'user', 'user');

-- --------------------------------------------------------

--
-- Struktura tabeli dla tabeli `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(100) NOT NULL,
  `enabled` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `username`, `password`, `enabled`) VALUES
(11, 'string', '{bcrypt}$2a$10$lfA30ui5maV8y93khL7d4uiZmenqciFB8/aZn7BDywS7YtDGMWQSK', 1),
(13, 'test', '{bcrypt}$2a$10$N7Ce.LlB2PouiBglDMetvu2Mn.cwddimgypENi/C7YM.saKAUBlqG', 1);

--
-- Indeksy dla zrzutów tabel
--

--
-- Indeksy dla tabeli `authorities`
--
ALTER TABLE `authorities`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username_authority` (`username`,`authority`);

--
-- Indeksy dla tabeli `director`
--
ALTER TABLE `director`
  ADD PRIMARY KEY (`iddirector`);

--
-- Indeksy dla tabeli `movie`
--
ALTER TABLE `movie`
  ADD PRIMARY KEY (`idmovie`);

--
-- Indeksy dla tabeli `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`iduser`);

--
-- Indeksy dla tabeli `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `authorities`
--
ALTER TABLE `authorities`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `director`
--
ALTER TABLE `director`
  MODIFY `iddirector` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `movie`
--
ALTER TABLE `movie`
  MODIFY `idmovie` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `iduser` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `authorities`
--
ALTER TABLE `authorities`
  ADD CONSTRAINT `fk_authorities_users` FOREIGN KEY (`username`) REFERENCES `users` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
