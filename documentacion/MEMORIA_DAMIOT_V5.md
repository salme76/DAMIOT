# DAMIOT - Sistema IoT Multiplataforma con ESP32, Spring Boot y Android

**Proyecto Final de Ciclo**

**CFGS Desarrollo de Aplicaciones Multiplataforma**

**IES Azarquiel - Toledo**

**Alumno:** Emilio José Salmerón Arjona

**Curso Académico:** 2025/2026

**Fecha de entrega:** 8 de diciembre de 2025

---

## Índice

1. Capítulo 1. Introducción y Objetivos
2. Capítulo 2. Especificación de Requisitos
3. Capítulo 3. Planificación Temporal y Evaluación de Costes
4. Capítulo 4. Tecnologías Utilizadas
5. Capítulo 5. Desarrollo e Implementación
6. Capítulo 6. Conclusiones y Líneas Futuras
7. Capítulo 7. Bibliografía

---

## Capítulo 1. Introducción y Objetivos

### 1.1. Contexto del Proyecto

El presente proyecto, denominado DAMIOT (DAM + IoT), surge como propuesta de Proyecto Final de Ciclo para el Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma del IES Azarquiel de Toledo, durante el curso académico 2025/2026.

En la actualidad, el Internet de las Cosas (IoT) ha revolucionado la forma en que interactuamos con nuestro entorno, permitiendo la conexión y comunicación entre dispositivos físicos y sistemas digitales. Este proyecto busca demostrar las competencias adquiridas durante el ciclo formativo mediante la creación de un sistema IoT completo que integra hardware, backend y aplicación móvil.

DAMIOT representa la convergencia de múltiples tecnologías y plataformas: desde la programación de microcontroladores ESP32 en C/C++, pasando por el desarrollo de servicios backend con Spring Boot en Java, hasta la creación de interfaces móviles modernas con Kotlin y Jetpack Compose. Esta integración multiplataforma justifica plenamente el nombre del ciclo formativo y demuestra la capacidad de desarrollar soluciones tecnológicas completas y funcionales.

### 1.2. Motivación Personal

La motivación principal para este proyecto surge de mi interés personal en la electrónica y la programación de microcontroladores, áreas que he explorado como hobby durante años. Sin embargo, sentía que mis conocimientos en esta área estaban desconectados de los contenidos del ciclo formativo de DAM.

DAMIOT me permite unir estas dos pasiones: por un lado, la programación de dispositivos IoT con ESP32, y por otro, el desarrollo de aplicaciones multiplataforma que es el núcleo del ciclo formativo. El proyecto demuestra que es posible crear sistemas realmente multiplataforma que abarquen desde el hardware más básico hasta aplicaciones móviles modernas, pasando por servicios backend robustos.

Además, este proyecto me ha permitido profundizar en aspectos técnicos avanzados como los protocolos de comunicación MQTT, la arquitectura de microservicios, la gestión de bases de datos relacionales y el diseño de interfaces móviles con las últimas tecnologías de Android.

### 1.3. Descripción General del Sistema

DAMIOT es un sistema IoT completo que permite la monitorización y control remoto de dispositivos ESP32 a través de una aplicación móvil Android. El sistema está compuesto por tres componentes principales que trabajan de forma coordinada:

**Dispositivos ESP32:** Microcontroladores que leen sensores de temperatura y humedad (DHT11) y controlan actuadores (LED azul). Cada dispositivo se conecta a la red WiFi DAMIOT y se comunica mediante el protocolo MQTT.

**Backend Spring Boot:** Servidor Java que actúa como intermediario entre los dispositivos y la aplicación móvil. Gestiona la base de datos MySQL, mantiene el estado de los dispositivos, procesa los datos de sensores y traduce los comandos REST en mensajes MQTT.

**Aplicación Android:** Interfaz móvil desarrollada en Kotlin con Jetpack Compose que permite visualizar el estado de los dispositivos, consultar las lecturas de los sensores en tiempo real y controlar los actuadores de forma remota.

El sistema funciona sobre una red WiFi dedicada creada con un router GLi.Net Mango (SSID: DAMIOT, red 192.168.8.0/24), lo que proporciona un entorno controlado y aislado para el desarrollo y las pruebas.

### 1.4. Objetivos del Proyecto

#### 1.4.1. Objetivo Principal

El objetivo principal de este proyecto es desarrollar un sistema IoT funcional y completo que demuestre las competencias adquiridas durante el Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma, integrando múltiples plataformas tecnológicas (hardware, backend y móvil) en una solución cohesionada y profesional.

#### 1.4.2. Objetivos Específicos

Los objetivos específicos que se persiguen con este proyecto son:

- **Diseñar e implementar firmware para ESP32** que permita la lectura de sensores DHT11 y el control de actuadores LED, con comunicación MQTT robusta y gestión de reconexiones automáticas.

- **Desarrollar un backend con Spring Boot** que implemente una API REST completa, integre un cliente MQTT para comunicación bidireccional, gestione la persistencia de datos en MySQL y proporcione mecanismos de resiliencia ante fallos.

- **Crear una aplicación Android moderna** utilizando Kotlin y Jetpack Compose, siguiendo la arquitectura MVVM con Clean Architecture, implementando inyección de dependencias con Hilt y proporcionando una experiencia de usuario fluida y responsive.

- **Implementar comunicación eficiente** mediante el protocolo MQTT para la telemetría de sensores y el control de actuadores, evitando el uso de polling y optimizando el consumo de recursos.

- **Garantizar la resiliencia del sistema** implementando mecanismos de recuperación ante fallos de red, caídas del broker MQTT, desconexiones de la base de datos y reinicios de dispositivos.

- **Documentar exhaustivamente el proyecto** siguiendo las normas académicas establecidas, incluyendo análisis de requisitos, diseño de arquitectura, detalles de implementación y conclusiones.

- **Aplicar buenas prácticas de desarrollo** incluyendo control de versiones con Git, organización modular del código, gestión segura de credenciales y pruebas funcionales del sistema completo.

### 1.5. Alcance del Proyecto

El proyecto DAMIOT abarca el desarrollo completo de un sistema IoT funcional con las siguientes características:

**Alcance técnico:**

- Soporte para múltiples dispositivos ESP32 conectados simultáneamente
- Monitorización en tiempo real de temperatura y humedad
- Control remoto de actuadores (LEDs) desde la aplicación móvil
- Persistencia de datos históricos de sensores
- Detección automática de dispositivos desconectados
- Sincronización de estados entre backend y dispositivos
- Gestión administrativa de dispositivos (activar/desactivar para ocultar dispositivos en mantenimiento)
- Interfaz adaptativa con soporte completo de modo oscuro (detección automática del sistema y control manual)

**Limitaciones:**

- El sistema está diseñado para funcionar en una red local dedicada, no contempla despliegue en Internet
- No incluye sistema de autenticación de usuarios (el acceso a la aplicación es abierto)
- Los sensores y actuadores son demostrativos (DHT11 y LED), no se incluyen dispositivos industriales complejos
- No se implementa cifrado de comunicaciones MQTT (se asume red confiable)
- La dirección IP del backend está hardcodeada en la aplicación Android para simplificar el despliegue en entorno de desarrollo

### 1.6. Estructura de la Memoria

Esta memoria se estructura en siete capítulos que documentan de forma exhaustiva el desarrollo del proyecto DAMIOT:

- **Capítulo 1 - Introducción y Objetivos:** Presenta el contexto del proyecto, la motivación personal, los objetivos perseguidos y el alcance del desarrollo.

- **Capítulo 2 - Especificación de Requisitos:** Define los requisitos funcionales y no funcionales del sistema, especificando qué debe hacer cada componente.

- **Capítulo 3 - Planificación Temporal y Evaluación de Costes:** Detalla la planificación del proyecto, las fases de desarrollo y el análisis económico de la solución.

- **Capítulo 4 - Tecnologías Utilizadas:** Describe todas las tecnologías, frameworks, herramientas y librerías empleadas en el desarrollo.

- **Capítulo 5 - Desarrollo e Implementación:** Explica en detalle la arquitectura del sistema, el diseño de cada componente y los aspectos técnicos de la implementación.

