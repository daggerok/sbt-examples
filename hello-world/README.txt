mkdir hello-world
cd hello-world
. ../wrapper/sbt-wrapper
mkdir -p src/main/scala project
echo 'object Main extends App { println("Hello") }' > src/main/scala/Main.scala
echo 'sbt.version=1.2.8' > project/build.build.properties
./sbtw compile
