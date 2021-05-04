mvn clean install
cd target
java -jar labBlockChain-1.0-SNAPSHOT.jar 1234 5678
#osascript -e 'tell app "Terminal"
#    do script "java -jar labBlockChain-1.0-SNAPSHOT.jar 2345 6789 ws://localhost:7001"
#end tell'
#xterm -hold -e "java -jar labBlockChain-1.0-SNAPSHOT.jar 2345 6789 ws://localhost:7001" &
#java -jar labBlockChain-1.0-SNAPSHOT.jar 2345 6789 ws://localhost:7001