- **Capítulo 6 - Conclusiones y Líneas Futuras:** Resume los resultados obtenidos, reflexiona sobre los objetivos cumplidos y propone mejoras y ampliaciones futuras.

- **Capítulo 7 - Bibliografía:** Recoge todas las referencias bibliográficas y recursos consultados durante el desarrollo del proyecto.

---

## Capítulo 2. Especificación de Requisitos

En este capítulo se detallan los requisitos funcionales y no funcionales que debe cumplir el sistema DAMIOT. Esta especificación ha servido como guía durante todo el proceso de desarrollo y como criterio de validación del resultado final.

### 2.1. Requisitos Funcionales

Los requisitos funcionales describen las funcionalidades específicas que debe proporcionar cada componente del sistema.

#### 2.1.1. Requisitos del Dispositivo ESP32

**RF-ESP01:** El dispositivo debe conectarse automáticamente a la red WiFi DAMIOT al iniciar.

**RF-ESP02:** El dispositivo debe reconectarse automáticamente al WiFi en caso de pérdida de conexión.

**RF-ESP03:** El dispositivo debe conectarse al broker MQTT y reconectarse automáticamente en caso de desconexión.

**RF-ESP04:** El dispositivo debe leer el sensor DHT11 cada 5 segundos y publicar los valores de temperatura y humedad en los tópicos MQTT correspondientes.

**RF-ESP05:** El dispositivo debe suscribirse al tópico de control de LED y responder a los comandos ON/OFF.

**RF-ESP06:** El dispositivo debe mantener el estado del LED en caso de pérdida temporal de conexión.

**RF-ESP07:** Al reiniciarse, el dispositivo debe sincronizar su estado con el backend consultando el estado actual del LED.

**RF-ESP08:** El dispositivo debe implementar un mecanismo Last Will & Testament para notificar al backend si se desconecta inesperadamente.

#### 2.1.2. Requisitos del Backend Spring Boot

**RF-BACK01:** El backend debe conectarse al broker MQTT al iniciar y mantener la conexión activa.

**RF-BACK02:** El backend debe suscribirse a todos los tópicos de sensores y actuadores para recibir telemetría y confirmaciones.

**RF-BACK03:** Al recibir datos de sensores, el backend debe almacenarlos en la base de datos MySQL con marca temporal.

**RF-BACK04:** El backend debe proporcionar una API REST con endpoints para consultar dispositivos, lecturas de sensores y controlar actuadores.

**RF-BACK05:** Cuando se recibe un comando de control de LED vía REST, el backend debe publicarlo en el tópico MQTT correspondiente y esperar confirmación.

**RF-BACK06:** El backend debe actualizar el estado de los dispositivos y actuadores en la base de datos en tiempo real.

**RF-BACK07:** El backend debe detectar dispositivos offline mediante el análisis de Last Will messages y timeout de heartbeat.

**RF-BACK08:** El backend debe seguir funcionando en modo degradado si el broker MQTT o la base de datos no están disponibles.

**RF-BACK09:** El backend debe registrar eventos de actuadores en una tabla de auditoría para trazabilidad.

**RF-BACK10:** El backend debe centralizar la configuración de conexiones y parámetros del sistema en un archivo de propiedades, facilitando su adaptación a diferentes entornos de despliegue.

#### 2.1.3. Requisitos de la Aplicación Android

**RF-APP01:** La aplicación debe mostrar una lista de todos los dispositivos registrados con su estado actual (online/offline).

**RF-APP02:** La aplicación debe actualizar automáticamente la lista de dispositivos cada 10 segundos.

**RF-APP03:** La aplicación debe permitir la actualización manual mediante navegación entre pantallas y botón de refresh en la pantalla de detalle.

**RF-APP04:** Al seleccionar un dispositivo, la aplicación debe mostrar una pantalla de detalle con lecturas de sensores y controles de actuadores.

**RF-APP05:** En la pantalla de detalle, los datos deben actualizarse automáticamente cada 5 segundos.

**RF-APP06:** La aplicación debe proporcionar un switch para controlar el estado del LED (ON/OFF).

**RF-APP07:** La aplicación debe mostrar visualmente cuando un dispositivo está offline (componentes en gris, deshabilitar controles).

**RF-APP08:** La aplicación debe gestionar correctamente los errores de red mostrando mensajes informativos al usuario.

**RF-APP09:** La aplicación debe permitir activar o desactivar dispositivos administrativamente desde la interfaz, ocultándolos de la vista sin eliminarlos de la base de datos.

### 2.2. Requisitos No Funcionales

Los requisitos no funcionales establecen las restricciones y cualidades que debe tener el sistema en términos de rendimiento, usabilidad, fiabilidad y mantenibilidad.

#### 2.2.1. Rendimiento

**RNF-PERF01:** El tiempo de respuesta de la API REST del backend debe ser inferior a 500ms en el 95% de las peticiones.

**RNF-PERF02:** La latencia de extremo a extremo en el control de actuadores (desde app hasta dispositivo) debe ser inferior a 2 segundos.

**RNF-PERF03:** El sistema debe soportar al menos 10 dispositivos ESP32 conectados simultáneamente sin degradación significativa.

**RNF-PERF04:** La aplicación Android debe renderizar la interfaz a 60 FPS en dispositivos de gama media.

#### 2.2.2. Fiabilidad

**RNF-FIAB01:** El sistema debe detectar dispositivos offline en menos de 30 segundos.

**RNF-FIAB02:** El backend debe recuperarse automáticamente de caídas del broker MQTT sin intervención manual.

**RNF-FIAB03:** Los dispositivos ESP32 deben reconectarse automáticamente al WiFi y MQTT tras cortes de energía.

**RNF-FIAB04:** La sincronización de estados debe garantizar consistencia tras reinicios de cualquier componente.

#### 2.2.3. Usabilidad

**RNF-USA01:** La interfaz de la aplicación Android debe seguir los principios de Material Design 3, incluyendo soporte completo para modo oscuro adaptativo.

**RNF-USA02:** La aplicación debe proporcionar feedback visual inmediato a las acciones del usuario.

**RNF-USA03:** Los mensajes de error deben ser claros e informativos, indicando al usuario cómo resolver el problema.

**RNF-USA04:** La navegación entre pantallas debe ser intuitiva y coherente con los patrones de Android.

#### 2.2.4. Mantenibilidad

**RNF-MANT01:** El código debe estar organizado siguiendo arquitecturas limpias y separación de responsabilidades.

**RNF-MANT02:** El proyecto debe utilizar Git para control de versiones con commits descriptivos.

**RNF-MANT03:** Las configuraciones del sistema deben centralizarse en archivos específicos (application.properties en backend, constantes de configuración en Android) para facilitar su modificación sin alterar la lógica de negocio del código fuente.

**RNF-MANT04:** El código debe incluir comentarios en secciones complejas y documentación de las APIs.

#### 2.2.5. Portabilidad

**RNF-PORT01:** La aplicación Android debe funcionar en dispositivos con Android 7.0 (API 24) o superior.

**RNF-PORT02:** El backend debe poder ejecutarse en cualquier sistema operativo con Java 21.

**RNF-PORT03:** La base de datos MySQL debe poder ejecutarse en versiones 8.0 o superiores.

### 2.3. Casos de Uso Principales

A continuación se describen los casos de uso principales que ilustran las interacciones fundamentales del sistema.

#### 2.3.1. CU-01: Monitorizar Dispositivos

**Actor:** Usuario de la aplicación Android

**Descripción:** El usuario visualiza el listado de dispositivos IoT conectados con su estado actual.

**Flujo principal:**

1. El usuario abre la aplicación Android.
2. La aplicación solicita al backend la lista de dispositivos mediante la API REST.
3. El backend consulta la base de datos y devuelve la información de todos los dispositivos.
4. La aplicación muestra la lista con indicadores visuales de estado (online/offline).
5. La lista se actualiza automáticamente cada 10 segundos.

#### 2.3.2. CU-02: Consultar Lecturas de Sensores

**Actor:** Usuario de la aplicación Android

**Descripción:** El usuario accede a la información detallada de un dispositivo específico para ver las lecturas en tiempo real.

