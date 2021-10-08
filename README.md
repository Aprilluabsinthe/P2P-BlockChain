P2P BlockCoin

you can see documentations of implementations here:

https://docs.google.com/document/d/18vbjDWMc0MyGzJYfua-oYNbnMEqpNCQdlmBuxg0niMA/edit?usp=sharing

As indicated in the lab writeup, you have an opportunity to design and implement your own distributed system, service, or application.  There is a lot of flexibility, so we aren't providing you with starter code.  This nearly empty repo should be your starting point, and you can build off any of your previous lab outcomes.  As usual, please use this repo for your project work, so the course staff can help you along the way


## Description of project topic, goals, and tasks

See proposal and report for details

## Dependencies to run this code

Java
Maven
jetty
fastjson
websocket

## Description of tests and how to run them

1. Local test (on block chain operations)

```
mvn test
```

2. Remote test (P2P network, transaction, consistency)

## usage

### start

#### start with build in shell command
you can easily build and start by using our peer command

```
/bin/zsh step1_startpeer1.sh
/bin/zsh step2_startpeer2.sh
/bin/zsh step3_startpeer3.sh
```
project build is in step 1 thus the first is mandatory.
step2: startpeer 2 is required for testing p2p service.
step3: startpeer 3 and more peers as you like, these are optional.

#### start manually
```
mvn clean install
cd target
```

To start a peer node, the command is:

Start a node with http service and p2p service, not connet to p2p network

(remeber to do these in /target directory, or you will not find the jar applicatipn)
```
java -jar labBlockChain-1.0-SNAPSHOT.jar http_port p2p_port
```
or Start a node with http service and p2p service, connect to p2p network
```
java -jar labBlockChain-1.0-SNAPSHOT.jar http_port p2p_port websocket_to_others
```
you can design de topology of a network as you like: 
1. connecting to the same centeral node
2. connect one by one ike a line
3. connect like a tree
4. ......

any way as long as the node is connected to one peer.

Here is an example:

A <- B <- C

Node A:
```
java -jar labBlockChain-1.0-SNAPSHOT.jar 1234 5678
```
Node B:
```
java -jar labBlockChain-1.0-SNAPSHOT.jar 2345 6789 ws://localhost:5678
```
Node C:
```
java -jar labBlockChain-1.0-SNAPSHOT.jar 3456 7890 ws://localhost:6789
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

## How to generate doc

```
  mvn javadoc:javadoc
```
Then go to /target/site/apidocs/index.html
