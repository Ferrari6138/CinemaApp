# 🎬 CinemaApp

Um sistema web de gerenciamento de cinema com autenticação de usuários, cadastro e visualização de filmes, e reservas de ingressos. Desenvolvido com **Spring Boot**, **Thymeleaf**, **MySQL** e **Spring Security**.

## ✅ Funcionalidades já implementadas

- Cadastro e login de usuários com senha criptografada (BCrypt)
- Proteção de rotas com Spring Security
- Listagem de filmes disponíveis
- Visualização de detalhes de filmes
- Cadastro de novos filmes (apenas ADMIN)
- Edição e deleção de filmes (apenas ADMIN)
- Deleção de filmes cadastrados(apenas ADMIN)
- Formulário de reserva com seleção de quantidade
- Listagem de reservas por usuário
- Cancelamento de reservas (status atualizado no banco de dados)
- Integração com CSRF para segurança de formulários
- Layout responsivo com Bootstrap
- Função de mostrar/ocultar senha no formulário de registro

## 🔧 Funcionalidades em desenvolvimento

- [ ] Definir e calcular valor do ingresso por filme
- [ ] Buscar filme por filtros como título, horário ou classificação
- [ ] Painel de administração mais robusto
- [ ] Página de perfil do usuário com histórico de reservas
- [ ] Upload de imagens dos filmes
- [ ] Validações avançadas nos formulários

## 🔒 Regras de acesso

- `ADMIN`: Pode adicionar, editar e deletar filmes
- `USER`: Pode apenas visualizar e reservar

## 💡 Tecnologias utilizadas

- Java 17
- Spring Boot 3
- Spring Security
- Thymeleaf
- MySQL
- JPA / Hibernate
- Bootstrap 5
