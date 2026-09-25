-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: localhost    Database: military_asset_management
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `audit_logs`
--

DROP TABLE IF EXISTS `audit_logs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `audit_logs` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `action` varchar(255) NOT NULL,
  `entity_id` bigint DEFAULT NULL,
  `entity_type` varchar(255) NOT NULL,
  `metadata` varchar(2000) DEFAULT NULL,
  `timestamp` datetime(6) NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_audit_user` (`user_id`),
  KEY `idx_audit_action` (`action`),
  CONSTRAINT `FKjs4iimve3y0xssbtve5ysyef0` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `audit_logs`
--

LOCK TABLES `audit_logs` WRITE;
/*!40000 ALTER TABLE `audit_logs` DISABLE KEYS */;
INSERT INTO `audit_logs` VALUES (1,'LOGIN',1,'USER','Seed data initialized','2026-09-25 04:57:26.141867',1),(2,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:37:15.230184',1),(3,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:37:30.069055',1),(4,'LOGIN',2,'USER','commander@military.local','2026-09-25 06:45:22.048152',2),(5,'LOGIN',3,'USER','logistics@military.local','2026-09-25 06:45:22.444589',3),(6,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:45:22.667738',1),(7,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:45:36.730160',1),(8,'UPDATE_PURCHASE',1,'PURCHASE','PO-1001','2026-09-25 06:45:37.124450',1),(9,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:45:58.870397',1),(10,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:46:10.035310',1),(11,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:46:18.907608',1),(12,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:46:38.845311',1),(13,'UPDATE_ASSET',1,'ASSET','AS-1001','2026-09-25 06:46:38.987933',1),(14,'UPDATE_EQUIPMENT_TYPE',1,'EQUIPMENT_TYPE','Rifle','2026-09-25 06:46:39.022385',1),(15,'LOGIN',2,'USER','commander@military.local','2026-09-25 06:48:03.804016',2),(16,'LOGIN',3,'USER','logistics@military.local','2026-09-25 06:48:04.174586',3),(17,'LOGIN',1,'USER','admin@military.local','2026-09-25 06:48:04.393443',1),(18,'LOGIN',3,'USER','logistics@military.local','2026-09-25 06:48:42.116876',3),(19,'LOGIN',1,'USER','admin@military.local','2026-09-25 07:00:34.083652',1),(20,'LOGIN',1,'USER','admin@military.local','2026-09-25 07:04:35.880543',1),(21,'CREATE_ASSET',4,'ASSET','QA-20260925','2026-09-25 07:04:58.026619',1),(22,'UPDATE_ASSET',4,'ASSET','QA-20260925','2026-09-25 07:05:03.547463',1),(23,'DEACTIVATE_ASSET',4,'ASSET','QA-20260925','2026-09-25 07:05:04.331347',1),(24,'CREATE_PURCHASE',2,'PURCHASE','QA-PO-20260925','2026-09-25 07:05:16.001590',1),(25,'UPDATE_PURCHASE',2,'PURCHASE','QA-PO-20260925','2026-09-25 07:06:22.010570',1),(26,'CREATE_TRANSFER',2,'TRANSFER','QA-TR-20260925','2026-09-25 07:06:30.060229',1),(27,'CANCEL_TRANSFER',2,'TRANSFER','QA-TR-20260925','2026-09-25 07:06:34.566148',1),(28,'UPDATE_USER',3,'USER','logistics@military.local','2026-09-25 07:06:50.174446',1),(29,'UPDATE_EQUIPMENT_TYPE',1,'EQUIPMENT_TYPE','Rifle','2026-09-25 07:06:51.549418',1),(30,'LOGIN',2,'USER','commander@military.local','2026-09-25 07:07:11.594608',2),(31,'LOGIN',3,'USER','logistics@military.local','2026-09-25 07:07:32.224406',3),(32,'LOGIN',1,'USER','admin@military.local','2026-09-25 07:28:27.827340',1);
/*!40000 ALTER TABLE `audit_logs` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-09-25 13:03:30
