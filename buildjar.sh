#Make sure to run Build artifacts (that generates jar that includes config.properties).
# cd to ...... /TValue/out/artifacts/TValue_jar
# run with command => ../../../buildjar.sh
tar -xvf TValue.jar
rm TValue.jar
cp ./META-INF/MANIFEST.MF ./MANIFEST.MF
ls
jar cf HGTVInvTrack.jar com
#rm -r com META-INF