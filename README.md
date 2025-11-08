# Meeting Planner

Base inicial para o sistema de planejamento de reuniões e tarefas com chat em tempo real.

## Estrutura

```
backend/  -> API Spring Boot (Java 17)
frontend/ -> Aplicação React + Vite
```

## Como executar

### Backend

```bash
cd backend
mvn spring-boot:run
```

Ajuste as credenciais do PostgreSQL em `src/main/resources/application.yml` e execute as migrations Flyway (`V1__init.sql`).

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Por padrão o Vite roda em `http://localhost:5173` e envia as requisições para `/api` (configure um proxy reverso ou use o mesmo domínio do backend).

> Para desenvolvimento local é recomendado configurar o proxy do Vite apontando `/api` e `/ws` para o host do backend.
