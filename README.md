# Lab 4: Choose-your-Own Distributed System

As indicated in the lab writeup, you have an opportunity to design and implement your own distributed system, service, or application.  There is a lot of flexibility, so we aren't providing you with starter code.  This nearly empty repo should be your starting point, and you can build off any of your previous lab outcomes.  As usual, please use this repo for your project work, so the course staff can help you along the way


## Description of project topic, goals, and tasks

...

## Dependencies to run this code

...

## Description of tests and how to run them

1. Local test (on block chain operations)

```
mvn test
```

2. Remote test (P2P network, transaction, consistency)

## usage

### start
```
/bin/zsh step1_startpeer1.sh
/bin/zsh step2_startpeer2.sh
/bin/zsh step3_startpeer3.sh
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
  curl --data "address=78415FA4D1BCFFF8AFBE5DD5C565C144" http://localhost:1234/mine
  ```

- Transaction(POST)

  ```
  curl -H "Content-type:application/json" --data '{"sender": "9555D32727BD391C0A1B3FBE6C53FDBE","recipient": "F784B5402BBCE003875E21C77550590A","amount": 10}' http://localhost:1234/transactions/new
  ```

- Query for UXTOs(GET)

  ```
  curl http://localhost:1234/transactions/get/unpacked
  ```

- Query for all Transactions(GET)

  ```
  curl http://localhost:1234/transactions/get/all
  ```

- Query for packed Transactions(GET)

  ```
  curl http://localhost:1234/transactions/get/packed
  ```

- All wallets in this node(GET)

  ```
  curl http://localhost:1234/wallet/get
  ```

- Other's Wallet Balance(GET)

  ```
  curl "http://localhost:1234/wallet/get/other"
  ```

- Wallet Balance(GET)

  ```
  curl "http://localhost:1234/wallet/get/balance?address=9555D32727BD391C0A1B3FBE6C53FDBE"
  ```

- see socket peers connected(GET)

  ```
  curl http://localhost:1234/peers
  ```


