CREATE DATABASE `t34`;

-- t34.certificate definition
CREATE TABLE `certificate` (
    `certId` int NOT NULL AUTO_INCREMENT,
    `userId` int DEFAULT NULL,
    `objectKey` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`certId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- t34.resume definition
CREATE TABLE `resume` (
    `resumeId` int NOT NULL AUTO_INCREMENT,
    `userId` int DEFAULT NULL,
    `objectKey` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`resumeId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- t34.`user` definition
CREATE TABLE `user` (
    `userId` int NOT NULL AUTO_INCREMENT,
    `email` varchar(255) DEFAULT NULL,
    `password` varchar(255) DEFAULT NULL,
    `name` varchar(255) DEFAULT NULL,
    `contactNo` varchar(255) DEFAULT NULL,
    `address` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`userId`),
    UNIQUE KEY `user_UN` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;