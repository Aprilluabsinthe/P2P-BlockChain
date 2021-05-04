# Lab 4: Choose-your-Own Distributed System

As indicated in the lab writeup, you have an opportunity to design and implement your own distributed system, service, or application.  There is a lot of flexibility, so we aren't providing you with starter code.  This nearly empty repo should be your starting point, and you can build off any of your previous lab outcomes.  As usual, please use this repo for your project work, so the course staff can help you along the way


## Description of project topic, goals, and tasks

...

## Dependencies to run this code

...

## Description of tests and how to run them

1. Test for...

```
make test
```

## usage

### start
```
/bin/zsh startServer.sh
```


### API
- Current BlockChain(GET)

  ```
  curl http://localhost:1234/chain
  ```

- Create Wallet(POST)

  ```
  curl -i -X POST http://localhost:1234/wallet/create
  ```

- Mine(POST)

  ```
  curl --data "address=281505BD9BF8E2C8B12D373114176F75" http://localhost:1234/mine
  ```

- Transaction(POST)

  ```
  curl -H "Content-type:application/json" --data '{"sender": "1F13EC6B7F7D82FF222171C74C917CB6","recipient": "281505BD9BF8E2C8B12D373114176F75","amount": 10}' http://localhost:1234/transactions/new
  ```

- Query for UXTOs(GET)

  ```
  curl http://localhost:1234/transactions/get/unpacked
  ```

- Query for all Transactions(GET)

  ```
  curl http://localhost:1234/transactions/get/all
  ```

- All wallets(GET)

  ```
  curl http://localhost:1234/wallet/get
  ```

- Wallet Balance(GET)

  ```
  curl "http://localhost:1234/wallet/get/balance?address=1F13EC6B7F7D82FF222171C74C917CB6"
  ```
