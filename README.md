# 🛒 EcoPreço

**EcoPreço** é uma aplicação web que compara preços de produtos sustentáveis entre diferentes supermercados, facilitando o consumo consciente e acessível. Desenvolvido como parte do Projeto Integrador III do curso de Análise e Desenvolvimento de Sistemas da Faculdade Municipal de Palhoça.

---

## 📌 Funcionalidades

- ✅ Cadastro de produtos
- ✅ Comparação de preços entre supermercados
- ✅ Histórico de variações de preços
- ✅ Busca por categoria
- ✅ Avaliação de produtos
- 🚧 Lista de compras (em desenvolvimento)
- 🚧 Sugestões baseadas em localização (em desenvolvimento)

---

## 🏗️ Estrutura do Projeto

```
ecopreco/
├── ecopreco-backend/     # API REST em Java com Spring Boot
├── ecopreco-frontend/    # Interface web (HTML, CSS, JS)
```

---

## 🔧 Tecnologias Utilizadas

| Backend              | Frontend             | Ferramentas de Apoio     |
|----------------------|----------------------|---------------------------|
| Java 21              | HTML5, CSS3, JS      | Postman, Trello           |
| Spring Boot          | Fetch API            | GitHub, VS Code           |
| Spring Data JPA      | Bootstrap (futuro)   | Figma (protótipos)        |
| MySQL + Hibernate    |                      | HeidiSQL (BD local)       |

---

## ⚙️ Como Rodar o Projeto

### 🔹 Backend (Spring Boot)

1. Configure seu banco MySQL:

```sql
CREATE DATABASE ecopreco;
```

2. Altere as credenciais no arquivo `application.properties`:

```properties
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

3. No terminal, vá até a pasta `ecopreco-backend` e execute:

```bash
mvn spring-boot:run
```

O backend será iniciado na porta `8080`.

---

### 🔹 Frontend (HTML/CSS/JS)

1. Vá até a pasta `ecopreco-frontend`
2. Abra o arquivo `index.html` com Live Server ou servidor local (porta 3000)
3. A interface consumirá os dados do backend via `fetch`

---

## 🔐 Configurações de CORS

No `application.properties`:

```properties
spring.web.cors.allowed-origins=http://localhost:3000
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
```

---

## 📂 Endpoints principais (API REST)

| Método | Endpoint                   | Descrição                        |
|--------|----------------------------|----------------------------------|
| GET    | `/produtos`                | Lista todos os produtos          |
| POST   | `/produtos`                | Cadastra um novo produto         |
| GET    | `/produtos/comparar`       | Compara preços por nome          |
| GET    | `/produtos/categoria`      | Busca por categoria              |
| GET    | `/produtos/{id}/historico` | Exibe histórico de preços        |
| POST   | `/avaliacoes`              | Avalia um produto                |

---

## 📸 Protótipos (Figma)

> *Em breve será disponibilizado o link com os protótipos de média e alta fidelidade.*

---

## 👥 Equipe

- [Dauda Sanhá](https://github.com/Daudasanha)
- Adalbino Caúncra Gomes Fernandes

---

## 📃 Licença

Este projeto está sob a licença MIT.  
Sinta-se livre para usar, estudar e modificar para fins educacionais.

---

> Desenvolvido com 💻 + ☕ + 💚 por estudantes da Faculdade Municipal de Palhoça.
