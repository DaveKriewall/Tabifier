cd g:/tabifier/Tabifier
cp src/META-INF/*.* build/META-INF
md build/data/com/wrq/tabifier
cp data/com/wrq/tabifier/*.java build/data/com/wrq/tabifier
cp src/TabifierIcon.png build/com/wrq/tabifier/TabifierIcon.png
cd g:/tabifier/Tabifier/build
jar cvf ../lib/tabifier.jar com org META-INF data/com/wrq/tabifier
cd g:/tabifier/Tabifier
cp -r test/testData/com "G:/Users/Dave/.IdeaIC14/system/plugins-sandbox/test/java/java-tests/testData"
