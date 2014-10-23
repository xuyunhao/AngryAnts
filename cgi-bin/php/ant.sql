-- MySQL dump 10.13  Distrib 5.5.29, for debian-linux-gnu (x86_64)
--
-- Host: mysql.cs.arizona.edu    Database: xuyunhao_angryant
-- ------------------------------------------------------
-- Server version	5.5.29-0ubuntu0.12.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ant`
--

DROP TABLE IF EXISTS `ant`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ant` (
  `vid` int(11) NOT NULL COMMENT 'video id',
  `vid2` varchar(1) NOT NULL DEFAULT 'a',
  `aid` int(11) NOT NULL COMMENT 'ant id',
  `picked` int(11) NOT NULL DEFAULT '0' COMMENT 'time picked, increase when file received, decrease when file rejected.',
  PRIMARY KEY (`vid`,`aid`,`vid2`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ant`
--

LOCK TABLES `ant` WRITE;
/*!40000 ALTER TABLE `ant` DISABLE KEYS */;
INSERT INTO `ant` VALUES (0,'a',5,4),(0,'b',5,4),(0,'a',6,4),(0,'b',6,4),(0,'a',27,5),(0,'b',27,4),(0,'a',36,4),(0,'b',36,4),(0,'a',44,4),(0,'b',44,4),(1,'a',5,4),(1,'b',5,4),(1,'a',6,4),(1,'b',6,4),(1,'a',27,4),(1,'b',27,4),(1,'a',36,4),(1,'b',36,4),(1,'a',44,4),(1,'b',44,4),(2,'a',5,4),(2,'b',5,4),(2,'a',6,4),(2,'b',6,4),(2,'a',27,4),(2,'b',27,4),(2,'a',36,4),(2,'b',36,4),(2,'a',44,3),(2,'b',44,4);
/*!40000 ALTER TABLE `ant` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `path`
--

DROP TABLE IF EXISTS `path`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `path` (
  `pid` int(11) NOT NULL,
  `userid` int(11) NOT NULL DEFAULT '0',
  `filename` varchar(200) NOT NULL,
  `vid` int(11) NOT NULL,
  `vid2` varchar(1) NOT NULL DEFAULT 'a',
  `aid` int(11) NOT NULL,
  PRIMARY KEY (`pid`,`vid`,`aid`,`vid2`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `path`
--

LOCK TABLES `path` WRITE;
/*!40000 ALTER TABLE `path` DISABLE KEYS */;
INSERT INTO `path` VALUES (0,8,'v0a_a5_u8_p0.txt',0,'a',5),(0,9,'v0b_a5_u9_p0.txt',0,'b',5),(0,8,'v0a_a6_u8_p0.txt',0,'a',6),(0,9,'v0b_a6_u9_p0.txt',0,'b',6),(0,9,'v0a_a27_u9_p0.txt',0,'a',27),(0,8,'v0b_a27_u8_p0.txt',0,'b',27),(0,9,'v0a_a36_u9_p0.txt',0,'a',36),(0,8,'v0b_a36_u8_p0.txt',0,'b',36),(0,9,'v0a_a44_u9_p0.txt',0,'a',44),(0,9,'v0b_a44_u9_p0.txt',0,'b',44),(0,9,'v1a_a5_u9_p0.txt',1,'a',5),(0,8,'v1b_a5_u8_p0.txt',1,'b',5),(0,9,'v1a_a6_u9_p0.txt',1,'a',6),(0,8,'v1b_a6_u8_p0.txt',1,'b',6),(0,8,'v1a_a27_u8_p0.txt',1,'a',27),(0,9,'v1b_a27_u9_p0.txt',1,'b',27),(0,8,'v1a_a36_u8_p0.txt',1,'a',36),(0,8,'v1b_a36_u8_p0.txt',1,'b',36),(0,9,'v1a_a44_u9_p0.txt',1,'a',44),(0,8,'v1b_a44_u8_p0.txt',1,'b',44),(0,8,'v2a_a5_u8_p0.txt',2,'a',5),(0,8,'v2b_a5_u8_p0.txt',2,'b',5),(0,8,'v2a_a6_u8_p0.txt',2,'a',6),(0,8,'v2b_a6_u8_p0.txt',2,'b',6),(0,8,'v2a_a27_u8_p0.txt',2,'a',27),(0,8,'v2b_a27_u8_p0.txt',2,'b',27),(0,8,'v2a_a36_u8_p0.txt',2,'a',36),(0,8,'v2b_a36_u8_p0.txt',2,'b',36),(0,8,'v2a_a44_u8_p0.txt',2,'a',44),(0,8,'v2b_a44_u8_p0.txt',2,'b',44),(1,8,'v0a_a5_u8_p1.txt',0,'a',5),(1,8,'v0b_a5_u8_p1.txt',0,'b',5),(1,8,'v0a_a6_u8_p1.txt',0,'a',6),(1,5,'v0b_a6_u5_p1.txt',0,'b',6),(1,8,'v0a_a27_u8_p1.txt',0,'a',27),(1,5,'v0b_a27_u5_p1.txt',0,'b',27),(1,8,'v0a_a36_u8_p1.txt',0,'a',36),(1,5,'v0b_a36_u5_p1.txt',0,'b',36),(1,8,'v0a_a44_u8_p1.txt',0,'a',44),(1,8,'v0b_a44_u8_p1.txt',0,'b',44),(1,5,'v1a_a5_u5_p1.txt',1,'a',5),(1,5,'v1b_a5_u5_p1.txt',1,'b',5),(1,8,'v1a_a6_u8_p1.txt',1,'a',6),(1,5,'v1b_a6_u5_p1.txt',1,'b',6),(1,5,'v1a_a27_u5_p1.txt',1,'a',27),(1,10,'v1b_a27_u10_p1.txt',1,'b',27),(1,5,'v1a_a36_u5_p1.txt',1,'a',36),(1,5,'v1b_a36_u5_p1.txt',1,'b',36),(1,5,'v1a_a44_u5_p1.txt',1,'a',44),(1,5,'v1b_a44_u5_p1.txt',1,'b',44),(1,5,'v2a_a5_u5_p1.txt',2,'a',5),(1,5,'v2b_a5_u5_p1.txt',2,'b',5),(1,5,'v2a_a6_u5_p1.txt',2,'a',6),(1,10,'v2b_a6_u10_p1.txt',2,'b',6),(1,5,'v2a_a27_u5_p1.txt',2,'a',27),(1,0,'v2b_a27_u0_p1.txt',2,'b',27),(1,10,'v2a_a36_u10_p1.txt',2,'a',36),(1,10,'v2b_a36_u10_p1.txt',2,'b',36),(1,10,'v2a_a44_u10_p1.txt',2,'a',44),(1,0,'v2b_a44_u0_p1.txt',2,'b',44),(2,0,'v0a_a5_u0_p2.txt',0,'a',5),(2,0,'v0b_a5_u0_p2.txt',0,'b',5),(2,0,'v0a_a6_u0_p2.txt',0,'a',6),(2,0,'v0b_a6_u0_p2.txt',0,'b',6),(2,0,'v0a_a27_u0_p2.txt',0,'a',27),(2,0,'v0b_a27_u0_p2.txt',0,'b',27),(2,0,'v0a_a36_u0_p2.txt',0,'a',36),(2,0,'v0b_a36_u0_p2.txt',0,'b',36),(2,0,'v0a_a44_u0_p2.txt',0,'a',44),(2,0,'v0b_a44_u0_p2.txt',0,'b',44),(2,0,'v1a_a5_u0_p2.txt',1,'a',5),(2,0,'v1b_a5_u0_p2.txt',1,'b',5),(2,0,'v1a_a6_u0_p2.txt',1,'a',6),(2,0,'v1b_a6_u0_p2.txt',1,'b',6),(2,0,'v1a_a27_u0_p2.txt',1,'a',27),(2,0,'v1b_a27_u0_p2.txt',1,'b',27),(2,0,'v1a_a36_u0_p2.txt',1,'a',36),(2,0,'v1b_a36_u0_p2.txt',1,'b',36),(2,0,'v1a_a44_u0_p2.txt',1,'a',44),(2,5,'v1b_a44_u5_p2.txt',1,'b',44),(2,5,'v2a_a5_u5_p2.txt',2,'a',5),(2,0,'v2b_a5_u0_p2.txt',2,'b',5),(2,0,'v2a_a6_u0_p2.txt',2,'a',6),(2,0,'v2b_a6_u0_p2.txt',2,'b',6),(2,0,'v2a_a27_u0_p2.txt',2,'a',27),(2,5,'v2b_a27_u5_p2.txt',2,'b',27),(2,0,'v2a_a36_u0_p2.txt',2,'a',36),(2,5,'v2b_a36_u5_p2.txt',2,'b',36),(2,5,'v2a_a44_u5_p2.txt',2,'a',44),(2,5,'v2b_a44_u5_p2.txt',2,'b',44),(3,0,'v0a_a5_u0_p3.txt',0,'a',5),(3,5,'v0b_a5_u5_p3.txt',0,'b',5),(3,5,'v0a_a6_u5_p3.txt',0,'a',6),(3,0,'v0b_a6_u0_p3.txt',0,'b',6),(3,5,'v0a_a27_u5_p3.txt',0,'a',27),(3,5,'v0b_a27_u5_p3.txt',0,'b',27),(3,5,'v0a_a36_u5_p3.txt',0,'a',36),(3,5,'v0b_a36_u5_p3.txt',0,'b',36),(3,5,'v0a_a44_u5_p3.txt',0,'a',44),(3,5,'v0b_a44_u5_p3.txt',0,'b',44),(3,0,'v1a_a5_u0_p3.txt',1,'a',5),(3,10,'v1b_a5_u10_p3.txt',1,'b',5),(3,5,'v1a_a6_u5_p3.txt',1,'a',6),(3,10,'v1b_a6_u10_p3.txt',1,'b',6),(3,0,'v1a_a27_u0_p3.txt',1,'a',27),(3,0,'v1b_a27_u0_p3.txt',1,'b',27),(3,0,'v1a_a36_u0_p3.txt',1,'a',36),(3,0,'v1b_a36_u0_p3.txt',1,'b',36),(3,2,'v1a_a44_u2_p3.txt',1,'a',44),(3,2,'v1b_a44_u2_p3.txt',1,'b',44),(3,2,'v2a_a5_u2_p3.txt',2,'a',5),(3,2,'v2b_a5_u2_p3.txt',2,'b',5),(3,0,'v2a_a6_u0_p3.txt',2,'a',6),(3,0,'v2b_a6_u0_p3.txt',2,'b',6),(3,2,'v2a_a27_u2_p3.txt',2,'a',27),(3,2,'v2b_a27_u2_p3.txt',2,'b',27),(3,0,'v2a_a36_u0_p3.txt',2,'a',36),(3,0,'v2b_a36_u0_p3.txt',2,'b',36),(3,2,'v2b_a44_u2_p3.txt',2,'b',44),(4,8,'v0a_a6_u8_p4.txt',0,'a',6),(4,2,'v0a_a27_u2_p4.txt',0,'a',27),(5,5,'v0a_a6_u5_p5.txt',0,'a',6);
/*!40000 ALTER TABLE `path` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `userid` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `username` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `highest_score` int(11) NOT NULL DEFAULT '0',
  `highest_multiplier` int(11) NOT NULL DEFAULT '0',
  `credential` varchar(300) NOT NULL,
  PRIMARY KEY (`userid`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (0,'guest','',0,0,'Guest02ak9a7pk16zowb4j90zf5dxhoca74rp'),(2,'player1','player1',1000,5,'player149wi1k6t291zw4et6xcdnkv2w5rpd4un'),(3,'player2','player2',0,0,'player287t9vcwbrrfdybzc6bdpsjgqmzujqg9m'),(4,'player3','player3',0,0,'player38783959gb2qdprrws6c9unk3cdd4kzlg'),(5,'ryan','ryan',0,0,'ryanzc2wyvhklbbfo0njghmodlqzxyfc8lp5'),(6,'asdf','asdf',0,0,'asdfy4bsddrce59k6x58m9qmtwnk1lc8t0p5'),(7,'yi','yi',0,0,'yibypthl88wi6rg4l3k7a2phpk5g19my10'),(8,'chenj1','abc123',0,0,'chenj16c4zw388wancxega3lzi64olrdvx8nt9'),(9,'123123','123123',0,0,'123123vfnzauk98yq7zkhsoqjufbvf7i2vj41z'),(10,'pshen','fireants',0,0,'pshen8ajtz7xkelq2hsia0itwlodw7l8qwpsc'),(11,'tals18','passw0rd',0,0,'tals18cr74em6qg9uov91lskw4a9ift91x2d8l'),(12,'ehudfo','passw0rd',0,0,'ehudform9la4y5e17iyqabfpfz6bo908jc3j38'),(13,'eee','eeeeee',0,0,'eeeeo8rdc8u9kjn1cbzj02px8n65vprq57i'),(14,'nettie','shadow',0,0,'nettie2qj3pc7phqn4z8jl7hphafhetoxero3c');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-02-13 11:45:47
