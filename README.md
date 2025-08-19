# Transaction API

## Visão Geral

Esta API permite gerenciar transações de compra e obter taxas de câmbio para valores em diferentes moedas. Foi desenvolvida para atender aos requisitos de armazenamento de transações e conversão de moedas usando a API Treasury Reporting Rates of Exchange.

## Funcionalidades Principais

1. **Armazenamento de Transações**: Cria e armazena transações de compra com descrição, data da transação e valor em dólares americanos.
2. **Conversão de Moeda**: Recupera transações e converte seus valores para moedas suportadas pela API Treasury Reporting Rates of Exchange.

## Requisitos Técnicos

- Java 21+
- Spring Boot 3.x
- Maven 3.x
- Banco de dados H2 (em memória para desenvolvimento/testes)

## Como Executar

### Pré-requisitos
- JDK 21 ou superior
- Maven 3.6 ou superior

### Comandos

```bash
# Clonar o repositório
git clone https://github.com/fabio-siqueira/transaction.git
cd transaction

# Compilar e executar os testes
mvn clean package

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## Endpoints

### Criar Transação

```
POST /api/transactions
```

**Corpo da Requisição:**
```json
{
  "description": "Exemplo de compra",
  "transactionDate": "2023-12-15",
  "amount": 150.00
}
```

**Validações:**
- Description: obrigatório, máximo 50 caracteres
- TransactionDate: obrigatório, formato de data válido
- Amount: obrigatório, valor positivo arredondado para centavos

**Resposta (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "description": "Exemplo de compra",
  "transactionDate": "2023-12-15",
  "amount": 150.00
}
```

### Obter Transação com Conversão de Moeda

```
GET /api/transactions/{id}/exchange?targetCurrency={code}
```

**Parâmetros:**
- id: ID da transação (path parameter)
- targetCurrency: Código da moeda alvo (query parameter)

**Resposta (200 OK):**
```json
{
  "transaction": {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "description": "Exemplo de compra",
    "transactionDate": "2023-12-15",
    "amount": "150.00"
  },
  "exchangeRateData": {
    "countryCurrencyDesc": "Brazil-Real",
    "exchangeRate": "0.85",
    "recordDate": "2023-10-25"
  },
  "convertedAmount": "127.50"
}
```

**Regras de Conversão:**
- Utiliza taxa de câmbio menor ou igual à data da compra, dentro dos últimos 6 meses
- Retorna erro se não houver taxa disponível no período
- Valor convertido é arredondado para duas casas decimais

## OpenAPI/Swagger
A documentação da API está disponível em `http://localhost:8080/swagger-ui/index.html` após a execução da aplicação. Você pode visualizar todos os endpoints, parâmetros e exemplos de requisições/respostas.

<img src="https://raw.githubusercontent.com/fabio-siqueira/transaction/main/swagger.png" alt="Swagger UI" width="800"/>

## Arquitetura

A aplicação segue uma arquitetura em camadas:

1. **Controllers**: Endpoints REST para receber requisições
2. **Services**: Lógica de negócio e integração com API externa
3. **Repositories**: Acesso ao banco de dados
4. **Models/Entities**: Objetos de domínio e entidades
5. **DTOs**: Objetos para transferência de dados

## Testes

O projeto inclui testes unitários e de integração:

- **Testes Unitários**: Verificam o comportamento de componentes isolados
- **Testes de Integração**: Verificam o fluxo completo da aplicação

Para executar os testes:
```bash 
  mvn test 
```

## Considerações Sobre a Implementação

- Todos os requisitos funcionais foram implementados e testados
- A aplicação foi projetada para ser executada sem necessidade de configurações externas
- A segurança não foi implementada nesta versão, sendo um ponto a ser considerado em futuras iterações

## Limitações e Melhorias Futuras

- Implementar segurança (OAuth2, JWT)
- Adicionar suporte a Docker