**Flujo principal:**

1. El usuario selecciona un dispositivo de la lista.
2. La aplicación navega a la pantalla de detalle del dispositivo.
3. La aplicación solicita al backend las últimas lecturas del dispositivo.
4. El backend devuelve temperatura, humedad y timestamps.
5. La aplicación muestra los valores con formato visual atractivo.
6. Los datos se actualizan automáticamente cada 5 segundos.

#### 2.3.3. CU-03: Controlar Actuador LED

**Actor:** Usuario de la aplicación Android

**Descripción:** El usuario enciende o apaga el LED de un dispositivo ESP32 de forma remota.

**Flujo principal:**

1. El usuario accede a la pantalla de detalle de un dispositivo online.
2. El usuario cambia el estado del switch del LED (ON/OFF).
3. La aplicación envía una petición POST a la API REST del backend.
4. El backend publica el comando en el tópico MQTT correspondiente.
5. El ESP32 recibe el comando y ejecuta la acción.
6. El ESP32 publica una confirmación en el tópico de estado del LED.
7. El backend actualiza el estado en la base de datos.
8. La aplicación muestra el nuevo estado confirmado.

#### 2.3.4. CU-04: Detección de Dispositivo Offline

**Actor:** Sistema (automático)

**Descripción:** El sistema detecta cuando un dispositivo se desconecta y actualiza su estado.

**Flujo principal:**

1. Un ESP32 pierde la conexión (corte de energía, fallo de red).
2. El broker MQTT publica el Last Will del dispositivo.
3. El backend recibe el Last Will y marca el dispositivo como offline en la base de datos.
4. La aplicación Android, en su próxima actualización, recibe el estado offline.
5. La aplicación muestra el dispositivo en gris y deshabilita los controles.

---

## Capítulo 3. Planificación Temporal y Evaluación de Costes

Este capítulo detalla la planificación temporal del proyecto, las fases de desarrollo implementadas y el análisis económico de la solución DAMIOT.

### 3.1. Metodología de Desarrollo

Para el desarrollo de DAMIOT se ha adoptado un enfoque iterativo e incremental, inspirado en metodologías ágiles pero adaptado a un proyecto individual con requisitos bien definidos. El desarrollo se ha organizado en ciclos de trabajo concentrados, permitiendo implementación, prueba y refinamiento continuos.

La estrategia de desarrollo se ha basado en los siguientes principios:

- **Desarrollo por capas:** Primero el firmware ESP32, luego el backend y finalmente la aplicación Android, asegurando que cada capa funciona correctamente antes de pasar a la siguiente.

- **Integración continua:** Pruebas de integración entre componentes al completar cada módulo principal para verificar la comunicación correcta.

- **Enfoque en la resiliencia:** Desde las primeras fases se ha priorizado la gestión de errores y la recuperación ante fallos.

- **Documentación progresiva:** Registro continuo de decisiones técnicas, problemas encontrados y soluciones implementadas.

### 3.2. Fases del Proyecto

El proyecto se ha dividido en seis fases principales, cada una con objetivos específicos y entregables definidos:

#### 3.2.1. Fase 1: Análisis y Diseño (1.5 semanas)

**Duración:** 25 septiembre - 3 octubre 2025

**Objetivos:**

- Definir los requisitos funcionales y no funcionales del sistema
- Diseñar la arquitectura general del sistema
- Seleccionar las tecnologías y herramientas a utilizar
- Diseñar el esquema de la base de datos
- Definir los tópicos MQTT y el protocolo de comunicación

**Entregables:**

- Documento de especificación de requisitos
- Diagramas de arquitectura del sistema
- Esquema de base de datos MySQL
- Definición de tópicos MQTT y mensajes

#### 3.2.2. Fase 2: Configuración del Entorno (0.5 semanas)

**Duración:** 4-7 octubre 2025

**Objetivos:**

- Configurar el router GLi.Net Mango y la red DAMIOT
- Instalar y configurar MySQL en Laragon
- Instalar y configurar Eclipse Mosquitto
- Configurar entornos de desarrollo (Arduino IDE, IntelliJ IDEA, Android Studio)
- Crear repositorio GitHub y estructura de carpetas

**Entregables:**

- Red DAMIOT operativa
- Base de datos MySQL creada y accesible
- Broker MQTT funcionando
- Repositorio GitHub inicializado

#### 3.2.3. Fase 3: Desarrollo del Firmware ESP32 (2 semanas)

**Duración:** 8-21 octubre 2025

**Objetivos:**

- Implementar conexión WiFi con reconexión automática
- Implementar cliente MQTT con Last Will & Testament
- Desarrollar lectura del sensor DHT11
- Implementar control del LED con confirmación de estado
- Implementar sincronización de estado al reiniciar

**Entregables:**

- Firmware ESP32 funcional
- Pruebas de conectividad y telemetría
- Documentación del código del firmware

#### 3.2.4. Fase 4: Desarrollo del Backend Spring Boot (2.5 semanas)

**Duración:** 22 octubre - 7 noviembre 2025

**Objetivos:**

- Crear proyecto Spring Boot con dependencias
- Implementar entidades JPA y repositorios
- Desarrollar controladores REST
- Implementar cliente MQTT con gestión de errores
- Desarrollar lógica de detección de dispositivos offline
- Implementar tabla de auditoría de actuadores
- Añadir manejo de resiliencia ante fallos

**Entregables:**

- Backend Spring Boot funcional
- API REST documentada
- Scripts SQL de creación y configuración
- Pruebas de integración con ESP32

#### 3.2.5. Fase 5: Desarrollo de la Aplicación Android (2 semanas)

**Duración:** 10-24 noviembre 2025

**Objetivos:**

- Crear proyecto Android con Kotlin y Jetpack Compose
- Configurar arquitectura MVVM con Hilt
- Implementar capa de red con Retrofit
- Desarrollar pantalla de listado de dispositivos
- Desarrollar pantalla de detalle de dispositivo
- Añadir actualizaciones automáticas y refresh manual
- Implementar manejo de estados offline
- Implementar modo oscuro con control automático y manual

**Entregables:**

- Aplicación Android funcional
- Interfaz Material Design 3 completa
- Pruebas de integración extremo a extremo

#### 3.2.6. Fase 6: Pruebas y Documentación (1.5 semanas)

**Duración:** 25 noviembre - 5 diciembre 2025

**Objetivos:**

- Realizar pruebas exhaustivas del sistema completo
- Pruebas de resiliencia (desconexiones, fallos, reinicios)
- Ajustes finales y corrección de bugs
- Redacción de la memoria del proyecto
- Preparación de la presentación

**Entregables:**

- Sistema completo probado y funcional
- Memoria del proyecto
- Presentación para la defensa
- Repositorio GitHub actualizado y documentado

### 3.3. Cronograma

El siguiente cronograma resume la distribución temporal de las fases del proyecto:

**Fase 1 - Análisis y Diseño:** 25 septiembre - 3 octubre 2025 (1.5 semanas, 7 días laborables, 25 horas)

**Fase 2 - Configuración del Entorno:** 4 - 7 octubre 2025 (0.5 semanas, 3 días laborables, 12 horas)

**Fase 3 - Desarrollo del Firmware ESP32:** 8 - 21 octubre 2025 (2 semanas, 10 días laborables, 37 horas)

**Fase 4 - Desarrollo del Backend Spring Boot:** 22 octubre - 7 noviembre 2025 (2.5 semanas, 13 días laborables, 49 horas)

**Fase 5 - Desarrollo de la Aplicación Android:** 10 - 24 noviembre 2025 (2 semanas, 10 días laborables, 37 horas)

**Fase 6 - Pruebas y Documentación:** 25 noviembre - 5 diciembre 2025 (1.5 semanas, 9 días laborables, 48 horas)

**TOTAL:** 25 septiembre - 5 diciembre 2025 (10 semanas, 52 días laborables, 208 horas)

**Régimen de trabajo:** 4 horas diarias de lunes a viernes (tardes), lo que permite compatibilizar el desarrollo del proyecto con otras actividades académicas y personales.

### 3.4. Evaluación de Costes

