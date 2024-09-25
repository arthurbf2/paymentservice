# Payment Service API 

API feita com Java e Spring Boot. Baseado no desafio [pickpay backend](https://github.com/PicPay/picpay-desafio-backend). 

## Funcionalidades

1. **Realizar Transações**:
   - Um usuário pode transferir fundos para outro, desde que o saldo seja suficiente e o tipo de usuário permita transações (por exemplo, lojistas não podem enviar transações).

2. **Visualização de Transações**:
   - Os usuários podem visualizar o histórico de transações enviadas e recebidas, acessando detalhes como data, valor e destinatário.
   - Links HATEOAS facilitam a navegação entre endpoints, como o histórico completo de transações e transações específicas.

3. **Notificações por E-mail**:
   - Quando uma transação é bem-sucedida, o sistema envia uma notificação por e-mail para o remetente e o destinatário.


## Endpoints

**USERS**
```markdown
GET /users - Lista todos os usuários.
``` 
```json
    {
        "id": "3095a047-9c51-47ec-9256-3849c3bb06ef",
        "name": "Magazine Luiza",
        "email": "magalu@magalu.com",
        "balance": 10000105.00,
        "cpfcnpj": "12345678900",
        "userType": "MERCHANT",
        "transferAllowedForUser": false,
        "links": [
            {
                "rel": "self",
                "href": "http://localhost:8080/users/3095a047-9c51-47ec-9256-3849c3bb06ef"
            }
        ]
    },
```
```markdown
GET /users/{id} - Retorna um usuário específico.
```

**TRANSACTIONS**
```markdown
POST /transactions - Cria uma transação.
```
```json
{
    "amount": "1.00",
    "senderId": "2d857f95-4cce-40f5-8878-48baa193f203",
    "receiverId": "69894b0a-4bcc-46a1-b5f0-4e68772bce06"
}
```
```markdown
GET /users/{userId}/transactions - Lista todas as transações de um usuário.
No final, também lista links relevantes: transações recebidas e transações enviadas do usuário.
```
```json
{
        "id": "be2fc684-5b4b-4e00-a079-2c183dd662dd",
        "amount": 90.00,
        "sender": {
            "id": "804a3676-823f-4bca-9ffc-de76d8a2f3e1",
            "name": "João Santos",
            "email": "joaosantos@gmail.com",
            "balance": 1160.00,
            "cpfcnpj": "63968636007",
            "userType": "CUSTOMER",
            "transferAllowedForUser": true,
            "links": [
                {
                    "rel": "self",
                    "href": "http://localhost:8080/users/804a3676-823f-4bca-9ffc-de76d8a2f3e1"
                }
            ]
        },
        "receiver": {
            "id": "3095a047-9c51-47ec-9256-3849c3bb06ef",
            "name": "Magazine Luiza",
            "email": "magalu@magalu.com",
            "balance": 10000105.00,
            "cpfcnpj": "04817137002",
            "userType": "MERCHANT",
            "transferAllowedForUser": false,
            "links": [
                {
                    "rel": "self",
                    "href": "http://localhost:8080/users/3095a047-9c51-47ec-9256-3849c3bb06ef"
                }
            ]
        },
        "transactionDate": "2024-09-11T20:29:12.584449",
        "status": "SUCCESS",
        "links": [
            {
                "rel": "self",
                "href": "http://localhost:8080/users/3095a047-9c51-47ec-9256-3849c3bb06ef/transactions/be2fc684-5b4b-4e00-a079-2c183dd662dd"
            },
            {
                "rel": "sent transactions",
                "href": "http://localhost:8080/users/3095a047-9c51-47ec-9256-3849c3bb06ef/transactions/sent"
            },
            {
                "rel": "received transactions",
                "href": "http://localhost:8080/users/3095a047-9c51-47ec-9256-3849c3bb06ef/transactions/received"
            }
        ]
    },
```

**Tecnologias**

* Spring Boot
* Spring Data JPA
* PostgreSQL
* ControllerAdvice
* Hibernate Validator
* RabbitMQ
* JUnit e Mockito

