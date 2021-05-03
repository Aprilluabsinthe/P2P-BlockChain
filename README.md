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
  curl --data "address=352572D908368BF8DC337A778A4B3E75" http://localhost:1234/mine
  ```

- Transaction(POST)

  ```
  curl -H "Content-type:application/json" --data '{"sender": "d4e44223434sdfdgerewfd3fefe9dfe","recipient": "45adiy5grt4544sdfdg454efe54dssq5","amount": 10}' http://localhost:1234/transactions/new
  ```

- Query for UXTOs(GET)

  ```
  curl http://localhost:1234/transactions/unpacked/get
  ```

- All wallets(GET)

  ```
  curl http://localhost:1234/wallet/get
  ```

- Wallet Balance(GET)

  ```
  curl "http://localhost:1234/wallet/balance/get?address=E68746BFA62BBC98653A7409F191E564"
  ```