El análisis de costes del proyecto DAMIOT se divide en tres categorías principales: costes de hardware, costes de software y licencias, y costes de recursos humanos.

#### 3.4.1. Costes de Hardware

El hardware necesario para el desarrollo y funcionamiento del sistema incluye:

- **ESP32 DevKitC WROOM-32D:** 1 unidad × 8,50 € = 8,50 €
- **Sensor DHT11:** 1 unidad × 2,50 € = 2,50 €
- **LED Azul 5mm:** 1 unidad × 0,15 € = 0,15 €
- **Resistencias, cables, protoboard:** 1 conjunto × 5,00 € = 5,00 €
- **Router GLi.Net Mango:** 1 unidad × 35,00 € = 35,00 €
- **PC de desarrollo:** Ya disponible = 0,00 €
- **Smartphone Android:** Ya disponible = 0,00 €

**SUBTOTAL HARDWARE: 51,15 €**

#### 3.4.2. Costes de Software y Licencias

Todo el software utilizado en el proyecto es gratuito o de código abierto:

- **Arduino IDE:** Gratuito y de código abierto
- **IntelliJ IDEA Community Edition:** Gratuito para desarrollo Java
- **Android Studio:** Gratuito, desarrollado por Google
- **Laragon (MySQL):** Gratuito, licencia open source
- **Eclipse Mosquitto:** Gratuito, licencia EPL/EDL
- **Git y GitHub:** Gratuito para repositorios públicos
- **Postman:** Versión gratuita para testing de APIs

**Coste total de software: 0,00 €**

#### 3.4.3. Costes de Recursos Humanos

Para calcular el coste de desarrollo se considera el tiempo invertido y una tarifa estimada de 15 €/hora para un desarrollador junior:

- **Análisis y diseño:** 25 horas × 15,00 €/hora = 375,00 €
- **Configuración del entorno:** 12 horas × 15,00 €/hora = 180,00 €
- **Desarrollo firmware ESP32:** 37 horas × 15,00 €/hora = 555,00 €
- **Desarrollo backend Spring Boot:** 49 horas × 15,00 €/hora = 735,00 €
- **Desarrollo aplicación Android:** 37 horas × 15,00 €/hora = 555,00 €
- **Pruebas y correcciones:** 24 horas × 15,00 €/hora = 360,00 €
- **Documentación y presentación:** 24 horas × 15,00 €/hora = 360,00 €

**SUBTOTAL RRHH: 208 horas = 3.120,00 €**

#### 3.4.4. Resumen de Costes Totales

- **Hardware:** 51,15 €
- **Software y Licencias:** 0,00 €
- **Recursos Humanos (208 horas):** 3.120,00 €

**COSTE TOTAL DEL PROYECTO: 3.171,15 €**

El coste total del proyecto DAMIOT asciende a **3.171,15 euros**, de los cuales el 98,4% corresponde a recursos humanos (desarrollo y documentación) y solo el 1,6% a hardware. La ausencia de costes de software demuestra el poder de las herramientas de código abierto en el desarrollo de soluciones tecnológicas modernas.

Este análisis de costes debe entenderse en el contexto educativo del proyecto. En un entorno profesional real, habría que añadir costes indirectos como electricidad, espacio de oficina, equipamiento adicional y márgenes comerciales.

---

## Capítulo 4. Tecnologías Utilizadas

Este capítulo describe en detalle todas las tecnologías, frameworks, herramientas y librerías que se han utilizado en el desarrollo del proyecto DAMIOT. La selección de estas tecnologías se ha basado en criterios de compatibilidad, soporte de la comunidad, documentación disponible y adecuación a los requisitos del sistema.

### 4.1. Hardware

#### 4.1.1. ESP32 DevKitC WROOM-32D

El ESP32 es un microcontrolador de bajo coste y bajo consumo desarrollado por Espressif Systems. El modelo DevKitC con chip WROOM-32D ofrece las siguientes características:

- **Procesador:** Xtensa dual-core 32-bit LX6 hasta 240 MHz
- **Memoria:** 520 KB SRAM, 4 MB Flash
- **Conectividad:** WiFi 802.11 b/g/n, Bluetooth 4.2
- **GPIO:** 34 pines programables
- **Periféricos:** ADC, DAC, I2C, SPI, UART, PWM

Se ha elegido el ESP32 por su excelente relación precio-prestaciones, su capacidad WiFi integrada y la amplia disponibilidad de librerías y documentación. El soporte nativo de WiFi elimina la necesidad de módulos adicionales.

#### 4.1.2. Sensor DHT11

El DHT11 es un sensor digital de temperatura y humedad relativa ampliamente utilizado en proyectos IoT. Sus características principales son:

- **Rango de temperatura:** 0-50°C con precisión de ±2°C
- **Rango de humedad:** 20-90% HR con precisión de ±5%
- **Frecuencia de muestreo:** 1 Hz (una lectura por segundo)
- **Alimentación:** 3-5V DC
- **Protocolo:** Single-bus digital

Aunque existen sensores más precisos como el DHT22, el DHT11 es suficiente para un proyecto demostrativo y tiene un coste muy reducido. En el proyecto se utiliza conectado al pin GPIO 4 del ESP32.

#### 4.1.3. Router GLi.Net Mango

El GLi.Net Mango es un router compacto basado en OpenWrt que proporciona la infraestructura de red para el proyecto. Sus características relevantes son:

- **Procesador:** MediaTek MT7628NN a 580 MHz
- **WiFi:** 802.11 b/g/n 2.4GHz
- **Puertos:** 2x Ethernet 100Mbps
- **Sistema:** OpenWrt con interfaz web personalizada

Se ha configurado para crear una red aislada (DAMIOT, 192.168.8.0/24) que facilita el desarrollo y las pruebas sin interferir con otras redes. El router proporciona servicios DHCP para asignación automática de IPs.

### 4.2. Software de Infraestructura

#### 4.2.1. MySQL 8.4.3

MySQL es el sistema de gestión de bases de datos relacional utilizado para la persistencia de datos en el backend. Se ha optado por la versión 8.4.3 ejecutándose en Laragon por las siguientes razones:

- Excelente integración con Spring Boot mediante Spring Data JPA
- Alto rendimiento en operaciones de lectura y escritura
- Soporte robusto de transacciones ACID
- Amplia documentación y comunidad de soporte
- Gratuito y de código abierto

Laragon proporciona un entorno de desarrollo local que facilita la gestión de MySQL sin necesidad de instalación compleja. La base de datos almacena información de dispositivos, sensores, actuadores y eventos del sistema.

#### 4.2.2. Eclipse Mosquitto

Mosquitto es un broker MQTT de código abierto que implementa las versiones 5.0, 3.1.1 y 3.1 del protocolo MQTT. Sus características principales son:

- Ligero y eficiente, adecuado para dispositivos con recursos limitados
- Soporte completo de las funcionalidades MQTT incluyendo QoS y Last Will
- Configuración sencilla mediante archivos de texto
- Excelente rendimiento con miles de clientes simultáneos
- Licencia EPL/EDL (open source)

En el proyecto se ejecuta en el puerto predeterminado 1883 sin autenticación (apropiado para una red de desarrollo aislada). Mosquitto actúa como intermediario entre los dispositivos ESP32 y el backend Spring Boot.

### 4.3. Tecnologías del Backend

#### 4.3.1. Java 21

Java 21 es una versión LTS (Long Term Support) del lenguaje Java que incluye numerosas mejoras modernas:

- **Pattern Matching para switch:** Sintaxis más expresiva y concisa
- **Records:** Clases inmutables perfectas para DTOs
- **Virtual Threads:** Concurrencia mejorada
- **Mejor rendimiento y optimizaciones del GC:** Ejecución más eficiente

Se ha elegido Java 21 por su estabilidad, soporte a largo plazo y compatibilidad con Spring Boot 3.5.6. La madurez del ecosistema Java garantiza disponibilidad de librerías y herramientas de calidad.

#### 4.3.2. Spring Boot 3.5.6

Spring Boot es el framework principal del backend. Proporciona:

