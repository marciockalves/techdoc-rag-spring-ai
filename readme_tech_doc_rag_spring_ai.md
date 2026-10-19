# Tech Doc RAG - Spring AI & PgVector

Projeto de referência para implementação de uma arquitetura **RAG (Retrieval-Augmented Generation)** local utilizando **Java 21**, **Spring Boot 3**, **Spring AI**, **PostgreSQL (PgVector)** e **Ollama**.

O objetivo desta aplicação é a ingestão de manuais técnicos em PDF, vetorização do conteúdo e disponibilização de consultas semânticas via streaming (SSE).

---

## 🛠️ Tech Stack & Requisitos

- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.x
- **Módulo de IA:** Spring AI (Ollama + PgVector Store)
- **Banco de Dados:** PostgreSQL com extensão `pgvector`
- **Modelos Locais (Ollama):**
  - **Chat / LLM:** `llama3`
  - **Embeddings:** `nomic-embed-text` (Dimensão **768**)
- **Porta da Aplicação:** `8000`

---

## 📐 Arquitetura do Projeto

A aplicação segue os princípios de **Clean Architecture** e **Ports & Adapters (Hexagonal)**:

```text
tech-doc-rag-spring-ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/marciockalves/techdocragspringai/
│   │   │       ├── application/
│   │   │       │   ├── dto/
│   │   │       │   │   └── ChatRequestDTO.java
│   │   │       │   ├── strategy/
│   │   │       │   │   └── ManualSearchStrategy.java
│   │   │       │   └── usecase/
│   │   │       │       └── ChatUseCase.java
│   │   │       ├── domain/
│   │   │       │   └── port/
│   │   │       │       └── VectorPort.java
│   │   │       └── infrastructure/
│   │   │           ├── config/
│   │   │           │   └── AiConfig.java
│   │   │           └── controller/
│   │   │               ├── ChatController.java
│   │   │               └── IngestionController.java
│   │   └── resources/
│   │       └── application.yml
```

---

## 🚀 Principais Implementações Realizadas

1. **Gestão Dinâmica de Dimensões de Embedding:**
   - Injeção das **768 dimensões** do modelo `nomic-embed-text` diretamente via `@Value` no bean `VectorStore` da classe `AiConfig.java`.
   - Remoção de valores *hardcoded* para permitir fácil troca de modelos no futuro.

2. **Pipeline de Ingestão e Vetorização (PDF):**
   - Leitura de manuais em formato PDF na pasta configurada.
   - Divisão dos textos (*chunking*) e geração de embeddings gravados na tabela `vector_store` do PostgreSQL.

3. **Busca Semântica & RAG:**
   - Implementação da estratégia de busca vetorial (`ManualSearchStrategy`) com suporte a limiar de similaridade (*threshold*) e limite de resultados (*top-k*).
   - Inclusão de logs estruturados (SLF4J) para rastreabilidade de requisições, métricas e origens dos documentos consultados.

4. **Endpoint Streaming (SSE):**
   - Disponibilização de respostas em tempo real via **Server-Sent Events** (`Flux<String>`) em `/api/chat/stream`.

---

## ⚙️ Configurações Principais (`application.yml`)

```yaml
server:
  port: 8000

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tech_doc_rag
    username: postgres
    password: postgres_password
  ai:
    ollama:
      base-url: http://localhost:11434
      chat:
        options:
          model: llama3
      embedding:
        options:
          model: nomic-embed-text
    vectorstore:
      pgvector:
        dimensions: 768
        initialize-schema: true

app:
  ai:
    search:
      top-k: 4
      threshold: 0.70
```

---

## 📡 Endpoints da API

### 1. Ingestão de Manuais
Realiza o fatiamento e a vetorização dos arquivos PDF.

- **URL:** `POST http://localhost:8000/api/maintenance/ingest`
- **Response:** `200 OK` (Mensagem de confirmação)

### 2. Chat / Consulta em Streaming
Realiza a busca vetorial no PostgreSQL e responde via streaming.

- **URL:** `POST http://localhost:8000/api/chat/stream`
- **Headers:** `Content-Type: application/json`
- **Body:**
```json
{
  "conversationId": "sessao-teste-1",
  "message": "Quais são os procedimentos de substituição de memória RAM no manual?"
}
```

---

## 🧹 Manutenção e Resolução de Problemas

Se for necessário redefinir a tabela de vetores para alterar dimensões ou reindexar documentos:

```sql
-- Executar no PostgreSQL (banco tech_doc_rag)
DROP TABLE IF EXISTS vector_store CASCADE;
```

---

*Projeto desenvolvido para fins de estudo, consolidação da stack Spring AI e exploração de arquiteturas Local-First RAG.*