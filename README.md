# 🎫 API de Cupons (Coupon API)

Esta é uma API REST para gerenciamento de cupons de desconto. O projeto foi construído seguindo os princípios da **Arquitetura Hexagonal (Ports and Adapters)** e **Domain-Driven Design (DDD)**, garantindo total isolamento da lógica de negócios em relação aos frameworks e ferramentas de infraestrutura.

---

## 🏗️ Arquitetura e Design de Software

A estrutura do projeto foi dividida de forma a respeitar a independência das camadas do núcleo da aplicação:

*   **`application`**: Contém os casos de uso da aplicação e a orquestração dos fluxos de negócio. É responsável por coordenar as operações entre os adaptadores de entrada, o domínio e as portas de saída, além de concentrar os DTOs utilizados na comunicação com a aplicação.
*   **`domain`**: Representa o núcleo do negócio, independente de frameworks e detalhes de infraestrutura. Contém as entidades, enums, exceções e interfaces que representam as portas de saída. É onde estão centralizadas as principais regras e comportamentos do domínio, como validações, sanitização do código e exclusão lógica dos cupons.
*   **`infrastructure`**: Contém os adaptadores e componentes responsáveis pela integração com recursos externos e detalhes técnicos da aplicação. Inclui as implementações de persistência, configurações de infraestrutura e integrações necessárias para conectar o núcleo da aplicação aos recursos externos, como o banco de dados utilizando Spring Data JPA e H2.
*   **`presentation`**: Camada responsável pela exposição da aplicação e pela comunicação com clientes externos. Contém os Controllers REST e os componentes relacionados à entrada e saída de dados, realizando a tradução entre os contratos da API e os objetos utilizados pela camada de aplicação, mantendo o domínio isolado de detalhes HTTP.
*   **`utils`**: Contém componentes e funções auxiliares de uso transversal que não pertencem diretamente às regras de negócio, aos casos de uso ou à infraestrutura. São utilizados para centralizar funcionalidades genéricas e reutilizáveis, evitando duplicação de código nas demais camadas.


### Principais Padrões Aplicados:
*   **Soft Delete (Exclusão Lógica):** A remoção de um cupom altera seu status para `INACTIVE` e a flag `deleted` para `true`, preservando o histórico físico no banco de dados e lançando mensagens informativas personalizadas caso o usuário tente interagir com um cupom já removido.
*   **Imutabilidade no Domínio:** Os atributos da entidade de domínio foram modelados como `final` onde aplicável para garantir a previsibilidade do estado do objeto durante o ciclo de vida da transação.
*   **Bean Validation:** Validação rigorosa na entrada de dados (ex: limite mínimo de desconto de `0.5` via `@DecimalMin`), blindando a API antes mesmo de atingir a camada de serviço.

---

## 🛠️ Tecnologias Utilizadas

*   **Java 17** & **Spring Boot 3.x**
*   **Spring Data JPA** & **Banco de Dados H2** (Em memória)
*   **JUnit 5** & **Mockito** (Testes unitários e de integração de mocks)
*   **JaCoCo** (Métricas de cobertura de código)
*   **Swagger/OpenAPI**
*   **Docker** & **Docker Compose**
*   **Maven**

---

---

## 📦 Como Executar o Projeto (com Docker)

Não é necessário ter o Java, o Maven ou o banco de dados instalados localmente. O projeto utiliza *Multi-Stage Build* no Docker para compilar e empacotar a aplicação de forma limpa.

Certifique-se de ter apenas o **Docker** e o **Docker Compose** instalados.

1.  Clone este repositório e navegue até a pasta raiz.
2.  No terminal, execute o comando abaixo para compilar e subir a aplicação:
    ```bash
    docker-compose up --build
    ```
3.  A API estará pronta para receber requisições HTTP na porta **8080** em: `http://localhost:8080`

### 🔍 Acessando o Banco de Dados (H2 Console)
O banco de dados roda em memória dentro do contêiner. Para auditar visualmente as tabelas e registros:
*   **URL:** `http://localhost:8082` (Acesso direto via porta dedicada)
*   **JDBC URL:** `jdbc:h2:mem:coupon_db`
*   **User Name:** `sa`
*   **Password:** *(Deixe em branco)*

Para derrubar os serviços e os contêineres, utilize o comando:
```bash
docker-compose down
```

---

## Endpoints

| Método | Path        | Descrição |
|--------|-------------|------------|
| POST   | /coupon     | Cria um cupom. Body: `code`, `description`, `discountValue`, `expirationDate`, `published`. Código deve ter exatamente 6 caracteres alfanuméricos após sanitização (especiais removidos). |
| GET    | /coupon/{id} | Retorna o cupom pelo ID. 404 se não existir ou estiver deletado. |
| DELETE | /coupon/{id} | Soft delete do cupom. Retorna 204. 404 se o ID não existir. 422 se o cupom já estiver deletado. |

### Exemplo de criação (POST /coupon)

```json
{
  "code": "ABC123",
  "description": "Desconto de exemplo",
  "discountValue": 0.8,
  "expirationDate": "2026-12-12",
  "published": false
}
```

O campo `expirationDate` é uma **data** (formato `2026-09-13T01:02:34.508Z`).

Resposta (201): `id`, `code` (sanitizado, ex: `ABC123`), `description`, `discountValue`, `expirationDate`, `published`.

## Swagger

http://localhost:8080/swagger-ui.html

## Regras de negócio (domínio)

- **Código**: alfanumérico, **exatamente** 6 caracteres após sanitização; caracteres especiais são removidos antes de validar. Se tiver mais ou menos de 6 alfanuméricos, retorna erro.
- **Desconto**: valor mínimo 0,5.
- **Data de expiração**: não pode ser no passado.
- **Delete**: soft delete (campo `deleted`); não é possível deletar um cupom já deletado (422).

## Testes

```bash
./mvnw test
```

- Testes de **domínio** (`CouponTest`): sanitização do código, validações (desconto, expiração, código longo/curto), soft delete e “já deletado”.
- Testes de **service** (`CouponServiceImplTest`): criação e delete com repositório mockado.
- Testes de **integração** (`CouponControllerTest`): fluxo completo de criação, GET e delete via HTTP.

### Cobertura (≥ 80% nas regras de negócio)

A cobertura é medida com **JaCoCo**. O mínimo de **80% de linhas** é exigido sobre o código de regras de negócio (domínio, service e controller); aplicação principal, DTOs e classes de exceção ficam fora da conta.

**Como ver o relatório:**

1. Rode os testes e gere o relatório:
   ```bash
   ./mvnw clean test jacoco:report
   ```
2. Abra no navegador o HTML:
   ```
   target/site/jacoco/index.html
   ```
   Você verá a cobertura por pacote e por classe (linhas e branches).

**Como garantir o mínimo de 80%:**

O plugin JaCoCo está configurado para **falhar o build** se a cobertura ficar abaixo de 80% no código considerado. Use:

```bash
./mvnw verify
```

Se a cobertura estiver abaixo do mínimo, o build falha com uma mensagem como:
`Rule violated for bundle coupon-api: lines covered ratio is 0.xx, but expected minimum is 0.80`.

O mínimo configurável está em `pom.xml` na propriedade `jacoco.minimum.line.coverage`.