- **Spring Web:** Para crear APIs REST con controladores anotados
- **Spring Data JPA:** Abstracción de acceso a datos con repositorios
- **Spring Integration MQTT:** Cliente MQTT integrado con el contenedor Spring
- **Configuración automática:** Reducción drástica de boilerplate code
- **Servidor embebido Tomcat:** Ejecución standalone sin servidor externo

Spring Boot permite desarrollar aplicaciones robustas con poca configuración manual. La inyección de dependencias facilita el testing y el mantenimiento del código.

#### 4.3.3. Maven

Maven es la herramienta de gestión de dependencias y construcción del proyecto backend. El archivo pom.xml define:

- Dependencias del proyecto y sus versiones
- Plugins de compilación y empaquetado
- Configuración de la versión de Java
- Repositorios de descarga de librerías

Maven facilita la gestión del proyecto y garantiza builds reproducibles. La integración con IntelliJ IDEA permite sincronización automática de dependencias.

### 4.4. Tecnologías del Firmware ESP32

#### 4.4.1. Arduino Framework

El framework Arduino proporciona una capa de abstracción sobre el ESP-IDF nativo que simplifica el desarrollo. Incluye:

- API estándar de Arduino (digitalWrite, analogRead, etc.)
- Gestión automática de WiFi y networking
- Amplio ecosistema de librerías compatibles
- Curva de aprendizaje suave para principiantes

Se ha elegido el framework Arduino en lugar de ESP-IDF nativo por su simplicidad y la disponibilidad de librerías maduras para MQTT y sensores.

#### 4.4.2. Librerías Clave del Firmware

**WiFi.h:** Librería estándar de ESP32 para conexión WiFi. Gestiona la conexión, reconexión automática y eventos de red.

**PubSubClient.h:** Cliente MQTT para Arduino. Implementa publicación, suscripción, QoS y Last Will. Se ha configurado con buffers de 512 bytes para soportar payloads grandes.

**DHT.h:** Librería de Adafruit para sensores DHT11/DHT22. Proporciona funciones readTemperature() y readHumidity() con gestión de errores.

**ArduinoJson.h:** Parser y generador JSON ligero y eficiente. Se utiliza para serializar/deserializar mensajes MQTT en formato JSON.

### 4.5. Tecnologías de la Aplicación Android

#### 4.5.1. Kotlin

Kotlin es el lenguaje de programación oficial de Android recomendado por Google. Sus ventajas sobre Java incluyen:

- **Null Safety:** Sistema de tipos que previene NullPointerException
- **Sintaxis concisa:** Menos código boilerplate
- **Coroutines:** Programación asíncrona simplificada
- **Extension Functions:** Añadir funcionalidad a clases existentes
- **Interoperabilidad con Java:** Uso de librerías Java sin problemas

#### 4.5.2. Jetpack Compose

Jetpack Compose es el toolkit moderno de Android para construir interfaces de usuario nativas. Características principales:

- **UI Declarativa:** Define qué mostrar, no cómo mostrarlo
- **Menos código:** Sin XML, todo en Kotlin
- **Reactividad:** La UI se actualiza automáticamente con los cambios de estado
- **Material Design 3:** Componentes modernos listos para usar
- **Theming adaptativo:** Soporte completo para modo claro y oscuro con detección automática y control manual
- **Preview:** Vista previa en tiempo real en Android Studio

Compose ha reemplazado el sistema tradicional de vistas XML, proporcionando una experiencia de desarrollo más productiva y mantenible.

#### 4.5.3. Arquitectura y Librerías Android

**ViewModel:** Componente de Android Architecture que almacena y gestiona datos relacionados con la UI de forma consciente del ciclo de vida. Sobrevive a cambios de configuración como rotaciones de pantalla.

**Hilt:** Framework de inyección de dependencias basado en Dagger pero con configuración simplificada. Gestiona automáticamente el ciclo de vida de los objetos y facilita el testing.

**Retrofit:** Cliente HTTP type-safe para Android y Java. Convierte interfaces Java en llamadas REST mediante anotaciones. Soporta conversión automática con Gson.

**Navigation Compose:** Librería de navegación para Jetpack Compose que gestiona la pila de navegación y el paso de argumentos entre pantallas.

**DataStore:** Sistema moderno de almacenamiento de preferencias que reemplaza a SharedPreferences, utilizado para persistir la configuración de tema de la aplicación.

### 4.6. Herramientas de Desarrollo

#### 4.6.1. Arduino IDE 2.x

Entorno de desarrollo para programación del firmware ESP32. Proporciona:

- Editor de código con resaltado de sintaxis
- Gestión de placas y librerías
- Compilación y carga de código al ESP32
- Monitor serial para debugging

#### 4.6.2. IntelliJ IDEA Community Edition

IDE de JetBrains para desarrollo Java y Spring Boot. Ofrece:

- Excelente soporte para Spring Framework
- Refactoring avanzado
- Debugging integrado
- Integración con Maven y Git

#### 4.6.3. Android Studio

IDE oficial de Google para desarrollo Android, basado en IntelliJ IDEA. Incluye:

- Soporte completo para Kotlin y Jetpack Compose
- Emuladores Android integrados
- Preview de Compose en tiempo real
- Profiler para análisis de rendimiento
- Herramientas de debugging avanzadas

#### 4.6.4. Postman

Herramienta para testing y documentación de APIs REST. Se ha utilizado extensivamente durante el desarrollo para:

- Probar todos los endpoints del backend
- Validar respuestas JSON
- Verificar códigos de estado HTTP
- Documentar ejemplos de peticiones y respuestas
- Depurar problemas de integración entre Android y backend

Postman ha sido fundamental para asegurar que la API REST funciona correctamente antes de integrarla con la aplicación móvil, permitiendo un desarrollo más ágil y reduciendo el tiempo de debugging.

#### 4.6.5. Git y GitHub

Sistema de control de versiones distribuido. El proyecto se aloja en GitHub en el repositorio https://github.com/salme76/DAMIOT con la siguiente estructura:

- **/esp32:** Código del firmware
- **/backend:** Proyecto Spring Boot
- **/android:** Aplicación móvil
- **/docs:** Documentación del proyecto

---

## Capítulo 5. Desarrollo e Implementación

Este capítulo describe en detalle la arquitectura del sistema DAMIOT, el diseño de cada componente y los aspectos más relevantes de la implementación. Se explican las decisiones técnicas tomadas y los retos superados durante el desarrollo.

### 5.1. Arquitectura General del Sistema

DAMIOT implementa una arquitectura de tres capas que separa claramente las responsabilidades:

**Capa de Dispositivos (ESP32):** Responsable de la interacción con el hardware físico, lectura de sensores y control de actuadores. Se comunica exclusivamente mediante MQTT.

**Capa de Lógica de Negocio (Backend Spring Boot):** Actúa como intermediario y orquestador del sistema. Traduce peticiones REST en comandos MQTT, gestiona la persistencia y mantiene la consistencia del estado.

**Capa de Presentación (Aplicación Android):** Proporciona la interfaz de usuario. Consume la API REST del backend y presenta la información de forma visual y accesible.

Esta arquitectura proporciona varios beneficios: desacoplamiento entre componentes, facilidad para escalar cada capa independientemente, claridad en el flujo de datos y facilidad de testing.

#### 5.1.1. Protocolos de Comunicación

**MQTT (ESP32 ↔ Backend):** Se utiliza el patrón publish-subscribe con tópicos organizados jerárquicamente. Los sensores publican telemetría con QoS 0 (fire and forget) mientras que los comandos de actuadores usan QoS 1 (al menos una vez) para garantizar entrega.

**REST (Android ↔ Backend):** API RESTful con verbos HTTP estándar. Endpoints para consultar dispositivos (GET), obtener lecturas (GET) y controlar actuadores (POST). Respuestas en formato JSON.

### 5.2. Implementación del Firmware ESP32

#### 5.2.1. Estructura del Código

El firmware se organiza en funciones que encapsulan responsabilidades específicas:

**setup():** Inicialización de WiFi, MQTT, sensor DHT y LED.

**loop():** Bucle principal que mantiene conexiones, lee sensores y procesa mensajes.

**connectWiFi():** Establece conexión WiFi con reintentos.

**connectMQTT():** Conecta al broker con Last Will y suscripciones.

