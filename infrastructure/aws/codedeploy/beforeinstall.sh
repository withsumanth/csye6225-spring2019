echo "Inside before install"
while [ ! -f completed.txt ]
do
  sleep 20
done
ls -l completed.txt
