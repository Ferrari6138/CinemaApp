# 🎬 CinemaApp

Sistema web completo de gerenciamento de cinema com autenticação de usuários, catálogo de filmes, sessões e reserva de ingressos com seleção de assentos.

🚀 **[Demo ao vivo](https://cinema-app-production-b591.up.railway.app)**

---

## 📸 Funcionalidades

- **Catálogo de filmes** com pôsteres, descrição, classificação etária e duração
- **Sessões** por sala com data/hora e capacidade
- **Reserva de ingressos** com seleção visual de assentos
- **Painel do usuário** com histórico e cancelamento de reservas
- **Painel administrativo** para gerenciar filmes, sessões e reservas
- **Autenticação completa** com cadastro, login, logout e recuperação de senha
- **Upload de imagens** de filmes com armazenamento local
- Layout **responsivo** com Bootstrap 5

## 🔒 Perfis de acesso

| Perfil | Permissões |
|--------|-----------|
| `ADMIN` | Criar, editar e remover filmes e sessões. Ver todas as reservas. |
| `USER`  | Visualizar filmes, reservar ingressos, cancelar e ver histórico. |

**Contas de teste:**
- Admin: `admin@cinema.com` / `Admin123`
- Usuário: `cliente@cinema.com` / `Cliente123`

## 🛠️ Tecnologias

- **Java 21** + **Spring Boot 3.4**
- **Spring Security** — autenticação e autorização por roles
- **Spring Session JDBC** — sessões persistidas no banco
- **Thymeleaf** — templates server-side
- **PostgreSQL** (Neon) — banco de dados em cloud
- **JPA / Hibernate** — ORM
- **Bootstrap 5** — UI responsiva
- **Maven** — gerenciamento de dependências

## 🚀 Rodando localmente

### Pré-requisitos
- Java 21+
- Maven 3.9+
- PostgreSQL (ou conta no [Neon](https://neon.tech))

### Passos

```bash
# Clone o repositório
git clone https://github.com/Ferrari6138/CinemaApp.git
cd CinemaApp/Backend

# Configure o banco em src/main/resources/application.properties
# Edite as propriedades spring.datasource.*

# Execute
./mvnw spring-boot:run
```

Acesse: `http://localhost:8080`

> Na primeira execução, o sistema cria automaticamente os usuários, gêneros, filmes e sessões de teste.

## 📁 Estrutura do projeto

```
Backend/
├── src/main/java/com/cinemaapp/
│   ├── config/          # SecurityConfig, DataInitializer
│   ├── controllers/     # FilmeController, ReservaController, ...
│   ├── models/          # Filme, Sessao, Reserva, Usuario, ...
│   ├── repository/      # Interfaces JPA
│   ├── service/         # Regras de negócio
│   └── security/        # CustomUserDetailsService
└── src/main/resources/
    ├── templates/       # Views Thymeleaf
    └── static/          # CSS, JS, uploads
```

## ☁️ Deploy

O projeto está hospedado no **Railway** com banco de dados **Neon PostgreSQL**.

Para fazer novo deploy:
```bash
cd Backend
railway up --service cinema-app
```

## 🔧 Variáveis de ambiente (produção)

| Variável | Descrição |
|----------|-----------|
| `PORT` | Porta do servidor (padrão: 8080) |
| `COOKIE_SECURE` | `true` em produção HTTPS |
| `MAIL_USER` | E-mail para envio de recuperação de senha |
| `MAIL_PASSWORD` | Senha do e-mail |