**publishSensorData():** Lee DHT11 y publica temperatura/humedad.

**callback():** Procesa mensajes MQTT recibidos.

#### 5.2.2. Tópicos MQTT Utilizados

Se ha implementado una convención de nombres que incluye la MAC del dispositivo para soporte multi-dispositivo:

**damiot/sensores/{MAC}/temperatura:** ESP32 → Backend. Publicación de temperatura en °C.

**damiot/sensores/{MAC}/humedad:** ESP32 → Backend. Publicación de humedad relativa en %.

**damiot/actuadores/{MAC}/led_azul:** Backend → ESP32. Comandos ON/OFF para el LED.

**damiot/actuadores/{MAC}/led_azul/estado:** ESP32 → Backend. Confirmación del estado actual del LED.

**damiot/heartbeat/{MAC}:** ESP32 → Backend. Heartbeat con IP (o "offline" para LWT).

#### 5.2.3. Gestión de Resiliencia

El firmware implementa varios mecanismos para garantizar la robustez del sistema:

**Reconexión WiFi:** Si se pierde la conexión WiFi, el ESP32 intenta reconectar cada 5 segundos. Si transcurren 10 segundos sin éxito, el dispositivo se reinicia automáticamente.

**Reconexión MQTT:** Tras perder conexión con el broker, se reintenta cada 5 segundos. Al reconectar, se resuscriben automáticamente todos los tópicos necesarios.

**Last Will & Testament:** Al conectar, el ESP32 configura un mensaje LWT que el broker publicará automáticamente si el dispositivo se desconecta abruptamente. Este mensaje contiene "offline" y permite al backend actualizar inmediatamente el estado.

**Sincronización post-reinicio:** Cuando el dispositivo vuelve a conectarse tras un reinicio, el backend detecta la transición offline→online y envía el estado actual de los actuadores para sincronizar el LED.

### 5.3. Implementación del Backend Spring Boot

#### 5.3.1. Estructura del Proyecto

El backend sigue una arquitectura en capas típica de Spring Boot:

**model/:** Entidades JPA que mapean las tablas de la base de datos.

**repository/:** Interfaces de repositorios Spring Data JPA.

**service/:** Lógica de negocio y orquestación.

**controller/:** Controladores REST con endpoints HTTP.

**mqtt/:** Configuración y manejo de mensajes MQTT.

**config/:** Clases de configuración de Spring.

**scheduler/:** Tareas programadas (verificación de dispositivos).

#### 5.3.2. Modelo de Datos

La base de datos MySQL contiene las siguientes entidades principales:

**Device (tabla: devices):** Información de dispositivos. Campos: id, macAddress, ipAddress, name, status, isEnabled, lastConnection.

**SensorReading (tabla: sensor_data):** Lecturas de sensores. Campos: id, deviceId, sensorType, value, unit, timestamp.

**ActuatorState (tabla: actuator_states):** Estados de actuadores. Campos: id, deviceId, actuatorType, state, updatedAt.

**ActuatorEvent (tabla: actuator_events):** Eventos de actuadores. Campos: id, deviceId, actuatorType, command, status, timestamp.

Las entidades JPA utilizan anotaciones como @Entity, @Id, @ManyToOne para definir el mapeo objeto-relacional. Se han definido índices en las tablas para optimizar las consultas más frecuentes.

#### 5.3.3. API REST Endpoints

El backend proporciona los siguientes endpoints REST:

**GET /api/devices:** Lista de todos los dispositivos.

**GET /api/devices/{id}:** Detalles de un dispositivo específico.

**PUT /api/devices/{id}/toggle:** Activa/desactiva un dispositivo administrativamente.

**GET /api/sensors/device/{deviceId}/latest:** Últimas lecturas de sensores de un dispositivo.

**GET /api/actuators/device/{deviceId}:** Estados de actuadores de un dispositivo.

**POST /api/actuators/command:** Envía comando a un actuador (ON/OFF).

#### 5.3.4. Integración MQTT

El backend utiliza la librería Eclipse Paho para actuar como cliente MQTT. La configuración incluye:

**MqttConfig:** Configuración del cliente con reconexión automática.

**MqttMessageHandler:** Procesa mensajes recibidos y actualiza base de datos.

**MqttService:** Publicación de mensajes a tópicos MQTT.

Cuando llegan datos de sensores vía MQTT, el handler extrae la MAC del tópico, busca el dispositivo correspondiente en la base de datos, y persiste la lectura. Cuando llega un comando de actuador vía REST, el servicio lo publica vía MQTT usando la MAC del dispositivo.

#### 5.3.5. Detección de Dispositivos Offline

El sistema implementa dos mecanismos complementarios:

**Last Will Messages:** El broker publica LWT cuando un dispositivo se desconecta. Tiempo de detección: inmediato (<1 segundo).

**Heartbeat Timeout:** Scheduler verifica última comunicación cada 15 segundos. Tiempo de detección: hasta 30 segundos.

Esta combinación dual garantiza detección rápida y fiable en todos los escenarios de fallo posibles.

### 5.4. Implementación de la Aplicación Android

#### 5.4.1. Arquitectura MVVM

La aplicación sigue el patrón Model-View-ViewModel con Clean Architecture:

**Model:** Data classes y entidades de dominio que representan dispositivos, sensores y actuadores.

**View:** Composables de Jetpack Compose que renderizan la UI de forma declarativa.

**ViewModel:** Gestiona el estado de la UI, ejecuta lógica de presentación y coordina las llamadas al repositorio.

Esta separación facilita el testing, mejora la mantenibilidad y permite que la UI reaccione automáticamente a cambios de estado mediante flows y StateFlow.

#### 5.4.2. Inyección de Dependencias con Hilt

Hilt gestiona la creación e inyección de dependencias siguiendo el ciclo de vida de Android:

- **@HiltAndroidApp:** Anotación en la clase Application para inicializar Hilt
- **@AndroidEntryPoint:** Marca Activities para recibir inyección
- **@HiltViewModel:** Permite inyección automática en ViewModels
- **@Module + @InstallIn:** Define módulos que proveen dependencias

La inyección centralizada de ApiService, repositorios y ViewModels simplifica el código y facilita el testing mediante mocks.

#### 5.4.3. Capa de Red con Retrofit

Retrofit proporciona una abstracción type-safe sobre HTTP:

**Definición de interfaces:** Se define ApiService con métodos anotados (@GET, @POST) que describen los endpoints.

**Generación automática:** Retrofit genera la implementación en tiempo de ejecución.

**Conversión JSON:** Gson convierte automáticamente entre JSON y data classes.

**Configuración:** La baseUrl del backend está hardcodeada para simplificar el despliegue en desarrollo.

Esta abstracción permite cambiar la implementación de red sin afectar al resto de la aplicación.

#### 5.4.4. Pantallas de la Aplicación

**HomeScreen (Lista de Dispositivos):**
- Muestra tarjetas con nombre, estado online/offline y timestamp de última conexión
- Actualización automática cada 10 segundos mediante LaunchedEffect
- Refresh manual al navegar de vuelta desde DetailScreen (lifecycle-aware)
- Indicadores visuales: dispositivos offline en escala de grises
- Switch para activar/desactivar dispositivos administrativamente
- Navegación a DetailScreen al tocar una tarjeta

**DetailScreen (Detalle de Dispositivo):**
- Información del dispositivo (nombre, MAC, IP, estado)
- Tarjetas con lecturas de sensores (temperatura, humedad) y timestamps
- Switch para controlar el LED con feedback inmediato
- Botón de refresh en TopAppBar para actualización manual
- Actualización automática cada 5 segundos
- Deshabilita controles si el dispositivo está offline
- Navegación de retorno con flecha atrás

La navegación se gestiona con Navigation Compose, pasando el deviceId como argumento de ruta.

#### 5.4.5. Soporte de Modo Oscuro

La aplicación implementa un sistema completo de theming con modo oscuro:

**Detección automática del sistema:**
- La aplicación detecta la preferencia de tema del dispositivo mediante `isSystemInDarkTheme()`
- Se adapta automáticamente cuando el usuario cambia el tema del sistema

