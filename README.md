# 🎬 CinemaApp

Um sistema web de gerenciamento de cinema com autenticação de usuários, cadastro e visualização de filmes, e reservas de ingressos. Desenvolvido com **Spring Boot**, **Thymeleaf**, **MySQL** e **Spring Security**.

---

⚠️ **Atenção:** A funcionalidade de "Esqueceu a senha" ainda não está funcionando corretamente. Estamos trabalhando para corrigir o problema. 

---

## ✅ Funcionalidades já implementadas

- Cadastro e login de usuários com senha criptografada (BCrypt)
- Proteção de rotas com Spring Security
- Listagem de filmes disponíveis com capas ilustrativas
- Visualização de detalhes de filmes com layout aprimorado
- Cadastro de novos filmes (apenas ADMIN)
- Edição e deleção de filmes (apenas ADMIN)
- Deleção de filmes cadastrados (apenas ADMIN)
- Upload de imagens dos filmes com armazenamento local
- Definição e cálculo do valor do ingresso por filme
- Exibição do valor total da reserva de acordo com a quantidade de ingressos
- Formulário de reserva com seleção de quantidade
- Listagem de reservas por usuário
- Cancelamento de reservas (status atualizado no banco de dados)
- Integração com CSRF para segurança de formulários
- Layout responsivo com Bootstrap
- Função de mostrar/ocultar senha no formulário de registro
- Navbar com avatar do usuário:
  - Menu suspenso com opções de "Perfil", "Configurações" e "Logout"
- Página de perfil do usuário para visualização e edição de dados
- Página de configurações com opções de alteração de senha e informações pessoais

## 🔧 Funcionalidades em desenvolvimento

- [ ] Correção da funcionalidade "Esqueceu a senha"
- [ ] Buscar filme por filtros como título, horário ou classificação
- [ ] Painel de administração mais robusto
- [ ] Página de perfil do usuário com histórico de reservas
- [ ] Validações avançadas nos formulários
- [ ] Melhorias na visualização das reservas para o usuário

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
