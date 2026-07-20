-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 18-07-2026 a las 08:02:21
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `encuestas_oxxo`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cuestionario`
--

CREATE TABLE `cuestionario` (
  `id` int(10) UNSIGNED NOT NULL,
  `plaza_id` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `cuestionario`
--

INSERT INTO `cuestionario` (`id`, `plaza_id`, `nombre`, `activo`) VALUES
(1, 1, 'Encuesta de satisfaccion', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `encuesta`
--

CREATE TABLE `encuesta` (
  `id` char(36) NOT NULL,
  `usuario_id` int(10) UNSIGNED DEFAULT NULL,
  `tienda_id` int(10) UNSIGNED NOT NULL,
  `cuestionario_id` int(10) UNSIGNED NOT NULL,
  `comentario` text DEFAULT NULL,
  `fecha_creacion_local` datetime NOT NULL,
  `sincronizado` tinyint(1) NOT NULL DEFAULT 0,
  `fecha_sincronizacion` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `encuesta`
--

INSERT INTO `encuesta` (`id`, `usuario_id`, `tienda_id`, `cuestionario_id`, `comentario`, `fecha_creacion_local`, `sincronizado`, `fecha_sincronizacion`) VALUES
('4fc7f056-1fb8-4c12-8c29-78b1fbb83275', 1, 180, 1, 'El Técnico fue grosero', '2026-07-17 23:28:30', 1, '2026-07-17 23:28:31'),
('b0f03426-d1c1-4d32-8ff5-2c47cdcf6f5e', 1, 125, 1, 'La TI Rosita es lo maximo', '2026-07-15 01:22:27', 1, '2026-07-15 01:22:26'),
('c476f7a6-dc5e-4d46-8837-63054dc7b387', 3, 82, 1, 'la app no sirve', '2026-07-17 23:37:01', 1, '2026-07-17 23:37:02'),
('d08ac568-02cb-4717-95e5-ff4a2aa4726d', 1, 118, 1, 'Son lentos', '2026-07-15 01:25:08', 1, '2026-07-15 01:25:07'),
('f56a8f9c-1c38-4cbd-8b70-e8cc07101d50', 3, 36, 1, 'son lentos', '2026-07-17 23:33:35', 1, '2026-07-17 23:33:36');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `negocio`
--

CREATE TABLE `negocio` (
  `id` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `es_default` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `negocio`
--

INSERT INTO `negocio` (`id`, `nombre`, `es_default`) VALUES
(1, 'OXXO', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `plaza`
--

CREATE TABLE `plaza` (
  `id` int(10) UNSIGNED NOT NULL,
  `region_id` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `cr` varchar(10) DEFAULT NULL,
  `es_default` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `plaza`
--

INSERT INTO `plaza` (`id`, `region_id`, `nombre`, `cr`, `es_default`) VALUES
(1, 1, 'Ciudad Valles', '32YXH', 1),
(2, 1, 'Ciudad Victoria', '32HJR', 0),
(3, 1, 'Matamoros', '32WPF', 0),
(4, 1, 'Tampico', '32RNA', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pregunta`
--