**Control manual en la aplicación:**
- Menú desplegable en la TopAppBar de HomeScreen con opciones:
  - **Sistema:** Sigue la preferencia del sistema operativo (por defecto)
  - **Claro:** Fuerza modo claro independientemente del sistema
  - **Oscuro:** Fuerza modo oscuro independientemente del sistema
- La preferencia se persiste usando DataStore para mantenerla entre sesiones

**Implementación técnica:**
- Paleta de colores adaptativa con Material Design 3
- Colores específicos para modo claro y oscuro que cumplen WCAG
- Todos los componentes Compose ajustan automáticamente sus colores
- El tema se gestiona a nivel de aplicación mediante un ThemeViewModel

Esta implementación dual (automática + manual) proporciona flexibilidad máxima al usuario, permitiendo tanto seguir las preferencias del sistema como personalizar el tema específicamente en la aplicación.

#### 5.4.6. Configuración de la Dirección del Backend

En esta versión del proyecto, la dirección IP del backend está definida como una constante hardcodeada en el código fuente de la aplicación. Esta decisión simplifica el despliegue en un entorno de desarrollo controlado con red local fija (192.168.8.0/24).

La IP se define en un objeto de configuración:

```kotlin
object ApiConfig {
    const val BASE_URL = "http://192.168.8.X:8080"
}
```

Esta aproximación es adecuada para:
- Entornos de desarrollo con infraestructura de red estable
- Prototipos y pruebas de concepto
- Despliegues en redes locales dedicadas

**Nota:** Para entornos de producción o uso en múltiples redes, sería recomendable implementar una pantalla de configuración que permita al usuario especificar la dirección del backend y persistir esta configuración mediante DataStore (ver Capítulo 6 - Líneas Futuras).

### 5.5. Retos Técnicos y Soluciones

#### 5.5.1. Sincronización de Estados

**Problema:** Cuando un ESP32 se reinicia (por corte de energía), pierde su estado y el LED vuelve a OFF, pero el backend podría tener estado ON en base de datos.

**Solución:** Al conectarse, el backend detecta la transición offline→online y envía automáticamente el estado almacenado al dispositivo para sincronizar el LED físico.

#### 5.5.2. Manejo de IPs Dinámicas

**Problema:** El router GLi.Net usa DHCP, por lo que las IPs de los ESP32 pueden cambiar entre reinicios.

**Solución:** El sistema identifica dispositivos por MAC address (inmutable). El backend actualiza automáticamente la IP almacenada cada vez que recibe telemetría de un dispositivo mediante los heartbeats.

#### 5.5.3. Consistencia en Tópicos MQTT

**Problema:** Inicialmente se consideró usar nombres genéricos sin identificador de dispositivo, lo que limitaba el soporte multi-dispositivo.

**Solución:** Se rediseñó la arquitectura de tópicos para incluir la MAC address en cada tópico (`damiot/sensores/{MAC}/temperatura`), permitiendo soporte real de múltiples dispositivos con enrutamiento correcto de mensajes.

#### 5.5.4. Gestión de Fallos del Broker

**Problema:** Si el broker Mosquitto no está disponible al iniciar, el backend fallaba completamente.

**Solución:** Se implementó reconexión automática con backoff exponencial. El backend mantiene la API REST operativa aunque MQTT esté caído, permitiendo operación en modo degradado (sin telemetría en tiempo real pero con acceso a datos históricos).

---

## Capítulo 6. Conclusiones y Líneas Futuras

### 6.1. Conclusiones

El proyecto DAMIOT ha cumplido satisfactoriamente todos los objetivos planteados al inicio del desarrollo. Se ha conseguido implementar un sistema IoT completo y funcional que integra hardware (ESP32), backend (Spring Boot) y frontend móvil (Android), demostrando las competencias adquiridas durante el Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma.

#### 6.1.1. Cumplimiento de Objetivos

**Objetivo principal:** Se ha desarrollado un sistema IoT real que demuestra el dominio de múltiples plataformas tecnológicas. El sistema no es solo un prototipo académico, sino una solución funcional que podría evolucionar hacia aplicaciones prácticas.

**Firmware ESP32:** Se logró implementar un firmware robusto con gestión automática de reconexiones WiFi y MQTT, lectura fiable de sensores DHT11 cada 5 segundos, y sincronización de estados tras reinicios. El uso del protocolo MQTT con QoS diferenciado según el tipo de mensaje demuestra comprensión de las características del protocolo.

**Backend Spring Boot:** El backend actúa eficazmente como intermediario entre dispositivos y aplicación móvil. La implementación de resiliencia ante fallos (broker MQTT caído, base de datos no disponible) garantiza que el sistema continúa funcionando parcialmente. La arquitectura en capas y el uso de Spring Data JPA facilitan el mantenimiento y la extensión futura. El uso de Postman durante el desarrollo permitió validar exhaustivamente todos los endpoints antes de la integración con Android.

**Aplicación Android:** La interfaz desarrollada con Jetpack Compose es moderna, fluida y sigue las directrices de Material Design 3. La implementación de modo oscuro con control dual (automático del sistema y manual por el usuario) mejora significativamente la experiencia de usuario. La arquitectura MVVM con Hilt y Clean Architecture demuestra conocimiento de las mejores prácticas de desarrollo Android. Las actualizaciones automáticas, el manejo de estados offline y la gestión administrativa de dispositivos proporcionan una experiencia de usuario completa y profesional.

#### 6.1.2. Aprendizajes Técnicos

Durante el desarrollo de DAMIOT se han adquirido y consolidado numerosos conocimientos técnicos:

- **Comunicación IoT:** Comprensión profunda del protocolo MQTT, sus niveles de QoS, el mecanismo de Last Will & Testament y los patrones publish-subscribe. Experiencia práctica con Eclipse Mosquitto como broker.

- **Programación de microcontroladores:** Desarrollo de firmware para ESP32 con gestión de conectividad WiFi, cliente MQTT, lectura de sensores digitales y control de actuadores. Comprensión de las limitaciones de memoria y recursos en dispositivos embebidos.

- **Backend con Spring Boot:** Diseño e implementación de APIs REST, integración con bases de datos mediante JPA, manejo de clientes MQTT en aplicaciones Java y gestión de concurrencia y operaciones asíncronas. Testing exhaustivo de endpoints con Postman.

- **Desarrollo Android moderno:** Dominio de Jetpack Compose para crear UIs declarativas, implementación de arquitecturas limpias (MVVM, Clean Architecture), uso de inyección de dependencias con Hilt, gestión de estados reactivos con StateFlow, y persistencia de preferencias con DataStore.

- **Resiliencia de sistemas:** Implementación de mecanismos de recuperación ante fallos, reconexiones automáticas, sincronización de estados y operación en modo degradado.

- **Metodología de desarrollo:** Aplicación práctica de control de versiones con Git, organización modular del código, gestión de configuraciones y documentación técnica exhaustiva.

#### 6.1.3. Reflexión Personal

Este proyecto ha sido enormemente enriquecedor desde el punto de vista técnico y personal. La posibilidad de integrar mi pasión por la electrónica y los microcontroladores con los conocimientos adquiridos en el ciclo de DAM ha resultado muy motivadora.

Uno de los mayores aprendizajes ha sido comprender la importancia de la resiliencia en sistemas distribuidos. Un sistema real debe anticipar y gestionar fallos de red, reinicios inesperados y pérdidas de conexión. Implementar estas capacidades desde el inicio, en lugar de añadirlas posteriormente, ha demostrado ser la decisión correcta.

También he valorado la importancia de mantener una organización clara del código y una nomenclatura consistente. La decisión de usar MACs en los tópicos MQTT para soporte multi-dispositivo real, aunque requirió refactorización, mejoró significativamente la escalabilidad del sistema.

El desarrollo en ciclos concentrados de 4 horas diarias permitió mantener el foco sin saturación, demostrando que la calidad del tiempo invertido es más importante que la cantidad total de horas.

Finalmente, el proyecto ha reforzado mi interés en continuar desarrollando mi carrera profesional en el ámbito del desarrollo de aplicaciones móviles y sistemas IoT, áreas con gran proyección y demanda en el mercado tecnológico actual.

### 6.2. Líneas Futuras

