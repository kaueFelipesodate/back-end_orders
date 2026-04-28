# CoteFacil

Projeto dividido em duas aplicacoes Spring Boot que atendem a prova tecnica:

- `cotefacil_api1` - gateway de autenticacao JWT e proxy das rotas de pedidos.
- `cotefacil_api2` - CRUD de pedidos e itens.

## Stack

- Java 17
- Spring Boot 3.3.5
- Spring Security
- JWT
- Spring Data JPA
- PostgreSQL
- Swagger/OpenAPI
- Docker

## Credenciais de teste

- `username`: `usuario`
- `password`: `Senha@123`

## Documentacao Swagger

- API 1: `http://localhost:8080/swagger-ui/index.html`
- API 2: `http://localhost:8081/swagger-ui/index.html`
- OpenAPI JSON:
  - API 1: `http://localhost:8080/v3/api-docs`
  - API 2: `http://localhost:8081/v3/api-docs`

## Variaveis de ambiente

### API 1

- `PORT`: porta da aplicacao, padrao `8080`
- `DB_URL`: URL JDBC do banco
- `DB_USERNAME`: usuario do banco
- `DB_PASSWORD`: senha do banco
- `JWT_SECRET`: segredo JWT
- `JWT_EXPIRATION`: expiracao do token em milissegundos, padrao `3600000`
- `JWT_ISSUER`: issuer do token, padrao `br.com.cotefacil_api1`
- `JWT_AUDIENCE`: audience do token, padrao `backend-clients`
- `CORS_ALLOWED_ORIGINS`: origens permitidas no CORS
- `LOGIN_RATE_LIMIT_PER_MINUTE`: limite de tentativas de login por minuto, padrão `10`
- `API2_BASE_URL`: URL base da API 2, padrao `http://localhost:8081`

### Gerar `JWT_SECRET`

Para gerar um valor seguro para o `JWT_SECRET`, use:

```bash
openssl rand -base64 32
````

No Windows PowerShell:

```powershell
[Convert]::ToBase64String((1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

Depois copie o valor gerado e coloque no `docker-compose.yml`:

```yaml
JWT_SECRET: valor_gerado_aqui
```

### API 2

- `PORT`: porta da aplicacao, padrao `8081`
- `DB_URL`: URL JDBC do banco
- `DB_USERNAME`: usuario do banco
- `DB_PASSWORD`: senha do banco

## Como executar

### API 2

```bash
cd cotefacil_api2
./mvnw spring-boot:run
```

### API 1

```bash
cd cotefacil_api1
./mvnw spring-boot:run
```

## Execucao com Docker

Na raiz do repositório:

```bash
docker compose up --build
```

Isso sobe:

- PostgreSQL em `localhost:5432`
- API 1 em `localhost:8080`
- API 2 em `localhost:8081`

### Banco de dados no Docker

- O `docker-compose.yml` usa `postgres` como valor padrão para `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` e `pg_isready`.
- Se você quiser usar outro usuário ou outra senha, ajuste esses três campos no [docker-compose.yml](C:\Users\Kauê Felipe Sodate\IdeaProjects\cotefacil\docker-compose.yml) antes de subir os containers.
- As APIs 1 e 2 também precisam receber os valores corretos em `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` para conseguirem conectar no PostgreSQL.
- Exemplo do padrão atual:
  - banco: `postgres`
  - usuário: `postgres`
  - senha: `postgres`

## Fluxo de uso

1. Faça login em `POST /auth/login` na API 1.
2. Copie o token JWT retornado.
3. Use o token no header `Authorization: Bearer <token>`.
4. Consuma as rotas de pedidos pela API 1, que faz proxy para a API 2.

## Endpoints

### API 1

- `POST /auth/login`
- `GET /api/orders`
- `GET /api/orders/{id}`
- `POST /api/orders`
- `PUT /api/orders/{id}`
- `DELETE /api/orders/{id}`
- `GET /api/orders/{id}/items`
- `POST /api/orders/{id}/items`

### API 2

Os mesmos endpoints de pedidos acima estao disponiveis diretamente na API 2.

## Exemplos

### Login

```bash
curl -X POST http://localhost:8080/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"username\":\"usuario\",\"password\":\"Senha@123\"}"
```

### Criar pedido

```bash
curl -X POST http://localhost:8080/api/orders ^
  -H "Content-Type: application/json" ^
  -H "Authorization: Bearer <token>" ^
  -d "{\"customerName\":\"Cliente Teste\",\"customerEmail\":\"cliente@teste.com\",\"status\":\"PENDING\",\"items\":[{\"productName\":\"Produto A\",\"quantity\":2,\"unitPrice\":10.50}]}"
```

## Observacoes tecnicas

- A API 1 valida o JWT e encaminha as rotas de pedidos para a API 2 via `RestTemplate`.
- A API 2 executa o CRUD no banco.
- No `POST /api/orders` e no `PUT /api/orders/{id}`, se o payload enviar `totalAmount`, a API usa esse valor; se vier `null` ou `0`, a API calcula o total somando os `subtotal` dos itens.
- A listagem de pedidos suporta paginação e filtros via query params.
- Paginação:
  - `page` define a página, começando em `1`
  - `size` define quantos itens virão por página
- Ordenação:
  - `orderBy` define o campo de ordenação
  - `orderDir` define a direção, normalmente `ASC` ou `DESC`
- Filtros:
  - os filtros são enviados como parâmetros no formato `filters[campo]=valor`
  - exemplo: `filters[status]=PENDING`
  - exemplo: `filters[customerName]=Cliente`
- Exemplo completo de listagem:
  - `GET /api/orders?page=1&size=10&orderBy=createdDate&orderDir=DESC&filters[status]=PENDING`
