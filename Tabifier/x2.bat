cd c:\tabifier3
cp src\META-INF\*.* build\META-INF
cp src\com\wrq\tabifier\Sample.java build\src\com\wrq\tabifier\Sample.java
cp src\TabifierIcon.png build\com\wrq\tabifier\TabifierIcon.png
cd c:\tabifier3\build
jar cvf ..\lib\tabifier.jar com org META-INF src\com\wrq\tabifier\Sample.java
cd c:\tabifier3
cp lib\tabifier.jar \intellij-idea-2072\plugins
cp -r test\testData\com \intellij-idea-2072\testData
rm \intellij-idea-2072\system\log\idea.log