Aunque DAMIOT cumple con todos los requisitos establecidos, existen numerosas posibilidades de mejora y ampliación que podrían implementarse en el futuro:

#### 6.2.1. Mejoras de Seguridad

- **Autenticación de usuarios:** Implementar un sistema de login con JWT tokens para proteger la API REST y controlar quién puede acceder a los dispositivos.

- **Cifrado MQTT:** Configurar Mosquitto con TLS/SSL para cifrar la comunicación entre ESP32 y backend.

- **HTTPS:** Configurar certificados SSL en el backend para comunicación cifrada con la aplicación móvil.

- **Autenticación MQTT:** Configurar usuario y contraseña en el broker para evitar conexiones no autorizadas.

#### 6.2.2. Ampliación Funcional

- **Más tipos de sensores:** Añadir soporte para sensores de movimiento (PIR), luz (LDR), presión barométrica (BMP280), calidad del aire (MQ-135), etc.

- **Más actuadores:** Controlar relés para cargas de potencia, servomotores, displays LCD, buzzers, etc.

- **Automatizaciones:** Implementar reglas condicionales (si temperatura > 30°C, encender ventilador) configurables desde la app.

- **Notificaciones push:** Alertas al móvil cuando se detecten condiciones anómalas (temperatura muy alta, dispositivo offline, etc.).

- **Gráficas históricas:** Visualización de tendencias de temperatura y humedad a lo largo del tiempo con librerías de gráficos como MPAndroidChart.

#### 6.2.3. Escalabilidad

- **Despliegue en cloud:** Migrar el backend a AWS, Azure o Google Cloud para acceso desde Internet, no solo red local.

- **Base de datos time-series:** Usar InfluxDB o TimescaleDB optimizadas para datos temporales, mejorando rendimiento con grandes volúmenes.

- **Clustering del broker:** Configurar múltiples instancias de Mosquitto para alta disponibilidad.

- **Contenedorización:** Dockerizar backend y broker MQTT para facilitar despliegue y portabilidad.

#### 6.2.4. Experiencia de Usuario

- **Configuración dinámica de IP:** Implementar una pantalla de configuración en la aplicación Android que permita al usuario especificar la dirección IP del backend, persistiendo la configuración con DataStore.

- **Personalización:** Permitir al usuario renombrar dispositivos, agruparlos por habitaciones, personalizar iconos.

- **Widgets:** Crear widgets de Android para ver temperatura/humedad sin abrir la app.

- **Multiidioma:** Añadir soporte para inglés y otros idiomas.

#### 6.2.5. Otras Plataformas

- **Aplicación iOS:** Desarrollar versión nativa para iPhone/iPad usando Swift y SwiftUI.

- **Web app:** Crear dashboard web con React o Vue.js para monitorización desde PC.

- **Integración con asistentes:** Soporte para Google Assistant o Alexa para control por voz.

#### 6.2.6. Optimizaciones Técnicas

- **Deep sleep en ESP32:** Implementar modo de bajo consumo para aplicaciones alimentadas por batería.

- **OTA updates:** Actualización del firmware ESP32 por WiFi sin necesidad de cable USB.

- **Caché inteligente:** Implementar caché en backend para reducir consultas a la base de datos.

- **Tests automatizados:** Añadir tests unitarios y de integración para todos los componentes.

### 6.3. Valoración Final

DAMIOT ha sido un proyecto técnicamente desafiante y gratificante. Ha permitido aplicar de forma práctica y real los conocimientos adquiridos en el ciclo de DAM, demostrando que es posible crear soluciones tecnológicas complejas integrando múltiples disciplinas.

El sistema resultante es funcional, robusto y extensible. Cumple con los objetivos académicos establecidos y, al mismo tiempo, constituye una base sólida para posibles desarrollos futuros más ambiciosos.

La experiencia de trabajar con tres plataformas tecnológicas distintas (microcontroladores, backend Java, aplicaciones móviles) ha proporcionado una visión integral del desarrollo de software moderno y ha demostrado la importancia de entender tanto el hardware como el software en la era del IoT.

Este proyecto confirma mi interés en continuar desarrollando mi carrera profesional en el ámbito del desarrollo de aplicaciones móviles y sistemas IoT, áreas con gran proyección y demanda en el mercado tecnológico actual.

---

## Capítulo 7. Bibliografía

A continuación se detallan todas las fuentes bibliográficas, documentación técnica y recursos web consultados durante el desarrollo del proyecto DAMIOT, siguiendo el formato APA.

### 7.1. Documentación Oficial

Espressif Systems. (2025). *ESP32 technical reference manual*. https://www.espressif.com/en/products/socs/esp32

Spring. (2025). *Spring Boot reference documentation*. https://spring.io/projects/spring-boot

Google. (2025). *Android developers documentation*. https://developer.android.com

Google. (2025). *Jetpack Compose documentation*. https://developer.android.com/jetpack/compose

Eclipse Foundation. (2025). *Eclipse Mosquitto documentation*. https://mosquitto.org/documentation/

OASIS. (2019). *MQTT Version 5.0 specification*. https://docs.oasis-open.org/mqtt/mqtt/v5.0/mqtt-v5.0.html

Oracle. (2025). *MySQL 8.4 reference manual*. https://dev.mysql.com/doc/refman/8.4/en/

### 7.2. Libros y Publicaciones

Kolban, N. (2018). *Kolban's book on ESP32*. Leanpub. https://leanpub.com/kolbans-book-on-esp32

Walls, C. (2024). *Spring in action* (7ª ed.). Manning Publications.

Smyth, N. (2024). *Jetpack Compose 1.7 essentials*. Payload Media.

Martin, R. C. (2017). *Clean architecture: A craftsman's guide to software structure and design*. Prentice Hall.

### 7.3. Recursos Web y Tutoriales

Random Nerd Tutorials. (2024). *ESP32 MQTT tutorial with Arduino IDE*. Random Nerd Tutorials. https://randomnerdtutorials.com/esp32-mqtt-publish-subscribe-arduino-ide/

Baeldung. (2024). *Spring Boot with MQTT*. Baeldung. https://www.baeldung.com/spring-boot-mqtt

Lackner, P. (2024). *Jetpack Compose navigation tutorial* [Canal de YouTube]. YouTube. https://www.youtube.com/@PhilippLackner

Stack Overflow. (2025). *Diversos posts sobre Spring Boot, MQTT y Android*. Stack Overflow. https://stackoverflow.com

### 7.4. GitHub y Repositorios Open Source

knolleary. (2025). *PubSubClient library for Arduino* [Librería]. GitHub. https://github.com/knolleary/pubsubclient

adafruit. (2025). *DHT sensor library* [Librería]. GitHub. https://github.com/adafruit/DHT-sensor-library

bblanchon. (2025). *ArduinoJson* [Librería]. GitHub. https://github.com/bblanchon/ArduinoJson

square. (2025). *Retrofit* [Cliente HTTP]. GitHub. https://github.com/square/retrofit

### 7.5. Herramientas y Software

JetBrains. (2025). *IntelliJ IDEA Community Edition* (Versión 2025.3) [Software]. https://www.jetbrains.com/idea/

Google. (2025). *Android Studio* (Versión Ladybug 2024.2.1) [Software]. https://developer.android.com/studio

Arduino. (2025). *Arduino IDE* (Versión 2.3.3) [Software]. https://www.arduino.cc/en/software

Laragon. (2025). *Laragon* (Versión 6.0) [Software]. https://laragon.org/

Git. (2025). *Git version control system* [Software]. https://git-scm.com/

Postman. (2025). *Postman API platform* [Software]. https://www.postman.com/

---

**Nota:** Todas las referencias han sido formateadas siguiendo las normas APA (7ª edición). Los recursos digitales están actualizados a diciembre de 2025. Algunos recursos, especialmente documentación oficial y estándares técnicos, se actualizan periódicamente, por lo que las versiones consultadas pueden diferir de versiones posteriores.

---

**DAMIOT - Sistema IoT Multiplataforma**

**Emilio José Salmerón Arjona**

**CFGS Desarrollo de Aplicaciones Multiplataforma - IES Azarquiel, Toledo**

**Curso 2025/2026**
