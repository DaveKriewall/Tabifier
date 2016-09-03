cd c:\tabifier3
rm -rf build
md build
md build\META-INF
cp -r out\production\tabifier\com build
cp src\META-INF\*.* build\META-INF
md build\data\com\wrq\tabifier
cp data\com\wrq\tabifier\*.java build\data\com\wrq\tabifier
cp src\TabifierIcon.png build\com\wrq\tabifier\TabifierIcon.png
cd c:\tabifier3\build
jar cvf ..\lib\tabifier.jar com org META-INF data\com\wrq\tabifier
cd c:\tabifier3
md "C:\Program Files\JetBrains\IntelliJ IDEA 8.0M1\testData"
cp -r test\testData\com "C:\Program Files\JetBrains\IntelliJ IDEA 8.0M1\testData"
