# 🐳 HERRERA ERP - Docker Setup

Sistema completo containerizado con Docker Compose.

## 📋 Prerequisitos

- Docker Desktop instalado y corriendo
- **Nada más** (no necesitas Java, Node, PostgreSQL)

## 🚀 Inicio Rápido

### 1. Construir y levantar todo

```bash
docker-compose up --build
```

Esto:
- ✅ Crea la base de datos PostgreSQL
- ✅ Ejecuta schema.sql y seed-data.sql automáticamente
- ✅ Compila y levanta el backend Spring Boot
- ✅ Compila y levanta el frontend React

### 2. Acceder a la aplicación

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080
- **PostgreSQL:** localhost:5432

### 3. Credenciales por defecto

```
Usuario: admin
Contraseña: herrera2026
```

## 🛠️ Comandos Útiles

### Levantar en background
```bash
docker-compose up -d
```

### Ver logs
```bash
docker-compose logs -f
```

### Ver logs de un servicio específico
```bash
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f database
```

### Detener todo
```bash
docker-compose down
```

### Detener y eliminar volúmenes (⚠️ borra la BD)
```bash
docker-compose down -v
```

### Rebuild solo un servicio
```bash
docker-compose up --build backend
docker-compose up --build frontend
```

### Entrar a un contenedor
```bash
docker exec -it herrera-backend sh
docker exec -it herrera-frontend sh
docker exec -it herrera-db psql -U postgres -d herrera_erp
```

## 🔧 Configuración

### Variables de Entorno (Opcional)

1. Copiar ejemplo:
```bash
cp .env.example .env
```

2. Editar `.env` con tus credenciales de Cloudinary (si lo vas a usar)

### Cloudinary (Opcional)

Si quieres habilitar subida de imágenes:
1. Regístrate en https://cloudinary.com (gratis)
2. Edita `.env` con tus credenciales
3. Rebuild backend: `docker-compose up --build backend`

## 📊 Health Checks

Todos los servicios tienen health checks:
- **Database:** Verifica que PostgreSQL responda
- **Backend:** Verifica `/actuator/health`
- **Frontend:** Verifica que nginx responda

Ver estado:
```bash
docker-compose ps
```

## 🐛 Troubleshooting

### El backend no levanta
```bash
# Ver logs completos
docker-compose logs backend

# Verificar que la BD esté lista
docker-compose ps database
```

### El frontend no conecta al backend
```bash
# Verificar que nginx esté proxy correctamente
docker exec -it herrera-frontend cat /etc/nginx/conf.d/default.conf
```

### Recrear base de datos
```bash
docker-compose down -v
docker-compose up --build database
```

### Cambios en código no se reflejan
```bash
# Rebuild el servicio correspondiente
docker-compose up --build backend
# o
docker-compose up --build frontend
```

## 📦 Arquitectura

```
┌─────────────────┐
│   FRONTEND      │  Puerto 80
│  React + Vite   │  nginx
│  + nginx        │
└────────┬────────┘
         │ /api/*
         ▼
┌─────────────────┐
│    BACKEND      │  Puerto 8080
│  Spring Boot    │  API REST
│  Java 17        │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│   DATABASE      │  Puerto 5432
│  PostgreSQL 15  │
└─────────────────┘
```

## 🎯 Siguiente Paso

Una vez que Docker esté corriendo:
1. Abre http://localhost en tu navegador
2. Login con `admin` / `herrera2026`
3. ¡Empieza a usar el sistema!

## 🔐 Producción

Para producción:
1. Cambiar `JWT_SECRET` en `docker-compose.yml`
2. Cambiar contraseñas de PostgreSQL
3. Configurar dominio real en nginx
4. Usar docker-compose.prod.yml separado