CREATE TABLE `pregunta` (
  `id` int(10) UNSIGNED NOT NULL,
  `cuestionario_id` int(10) UNSIGNED NOT NULL,
  `creado_por_usuario_id` int(10) UNSIGNED DEFAULT NULL,
  `texto` varchar(255) NOT NULL,
  `orden` smallint(5) UNSIGNED NOT NULL DEFAULT 0,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  `es_fija` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `pregunta`
--

INSERT INTO `pregunta` (`id`, `cuestionario_id`, `creado_por_usuario_id`, `texto`, `orden`, `activo`, `es_fija`) VALUES
(1, 1, NULL, 'Como calificarias el servicio del area de TI', 9999, 1, 1),
(2, 1, 2, 'Como fue el trato del ingeniero?', 2, 1, 0),
(3, 1, 2, 'como consideras la atencion del area de sistemas via telefonica', 3, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `region`
--

CREATE TABLE `region` (
  `id` int(10) UNSIGNED NOT NULL,
  `negocio_id` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `cr` varchar(10) DEFAULT NULL,
  `es_default` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `region`
--

INSERT INTO `region` (`id`, `negocio_id`, `nombre`, `cr`, `es_default`) VALUES
(1, 1, 'Tamaulipas', '10UMI', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `respuesta_detalle`
--

CREATE TABLE `respuesta_detalle` (
  `id` char(36) NOT NULL,
  `encuesta_id` char(36) NOT NULL,
  `pregunta_id` int(10) UNSIGNED NOT NULL,
  `calificacion` tinyint(3) UNSIGNED NOT NULL
) ;

--
-- Volcado de datos para la tabla `respuesta_detalle`
--

INSERT INTO `respuesta_detalle` (`id`, `encuesta_id`, `pregunta_id`, `calificacion`) VALUES
('077f4911-671a-4162-bacd-9f6684ed5eb5', 'b0f03426-d1c1-4d32-8ff5-2c47cdcf6f5e', 1, 8),
('4ddcae33-1430-46ad-a1a8-100b65e51eec', 'f56a8f9c-1c38-4cbd-8b70-e8cc07101d50', 3, 7),
('5293849f-319c-4f28-98bd-598cda85ec33', '4fc7f056-1fb8-4c12-8c29-78b1fbb83275', 1, 7),
('5494ce2f-40dc-49a3-b7c4-3acf83a5b947', 'f56a8f9c-1c38-4cbd-8b70-e8cc07101d50', 1, 4),
('5badba22-1594-4315-8372-bdb85f2b305e', 'c476f7a6-dc5e-4d46-8837-63054dc7b387', 1, 6),
('63629649-e958-4349-bd7a-bc49f0268cd1', 'd08ac568-02cb-4717-95e5-ff4a2aa4726d', 2, 3),
('7e916b20-0a24-4bba-8a88-f8ca460f3b4f', 'f56a8f9c-1c38-4cbd-8b70-e8cc07101d50', 2, 10),
('9b12a30d-edd5-40bc-9c12-de16a51b6edc', 'd08ac568-02cb-4717-95e5-ff4a2aa4726d', 1, 9),
('a190305b-743b-45d9-bee0-7f229a387242', '4fc7f056-1fb8-4c12-8c29-78b1fbb83275', 3, 3),
('a3b6930f-be50-47de-8f3e-748d044868a4', 'c476f7a6-dc5e-4d46-8837-63054dc7b387', 2, 5),
('c080837e-0704-4d7d-8848-83ac7df678ff', 'c476f7a6-dc5e-4d46-8837-63054dc7b387', 3, 6),
('c4e6d0a0-54f6-4e8f-9b2b-dc9331f8905b', 'd08ac568-02cb-4717-95e5-ff4a2aa4726d', 3, 5),
('e18f6e3f-c48e-43c3-bddf-c44ab8a38197', '4fc7f056-1fb8-4c12-8c29-78b1fbb83275', 2, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `id` int(10) UNSIGNED NOT NULL,
  `nombre` varchar(30) NOT NULL,
  `gestiona_preguntas` tinyint(1) NOT NULL DEFAULT 0,
  `gestiona_usuarios` tinyint(1) NOT NULL DEFAULT 0,
  `es_encuestable` tinyint(1) NOT NULL DEFAULT 0,
  `ve_resultados_tiendas` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id`, `nombre`, `gestiona_preguntas`, `gestiona_usuarios`, `es_encuestable`, `ve_resultados_tiendas`) VALUES
(1, 'ATI', 1, 0, 0, 1),
(2, 'WEBMASTER', 1, 1, 1, 0),
(3, 'PFS', 0, 0, 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tienda`
--

CREATE TABLE `tienda` (
  `id` int(10) UNSIGNED NOT NULL,
  `plaza_id` int(10) UNSIGNED NOT NULL,
  `codigo` varchar(20) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `latitud` decimal(10,7) DEFAULT NULL,
  `longitud` decimal(10,7) DEFAULT NULL,
  `asesor_ti_usuario_id` int(10) UNSIGNED DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `tienda`
--

INSERT INTO `tienda` (`id`, `plaza_id`, `codigo`, `nombre`, `direccion`, `latitud`, `longitud`, `asesor_ti_usuario_id`) VALUES
(1, 1, '500FM', 'Paso del Humo', NULL, 22.2056498, -97.8439232, 5),
(2, 1, '50D30', 'El Sion Tam', NULL, 22.1933025, -97.8346104, 5),
(3, 1, '50DRN', 'Los Cocos Tam', NULL, 22.2080466, -97.8611097, 5),
(4, 1, '50DT8', 'El Rio Tam', NULL, 22.2074264, -97.8395522, 5),
(5, 1, '50GZU', 'California Tam', NULL, 22.2024827, -97.8424381, 5),
(6, 1, '50HXA', 'Anahuac Tam', NULL, 22.2055165, -97.8534921, 5),
(7, 1, '50IM5', 'Carretera Nacional Maf', NULL, 22.1982263, -97.8382790, 5),
(8, 1, '50JCL', 'La Loma Tam', NULL, 22.1976155, -97.8555570, 5),
(9, 1, '50O56', 'Independencia Tam', NULL, 22.2039800, -97.8371100, 5),
(10, 1, '501IO', 'Matinchild Tam', NULL, 21.6630747, -97.8517972, 5),
(11, 1, '5022Z', 'El Mango Tam', NULL, 22.2237704, -97.8201420, 5),
(12, 1, '506WR', 'Gas Cuauhtemoc Tam', NULL, 22.1929447, -97.8252318, 5),
(13, 1, '5084Z', 'Veteranos Tam', NULL, 22.1786048, -97.8361449, 5),
(14, 1, '50C7P', 'Horconcitos Tam', NULL, 21.8498592, -97.7493161, 5),
(15, 1, '50CXO', 'Ozuluama Centro Tam', NULL, 21.6599176, -97.8501806, 5),
(16, 1, '50EG5', 'Villa Tampico Tam', NULL, 22.1098653, -97.8036020, 5),
(17, 1, '50K2P', 'Abasolo Tam', NULL, 22.1879000, -97.8342000, 5),
(18, 1, '50K5T', 'Mata Redonda Tam', NULL, 22.2288193, -97.8277806, 5),
(19, 1, '50KOJ', 'Edarsi Tam', NULL, 21.7981573, -97.7727056, 5),
(20, 1, '50LDJ', 'Pueblo Viejo Tam', NULL, 22.1845903, -97.8362164, 5),
(21, 1, '50OPF', 'Tampico Alto II Tam', NULL, 22.1114030, -97.8011370, 5),
(22, 1, '50OZL', 'Ozuluama Ii Tam', NULL, 21.6750403, -97.8598394, 5),
(23, 1, '50OZU', 'Ozuluama Tam', NULL, 21.6845983, -97.8549675, 5),
(24, 1, '50SNN', 'Congreg Hidalgo Tam', NULL, 22.2367397, -97.8300033, 5),
(25, 1, '5080M', 'Cruz Tam', NULL, 22.0497090, -98.1785925, 5),
(26, 1, '50AGD', 'Burocrata Tam', NULL, 22.0578619, -98.1869665, 5),
(27, 1, '50DGC', 'Flores Magon Tam', NULL, 22.0535256, -98.1823304, 5),
(28, 1, '50IPO', 'Tempoal Centro Tam', NULL, 21.5209546, -98.3952150, 5),
(29, 1, '50JWA', 'Santos Tam', NULL, 21.5221375, -98.3914051, 5),
(30, 1, '50TI2', 'Llave Tam', NULL, 21.5197000, -98.3914000, 5),
(31, 1, '50TPY', 'Tempoal Tam', NULL, 21.5231334, -98.3798279, 5),
(32, 1, '50WM8', 'La Gloria Tam', NULL, 21.5161449, -98.3824960, 5),
(33, 1, '50X0E', 'Gas Tempoal Tam', NULL, 21.4930343, -98.3569612, 5),
(34, 1, '501W1', 'Gas Jaibo Tam', NULL, 22.0460899, -98.1911686, 5),
(35, 1, '50BDR', 'Olmecas Tam', NULL, 22.0414595, -98.1824095, 5),
(36, 1, '50JBU', 'Panuco Dif Tam', NULL, 22.0500177, -98.1899044, 5),
(37, 1, '50MT3', '21 De Abril Tam', NULL, 22.0379611, -98.1868000, 5),
(38, 1, '50OS4', '05 De Febrero Tam', NULL, 22.0532771, -98.1888534, 5),
(39, 1, '50PUC', 'Panuco II Tam', NULL, 22.0456466, -98.1920340, 5),
(40, 1, '50QFG', 'Panuco Iv Tam', NULL, 22.0495806, -98.1827407, 5),
(41, 1, '5004C', 'Aquiles Serdan Tam', NULL, 21.7139846, -98.5859814, 5),
(42, 1, '50AGP', 'San Vicente Tam', NULL, 21.7181165, -98.5886067, 5),
(43, 1, '50D37', 'Tanquian Escobedo Maf', NULL, 21.6091186, -98.6611923, 5),
(44, 1, '50GUB', 'Tanquian Tam', NULL, 21.5988000, -98.6634423, 5),
(45, 1, '500BP', 'Ebano Carretera Tam', NULL, 22.2176034, -98.3772980, 5),
(46, 1, '50264', 'Corregidora Tam', NULL, 22.0050419, -98.7690209, 5),
(47, 1, '506A4', 'Miguel Hidalgo  Tam', NULL, 22.2121752, -98.3879670, 5),
(48, 1, '506ZZ', 'Yucatan Tam', NULL, 22.2128112, -98.3964140, 5),
(49, 1, '50ARP', 'Larraga Tam', NULL, 22.2231652, -98.3718434, 5),
(50, 1, '50EWK', 'Ebano Tam', NULL, 22.2143100, -98.3746300, 5),
(51, 1, '50F4Z', 'La Estacion Tmp', NULL, 22.2278751, -98.3692853, 5),
(52, 1, '50IMG', 'Tamuin Centro Tam', NULL, 22.0055106, -98.7746775, 5),
(53, 1, '50M3P', 'Pujal Coy Tam', NULL, 22.1520340, -98.5058330, 5),
(54, 1, '50O3Y', 'Huasteca Tmp', NULL, 22.0083565, -98.7819901, 5),
(55, 1, '50QFS', 'Tres Filos Tam', NULL, 21.9703968, -98.8188430, 5),
(56, 1, '50ST3', 'Boulevard Tamuin Tam', NULL, 22.0112034, -98.7885990, 5),
(57, 1, '50TUI', 'Tamuin Tam', NULL, 22.0775039, -98.6525403, 5),
(58, 1, '50VOG', 'Ebano Centro Tam', NULL, 22.2103107, -98.3787688, 5),
(59, 1, '50XDY', 'Iturbide Tam', NULL, 22.0025619, -98.7798619, 5),
(60, 1, '500HQ', 'Santa Maria Tam', NULL, 22.0055000, -99.0308590, 2),
(61, 1, '502AS', 'Soto Y Gama Tam', NULL, 22.0102000, -99.0159580, 2),
(62, 1, '505GO', 'Tambaca Tam', NULL, 21.9614000, -99.3022060, 2),
(63, 1, '507QF', 'Mexico Tam', NULL, 22.0007094, -99.0189598, 2),
(64, 1, '50916', 'La Curva Tam', NULL, 21.9989687, -99.0243617, 2),
(65, 1, '50993', 'Emiliano Zapata Tam', NULL, 22.0110927, -99.0261899, 2),
(66, 1, '509YH', 'Agua Buena Tam', NULL, 21.9572721, -99.3924110, 2),
(67, 1, '509YS', 'Carretera Tamasopo Tam', NULL, 21.9272000, -99.3915000, 2),
(68, 1, '50APO', 'Praderas Del Rio Tam', NULL, 22.0110984, -99.0537467, 2),
(69, 1, '50BMT', 'Santa Lucia Tam', NULL, 22.0089943, -99.0377581, 2),
(70, 1, '50GEC', 'El Consuelo Tam', NULL, 22.0270426, -99.0260080, 2),
(71, 1, '50GSH', 'Valles Tam', NULL, 21.9924207, -99.0302368, 2),
(72, 1, '50JZ9', 'Tamasopo Tam', NULL, 21.9221697, -99.3924950, 2),
(73, 1, '50K9Q', 'De Las Rosas Tam', NULL, 22.0060725, -99.0128719, 2),
(74, 1, '50NFH', 'La Pimienta Tam', NULL, 22.0020355, -98.9980615, 2),
(75, 1, '50OR1', 'Avance  Tam', NULL, 21.9958470, -99.0192000, 2),
(76, 1, '50SFL', 'Doracely Tam', NULL, 22.0055010, -99.0195764, 2),
(77, 1, '50UT9', 'Vista Hermosa Tam', NULL, 22.0195523, -99.0273680, 2),
(78, 1, '50WXT', 'Aire Tam', NULL, 21.9934396, -99.0236648, 2),
(79, 1, '50380', 'El Cafetal Maf', NULL, 21.9232216, -99.3991513, 2),
(80, 1, '50ANI', 'Panuco I Tam', NULL, 22.0574618, -98.1793394, 5),
(81, 1, '50F31', 'Villa Cacalilao Tam', NULL, 22.1522554, -98.1753120, 5),
(82, 1, '50O58', 'Caseta Panuco Tam', NULL, 22.1504734, -98.1467689, 5),
(83, 1, '50TDF', 'Xalapa Tam', NULL, 22.0660101, -98.1837070, 5),
(84, 1, '50TT2', 'Mercado Panuco Tam', NULL, 22.0592898, -98.1808937, 5),
(85, 1, '50UCT', 'Panuco Iii Tam', NULL, 22.0636679, -98.1696990, 5),
(86, 1, '50ZMJ', 'Malecon Tam', NULL, 22.0567120, -98.1775361, 5),
(87, 1, '50EFS', 'Tampico Valles Tam', NULL, 22.2241500, -97.9079500, 5),
(88, 1, '50H3I', 'Tamos Tam', NULL, 22.2195134, -98.0002632, 5),
(89, 1, '50L80', 'Calentadores Tam', NULL, 22.1729301, -98.0887500, 5),
(90, 1, '50OMU', 'Moralillo Ii Tam', NULL, 22.2251655, -97.9018293, 5),
(91, 1, '50OOM', 'Moralillo Tam', NULL, 22.2242319, -97.9032011, 5),
(92, 1, '50SAQ', 'Sabalo Tam', NULL, 22.2227377, -97.9166371, 5),
(93, 1, '5006G', 'Las Puentes Tam', NULL, 21.7733753, -98.4195754, 2),
(94, 1, '502AO', 'Ejercito Nacional Tmp', NULL, 21.7684000, -98.4498000, 2),
(95, 1, '502J3', 'Miguel Aleman Tam', NULL, 21.7618727, -98.4525290, 2),
(96, 1, '508ZZ', 'Ribera Tam', NULL, 21.7728028, -98.4556698, 2),
(97, 1, '50JWC', 'El Higo Tam', NULL, 21.7683970, -98.4529707, 2),
(98, 1, '50NFI', 'Gas Higo Tam', NULL, 21.7770438, -98.4462471, 2),
(99, 1, '50M20', 'Televalles Tam', NULL, 21.9767045, -99.0036979, 2),
(100, 1, '500EP', 'Rotarios Tam', NULL, 21.9808362, -99.0132570, 2),
(101, 1, '500H3', '16 De Septiembre  Tam', NULL, 21.9889434, -99.0136830, 2),
(102, 1, '500S1', 'Eco Grande Tam', NULL, 21.9827766, -99.0150489, 2),
(103, 1, '505WE', 'Tercer Mundo Tam', NULL, 22.0003000, -99.0132000, 2),
(104, 1, '5073X', 'Mendez Tam', NULL, 21.9844104, -99.0014872, 2),
(105, 1, '508K0', 'Comonfort Tam', NULL, 21.9904000, -99.0176000, 2),
(106, 1, '50AGE', 'Salazar Tam', NULL, 21.9929822, -99.0138339, 2),
(107, 1, '50BDQ', 'Lerdo De Tejada Tam', NULL, 21.9857741, -99.0064560, 2),
(108, 1, '50C7Y', 'Unidad Norte Maf', NULL, 21.6110573, -98.6635611, 2),
(109, 1, '50E1G', 'Ferrocarril', NULL, 21.9887511, -98.9961888, 2),
(110, 1, '50EFO', 'Pedregal Tam', NULL, 22.0098557, -99.0015364, 2),
(111, 1, '50IMH', 'Frontera Tam', NULL, 21.9930698, -99.0019358, 2),
(112, 1, '50IMJ', 'Ecocentralita Tam', NULL, 21.9843314, -99.0160706, 2),
(113, 1, '50J2L', 'Fray Andres Tam', NULL, 21.9767632, -99.0059428, 2),
(114, 1, '50K58', 'El Carmen Tam', NULL, 21.9960371, -98.9944388, 2),
(115, 1, '50L5A', 'Aurora Tam', NULL, 21.9801702, -98.9979780, 2),
(116, 1, '50LCB', 'General Anaya Tam', NULL, 21.9754455, -99.0101811, 2),
(117, 1, '50QV1', 'Villa Huasteca Tam', NULL, 22.0172000, -98.9972000, 2),
(118, 1, '50R68', 'Acuario Tam', NULL, 21.9861561, -99.0132300, 2),
(119, 1, '50S9S', 'Motolinia', NULL, 21.9927522, -99.0094942, 2),
(120, 1, '50SFT', 'Linares Tam', NULL, 22.0005347, -99.0042936, 2),
(121, 1, '50T0H', 'Zaragoza Tam', NULL, 21.9902600, -99.0050460, 2),
(122, 1, '50TBI', 'Tampaon Tam', NULL, 21.9975870, -99.0105643, 2),
(123, 1, '50TEJ', 'Glorieta Tam', NULL, 21.9821213, -99.0055280, 2),
(124, 1, '50VBC', 'Valles Centro Tam', NULL, 21.9864463, -99.0183428, 2),
(125, 1, '50YNI', 'Apolo Tam', NULL, 22.0094553, -99.0090994, 2),
(126, 1, '50DHN', 'Las Aguilas Tam', NULL, 21.9767562, -98.9717610, 2),
(127, 1, '50LFI', 'San Felipe Tam', NULL, 21.9768834, -98.9565067, 2),
(128, 1, '50Z98', 'Del Campo Tam', NULL, 21.9816283, -98.9782830, 2),
(129, 1, '50U2F', 'Galeana Valles Maf', NULL, 21.9907751, -99.0123331, 2),
(130, 1, '500Q5', 'Servando Tam', NULL, 22.7449000, -98.9723000, 5),
(131, 1, '500X5', 'Ciudad Del Maiz Tam', NULL, 22.4038794, -99.6028670, 5),
(132, 1, '5012J', 'Xico Mercado Tam', NULL, 22.9978879, -98.9383770, 5),
(133, 1, '502E1', 'Del Pino Maf', NULL, 22.7345778, -98.9642886, 5),
(134, 1, '503BF', 'Loma Alta Tam', NULL, 22.8826157, -99.0272300, 5),
(135, 1, '5081M', 'El Abra Maf', NULL, 22.6282696, -99.0229962, 5),
(136, 1, '50947', 'Blvd Colosio Tam', NULL, 22.7559844, -98.9691050, 5),
(137, 1, '5095M', 'Guillermo Prieto', NULL, 22.7402653, -98.9827835, 5),
(138, 1, '509DV', 'Luis Echeverria Tam', NULL, 22.7383714, -98.9635837, 5),
(139, 1, '50A7Z', 'Ganadera Tam', NULL, 22.8456001, -99.3323500, 5),
(140, 1, '50EEN', 'Rio Mante Tam', NULL, 22.7323883, -98.9712167, 5),
(141, 1, '50GFU', 'Escobedo Tam', NULL, 22.7422834, -98.9683141, 5),
(142, 1, '50GG2', 'Nuevo Morelos Maf', NULL, 22.5338503, -99.2212434, 5),
(143, 1, '50GMH', 'Mante Tam', NULL, 22.7088093, -98.9923335, 5),
(144, 1, '50HN6', 'Ciudad Ocampo Tam', NULL, 22.8439131, -99.3365421, 5),
(145, 1, '50NML', 'Limon Tam', NULL, 22.8218502, -99.0103359, 5),
(146, 1, '50NTK', 'Autopark Mante Tam', NULL, 22.7474075, -98.9921242, 5),
(147, 1, '50NWL', 'Bernal Tam', NULL, 22.7578550, -98.9618210, 5),
(148, 1, '50OR5', 'El Martillo Tam', NULL, 22.7486649, -98.9617438, 5),
(149, 1, '50PGW', 'Paniagua Tam', NULL, 22.7498627, -98.9713478, 5),
(150, 1, '50Q0M', 'Condueños Maf', NULL, 22.7469111, -98.9765905, 5),
(151, 1, '50Q7H', 'Naranjo Centro Tam', NULL, 22.5224433, -99.3259000, 5),
(152, 1, '50RD4', 'Aviacion Mante Tam', NULL, 22.7534037, -99.0053359, 5),
(153, 1, '50RG8', 'Carretera Del Maiz Tam', NULL, 22.4006041, -99.6076250, 5),
(154, 1, '50SM7', 'Carretera Mante Tam', NULL, 22.7260481, -98.9622627, 5),
(155, 1, '50U5K', 'Mante Platino Maf', NULL, 22.8034639, -99.0134389, 5),
(156, 1, '50U9L', 'Mante Centro Tam', NULL, 22.7377535, -98.9726290, 5),
(157, 1, '50UR4', 'Nicolas Moreno Tam', NULL, 22.7336489, -98.9816130, 5),
(158, 1, '50V1D', 'Chicoasen Maf', NULL, 22.7326240, -98.9598331, 5),
(159, 1, '50V37', 'Antiguo Morelos Tam', NULL, 22.5543000, -99.0785660, 5),
(160, 1, '50VMJ', 'Olivo Tam', NULL, 22.7453550, -98.9837176, 5),
(161, 1, '50W7N', 'La Esperanza Tam', NULL, 22.5236676, -99.3332986, 5),
(162, 1, '50XIJ', 'Xico Tam', NULL, 22.9889816, -98.9467760, 5),
(163, 1, '501E4', 'Estacion Cardenas Maf', NULL, 22.7455713, -98.9865396, 5),
(164, 1, '502RQ', 'Loma Bonita', NULL, 21.4378530, -98.8682726, 2),
(165, 1, '503HS', 'Aquismon Tam', NULL, 21.6233499, -98.9869980, 2),
(166, 1, '5068M', 'Centro Tampacan', NULL, 21.4021360, -98.7285800, 2),
(167, 1, '506F8', 'Deportiva Valles Tam', NULL, 21.9710406, -98.9939960, 2),
(168, 1, '506J3', 'San Miguel Tam', NULL, 21.2541425, -98.7869140, 2),
(169, 1, '5077X', 'Tetlama Tam', NULL, 21.2629492, -98.7927803, 2),
(170, 1, '5083G', 'Tampacan Tam', NULL, 21.3289000, -98.8199000, 2),
(171, 1, '5089E', 'Chapulhuacanito Tam', NULL, 21.2083661, -98.6712625, 2),
(172, 1, '50AGG', 'Xolol Tam', NULL, 21.5934401, -98.9928564, 2),
(173, 1, '50ARH', 'Tencaxapa Tam', NULL, 21.2480868, -98.7806925, 2),
(174, 1, '50BPR', 'Lomasdesantiago Tam', NULL, 21.9586000, -98.9939000, 2),
(175, 1, '50CLC', 'Entronquexilitla Tam', NULL, 21.4449578, -98.9280247, 2),
(176, 1, '50FBM', 'Hospital Tam', NULL, 21.9488727, -98.9943585, 2),
(177, 1, '50FCQ', 'Matlapa Tam', NULL, 21.3353659, -98.8269595, 2),
(178, 1, '50GQU', 'Tampamolon Tam', NULL, 21.5597810, -98.8166575, 2),
(179, 1, '50HXF', 'Providencia Tam', NULL, 21.9325400, -98.9727400, 2),
(180, 1, '50IMI', 'Auto Park Valles Tam', NULL, 21.9651052, -98.9990242, 2),
(181, 1, '50IUD', 'Villavicencio Tam', NULL, 21.9693263, -98.9996087, 2),
(182, 1, '50K35', 'Centro Aquismon Maf', NULL, 21.6217907, -99.0205226, 2),
(183, 1, '50L8J', 'La Purisima Tam', NULL, 21.4409000, -98.8776000, 2),
(184, 1, '50L9W', 'Plaza Tancanhuitz Tam', NULL, 21.5976000, -98.9671000, 2),
(185, 1, '50MDN', 'Zacatipan Tam', NULL, 21.2481979, -98.7749820, 2),
(186, 1, '50O9V', 'Huehuetlan Tam', NULL, 21.4814000, -98.9667000, 2),
(187, 1, '50PJW', 'Pujal Tam', NULL, 21.8543270, -98.9424869, 2),
(188, 1, '50Q27', 'Buenos Aires Tam', NULL, 21.2521082, -98.7494853, 2),
(189, 1, '50RF7', 'San Martin Tam', NULL, 21.3705254, -98.6581382, 2),
(190, 1, '50TES', 'Xilitla Tam', NULL, 21.3849662, -98.9898080, 2),
(191, 1, '50THJ', 'Centro Tamanzuchale Tam', NULL, 21.2605796, -98.7893690, 2),
(192, 1, '50TTN', 'Crucero Tam', NULL, 21.2593876, -98.7880150, 2),
(193, 1, '50U1U', 'Coxcatlan Tam', NULL, 21.5408801, -98.9058100, 2),
(194, 1, '50W6J', 'Ahuacatlan Tam', NULL, 21.3211170, -99.0520400, 2),
(195, 1, '50XXC', 'Axtla Tam', NULL, 21.4360760, -98.8746615, 2),
(196, 1, '509Q2', 'Azalea Maf', NULL, 21.3875846, -98.9853257, 2),
(197, 1, '50PZ5', 'Huichihuayan Maf', NULL, 21.4836677, -98.9709730, 2),
(198, 1, '507LE', 'Gas Tamazunchale Maf', NULL, 21.2662874, -98.7756415, 2),
(199, 1, '50Y75', 'Palmira Maf', NULL, 21.6776155, -98.9713187, 2),
(200, 1, '50EW2', 'Comoca Maf', NULL, 21.4248448, -98.8901577, NULL),
(201, 1, '506LS', 'Rascon 2 Maf', NULL, 21.9666243, -99.2558955, 2),
(202, 2, '506RG', 'Llera Centro Tam', NULL, 23.3183449, -99.0234087, 4),
(203, 2, '50AU4', 'Villa Llera Maf', NULL, 23.3080896, -99.0212745, 4),
(204, 2, '50LFF', 'Llera Tam', NULL, 23.1164182, -98.7507026, 4),
(205, 2, '50Y9K', 'Jose Silva Tam', NULL, 23.3130930, -99.0217376, 4),
(206, 2, '50SO2', '14 Carrera Maf', NULL, 23.7381528, -99.1492969, 4),
(207, 2, '50UXE', 'Estadio Maf', NULL, 23.7394869, -99.1536816, 4),
(208, 2, '50VDI', '16 Veracruz Maf', NULL, 23.7469388, -99.1502768, 4),
(209, 2, '50CRW', '8 Carrera Maf', NULL, 23.7371000, -99.1436000, 4),
(210, 2, '50JZU', '9 Y Berriozabal Maf', NULL, 23.7401431, -99.1447991, 4),
(211, 2, '502CG', '31 Berriozabal Maf', NULL, 23.7422000, -99.1683000, 10),
(212, 2, '50BAA', 'Refineria Maf', NULL, 23.7434448, -99.1645185, 10),
(213, 2, '50D9A', 'Keppler Maf', NULL, 23.7505901, -99.1705524, 10),
(214, 2, '50FEY', 'Nacozary Maf', NULL, 23.7379479, -99.1629987, 10),
(215, 2, '50JZV', 'Oaxaca Maf', NULL, 23.7550317, -99.1519488, 10),
(216, 2, '50OUK', 'Adelitas Maf', NULL, 23.7470733, -99.1594454, 10),
(217, 2, '50OUW', 'La Escondida Maf', NULL, 23.7408888, -99.1599736, 10),
(218, 2, '50SNQ', 'San Luisito Maf', NULL, 23.7594442, -99.1522882, 10),
(219, 2, '50YJH', 'Conrado Maf', NULL, 23.7448255, -99.1527471, 10),
(220, 2, '50YM7', 'Almendros Maf', NULL, 23.7593510, -99.1584847, 10),
(221, 2, '50YTC', 'Tec Maf', NULL, 23.7522734, -99.1649102, 10),
(222, 2, '50VJE', 'Eje Vial Maf', NULL, 23.7565400, -99.1603400, 10),
(223, 2, '50HJB', '15 Berriozabal Maf', NULL, 23.7414754, -99.1495148, 10),
(224, 2, '5065F', 'La Estrella Maf', NULL, 23.7715000, -99.1732000, 10),
(225, 2, '506AR', 'El Barretal Maf', NULL, 24.0854750, -99.1244110, 10),
(226, 2, '507SX', 'Monte Alto Umi', NULL, 23.7814000, -99.1675000, 10),
(227, 2, '50BRK', 'Laborcitas Maf', NULL, 23.8214000, -99.1225000, 10),
(228, 2, '50DPJ', 'Zeferino 2 Maf', NULL, 23.7563600, -99.1644700, 10),
(229, 2, '50DQV', 'La Presita Maf', NULL, 23.7749801, -99.1671974, 10),
(230, 2, '50DQZ', 'Zeferino Maf', NULL, 23.7627227, -99.1676992, 10),
(231, 2, '50GFO', 'Nacional Maf', NULL, 23.7920105, -99.1571523, 10),
(232, 2, '50GZK', 'Guemez Maf', NULL, 23.7900388, -99.1506470, 10),
(233, 2, '50HVX', 'Las Americas Maf', NULL, 23.7660899, -99.1617688, 10),
(234, 2, '50K6D', 'Los Troncones Maf', NULL, 23.7989630, -99.1780510, 10),
(235, 2, '50MY5', 'Tomaseno Maf', NULL, 24.2605000, -99.4366000, 10),
(236, 2, '50NUD', 'Naciones Unidas II Maf', NULL, 23.7732405, -99.1610827, 10),
(237, 2, '50SDQ', 'Naciones Unidas Maf', NULL, 23.7730148, -99.1391155, 10),
(238, 2, '50TMK', 'Oralia Guerra Maf', NULL, 23.7733371, -99.1530167, 10),
(239, 2, '50VE7', 'Subida Alta Maf', NULL, 23.9122020, -99.1135230, 10),
(240, 2, '50W7M', 'Colinas Del Valle Maf', NULL, 23.7673000, -99.1706000, 10),
(241, 2, '50WU2', 'Villagran Maf', NULL, 24.4689360, -99.4931638, 10),
(242, 2, '50OSD', '16 Sierra de Casas Maf', NULL, 23.7658021, -99.1475256, 10),
(243, 2, '50XIQ', 'Naciones Unidas III Maf', NULL, 23.7738877, -99.1438848, 10),
(244, 2, '50WM4', 'La Salle Victoria Maf', NULL, 23.7792483, -99.1461479, 10),
(245, 2, '508QJ', 'La Libertad Maf', NULL, NULL, NULL, 10),
(246, 2, '5029T', 'Gas Altamira Tam', NULL, 22.4648285, -97.9034906, 4),
(247, 2, '503OF', 'Tres Marias Maf', NULL, 22.4823693, -98.0155360, 4),
(248, 2, '506L1', 'Santa Fe Tam', NULL, 22.8220313, -98.4281815, 4),
(249, 2, '5074G', 'De Los Rios Tam', NULL, 22.4530549, -97.8998904, 4),
(250, 2, '5081E', 'Esteros Tam', NULL, 22.5217400, -98.1245533, 4),
(251, 2, '509KX', 'Gas El 40 Tam', NULL, 22.4978707, -98.0602239, 4),
(252, 2, '50CGZ', 'Gonzalez Centro Tam', NULL, 22.8284285, -98.4264394, 4),
(253, 2, '50ESH', 'Est. Cuauhtemoc Tam', NULL, 22.5528278, -98.1496564, 4),
(254, 2, '50KHB', 'Manuel Centro Tam', NULL, 22.7280785, -98.3216801, 4),
(255, 2, '50LFD', 'Lib. Manuel Tam', NULL, 22.7437762, -98.2984855, 4),
(256, 2, '50LO8', 'Agropecuarios Maf', NULL, 22.4497570, -97.9768308, 4),
(257, 2, '50M73', 'Gas Manuel Tmp', NULL, 22.7613614, -98.3154911, 4),
(258, 2, '50NF7', 'Seis Tam', NULL, 22.7294424, -98.3121644, 4),
(259, 2, '50PV1', 'Jose Maria Tam', NULL, 22.7324790, -98.3243282, 4),
(260, 2, '50QDV', 'Cuauhtemoc Centro Tam', NULL, 22.5425223, -98.1504510, 4),
(261, 2, '50QLK', 'Puerto Altamira Tam', NULL, 22.4336497, -97.8914126, 4),
(262, 2, '50RA5', 'Estacion Colonias Tam', NULL, 22.4412578, -98.0166804, 4),
(263, 2, '50TS7', 'Puente Roto Maf', NULL, 22.4591589, -97.9816275, 4),
(264, 2, '50WIY', 'Villa Cuauhtemoc Tam', NULL, 22.5559397, -98.1511849, 4),
(265, 2, '50ZBK', 'Gonzalez Tam', NULL, 22.8235155, -98.4377925, 4),
(266, 2, '505Q5', 'Rio Tamesi Maf', NULL, 22.6605670, -98.5513413, 4),
(267, 2, '50H2A', 'Villa Manuel Maf', NULL, 22.7285973, -98.3170772, 4),
(268, 2, '505EV', 'Gonzalitos Maf', NULL, 22.8322614, -98.4347266, 4),
(269, 2, '50FE7', 'Santa Amalia Tam', NULL, 22.4320309, -97.9668336, 4),
(270, 2, '501XQ', 'Vialidad Pd Tam', NULL, 22.3971000, -97.8990410, 4),
(271, 2, '5096D', 'Electricistas Tam', NULL, 22.4195635, -97.9345376, 4),
(272, 2, '50GKB', 'Ornato Tam', NULL, 22.4159374, -97.9229252, 4),
(273, 2, '50181', 'Camaleon Tam', NULL, 22.4042280, -97.9294080, 4),
(274, 2, '501HK', 'Santa Elena', NULL, 22.3252571, -97.8492715, 4),
(275, 2, '502R0', 'C-5 Maf', NULL, 22.3904766, -97.9078756, 4),
(276, 2, '506IK', 'Valle Dorado Tam', NULL, 22.3244245, -97.8704158, 4),
(277, 2, '509UI', 'Gas Monte Alto Tam', NULL, 22.3813595, -97.9144040, 4),
(278, 2, '50ABO', 'Arboledas Tam', NULL, 22.3875104, -97.9123526, 4),
(279, 2, '50AV6', 'Valle Esmeralda Tam', NULL, 22.3996579, -97.9135001, 4),
(280, 2, '50BAM', 'Nuevo Madero Tam', NULL, 22.3475830, -97.8548613, 4),
(281, 2, '50BXH', 'Deportivo Sur Tam', NULL, 22.4033879, -97.9106014, 4),
(282, 2, '50CZK', 'Vidal Tam', NULL, 22.3287594, -97.8744190, 4),
(283, 2, '50D8L', 'Avenida Cuarta Tam', NULL, 22.3417435, -97.8689310, 4),
(284, 2, '50DCO', 'Crit Tam', NULL, 22.4025861, -97.9271686, 4),
(285, 2, '50DVR', 'Divisoria Tam', NULL, 22.3236853, -97.8760260, 4),
(286, 2, '50HVY', 'Durazno Tam', NULL, 22.4093664, -97.9192421, 4),
(287, 2, '50NVK', 'Pedrera Tam', NULL, 22.3885761, -97.8847904, 4),
(288, 2, '50OP3', 'Retama Tam', NULL, 22.3819510, -97.9112913, 4),
(289, 2, '50SVA', 'Los Olivos Tam', NULL, 22.3963415, -97.9031520, 4),
(290, 2, '50VIA', 'Villas Tam', NULL, 22.3959173, -97.8863698, 4),
(291, 2, '50WXF', 'Bateria 7 Tam', NULL, 22.4160926, -97.9315814, 4),
(292, 2, '50YUO', 'Ficus Tam', NULL, 22.3928603, -97.9099713, 4),
(293, 2, '50YUV', 'Arrecifes Tam', NULL, 22.3954222, -97.8877749, 4),
(294, 2, '50YUX', 'Sauce II Tam', NULL, 22.3952654, -97.9016043, 4),
(295, 2, '50973', 'Sector 3 Tam', NULL, 22.4089078, -97.9381230, 4),
(296, 2, '50H6R', 'Madrid Maf', NULL, 22.3301460, -97.8628730, 4),
(297, 2, '5008G', 'Palmillas Maf', NULL, 23.3083459, -99.5568429, 4),
(298, 2, '501EA', 'Baez Maf', NULL, 23.7052814, -99.1190102, 4),
(299, 2, '502K2', 'Jaumave Plaza Maf', NULL, 23.4053852, -99.3750376, 4),
(300, 2, '5060E', 'Pedro Sosa 1 Maf', NULL, 23.7166692, -99.1327118, 4),
(301, 2, '507EU', 'Rio Blanco Maf', NULL, 23.7249408, -99.1351195, 4),
(302, 2, '508HH', 'Clinica Imss Maf', NULL, 22.9917444, -99.7181107, 4),
(303, 2, '50GZW', '8 Y Garza Maf', NULL, 23.7274095, -99.1442639, 4),
(304, 2, '50JQ5', 'Lomas De Guadalupe Maf', NULL, 23.7126659, -99.1285782, 4),
(305, 2, '50JUM', 'Jaumave Maf', NULL, 23.4083813, -99.3857221, 4),
(306, 2, '50JYD', 'Santuario 2 Maf', NULL, 23.7181000, -99.1453000, 4),
(307, 2, '50JZW', 'San Luis Maf', NULL, 23.6633332, -99.1075913, 4),
(308, 2, '50P0X', 'Arroyo Loco Maf', NULL, 22.9994383, -99.7120471, 4),
(309, 2, '50PD1', 'Jaumave 2 Maf', NULL, 23.3951258, -99.4141151, 4),
(310, 2, '50SUO', 'Santuario Maf', NULL, 23.7173700, -99.1444100, 4),
(311, 2, '50W07', 'Tula Centro Maf', NULL, 22.9967716, -99.7118080, 4),
(312, 2, '50Y4M', 'Jaumave Centro Maf', NULL, 23.4059013, -99.3832764, 4),
(313, 2, '50YLO', 'La Loma Maf', NULL, 23.7193664, -99.1288111, 4),
(314, 2, '50DY4', '4 Boulevard Maf', NULL, 23.7291732, -99.1404331, 4),
(315, 2, '50FMY', '8 Boulevard Maf', NULL, 23.7298609, -99.1440892, 4),
(316, 2, '50SA8', '12 de Septiembre Maf', NULL, 23.7111730, -99.1426410, 4),
(317, 2, '50401', 'Revolucion Verde Maf', NULL, 23.7487025, -99.1304042, 10),
(318, 2, '509AD', 'Del Norte Maf', NULL, 23.7498650, -99.1390894, 10),
(319, 2, '501XB', 'Martires Maf', NULL, 23.7888000, -99.1334000, 10),
(320, 2, '503CP', 'Sulaiman Maf', NULL, 23.7471960, -99.1263570, 10),
(321, 2, '505CV', 'Hombres Ilustres 2 Maf', NULL, 23.7512782, -99.1292846, 10),
(322, 2, '507Q4', 'Teosuchil Maf', NULL, 23.7556074, -99.1283171, 10),
(323, 2, '50BSX', 'Abasolo Maf', NULL, 23.7350065, -99.1300317, 10),
(324, 2, '50CSB', 'Fidel Velazquez II Maf', NULL, 23.7311674, -99.1298976, 10),
(325, 2, '50DQC', '14 Boulevard Maf', NULL, 23.7527134, -99.1471809, 10),
(326, 2, '50FDV', 'Fidel Velazquez Maf', NULL, 23.7414177, -99.1333942, 10),
(327, 2, '50FYA', 'Arguelles Maf', NULL, 23.7321169, -99.1424399, 10),
(328, 2, '50HII', 'Hombres Ilustres Maf', NULL, 23.7523835, -99.1362296, 10),
(329, 2, '50HVZ', '5 Ceros Maf', NULL, 23.7357195, -99.1326041, 10),
(330, 2, '50RRA', '18 De Julio Maf', NULL, 23.7379382, -99.1298648, 10),
(331, 2, '50SAC', 'Salubridad Maf', NULL, 23.7354362, -99.1517294, 10),
(332, 2, '50TGU', '13 Yucatan Maf', NULL, 23.7570481, -99.1462740, 10),
(333, 2, '50TWQ', 'Tenochtitlan Maf', NULL, 23.7535578, -99.1274609, 10),
(334, 2, '50UXB', 'Berriozabal Maf', NULL, 23.7401039, -99.1372245, 10),
(335, 2, '50VGY', 'Valle De Aguayo Maf', NULL, 23.7529611, -99.1434371, 10),
(336, 2, '50WMI', 'Ocho Michoacan Maf', NULL, 23.7591296, -99.1400879, 10),
(337, 2, '50WQN', 'Castaneda Maf', NULL, 23.7372489, -99.1235536, 10),
(338, 2, '50XDG', '1 Matamoros Maf', NULL, 23.7324460, -99.1377946, 10),
(339, 2, '50XJL', 'Rectoria Maf', NULL, 23.7332193, -99.1449377, 10),
(340, 2, '50YCR', '3 Carrera Maf', NULL, 23.7368433, -99.1391557, 10),
(341, 2, '50YHI', '14 Hidalgo Maf', NULL, 23.7319460, -99.1496888, 10),
(342, 2, '50YJF', 'Rotario Maf', NULL, 23.7507466, -99.1239292, 10),
(343, 2, '50YJI', 'Olivia Maf', NULL, 23.7423570, -99.1412599, 10),
(344, 2, '50YJN', 'Hospital General Maf', NULL, 23.7490888, -99.1379780, 10),
(345, 2, '50YNX', 'Colon Maf', NULL, 23.7467007, -99.1444532, 10),
(346, 2, '5085F', 'Tamaulipas II Tam', NULL, 22.3933806, -97.9338671, 4),
(347, 2, '50ALR', 'Altamira Tam', NULL, 22.4145351, -97.9374062, 4),
(348, 2, '50BAX', 'Fco I. Madero Tam', NULL, 22.3582522, -97.8830196, 4),
(349, 2, '50CMP', 'Floresta Tam', NULL, 22.3236620, -97.8875965, 4),
(350, 2, '50DDL', 'Santa Anita Tam', NULL, 22.3912605, -97.9463695, 4),
(351, 2, '50E20', 'Capitan Tam', NULL, 22.3936427, -97.9413116, 4),
(352, 2, '50EJN', 'Allende Tam', NULL, 22.4136361, -97.9408672, 4),
(353, 2, '50IFF', 'Florida Tam', NULL, 22.3995165, -97.9438036, 4),
(354, 2, '50IOX', 'El Eden Tam', NULL, 22.3957319, -97.9279432, 4),
(355, 2, '50LB3', 'Ejido Miramar Tam', NULL, 22.3308851, -97.8668281, 4),
(356, 2, '50M82', 'Osa Mayor Tam', NULL, 22.3618817, -97.8825664, 4),
(357, 2, '50MBJ', 'Altamira Mercado Tam', NULL, 22.3965613, -97.9358785, 4),
(358, 2, '50NXH', '18 De Marzo Tam', NULL, 22.3356332, -97.8662991, 4),
(359, 2, '50O53', 'Tec Monterrey Tam', NULL, 22.3815732, -97.9022013, 4),
(360, 2, '50OAT', 'Altamira Centro Tam', NULL, 22.3917731, -97.9357760, 4),
(361, 2, '50ODK', 'Gaviotas Tam', NULL, 22.3915643, -97.9300480, 4),
(362, 2, '50RRG', 'Benito Juarez Tam', NULL, 22.4056079, -97.9423943, 4),
(363, 2, '50RXV', 'Revolucion Tam', NULL, 22.3873778, -97.9450338, 4),
(364, 2, '50TY6', 'Santos Degollado Tam', NULL, 22.3264677, -97.8827363, 4),
(365, 2, '50U0F', 'Movil Victoria Maf', NULL, 23.7322000, -99.1498000, 4),
(366, 2, '50V28', 'El Faro', NULL, 22.3875552, -97.9500173, 4),
(367, 2, '50VJ7', 'Secundaria 1 Maf', NULL, 22.3903273, -97.9389727, 4),
(368, 2, '50XBK', 'Mina Tam', NULL, 22.3941533, -97.9373321, 4),
(369, 2, '50YNE', 'Argentina Tam', NULL, 22.3267327, -97.8622229, 4),
(370, 2, '50ZMH', 'Guerrero II Tam', NULL, 22.3332118, -97.8811296, 4),
(371, 2, '50F4P', 'Fundo Legal Maf', NULL, 22.3956616, -97.9407397, 4),
(372, 2, '50V7Z', 'Bahia Tam', NULL, 22.3338463, -97.8605004, 4),
(373, 2, '503OG', 'Barquito Tam', NULL, 22.3427330, -97.8756858, 4),
(374, 2, '50KH9', 'Avenida De La Industria Tam', NULL, 22.3475251, -97.8776009, 4),
(375, 2, '502O6', 'Colinas Tam', NULL, 22.3682037, -97.8813073, 4),
(376, 2, '503DX', 'Encinos Tam', NULL, 22.3533329, -97.8866309, 4),
(377, 2, '504W9', 'Moroncito Tam', NULL, 22.9178004, -98.0779755, 4),
(378, 2, '505MH', 'Lagunas De Miralta Tam', NULL, 22.3554676, -97.8893216, 4),
(379, 2, '507OV', 'Villas Del Sol Tam', NULL, 22.3681363, -97.8857603, 4),
(380, 2, '508SU', 'Canarios Tam', NULL, 22.3760534, -97.9141274, 4),
(381, 2, '50956', 'Luis Caballero Tam', NULL, 22.9313100, -98.0789700, 4),
(382, 2, '50APC', 'Lirios Tam', NULL, 22.3691866, -97.9061922, 4),
(383, 2, '50BDP', 'Azteca Tam', NULL, 22.3636059, -97.9101294, 4),
(384, 2, '50DMW', 'Aldama II Tam', NULL, 22.9229804, -98.0809626, 4),
(385, 2, '50KAD', 'Aldama Tam', NULL, 22.9210753, -98.0848413, 4),
(386, 2, '50LCI', 'Constitucion Tam', NULL, 22.9126553, -98.0755837, 4),
(387, 2, '50MXY', 'Monte Alto Tam', NULL, 22.3777662, -97.9101116, 4),
(388, 2, '50PHB', 'Brownsville Tam', NULL, 22.9260024, -98.0740950, 4),
(389, 2, '50SIR', 'Satelite Tam', NULL, 22.3619408, -97.8876417, 4),
(390, 2, '50TG3', 'Rio Blanco Tam', NULL, 22.3629130, -97.9154932, 4),
(391, 2, '50TML', 'Miraltas Tam', NULL, 22.3591184, -97.8913691, 4),
(392, 2, '50UUM', 'Aldama Centro Tam', NULL, 22.9199131, -98.0734628, 4),
(393, 2, '50W74', 'Carmin Tam', NULL, 22.3646636, -97.9046563, 4),
(394, 2, '50YOB', 'Arecas Tam', NULL, 22.3794245, -97.9065431, 4),
(395, 2, '50CQ8', 'Club de Leones Maf', NULL, 22.9170097, -98.0705718, 4),
(396, 2, '50EUP', 'La Paz II Maf', NULL, 23.7476711, -99.1202239, 10),
(397, 2, '504AD', 'Marina Centro', NULL, 23.7663322, -98.2060228, 10),
(398, 2, '505CO', 'Ejido la Pesca Maf', NULL, 23.7875995, -97.7755656, 10),
(399, 2, '508DH', 'Vicente Guerrero Maf', NULL, 23.7374352, -99.1137053, 10),
(400, 2, '508FV', 'Coplamar Maf', NULL, 23.7750000, -98.2050000, 10),
(401, 2, '5097Z', 'Marte Maf', NULL, 23.7336594, -99.0917070, 10),
(402, 2, '509YX', 'Aptiv Maf', NULL, 23.7256000, -99.0831000, 10),
(403, 2, '50C2U', 'Valle del Sol', NULL, 23.7432379, -99.1130177, 10),
(404, 2, '50D60', 'Todos Por Tamaulipas Maf', NULL, 23.7369375, -99.0976017, 10),
(405, 2, '50DM5', 'Lindavista Maf', NULL, 23.7350000, -99.1064000, 10),
(406, 2, '50DQT', 'La Moderna Maf', NULL, 23.7328680, -99.1129095, 10),
(407, 2, '50E2Q', 'Aviles Maf', NULL, 23.7197845, -99.1047527, 10),
(408, 2, '50GFP', 'La Paz Maf', NULL, 23.7415548, -99.1168614, 10),
(409, 2, '50GZA', 'Zaragoza Maf', NULL, 23.7070527, -98.9929217, 10),
(410, 2, '50JYC', 'Chapultepec Maf', NULL, 23.7189906, -99.1166326, 10),
(411, 2, '50JZT', 'Zaragoza 2 Maf', NULL, 23.7226587, -98.9984358, 10),
(412, 2, '50KD1', 'Marina Vieja II Maf', NULL, 23.7309564, -98.2199911, 10),
(413, 2, '50MRJ', 'Marina Vieja Maf', NULL, 23.7703727, -98.2035909, 10),
(414, 2, '50RNV', 'Rumbo Nuevo Maf', NULL, 23.7183419, -99.0949671, 10),
(415, 2, '50S1T', 'Alta Vista Maf', NULL, 23.7195000, -99.1078000, 10),
(416, 2, '50Z3O', 'San Felipe II Maf', NULL, 23.7164547, -99.1086900, 10),
(417, 2, '50A92', 'Zurita Maf', NULL, 23.7492368, -99.1220400, 10),
(418, 2, '504VH', 'Malitzin Maf', NULL, 23.7591500, -99.1254340, 10),
(419, 2, '50UDG', 'Campestre Maf', NULL, 23.7606800, -99.1299400, 10),
(420, 2, '5025Q', 'Royal Country Maf', NULL, 23.7614244, -99.1335385, 10),
(421, 2, '504XP', 'Valle de Pajaritos Maf', NULL, 23.7623554, -99.1089450, 10),
(422, 2, '505C7', 'Sierra Gorda Maf', NULL, 24.2137141, -98.4830154, 10),
(423, 2, '507JI', 'Nuevo Santander Umi', NULL, 23.7508536, -99.1115165, 10),
(424, 2, '50B9S', 'Olivin Maf', NULL, 23.7722140, -99.1083942, 10),
(425, 2, '50C6M', 'Agronomos Maf', NULL, 23.7697561, -99.1232227, 10),
(426, 2, '50C8A', 'Privanzas Maf', NULL, 23.7657374, -99.1171783, 10),
(427, 2, '50DWQ', 'Nuevo Padilla II Maf', NULL, 24.0579771, -98.8909636, 10),
(428, 2, '50JJZ', 'Jimenez II Maf', NULL, 24.2214690, -98.4928239, 10),
(429, 2, '50K4E', 'Guemez Centro Maf', NULL, 23.9185275, -99.0079688, 10),
(430, 2, '50N72', 'Servitrail Maf', NULL, 23.8539470, -99.0554080, 10),
(431, 2, '50NPD', 'Nuevo Padilla Maf', NULL, 24.0441753, -98.8978653, 10),
(432, 2, '50S7R', 'Azteca Maf', NULL, 23.7447247, -99.1088034, 10),
(433, 2, '50T2X', 'Benito Sierra 1 Maf', NULL, 24.0589274, -98.3725795, 10),
(434, 2, '50U2L', 'Padilla Centro Maf', NULL, 24.0501997, -98.9009642, 10),
(435, 2, '50V8G', 'Mirlos Maf', NULL, 23.7711868, -99.1035538, 10),
(436, 2, '50VYB', 'Especialidades Maf', NULL, 23.7615756, -99.1093386, 10),
(437, 2, '50XQZ', 'Pajaritos Maf', NULL, 23.7540720, -99.1065068, 10),
(438, 2, '50D83', 'Olivin Centro Maf', NULL, 23.7848040, -99.1001770, 10),
(439, 2, '50BBG', 'Cuartel Maf', NULL, 23.7558731, -99.1738785, 10),
(440, 2, '500N0', 'Relaciones Exteriores Maf', NULL, 23.7321000, -99.1519000, 10),
(441, 2, '50QOL', '17 Y Juarez Maf', NULL, 23.7307961, -99.1527349, 10),
(442, 2, '508UF', '33 Juarez Maf', NULL, 23.7306000, -99.1690000, 10),
(443, 2, '50ASH', 'Asent. Humanos Maf', NULL, 23.7146451, -99.1616360, 10),
(444, 2, '50CP6', 'Lara Maf', NULL, 23.7238728, -99.1694974, 10),
(445, 2, '50DQF', '22 Rosales Maf', NULL, 23.7262927, -99.1578771, 10),
(446, 2, '50HBI', 'Paseo Mendez Maf', NULL, 23.7242275, -99.1536563, 10),
(447, 2, '50IKT', 'Tamatan Maf', NULL, 23.7204631, -99.1650483, 10),
(448, 2, '50ISP', 'Americo Maf', NULL, 23.7185747, -99.1792393, 10),
(449, 2, '50JZY', 'Sierra Maf', NULL, 23.7459046, -99.1722304, 10),
(450, 2, '50KMT', 'Del Maestro Maf', NULL, 23.7216800, -99.1641660, 10),
(451, 2, '50MC0', 'Alvaro Obregon Maf', NULL, 23.7259451, -99.1802320, 10),
(452, 2, '50N7H', 'Laurel Maf', NULL, 23.7271023, -99.1695991, 10),
(453, 2, '50NBQ', '7 De Noviembre Maf', NULL, 23.7149300, -99.1781200, 10),
(454, 2, '50PA0', '32 Y Matamoros Maf', NULL, 23.7342098, -99.1690193, 10),
(455, 2, '50RW8', 'Casas Blancas 2 Maf', NULL, 23.7092110, -99.1574290, 10),
(456, 2, '50TKE', 'Libramiento II Maf', NULL, 23.7338197, -99.1768403, 10),
(457, 2, '50U7A', 'Amalia G Maf', NULL, 23.7078323, -99.1612522, 10),
(458, 2, '50UQX', 'Sierra Madre Maf', NULL, 23.7104842, -99.1806651, 10),
(459, 2, '50WSR', '28 Juarez Maf', NULL, 23.7306691, -99.1632478, 10),
(460, 2, '50YIT', '22 Iturbide Maf', NULL, 23.7320349, -99.1575894, 10),
(461, 2, '50NS8', '21 de Marzo Maf', NULL, NULL, NULL, NULL),
(462, 2, '50K87', 'Villa Padilla Maf', NULL, NULL, NULL, NULL),
(463, 3, '50AZQ', '14 Diagonal Maf', NULL, 25.8742047, -97.5103100, 7),
(464, 3, '50BIR', 'Laguna Salada Maf', NULL, 25.8693195, -97.5052897, 7),
(465, 3, '50D98', 'Laguneta Maf', NULL, 25.8804160, -97.5183610, 7),
(466, 3, '50FXY', '8 Abasolo Maf', NULL, 25.8809935, -97.5066587, 7),
(467, 3, '50HYA', 'Ayala Maf', NULL, 25.8705779, -97.5171672, 7),
(468, 3, '50JZL', 'Centro Historico Maf', NULL, 25.8802397, -97.5049674, 7),
(469, 3, '50KDE', 'Teran Maf', NULL, 25.8748598, -97.5023051, 7),
(470, 3, '50KTV', 'Mercado Juarez Maf', NULL, 25.8815000, -97.5084000, 7),
(471, 3, '50NAQ', 'Laguna Jasso Maf', NULL, 25.8674235, -97.5038458, 7),
(472, 3, '50NKV', '20 Y Gonzalez Maf', NULL, 25.8780361, -97.5166336, 7),
(473, 3, '50OTU', 'Triangulo Maf', NULL, 25.8669576, -97.5106138, 7),
(474, 3, '50PAY', 'Plan De Ayutla Maf', NULL, 25.8651374, -97.5047626, 7),
(475, 3, '50SGZ', 'Seguro Social Maf', NULL, 25.8733180, -97.5044445, 7),
(476, 3, '50UCA', 'San Francisco Maf', NULL, 25.8690937, -97.5146428, 7),
(477, 3, '50UGZ', 'Gonzalez Maf', NULL, 25.8791971, -97.5105856, 7),
(478, 3, '50UOM', 'Morelos Maf', NULL, 25.8783491, -97.5110264, 7),
(479, 3, '50UOV', 'Diagonal Y 20 Maf', NULL, 25.8759307, -97.5170314, 7),
(480, 3, '50USC', 'Canales Maf', NULL, 25.8711667, -97.5036912, 7),
(481, 3, '50ZPZ', 'Plaza Allende Maf', NULL, 25.8788759, -97.5079153, 7),
(482, 3, '50ZYU', 'Zaragoza Maf', NULL, 25.8753260, -97.5040665, 7),
(483, 3, '502BE', 'Sat Maf', NULL, 25.8752000, -97.5203000, 7),
(484, 3, '50UOA', 'Aguila Maf', NULL, 25.8758764, -97.5253270, 7),
(485, 3, '505JL', 'Puente Viejo Maf', NULL, 25.8864115, -97.5084126, 7),
(486, 3, '500OX', 'Brecha 119 Maf', NULL, 25.6792000, -97.8257000, 8),
(487, 3, '50MDW', 'Madero Maf', NULL, 25.6695698, -97.8156647, 8),
(488, 3, '50DNS', 'Cardenas Maf', NULL, 25.6796949, -97.8163557, 8),
(489, 3, '5000G', 'Alameda Maf', NULL, 25.6667990, -97.8171370, 8),
(490, 3, '501TH', 'Paso Real Maf', NULL, 24.8404822, -98.1647229, 8),
(491, 3, '506BJ', 'Rosalinda Guerrero Maf', NULL, 25.6786581, -97.8099300, 8),
(492, 3, '50805', '6 Y Juarez Maf', NULL, 25.6721434, -97.8074142, 8),
(493, 3, '50CHF', 'Chavez Maf', NULL, 25.6664744, -97.8124667, 8),
(494, 3, '50CHV', 'Echeverria Maf', NULL, 25.6670584, -97.8004320, 8),
(495, 3, '50DBK', 'Dos De Abril Maf', NULL, 25.6720959, -97.8202719, 8),
(496, 3, '50DZJ', '15 Y Juarez Maf', NULL, 25.6720459, -97.7959015, 8),
(497, 3, '50EUE', 'Echeverria III Maf', NULL, 25.6662950, -97.7963139, 8),
(498, 3, '50GHE', 'Echeverria Iimaf', NULL, 25.6662598, -97.7841767, 8),
(499, 3, '50HQW', 'Hidalgo Maf', NULL, 25.6729996, -97.8130822, 8),
(500, 3, '50ICT', 'Castillo Maf', NULL, 25.6673187, -97.8256469, 8),
(501, 3, '50KCS', 'Cardenas Ii Maf', NULL, 25.6933696, -97.8152913, 8),
(502, 3, '50L32', 'Cosme Santos Maf', NULL, 25.6741884, -97.8259646, 8),
(503, 3, '50Q58', 'Independencia Maf', NULL, 25.6733341, -97.8357356, 8),
(504, 3, '50RLT', 'El Realito Maf', NULL, 25.6670082, -97.8676961, 8),
(505, 3, '50SFS', 'Los Fresnos Maf', NULL, 25.6512403, -97.8167147, 8),
(506, 3, '50V6D', 'Brecha 82 Maf', NULL, 25.6665544, -97.8401282, 8),
(507, 3, '50YME', 'America Maf', NULL, 25.6754963, -97.8162888, 8),
(508, 3, '50ZZZ', 'Cipres Maf', NULL, 25.6608093, -97.8153069, 8),
(509, 3, '502GT', 'Lopez Mateos Maf', NULL, 25.6859207, -97.8075443, 8),
(510, 3, '50L92', 'Soberon Maf', NULL, 25.6668511, -97.7892827, 8),
(511, 3, '505UA', 'Las Rusias Maf', NULL, 25.9079320, -97.5518150, 8),
(512, 3, '50BNK', 'Luicio Blanco Ii Maf', NULL, 25.9359470, -97.7777460, 8),
(513, 3, '50BYS', 'Las Brisas Maf', NULL, 25.8643968, -97.5568631, 8),
(514, 3, '50DEW', 'Sendero Iii Maf', NULL, 25.8637758, -97.5778104, 8),
(515, 3, '50EIB', 'El Capote Maf', NULL, 25.9716305, -97.6962433, 8),
(516, 3, '50HWC', 'Anahuac Maf', NULL, 25.7754468, -97.7955433, 8),
(517, 3, '50J3R', 'Anahuac Centro Maf', NULL, 25.7750744, -97.7773583, 8),
(518, 3, '50JZI', 'Las Brisas 3 Maf', NULL, 25.8648523, -97.5634755, 8),
(519, 3, '50QOO', 'Lucio Blanco Maf', NULL, 25.9543622, -97.7686236, 8),
(520, 3, '50T77', 'Inteva Maf', NULL, 25.8829600, -97.5470270, 8),
(521, 3, '50TBO', 'Tres Moras Maf', NULL, 25.8623141, -97.5870360, 8),
(522, 3, '50WKI', 'Los Presidentes Maf', NULL, 25.8582205, -97.5836204, 8),
(523, 3, '50XFN', 'Las Brisas Ii Maf', NULL, 25.8581656, -97.5594262, 8),
(524, 3, '50YHF', 'Empalme Maf', NULL, 25.9054743, -97.8430336, 8),
(525, 3, '50060', 'Delphi Maf', NULL, 25.8844850, -97.5529020, 8),
(526, 3, '50423', 'El Caracol Maf', NULL, 25.8701168, -97.5657097, 8),
(527, 3, '50A35', 'Santa Maria Maf', NULL, 25.8931190, -97.5327148, 8),
(528, 3, '50BBH', 'Villa Del Parque Mam', NULL, 25.8786955, -97.5564572, 8),
(529, 3, '50BIY', 'Ejido Los Arados Mam', NULL, 25.8687895, -97.5575422, 8),
(530, 3, '50EBA', 'El Ebanito Maf', NULL, 25.9388959, -97.6200036, 8),
(531, 3, '50EKE', 'Del Valle Maf', NULL, 25.8733763, -97.5515080, 8),
(532, 3, '50IBF', 'Uniones Maf', NULL, 25.8870743, -97.5441336, 8),
(533, 3, '50SFY', 'Sendero Ii Maf', NULL, 25.8656493, -97.5721929, 8),
(534, 3, '50SYN', 'Sendero I Maf', NULL, 25.8702000, -97.5521000, 8),
(535, 3, '50UJD', 'Industrial Maf', NULL, 25.8777303, -97.5284113, 8),
(536, 3, '50XLF', 'Las Fuentes Maf', NULL, 25.8890703, -97.5389486, 8),
(537, 3, '50GGG', 'Brisas Del Valle Maf', NULL, 25.8509000, -97.5621000, 8),
(538, 3, '506GX', 'Diego Rivera Maf', NULL, 25.8233074, -97.4943356, 8),
(539, 3, '50CAU', 'Curacao Maf', NULL, 25.8240191, -97.5060443, 8),
(540, 3, '50GPL', 'Nicolas Guerra Maf', NULL, 25.8400810, -97.5011867, 8),
(541, 3, '50II0', 'Jardines De San Juan Maf', NULL, 25.8259000, -97.4829000, 8),
(542, 3, '50IUX', 'El Saucito Mam', NULL, 25.8377802, -97.4891973, 8),
(543, 3, '50JD7', 'Expofiesta Oriente Maf', NULL, 25.8279375, -97.5050085, 8),
(544, 3, '50JZD', 'Juarez Ii Maf', NULL, 25.8421445, -97.4901690, 8),
(545, 3, '50JZH', 'San Miguel Maf', NULL, 25.8288729, -97.4814801, 8),
(546, 3, '50LTR', 'Las Torres Maf', NULL, 25.8215993, -97.5053833, 8),
(547, 3, '50MJN', 'San Juan Maf', NULL, 25.8241887, -97.4900686, 8),
(548, 3, '50NDN', 'Del Nino Maf', NULL, 25.8363471, -97.4979733, 8),
(549, 3, '50SFN', 'San Fernando Maf', NULL, 25.8336445, -97.4990391, 8),
(550, 3, '50SJP', 'Solidaridad Ii Maf', NULL, 25.8453096, -97.4921666, 8),
(551, 3, '50SLD', 'Solidaridad Maf', NULL, 25.8484100, -97.4832260, 8),
(552, 3, '50UBJ', 'Benito Juarez Maf', NULL, 25.8290533, -97.4878264, 8),
(553, 3, '50UXJ', 'Lomas De San Juan Maf', NULL, 25.8301144, -97.4875399, 8),
(554, 3, '50VVN', '20 Noviembre Maf', NULL, 25.8347045, -97.5031990, 8),
(555, 3, '50WPR', 'Tampico Maf', NULL, 25.8443396, -97.4966665, 8),
(556, 3, '50F4I', 'Nogalar Maf', NULL, 25.8401416, -97.4818287, 8),
(557, 3, '5003W', 'Tercera Maf', NULL, 25.8494224, -97.5008485, 8),
(558, 3, '50KJB', 'Satelite Maf', NULL, 25.8506462, -97.4960710, 8),
(559, 3, '50UML', 'Longoria Maf', NULL, 25.8478360, -97.5011535, 8),
(560, 3, '5055Q', 'Benjamin Gaona Maf', NULL, 25.8062911, -97.5102308, 8),
(561, 3, '50OSU', 'Suriname Maf', NULL, 25.8172354, -97.5052259, 8),
(562, 3, '50ZMI', 'La Amistad Maf', NULL, 25.8117633, -97.5102396, 8),
(563, 3, '50Z8B', 'Tianguis del Niño Maf', NULL, 25.8370527, -97.4954504, 8),
(564, 3, '508DX', 'Electricistas Maf', NULL, 25.8268948, -97.5006529, 8),
(565, 3, '50182', 'Guadalupe Mainero Maf', NULL, 25.8529509, -97.4641130, 7),
(566, 3, '50ABD', 'Arboledas Maf', NULL, 25.8674941, -97.4720013, 7),
(567, 3, '50AQ1', 'Mainero Maf', NULL, 25.8542219, -97.4624098, 7),
(568, 3, '50AXJ', 'Alianza Maf', NULL, 25.8658145, -97.4885009, 7),
(569, 3, '50CRE', 'Central Maf', NULL, 25.8709207, -97.4993979, 7),
(570, 3, '50IDZ', 'Alvaro Obregon Maf', NULL, 25.8904414, -97.5026883, 7),
(571, 3, '50JS8', 'El Laguito Maf', NULL, 25.8650948, -97.4945081, 7),
(572, 3, '50MPO', 'Ocampo Maf', NULL, 25.8714000, -97.4943731, 7),
(573, 3, '50NSF', 'Pumarejo Maf', NULL, 25.8697065, -97.4958859, 7),
(574, 3, '50OEM', 'El Moro Maf', NULL, 25.8785551, -97.4995021, 7),
(575, 3, '50P8O', 'Del Cambio Maf', NULL, 25.8648706, -97.4845455, 7),
(576, 3, '50PMA', 'Primera Maf', NULL, 25.8813878, -97.4991900, 7),
(577, 3, '50SQC', 'San Carlos Maf', NULL, 25.8677649, -97.4853679, 7),
(578, 3, '50UDN', 'Division Del Norte Maf', NULL, 25.8628409, -97.4718844, 7),
(579, 3, '50UGO', 'Gobernacion Maf', NULL, 25.8708876, -97.4866349, 7),
(580, 3, '50UOJ', 'Jardin Maf', NULL, 25.8936686, -97.4991829, 7),
(581, 3, '50UOL', 'Lauro Villar Maf', NULL, 25.8625951, -97.4784878, 7),
(582, 3, '50UOR', 'Rio Maf', NULL, 25.8788409, -97.4939856, 7),
(583, 3, '50URR', 'Arrese Maf', NULL, 25.8716804, -97.4898841, 7),
(584, 3, '50VPA', 'Las Palmas Maf', NULL, 25.8686324, -97.4812072, 7),
(585, 3, '50XWF', 'Imss Maf', NULL, 25.8608945, -97.4750444, 7),
(586, 3, '50A2A', 'Molino Del Rey 2 Maf', NULL, 25.8501000, -97.5865000, 7),
(587, 3, '50A4S', 'Arecas Maf', NULL, 25.8436912, -97.5930637, 7),
(588, 3, '50JZK', 'Molino Del Rey Maf', NULL, 25.8459892, -97.5849913, 7),
(589, 3, '50WPP', 'Pueblitos Maf', NULL, 25.8224103, -97.5813562, 7),
(590, 3, '50T5W', 'Washington Maf', NULL, 25.8746620, -97.4959934, 7),
(591, 3, '506ZG', 'Nuevo Milenio Maf', NULL, 25.8555361, -97.5424015, 8),
(592, 3, '509CO', 'Porfirio Diaz Maf', NULL, 25.8670000, -97.5278000, 8),
(593, 3, '50ACI', 'Acuario Maf', NULL, 25.8625932, -97.5288622, 8),
(594, 3, '50COW', 'Constituyentes Maf', NULL, 25.8626046, -97.5421662, 8),
(595, 3, '50CSA', 'Casa Blanca Maf', NULL, 25.8613689, -97.5408033, 8),
(596, 3, '50DQY', 'Mexicali Maf', NULL, 25.8497545, -97.5205418, 8),
(597, 3, '50F3Z', 'Rigo Tovar Maf', NULL, 25.8748053, -97.5307469, 8),
(598, 3, '50JYE', 'Villa Azteca Maf', NULL, 25.8453120, -97.5210421, 8),
(599, 3, '50LXF', 'Leyes De Reforma Maf', NULL, 25.8624528, -97.5233816, 8),
(600, 3, '50MFU', 'Magisterio Maf', NULL, 25.8568356, -97.5302717, 8),
(601, 3, '50MYV', '12 De Marzo Maf', NULL, 25.8699663, -97.5373839, 8),
(602, 3, '50OEG', 'Egipto Maf', NULL, 25.8642923, -97.5447542, 8),
(603, 3, '50QEN', 'Quinta Real Maf', NULL, 25.8584111, -97.5305841, 8),
(604, 3, '50SFW', 'San Felipe Maf', NULL, 25.8512689, -97.5399623, 8),
(605, 3, '50UOP', 'Puerto Rico Maf', NULL, 25.8523929, -97.5285052, 8),
(606, 3, '50URF', 'San Rafael Maf', NULL, 25.8697046, -97.5228201, 8),
(607, 3, '50VWR', 'Valle Real Maf', NULL, 25.8530343, -97.5335801, 8),
(608, 3, '50ZDK', 'Quinta Real 2 Maf', NULL, 25.8585590, -97.5354061, 8),
(609, 3, '505YL', 'Crucero Sendero Maf', NULL, 25.8708135, -97.5492455, 8),
(610, 3, '50MSI', 'Santa Anita Maf', NULL, 25.8453974, -97.5350302, 8),
(611, 3, '50QQM', 'Bagdad Maf', NULL, 25.8418613, -97.5276694, 8),
(612, 3, '50X92', 'Puerto Rico 2 Maf', NULL, 25.8502849, -97.5319727, 8),
(613, 3, '501DD', 'Bella Vista Sur', NULL, 24.8423235, -98.1459179, 8),
(614, 3, '503D1', 'Libramiento San Fer Maf', NULL, 24.8468955, -98.1082557, 8),
(615, 3, '505DK', 'Loma Alta Maf', NULL, 24.8556414, -98.1573260, 8),
(616, 3, '505UT', 'Las Yescas Maf', NULL, 25.6120010, -97.8165991, 8),
(617, 3, '506VP', 'Padre Mier Maf', NULL, 24.8502398, -98.1457973, 8),
(618, 3, '50E7Y', 'Ignacio Allende Maf', NULL, 24.8416660, -98.1321530, 8),
(619, 3, '50I03', 'San German Maf', NULL, 25.2048404, -97.9332291, 8),
(620, 3, '50JZN', 'Moquetito Maf', NULL, 25.5147110, -97.7329200, 8),
(621, 3, '50MWZ', 'Bella Vista Maf', NULL, 24.8444524, -98.1388264, 8),
(622, 3, '50NRW', 'Las Norias Maf', NULL, 24.6994295, -98.2632958, 8),
(623, 3, '50NXE', 'Ruiz Cortinez Maf', NULL, 24.8508859, -98.1534792, 8),
(624, 3, '50QKJ', 'Plaza Maf', NULL, 24.8475708, -98.1601975, 8),
(625, 3, '50QW7', 'Carretera San Fernando Maf', NULL, 24.8593651, -98.1418532, 8),
(626, 3, '50RJO', 'Rancho Viejo Maf', NULL, 25.0661093, -98.0700480, 8),
(627, 3, '50RYI', '2Do Centenario Maf', NULL, 24.8527741, -98.1556531, 8),
(628, 3, '50TUU', 'Ruiz Cortinez Ii Maf', NULL, 24.8477478, -98.1540175, 8),
(629, 3, '50XLJ', 'Loma Colorada Maf', NULL, 24.8380492, -98.1237541, 8),
(630, 3, '50YNO', 'Pino Suarez Maf', NULL, 24.8474780, -98.1516211, 8),
(631, 3, '506BY', 'Ignacio Allende 2 Maf', NULL, 24.8467618, -98.1462925, 8),
(632, 3, '50R1K', 'Juan de la Barrera Maf', NULL, 24.8371240, -98.1463320, 8),
(633, 3, '5017X', 'Las Culturas Maf', NULL, 25.8439650, -97.4554360, 7),
(634, 3, '503JN', 'Vancouver Maf', NULL, 25.8298000, -97.4248000, 7),
(635, 3, '50DCW', 'Canada Maf', NULL, 25.8340244, -97.4231346, 7),
(636, 3, '50ESC', 'Escandon Maf', NULL, 25.8369178, -97.4369488, 7),
(637, 3, '50EUQ', 'Campestre Del Lago Maf', NULL, 25.8427663, -97.4500007, 7),
(638, 3, '50FIW', 'Finsa Maf', NULL, 25.8359302, -97.4299393, 7),
(639, 3, '50FUI', 'Fue. Industrialesmaf', NULL, 25.8377665, -97.4433630, 7),
(640, 3, '50GV9', 'Ciudad Industrial Maf', NULL, 25.8392000, -97.4307000, 7),
(641, 3, '50IFX', 'Palmas De Mar Maf', NULL, 25.8228378, -97.4576106, 7),
(642, 3, '50MHX', 'Camino Real Maf', NULL, 25.8429566, -97.4580562, 7),
(643, 3, '50NU8', 'Fundadores Maf', NULL, 25.8351632, -97.4521290, 7),
(644, 3, '50SJW', 'San Jeronimo 2 Maf', NULL, 25.8286417, -97.4368724, 7),
(645, 3, '50SJX', 'San Jeronimo Maf', NULL, 25.8281214, -97.4344354, 7),
(646, 3, '50TEH', 'Teotihuacan Maf', NULL, 25.8307640, -97.4526476, 7),
(647, 3, '50UOY', 'Playa Maf', NULL, 25.8467902, -97.4570711, 7),
(648, 3, '50UPV', 'Palo Verde Maf', NULL, 25.8411107, -97.4577804, 7),
(649, 3, '50XCI', 'La Cima Maf', NULL, 25.8332368, -97.4445116, 7),
(650, 3, '50XGZ', 'Magnolias Maf', NULL, 25.8496751, -97.4596750, 7),
(651, 3, '50YDK', 'Longoreno Maf', NULL, 25.8325859, -97.3735251, 7),
(652, 3, '50Z6S', 'Taxquena Maf', NULL, 25.8285000, -97.4575000, 7),
(653, 3, '50AZP', 'Realdelas Palmas Maf', NULL, 25.8417824, -97.5542694, 7),
(654, 3, '50G9W', 'Palmares Norte Maf', NULL, 25.8481334, -97.5539455, 7),
(655, 3, '50J3M', 'Tianguis Palmares Maf', NULL, 25.8369900, -97.5430900, 7),
(656, 3, '50LXP', 'Los Palmares Maf', NULL, 25.8398115, -97.5478595, 7),
(657, 3, '50NE6', 'Paseo De Los Palmares Maf', NULL, 25.8435188, -97.5595464, 7),
(658, 3, '508QZ', 'Popular Maf', NULL, 25.8511393, -97.4800842, 7),
(659, 3, '50YXC', 'Mexico Maf', NULL, 25.8349909, -97.4618113, 7),
(660, 3, '50AIE', 'Emiliano Zapata Maf', NULL, 25.8439873, -97.4802976, 7),
(661, 3, '50AKV', 'Accion Civica Maf', NULL, 25.8630614, -97.4805664, 7),
(662, 3, '50AWC', 'Avellano Maf', NULL, 25.8574158, -97.4896494, 7),
(663, 3, '50D8C', 'Alamo Maf', NULL, 25.8341900, -97.4877200, 7),
(664, 3, '50EUV', 'Trevino Zapata Maf', NULL, 25.8618212, -97.4806404, 7),
(665, 3, '50KXW', 'Paraiso Maf', NULL, 25.8427154, -97.4705126, 7),
(666, 3, '50M5R', 'Republica de Argentina Maf', NULL, 25.8371594, -97.4657845, 7),
(667, 3, '50MCF', 'Cantinflas Maf', NULL, 25.8599725, -97.4766154, 7),
(668, 3, '50MPP', 'Durazno Maf', NULL, 25.8360098, -97.4703018, 7),
(669, 3, '50O0A', 'Oceano Pacifico Sur Maf', NULL, 25.8349092, -97.4828988, 7),
(670, 3, '50OBR', 'Valle Verde Maf', NULL, 25.8383686, -97.4780041, 7),
(671, 3, '50OL6', 'El Porvenir Maf', NULL, 25.8282000, -97.4755000, 7),
(672, 3, '50Q0N', 'Jilgueros Maf', NULL, 25.8347130, -97.4777331, 7),
(673, 3, '50SOY', 'Playa Sol Maf', NULL, 25.8539599, -97.4750105, 7),
(674, 3, '50UOG', 'Guerra I Maf', NULL, 25.8608408, -97.4865622, 7),
(675, 3, '50UTR', 'Tarahumara Maf', NULL, 25.8485350, -97.4645443, 7),
(676, 3, '50WK3', 'Democracia Social Maf', NULL, 25.8448867, -97.4759711, 7),
(677, 3, '50XTF', 'Roberto Guerra Maf', NULL, 25.8570171, -97.4792955, 7),
(678, 3, '50YZT', 'Playa Hornos Maf', NULL, 25.8475690, -97.4714162, 7),
(679, 3, '507UH', 'Las Palmitas Maf', NULL, 25.8310511, -97.4690937, 7),
(680, 3, '50B9K', 'Portillo Maf', NULL, 25.8718610, -97.5423800, 7),
(681, 3, '501XK', 'Mariano Matamoros Maf', NULL, 25.8535838, -97.5171595, 7),
(682, 3, '50AWW', 'Espana Maf', NULL, 25.8625345, -97.5099804, 7),
(683, 3, '50DQE', '18 Y Espana Maf', NULL, 25.8627532, -97.5152991, 7),
(684, 3, '50DQQ', 'El Roble Maf', NULL, 25.8568059, -97.5131070, 7),
(685, 3, '50FD8', 'Carlos Salazar Maf', NULL, 25.8522982, -97.5099409, 7),
(686, 3, '50FYT', 'Nafarrete Maf', NULL, 25.8589764, -97.5042257, 7),
(687, 3, '50LPW', 'La Salle Maf', NULL, 25.8589181, -97.5074387, 7),
(688, 3, '50LTO', 'Valle Alto Maf', NULL, 25.8481306, -97.5121054, 7),
(689, 3, '50Q9L', 'Carmen Serdan Maf', NULL, 25.8533637, -97.4886076, 7),
(690, 3, '50QLC', 'Tlaxcala Maf', NULL, 25.8543441, -97.5011843, 7),
(691, 3, '50RUO', 'La Aurora Maf', NULL, 25.8593638, -97.5009491, 7),
(692, 3, '50SQW', 'Solernau Maf', NULL, 25.8594860, -97.4949175, 7),
(693, 3, '50UVI', 'Vizcaya Maf', NULL, 25.8649723, -97.4985405, 7),
(694, 3, '50XAD', 'Paseoresidencial Maf', NULL, 25.8587621, -97.5165153, 7),
(695, 3, '50XER', 'Periferico Maf', NULL, 25.8566109, -97.4918255, 7),
(696, 3, '50XYV', 'Virgo Maf', NULL, 25.8506182, -97.4915524, 7),
(697, 3, '50Y52', 'Mediterraneo Maf', NULL, 25.8573751, -97.4853534, 7),
(698, 3, '501UA', 'Seccion 16 Maf', NULL, 25.8343732, -97.5194861, 7),
(699, 3, '50DQG', 'Agapito Gonzalez Maf', NULL, 25.8340752, -97.5178910, 7),
(700, 3, '50FVN', 'Valle Dorado Maf', NULL, 25.8395689, -97.5127494, 7),
(701, 3, '50I79', 'Pedro Cardenas Maf', NULL, 25.8403510, -97.5082130, 8),
(702, 3, '50QFA', 'Santa Cecilia Maf', NULL, 25.8349051, -97.5100059, 8),
(703, 3, '50UGI', 'Gimnasio Maf', NULL, 25.8257958, -97.5130588, 8),
(704, 3, '50UMN', 'Mundo Nuevo Maf', NULL, 25.8286254, -97.5117446, 8),
(705, 3, '50UMZ', 'Mezquital Maf', NULL, 25.7159462, -97.5718876, 8),
(706, 3, '50UOO', 'Rago Maf', NULL, 25.8440681, -97.5077475, 8),
(707, 3, '50WQX', 'Portes Gil  Maf', NULL, 25.8109229, -97.5192036, 8),
(708, 3, '50YJL', 'Misiones Maf', NULL, 25.8263878, -97.5416951, 8),
(709, 3, '5006I', 'El Galañero Maf', NULL, 25.7603619, -97.5451446, 8),
(710, 3, '50AWU', 'Expo Fiesta Maf', NULL, 25.8267698, -97.5200817, 8),
(711, 3, '50BAG', 'Voluntad y trabajo Mam', NULL, 25.8328641, -97.5282452, 8),
(712, 3, '50FBV', 'Fco I. Madero Maf', NULL, 25.8253578, -97.5268295, 8),
(713, 3, '50HGQ', 'Marte R. Gomez Maf', NULL, 25.8275596, -97.5315278, 8),
(714, 3, '50IG0', 'Joaquin Pardave Maf', NULL, 25.8238242, -97.5233188, 8),
(715, 3, '50UMA', 'Aeropuerto Maf', NULL, 25.7822485, -97.5329835, 8),
(716, 3, '50UOZ', 'Ragoz Maf', NULL, 25.8184326, -97.5172559, 8),
(717, 3, '50XRT', 'Las Flores Maf', NULL, 25.8157007, -97.5237789, 8);
INSERT INTO `tienda` (`id`, `plaza_id`, `codigo`, `nombre`, `direccion`, `latitud`, `longitud`, `asesor_ti_usuario_id`) VALUES
(718, 3, '5025A', 'Virgilio Garza Maf', NULL, 25.8331990, -97.5348540, 8),
(719, 3, '50MJW', 'Misiones 2 Maf', NULL, 25.8264500, -97.5562400, 8),
(720, 3, '50WHT', 'Martha Rita Maf', NULL, 25.8200437, -97.5471306, 8),
(721, 3, '50YJG', '12 De Marzo 2 Maf', NULL, 25.8271638, -97.5457944, 8),
(722, 3, '50C4W', 'Aguas Subterraneas Maf', NULL, 25.8438830, -97.5047266, 8),
(723, 4, '506NE', 'Centenario Tam', NULL, 22.2400918, -97.8414735, 9),
(724, 4, '50APH', 'Imss Tam', NULL, 22.2474150, -97.8548590, 9),
(725, 4, '50CHT', 'Cuauhtemoc Tam', NULL, 22.2402554, -97.8636529, 9),
(726, 4, '50FGM', 'Frente Democratico Tam', NULL, 22.2446823, -97.8579525, 9),
(727, 4, '50HVH', 'Santo Nino Tam', NULL, 22.2453497, -97.8551292, 9),
(728, 4, '50QVW', 'Colonias Tam', NULL, 22.2453815, -97.8636680, 9),
(729, 4, '50SIT', 'Central Tam', NULL, 22.2494830, -97.8579179, 9),
(730, 4, '50TRO', 'Rosalio Tam', NULL, 22.2521513, -97.8580968, 9),
(731, 4, '50VR7', 'Zapotal Tam', NULL, 22.2500977, -97.8551043, 9),
(732, 4, '50NPC', 'Servando Canales Tam', NULL, 22.2461520, -97.8324409, 9),
(733, 4, '5072G', 'Victoria Tam', NULL, 22.2244000, -97.8493000, 9),
(734, 4, '50AJH', 'Alameda Tam', NULL, 22.2241485, -97.8445084, 9),
(735, 4, '50ASA', 'Tula Tam', NULL, 22.2296865, -97.8419041, 9),
(736, 4, '50AYN', 'Ayuntamiento Tam', NULL, 22.2319097, -97.8647640, 9),
(737, 4, '50FH3', 'Simon Bolivar', NULL, 22.2325060, -97.8401124, 9),
(738, 4, '50GKT', 'Canseco Tam', NULL, 22.2235566, -97.8585613, 9),
(739, 4, '50IMW', 'Volantin Tam', NULL, 22.2254171, -97.8629873, 9),
(740, 4, '50VJP', 'Torreon Tam', NULL, 22.2266188, -97.8648780, 9),
(741, 4, '50Y5Z', 'Tampico Tam', NULL, 22.2278275, -97.8462807, 9),
(742, 4, '50YE9', 'Bella Vista Tam', NULL, 22.2234953, -97.8649930, 9),
(743, 4, '50ZMW', 'Metropolitano Tam', NULL, 22.2293202, -97.8595322, 9),
(744, 4, '5023U', 'Reforma Tam', NULL, 22.2427848, -97.8504512, 9),
(745, 4, '50BTC', 'Tolteca Tam', NULL, 22.2361586, -97.8602541, 9),
(746, 4, '50ENP', 'Rio Verde Tam', NULL, 22.2376532, -97.8510503, 9),
(747, 4, '50YZD', 'Obrera Tam', NULL, 22.2391905, -97.8471824, 9),
(748, 4, '50ELO', 'Leones Tam', NULL, 22.2573028, -97.8570304, 9),
(749, 4, '50GIC', 'Tecnologico Tam', NULL, 22.2540458, -97.8489830, 9),
(750, 4, '50OGO', 'Obregon Tam', NULL, 22.2440902, -97.8388836, 9),
(751, 4, '50PHW', 'Pachuca Tam', NULL, 22.2465428, -97.8454973, 9),
(752, 4, '50WPU', 'Libertad Tam', NULL, 22.2402058, -97.8400217, 9),
(753, 4, '50WXC', 'Canaco Tam', NULL, 22.2428598, -97.8363107, 9),
(754, 4, '50ZJG', 'Los Mangos Tam', NULL, 22.2551655, -97.8508869, 9),
(755, 4, '505P0', 'Mercado Madero Tam', NULL, 22.2490590, -97.8354170, 9),
(756, 4, '509SJ', 'Via Monterrey', NULL, 22.2504311, -97.8353690, 9),
(757, 4, '50A5F', 'Talleres Tam', NULL, 22.2505723, -97.8272410, 9),
(758, 4, '50ARV', 'Quintero Tam', NULL, 22.2612383, -97.8288592, 9),
(759, 4, '50CMW', 'Civil Madero Tam', NULL, 22.2540943, -97.8216828, 9),
(760, 4, '50DAY', '5 De Mayo Tam', NULL, 22.2486941, -97.8389068, 9),
(761, 4, '50FIJ', 'Fraile Tam', NULL, 22.2451753, -97.8359134, 9),
(762, 4, '50GTE', 'Guatemala Tam', NULL, 22.2530627, -97.8307192, 9),
(763, 4, '50IKY', 'Leon Tam', NULL, 22.2554012, -97.8286744, 9),
(764, 4, '50POM', '1o. De Mayo Tam', NULL, 22.2512252, -97.8436461, 9),
(765, 4, '50SBA', 'Sarabia Tam', NULL, 22.2516958, -97.8474805, 9),
(766, 4, '50TJB', 'Auditorio Tam', NULL, 22.2472613, -97.8398047, 9),
(767, 4, '50XTN', 'Necaxa Tam', NULL, 22.2516404, -97.8404364, 9),
(768, 4, '50ZAX', 'Plaza Madero Tam', NULL, 22.2480483, -97.8374402, 9),
(769, 4, '50DM3', 'Gas Sunoco Tam', NULL, 22.2770598, -97.8416000, 9),
(770, 4, '50HHJ', 'Ocampo Tam', NULL, 22.2770121, -97.8395734, 9),
(771, 4, '50IQD', 'Insurgentes Tam', NULL, 22.2754165, -97.8416257, 9),
(772, 4, '50RIM', 'Miramar Tam', NULL, 22.2767315, -97.8413156, 9),
(773, 4, '50Y6Q', 'Brasil Maf', NULL, 22.2527230, -97.8366264, 9),
(774, 4, '50EM0', 'Kehoe Tam', NULL, 22.2867000, -97.8363000, 9),
(775, 4, '50YPV', 'Sahop Tam', NULL, 22.2814155, -97.8291812, 9),
(776, 4, '508CF', 'Pakistan Tam', NULL, 22.3212131, -97.8553975, 9),
(777, 4, '50HHP', 'Palafox Tam', NULL, 22.3005726, -97.8441667, 9),
(778, 4, '50I2V', 'Calle 7 Tam', NULL, 22.3036250, -97.8511802, 9),
(779, 4, '50IKW', 'Borreguera Tam', NULL, 22.3164998, -97.8592618, 9),
(780, 4, '50RAK', 'Cardenas Tam', NULL, 22.3082190, -97.8494846, 9),
(781, 4, '50TA9', 'Australia Tam', NULL, 22.3115968, -97.8534100, 9),
(782, 4, '50TH1', 'Juan Pablo II Tam', NULL, 22.3078944, -97.8560616, 9),
(783, 4, '502MB', 'Burgos Tam', NULL, 22.2942939, -97.8514960, 9),
(784, 4, '502YC', 'Nicolas Bravo Tam', NULL, 22.2863517, -97.8480276, 9),
(785, 4, '504EC', 'Bujanos Tam', NULL, 22.2948053, -97.8454010, 9),
(786, 4, '504KG', 'Las Chacas Tam', NULL, 22.2921267, -97.8529840, 9),
(787, 4, '50IKH', 'Lopez Portillo Tam', NULL, 22.2944825, -97.8555456, 9),
(788, 4, '50LUL', 'Luna Luna Tam', NULL, 22.2901142, -97.8514780, 9),
(789, 4, '50N7U', 'Lucio Blanco Tam', NULL, 22.2648740, -97.8340256, 9),
(790, 4, '50OOT', 'Ampliacion II Tam', NULL, 22.2850275, -97.8437371, 9),
(791, 4, '50THR', 'Pescador Tam', NULL, 22.2662122, -97.8369960, 9),
(792, 4, '50WYR', 'Flores Tam', NULL, 22.2906205, -97.8466264, 9),
(793, 4, '50YNB', 'Campo Faja De Oro Tam', NULL, 22.2989154, -97.8487652, 9),
(794, 4, '501O5', 'La Perla Maf', NULL, 22.3147055, -97.8517789, 9),
(795, 4, '5071I', 'Isauro Alfaro Tam', NULL, 22.2136982, -97.8541681, 6),
(796, 4, '50AMH', 'Isleta Tam', NULL, 22.2122033, -97.8485310, 6),
(797, 4, '50EDK', 'Carranza Tam', NULL, 22.2152704, -97.8557611, 6),
(798, 4, '50ENT', 'Centro Tam', NULL, 22.2152705, -97.8580172, 6),
(799, 4, '50FV5', 'General San Martin Maf', NULL, 22.2142575, -97.8519707, 6),
(800, 4, '50JWE', 'Zona Centro Tam', NULL, 22.2173204, -97.8581850, 6),
(801, 4, '50KFI', 'Fiscal Tam', NULL, 22.2141516, -97.8529402, 6),
(802, 4, '50LAO', 'Centralita Tam', NULL, 22.2150503, -97.8596108, 6),
(803, 4, '50N4D', 'Pedro J. Mendez Tam', NULL, 22.2133113, -97.8584535, 6),
(804, 4, '50O4H', 'Aquiles Tam', NULL, 22.2155732, -97.8520500, 6),
(805, 4, '50OPW', 'El Chorro Tam', NULL, 22.2146085, -97.8609575, 6),
(806, 4, '50TTI', 'Imperial Tam', NULL, 22.2147545, -97.8545713, 6),
(807, 4, '50VWI', 'Diaz Miron Tam', NULL, 22.2141740, -97.8555030, 6),
(808, 4, '50XOP', 'Plaza Tam', NULL, 22.2128017, -97.8567828, 6),
(809, 4, '50CYH', '2 De Enero Tam', NULL, 22.2150315, -97.8482360, 6),
(810, 4, '50ESN', 'Escandon Tam', NULL, 22.2196332, -97.8542045, 6),
(811, 4, '50JKJ', 'Mainero Tam', NULL, 22.2195772, -97.8495296, 6),
(812, 4, '50OOG', 'Golfo Tam', NULL, 22.2204626, -97.8489580, 6),
(813, 4, '50SIW', 'Nautica Tam', NULL, 22.2212619, -97.8508814, 6),
(814, 4, '50UUV', 'Plaza Golfo Tam', NULL, 22.2179618, -97.8434409, 6),
(815, 4, '5058M', 'Alvaro Tam', NULL, 22.2617348, -97.8209610, 9),
(816, 4, '505Y9', 'Corredor Urbano Tam', NULL, 22.2888478, -97.8140310, 9),
(817, 4, '508R4', 'Jimenez Tam', NULL, 22.2753000, -97.8338000, 9),
(818, 4, '50AEV', 'Maeva Tam', NULL, 22.2868238, -97.8032004, 9),
(819, 4, '50BEH', 'Real Del Mar Tam', NULL, 22.2906150, -97.8095636, 9),
(820, 4, '50EZL', 'Escolleras Tam', NULL, 22.2640751, -97.7866984, 9),
(821, 4, '50GZP', 'Nardos Tam', NULL, 22.2917995, -97.8368210, 9),
(822, 4, '50HGM', 'Calle 15 Tam', NULL, 22.2824745, -97.8348060, 9),
(823, 4, '50HOO', 'Hipodromo Tam', NULL, 22.2653073, -97.8246947, 9),
(824, 4, '50ILQ', 'Barriles Tam', NULL, 22.2726202, -97.8040488, 9),
(825, 4, '50LGS', '8 Leguas Tam', NULL, 22.2768057, -97.8366885, 9),
(826, 4, '50MAK', 'Miramapolis Tam', NULL, 22.2935145, -97.8201829, 9),
(827, 4, '50MWF', 'Mirador Tam', NULL, 22.2698285, -97.7903473, 9),
(828, 4, '50OUC', 'Tercera Avenida Tam', NULL, 22.2855971, -97.8215139, 9),
(829, 4, '50QX2', 'Francisco Villa Tam', NULL, 22.2726238, -97.8275480, 9),
(830, 4, '50RFI', 'Refineria Tam', NULL, 22.2583543, -97.8244919, 9),
(831, 4, '50RTV', 'Recreativo II Tam', NULL, 22.3017841, -97.8166165, 9),
(832, 4, '50RXA', 'Mar Tam', NULL, 22.2797729, -97.7977554, 9),
(833, 4, '50SRW', 'Sirenas Tam', NULL, 22.2753292, -97.7943555, 9),
(834, 4, '50V3Z', 'Vela Maria Tam', NULL, 22.3037888, -97.8178030, 9),
(835, 4, '50BXI', 'Arenal Tam', NULL, 22.2928132, -97.8773818, 9),
(836, 4, '505ER', 'Torres Norte Tam', NULL, 22.3178000, -97.8764000, 9),
(837, 4, '50AIC', 'Revolucion Verde Tam', NULL, 22.3176917, -97.8513481, 9),
(838, 4, '50BKE', 'Del Bosque Tam', NULL, 22.3233451, -97.8762085, 9),
(839, 4, '50DNY', 'Germinal Tam', NULL, 22.3048498, -97.8584468, 9),
(840, 4, '50DOQ', 'Pino Tam', NULL, 22.3142830, -97.8790809, 9),
(841, 4, '50GVI', 'Villahermosa Tam', NULL, 22.3216790, -97.8696383, 9),
(842, 4, '50JFT', 'Josefa Ortiz Tam', NULL, 22.3106220, -97.8690780, 9),
(843, 4, '50KGA', 'Del Valle Tam', NULL, 22.3073675, -97.8631365, 9),
(844, 4, '50KSX', 'Sexta Avenida Tam', NULL, 22.3072336, -97.8688548, 9),
(845, 4, '50QOU', 'Nuevo Progreso Tam', NULL, 22.3109750, -97.8737853, 9),
(846, 4, '50RZR', 'Laguna Puerta Tam', NULL, 22.3170829, -97.8714447, 9),
(847, 4, '50SKS', 'Enrique Cardenas Tam', NULL, 22.3002373, -97.8555718, 9),
(848, 4, '50TMC', 'Curva Texas Tam', NULL, 22.3097673, -97.8807121, 9),
(849, 4, '50K0A', 'Bravos Maf', NULL, 22.3071407, -97.8737006, 9),
(850, 4, '505MR', 'Colegio Militar Tam', NULL, 22.3188924, -97.8869930, 9),
(851, 4, '505XR', 'Chairel Tam', NULL, 22.2987000, -97.8985120, 9),
(852, 4, '50893', 'Petroquimicas Tam', NULL, 22.3093200, -97.8907200, 9),
(853, 4, '50AER', 'Las Americas Tam', NULL, 22.3086400, -97.8812706, 9),
(854, 4, '50BMS', 'Colombia Tam', NULL, 22.3072687, -97.8858332, 9),
(855, 4, '50CWO', 'Chapultepec Tam', NULL, 22.3228792, -97.8827834, 9),
(856, 4, '50HX3', 'Colosio II', NULL, 22.3232773, -97.8957475, 9),
(857, 4, '50IJL', 'Campanula Tam', NULL, 22.3164763, -97.8906667, 9),
(858, 4, '50IKL', 'La Paz Ii Tam', NULL, 22.3044610, -97.8931802, 9),
(859, 4, '50SHY', 'Champayan Tam', NULL, 22.2996490, -97.8938403, 9),
(860, 4, '50TFX', 'Las Torres Tam', NULL, 22.3099869, -97.8876883, 9),
(861, 4, '50ULW', 'Colosio Tam', NULL, 22.3197576, -97.8957361, 9),
(862, 4, '50XO5', 'Canada Tam', NULL, 22.3121000, -97.8822000, 9),
(863, 4, '50XPT', 'San Pedro Tam', NULL, 22.3013441, -97.8795111, 9),
(864, 4, '50YOK', 'Haiti Tam', NULL, 22.3162052, -97.8825028, 9),
(865, 4, '50WU4', 'Jaibos Maf', NULL, 22.3079596, -97.8790628, 9),
(866, 4, '507HY', 'El Navegante Maf', NULL, 22.3026576, -97.8638509, 9),
(867, 4, '502UP', 'San Antonio Tam', NULL, 22.2991778, -97.8862780, 6),
(868, 4, '50AET', 'Aeropuerto Tam', NULL, 22.2870284, -97.8691055, 6),
(869, 4, '50TNK', 'Tancol Tam', NULL, 22.2951037, -97.8816731, 6),
(870, 4, '50TXP', 'Palmas Tam', NULL, 22.2866126, -97.8730844, 6),
(871, 4, '501LU', 'Sierra Morena Tam', NULL, 22.2529434, -97.8756376, 6),
(872, 4, '503HL', 'Hydros Tam', NULL, 22.2905255, -97.8942460, 6),
(873, 4, '506OI', 'Vista Bella Tam', NULL, 22.2869255, -97.8907975, 6),
(874, 4, '50AGW', 'Agua Dulce Tam', NULL, 22.2571835, -97.8740895, 6),
(875, 4, '50BTM', 'Bolitam Tam', NULL, 22.2765973, -97.8775987, 6),
(876, 4, '50GRD', 'San Gerardo Tam', NULL, 22.2862714, -97.8872238, 6),
(877, 4, '50H7X', 'Flamingo Maf', NULL, 22.2490269, -97.8743295, 6),
(878, 4, '50ITV', 'Infonavit Tam', NULL, 22.2835539, -97.8829214, 6),
(879, 4, '50KCH', 'Charro Tam', NULL, 22.2710456, -97.8754042, 6),
(880, 4, '50LLR', 'Lomas Tam', NULL, 22.2677664, -97.8648438, 6),
(881, 4, '50N0O', 'Jaguar Tam', NULL, 22.2822140, -97.8726301, 6),
(882, 4, '50OFO', 'Faja De Oro Tam', NULL, 22.2567939, -97.8682677, 6),
(883, 4, '50OFY', 'Flamboyanes Tam', NULL, 22.2752003, -97.8732835, 6),
(884, 4, '50RAE', 'Rosales Tam', NULL, 22.2653271, -97.8728566, 6),
(885, 4, '50SFV', 'Wisconsin Tam', NULL, 22.2703964, -97.8631066, 6),
(886, 4, '50TPI', 'Unidad Modelo Tam', NULL, 22.2814131, -97.8868729, 6),
(887, 4, '50TQP', 'Herradura Tam', NULL, 22.2721756, -97.8749586, 6),
(888, 4, '50VS6', 'Calle A Tam', NULL, 22.2794000, -97.8916000, 6),
(889, 4, '50ZDA', 'Calzada Tam', NULL, 22.2863379, -97.8772486, 6),
(890, 4, '50ZGP', 'Guadalupe Tam', NULL, 22.2530798, -97.8727749, 6),
(891, 4, '5012S', 'Cultural Tam', NULL, 22.2665000, -97.8597000, 6),
(892, 4, '50ARO', 'Oro Tam', NULL, 22.2795199, -97.8683851, 6),
(893, 4, '50KDB', 'Dos Bocas Tam', NULL, 22.2565182, -97.8641066, 6),
(894, 4, '50PTB', 'Uni. Poniente Tam', NULL, 22.2821350, -97.8639651, 6),
(895, 4, '50UDI', 'Universidad Tam', NULL, 22.2632846, -97.8605539, 6),
(896, 4, '503RS', 'Plaza Dorada Maf', NULL, 22.2603664, -97.8752090, 6),
(897, 4, '508OK', 'El Dorado Tam', NULL, 22.2954365, -97.8793838, 6),
(898, 4, '50839', 'Grecia Maf', NULL, 22.2835625, -97.8807179, 6),
(899, 4, '50BDK', 'San Luis Tam', NULL, 22.2676613, -97.8493339, 6),
(900, 4, '50DUI', 'Dona Cecilia Tam', NULL, 22.2566257, -97.8438156, 6),
(901, 4, '50ESO', 'Estadio Tam', NULL, 22.2728819, -97.8519400, 6),
(902, 4, '50FEG', 'Regional Tam', NULL, 22.2662101, -97.8558350, 6),
(903, 4, '50GRF', 'Grafer Tam', NULL, 22.2616169, -97.8503733, 6),
(904, 4, '50KC4', 'Calle 9 Tam', NULL, 22.2629370, -97.8503016, 6),
(905, 4, '50KMA', 'Madero Tam', NULL, 22.2711918, -97.8488342, 6),
(906, 4, '50NYR', 'Nayarit Tam', NULL, 22.2634899, -97.8470432, 6),
(907, 4, '50PXQ', 'Monteverde Tam', NULL, 22.2703896, -97.8582233, 6),
(908, 4, '50VNV', '20 De Noviembre Tam', NULL, 22.2616543, -97.8558571, 6),
(909, 4, '50YVY', 'Lopez Mateos Tam', NULL, 22.2573853, -97.8523001, 6),
(910, 4, '50ZNL', 'Sinaloa Tam', NULL, 22.2717000, -97.8499960, 6),
(911, 4, '5072E', 'Sonora Tam', NULL, 22.2738511, -97.8459941, 6),
(912, 4, '50AUN', 'Ampliacion Tam', NULL, 22.2825666, -97.8429225, 6),
(913, 4, '50QDS', 'Oma Tam', NULL, 22.2835587, -97.8645661, 6),
(914, 4, '50TOC', 'Cedros Tam', NULL, 22.2837719, -97.8496313, 6),
(915, 4, '50TTJ', 'Jalisco Tam', NULL, 22.2769187, -97.8506785, 6),
(916, 4, '50UNA', 'Unidad Nacional Tam', NULL, 22.2669954, -97.8444785, 6),
(917, 4, '50ZZM', 'Monterrey Ii Tam', NULL, 22.2779789, -97.8465168, 6),
(918, 4, '50IIL', 'Saltillo Ii Tam', NULL, 22.2620564, -97.8399369, 6),
(919, 4, '50SFR', 'Honduras Tam', NULL, 22.2598010, -97.8361634, 6),
(920, 4, '50IOY', 'Movil Tam', NULL, 22.2926000, -97.8770000, 6),
(921, 4, '5038G', 'Campbell Tam', NULL, 22.2243607, -97.8692900, 6),
(922, 4, '5055S', 'Calzada Blanca Maf', NULL, 22.2234478, -97.8877076, 6),
(923, 4, '507KD', 'Plaza Altavista Maf', NULL, 22.2398917, -97.8694066, 6),
(924, 4, '507VU', 'Petrolera Tam', NULL, 22.2518963, -97.8664832, 6),
(925, 4, '50ALC', 'Alarcon Tam', NULL, 22.2199227, -97.8614848, 6),
(926, 4, '50EJE', 'Ejercito Tam', NULL, 22.2490924, -97.8679941, 6),
(927, 4, '50HDL', 'Hidalgo Tam', NULL, 22.2388190, -97.8697954, 6),
(928, 4, '50I2L', 'Gaona Tam', NULL, 22.2333259, -97.8671494, 6),
(929, 4, '50KHI', 'Avenida Hidalgo Tam', NULL, 22.2459905, -97.8726474, 6),
(930, 4, '50LIG', 'Aguila Tam', NULL, 22.2430801, -97.8716188, 6),
(931, 4, '50LRH', 'Moscu Tam', NULL, 22.2268884, -97.8947511, 6),
(932, 4, '50LRV', 'Lauro Aguirre Tam', NULL, 22.2476048, -97.8676397, 6),
(933, 4, '50M1V', 'Smith Tam', NULL, 22.2422973, -97.8670517, 6),
(934, 4, '50MEV', 'Morelos Tam', NULL, 22.2200541, -97.8773910, 6),
(935, 4, '50N0P', 'La Isla Tam', NULL, 22.2249016, -97.8933985, 6),
(936, 4, '50QVO', 'Camelia Tam', NULL, 22.2467148, -97.8734627, 6),
(937, 4, '50RGN', 'Alijadores Tam', NULL, 22.2297008, -97.8705262, 6),
(938, 4, '50WAY', 'Alemanes Tam', NULL, 22.2552657, -97.8615773, 6),
(939, 4, '50WMZ', 'Morelos Ii Tam', NULL, 22.2217362, -97.8812974, 6),
(940, 4, '50YWJ', 'Cascajal Tam', NULL, 22.2186000, -97.8651000, 6),
(941, 4, '50YZF', 'Otomi Tam', NULL, 22.2368149, -97.8662184, 6),
(942, 4, '506QT', 'Matienzo Tam', NULL, 22.2182443, -97.8601070, 6),
(943, 4, '50CJN', 'Colon Tam', NULL, 22.2179068, -97.8572504, 6),
(944, 4, '50KJU', 'Juarez Tam', NULL, 22.2171709, -97.8553287, 6),
(945, 4, '50QLJ', 'Sor Juana Tam', NULL, 22.2196154, -97.8585277, 6),
(946, 4, '50U6I', 'Plaza Covadonga Maf', NULL, 22.2519280, -97.8636320, 6),
(947, 4, '504NY', 'Movil 2 Tam', NULL, 22.2926000, -97.8770000, 9),
(948, 4, '5049H', 'Movil 3 Tam', NULL, 22.2926000, -97.8770000, 6);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `token_acceso`
--

CREATE TABLE `token_acceso` (
  `token` char(64) NOT NULL,
  `usuario_id` int(10) UNSIGNED NOT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_expiracion` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `token_acceso`
--

INSERT INTO `token_acceso` (`token`, `usuario_id`, `fecha_creacion`, `fecha_expiracion`) VALUES
('10cf31f183e609b7bac14f36f7b456fb66b4e2e90de80471a89ed1d4c62137c8', 1, '2026-07-15 01:34:59', '2026-08-14 01:34:59'),
('3e64c8f44417eab571808611d57c16d24d0c28000b1e5cd6cc1f938c784e4937', 5, '2026-07-17 23:45:48', '2026-08-16 23:45:48'),
('490d9e3969ba4973258a127c2eae1372ded97993af8eaa9aaf36e6007096c8b3', 1, '2026-07-15 01:21:15', '2026-08-14 01:21:15'),
('687385d512df37dc369ef4b1eff65447a02537269e1f6b01df6b17ec46d667f6', 3, '2026-07-17 23:33:03', '2026-08-16 23:33:03'),
('7c76d549ab1511041238ee9f15acfd99511a5359f211db665563cf3c39d6b136', 2, '2026-07-15 01:34:04', '2026-08-14 01:34:04'),
('8edde550d7300341a609e8d5b144cf587f8b67cbd84c568e29836d0ea836e4c3', 2, '2026-07-15 10:28:11', '2026-08-14 10:28:11'),
('992e6b9f67e1bb8a574563ca8ae98a1cecda3e4b89717884cb08dfc802dd309f', 2, '2026-07-17 23:35:19', '2026-08-16 23:35:19'),
('c966c57746967a0c357c961d0d1c0bce27375e61fe50853e92dd540efba9c2bb', 2, '2026-07-17 23:41:21', '2026-08-16 23:41:21'),
('ca09180f7eb6af1fb7045bdaf09b66e98ca1a7543f25ca342cfc172601860eb4', 2, '2026-07-17 23:28:57', '2026-08-16 23:28:57');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id` int(10) UNSIGNED NOT NULL,
  `rol_id` int(10) UNSIGNED NOT NULL,
  `plaza_id` int(10) UNSIGNED DEFAULT NULL,
  `correo` varchar(150) NOT NULL,
  `nombre_completo` varchar(150) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `debe_cambiar_password` tinyint(1) NOT NULL DEFAULT 0,
  `foto_perfil` varchar(255) DEFAULT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `rol_id`, `plaza_id`, `correo`, `nombre_completo`, `password_hash`, `debe_cambiar_password`, `foto_perfil`, `fecha_registro`) VALUES
(1, 2, 1, 'raul.huerta@getic.com.mx', 'Raul Huerta Aguilar', '$2y$10$zua9xncZeFs6hcMrf.dI6.rAJvEbEtTLXc8fkZ8f3RIdO56.RMw1G', 0, 'uploads/perfiles/f23f920fc49bff1f42de60c1e484b7fc.jpg', '2026-07-10 19:08:00'),
(2, 1, 1, 'rosa.ramirez@oxxo.com', 'Rosa Martha Ramirez Castillo', '$2y$10$zua9xncZeFs6hcMrf.dI6.rAJvEbEtTLXc8fkZ8f3RIdO56.RMw1G', 0, 'uploads/perfiles/b4be8bd989f7c40ae24732de55683798.jpg', '2026-07-10 19:08:00'),
(3, 3, 1, 'erick.cruz@getic.com.mx', 'Erick Cruz', '$2y$10$VPztRe1US3CxpwKxys7NnO8Spf.FXC5ZrpYFLdtM5CfQ9cpqKY2Ha', 1, NULL, '2026-07-10 19:08:00'),
(4, 1, NULL, 'charlie.ruiz@pendiente.getic.com.mx', 'Charlie Ruiz Carapia', '$2y$10$ymHAiB.ZBGMRTMpUb8vSXOJocAGLJ1AnqP5Xv2WW.OZIyeAVcinby', 1, NULL, '2026-07-15 00:56:48'),
(5, 1, NULL, 'enrique.gil@pendiente.getic.com.mx', 'Enrique Gil Zarate', '$2y$10$kT7YRk7UX36xIlUHI3jgQu34u9GoQHR.69I6kc2qaOV1dyfJc5896', 1, NULL, '2026-07-15 00:56:48'),
(6, 1, 4, 'felipe.trejo@pendiente.getic.com.mx', 'Felipe Trejo Gonzalez', '$2y$10$z7pUeX3sgSeN/AYDixNw0.7Vlpw8prCkODdnAohE2bdPSxuBm6SLC', 1, NULL, '2026-07-15 00:56:48'),
(7, 1, NULL, 'hugo.perez@pendiente.getic.com.mx', 'Hugo Perez Santamarina', '$2y$10$9aMqT8k3IpzbTpISFKCEee/4qhSchbAvE2OkrGlLyGVImWzulccEy', 1, NULL, '2026-07-15 00:56:48'),
(8, 1, NULL, 'jonathan.husai@pendiente.getic.com.mx', 'Jonathan Husai Ramirez Garcia', '$2y$10$1AehmCkpyPP6dQF7EkFo3ey4r98SIZEeHIpI7NZ7SsKgSe0kgSV1i', 1, NULL, '2026-07-15 00:56:48'),
(9, 1, 4, 'juana.de@pendiente.getic.com.mx', 'Juana De la Cruz Castillo', '$2y$10$RetGSkuyw4kY.gSVQA1qEO1f6zq6S7/ozRF6Zu7ZapHBM6gAcRHoW', 1, NULL, '2026-07-15 00:56:48'),
(10, 1, NULL, 'ramon.arturo@pendiente.getic.com.mx', 'Ramon Arturo Morales Salazar', '$2y$10$31kLQxSca3azuJCV/x1FJObcoMs41N8uPm7qVS//owqifAiHraTeW', 1, NULL, '2026-07-15 00:56:48');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `cuestionario`
--
ALTER TABLE `cuestionario`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_cuestionario_plaza` (`plaza_id`);

--
-- Indices de la tabla `encuesta`
--
ALTER TABLE `encuesta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_encuesta_usuario` (`usuario_id`),
  ADD KEY `fk_encuesta_cuestionario` (`cuestionario_id`),
  ADD KEY `idx_encuesta_tienda` (`tienda_id`),
  ADD KEY `idx_encuesta_sincronizado` (`sincronizado`),
  ADD KEY `idx_encuesta_fecha` (`fecha_creacion_local`);

--
-- Indices de la tabla `negocio`
--
ALTER TABLE `negocio`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_negocio_default` (`es_default`);

--
-- Indices de la tabla `plaza`
--
ALTER TABLE `plaza`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_plaza_region` (`region_id`),
  ADD KEY `idx_plaza_default` (`es_default`);

--
-- Indices de la tabla `pregunta`
--
ALTER TABLE `pregunta`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_pregunta_usuario` (`creado_por_usuario_id`),
  ADD KEY `idx_pregunta_cuestionario_orden` (`cuestionario_id`,`orden`);

--
-- Indices de la tabla `region`
--
ALTER TABLE `region`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_region_negocio` (`negocio_id`),
  ADD KEY `idx_region_default` (`es_default`);

--
-- Indices de la tabla `respuesta_detalle`
--
ALTER TABLE `respuesta_detalle`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_respuesta_unica` (`encuesta_id`,`pregunta_id`),
  ADD KEY `fk_respuesta_pregunta` (`pregunta_id`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `tienda`
--
ALTER TABLE `tienda`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_tienda_codigo` (`codigo`),
  ADD KEY `fk_tienda_plaza` (`plaza_id`),
  ADD KEY `idx_tienda_asesor` (`asesor_ti_usuario_id`);

--
-- Indices de la tabla `token_acceso`
--
ALTER TABLE `token_acceso`
  ADD PRIMARY KEY (`token`),
  ADD KEY `idx_token_usuario` (`usuario_id`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `correo` (`correo`),
  ADD KEY `fk_usuario_rol` (`rol_id`),
  ADD KEY `fk_usuario_plaza` (`plaza_id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `cuestionario`
--
ALTER TABLE `cuestionario`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `negocio`
--
ALTER TABLE `negocio`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `plaza`
--
ALTER TABLE `plaza`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `pregunta`
--
ALTER TABLE `pregunta`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `region`
--
ALTER TABLE `region`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tienda`
--
ALTER TABLE `tienda`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=949;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `cuestionario`
--
ALTER TABLE `cuestionario`
  ADD CONSTRAINT `fk_cuestionario_plaza` FOREIGN KEY (`plaza_id`) REFERENCES `plaza` (`id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `encuesta`
--
ALTER TABLE `encuesta`
  ADD CONSTRAINT `fk_encuesta_cuestionario` FOREIGN KEY (`cuestionario_id`) REFERENCES `cuestionario` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_encuesta_tienda` FOREIGN KEY (`tienda_id`) REFERENCES `tienda` (`id`) ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_encuesta_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Filtros para la tabla `plaza`
--
ALTER TABLE `plaza`
  ADD CONSTRAINT `fk_plaza_region` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `pregunta`
--
ALTER TABLE `pregunta`
  ADD CONSTRAINT `fk_pregunta_cuestionario` FOREIGN KEY (`cuestionario_id`) REFERENCES `cuestionario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_pregunta_usuario` FOREIGN KEY (`creado_por_usuario_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

--
-- Filtros para la tabla `region`
--
ALTER TABLE `region`
  ADD CONSTRAINT `fk_region_negocio` FOREIGN KEY (`negocio_id`) REFERENCES `negocio` (`id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `respuesta_detalle`
--
ALTER TABLE `respuesta_detalle`
  ADD CONSTRAINT `fk_respuesta_encuesta` FOREIGN KEY (`encuesta_id`) REFERENCES `encuesta` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_respuesta_pregunta` FOREIGN KEY (`pregunta_id`) REFERENCES `pregunta` (`id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `tienda`
--
ALTER TABLE `tienda`
  ADD CONSTRAINT `fk_tienda_asesor` FOREIGN KEY (`asesor_ti_usuario_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_tienda_plaza` FOREIGN KEY (`plaza_id`) REFERENCES `plaza` (`id`) ON UPDATE CASCADE;

--
-- Filtros para la tabla `token_acceso`
--
ALTER TABLE `token_acceso`
  ADD CONSTRAINT `fk_token_usuario` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `fk_usuario_plaza` FOREIGN KEY (`plaza_id`) REFERENCES `plaza` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`rol_id`) REFERENCES `rol` (`id`) ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
