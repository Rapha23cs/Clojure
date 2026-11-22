# Clojure - Todo List

> 🎥 **[Assista ao Vídeo de Apresentação do Projeto](https://drive.google.com/file/d/1jbWdhFySB6d4zVpPAkXKbTwtgzDBpQsG/view?usp=sharing)**

---

## 👥 Autores

* **Raphael Camara Sa**
* **Daniel Lucas Silva Aires**

---

## 📚 Tutorial de Referência

* **Tutorial consultado:** [LambdaGeo Tutorials — Clojure Full-Stack](https://lambdageo-edu.github.io/lambdageo-tutorials/clojure/)

---

## 📝 Descrição do Projeto

Aplicação web **Full-Stack** de gerenciamento de tarefas (**Todo List**) com operações **CRUD** completas (criação, listagem, marcação de conclusão/toggle e remoção) e persistência real em banco de dados relacional.

### Tecnologias Utilizadas

* **Backend (Clojure):**
  * **Ring** (`ring-core`, `ring-jetty-adapter`, `ring-json`, `ring-cors`) para servidor HTTP Jetty, tratamento de JSON e configuração de CORS
  * **Reitit** para roteamento *data-driven* da API REST
  * **next.jdbc** para acesso ao banco de dados
* **Frontend (ClojureScript):**
  * **Reagent 2.0** sobre **React 19** para interface reativa baseada em componentes e estado com `r/atom`
  * **Shadow-CLJS** para compilação ClojureScript, servidor de desenvolvimento e *hot-reload*
  * **cljs-http** e `core.async` para comunicação assíncrona com a API
* **Banco de Dados:**
  * **SQLite** (`sqlite-jdbc`) com criação automática da tabela `todos` em `prod.db`

---

## 🚀 Instruções de Execução Local

### 1. Pré-requisitos

Certifique-se de ter as seguintes ferramentas instaladas em sua máquina:

* **Java JDK** (versão 17 ou superior) com `JAVA_HOME` e `java` no `PATH`
* **Clojure CLI** (`clj` / `clojure`)
* **Node.js** e **npm**
* **Git**

### 2. Clonar o Repositório e Instalar Dependências

```bash
git clone https://github.com/Rapha23cs/Clojure.git
cd Clojure
npm install
```

### 3. Iniciar os Dois Servidores Simultaneamente

Abra **dois terminais** na pasta raiz do projeto:

* **Terminal 1 (Backend — API REST na porta `3000`):**
  ```bash
  clj -M:run
  ```
  > Aguarde a mensagem `Servidor iniciado na porta 3000`. O banco SQLite (`prod.db`) será inicializado automaticamente.

* **Terminal 2 (Frontend — Shadow-CLJS na porta `8000`):**
  ```bash
  npx shadow-cljs watch frontend
  ```
  > *(Também é possível executar via `npm run frontend:watch`)*. Aguarde a mensagem `[:frontend] Build completed`.

### 4. Acessar a Aplicação

Com ambos os servidores em execução, abra no navegador:

* **Interface Web:** `http://localhost:8000`
* **Endpoints da API (`http://localhost:3000`):**
  * `GET /api/hello` — Verificação de saúde da API
  * `GET /api/todos` — Lista todas as tarefas
  * `POST /api/todos` — Cria uma nova tarefa (`title` e `description`)
  * `GET /api/todos/:id` — Busca uma tarefa por ID
  * `PUT / PATCH /api/todos/:id` — Atualiza uma tarefa
  * `PATCH / PUT /api/todos/:id/toggle` — Alterna o status de conclusão
  * `DELETE /api/todos/:id` — Remove uma tarefa

---

## 📋 Jornada Incremental

1. **Fundação**: Verificação do ambiente, Git e `.gitignore`
2. **Backend mínimo**: Servidor "Hello World" com Jetty e Reitit
3. **Banco em memória**: Criando e lendo tarefas com `atom`
4. **Frontend reativo isolado**: Interface dinâmica com Reagent
5. **Integração full-stack**: Comunicação via API REST e CORS
6. **Banco real**: Migração para persistência SQLite com `next.jdbc`
7. **CRUD completo**: Atualização (toggle) e remoção de tarefas
8. **Documentação**: `README.md` completo com vídeo, integrantes e guia de execução
