echo "Inside before install"
while [ ! -f completed.txt ]
do
  sleep 5
done
ls -l completed.txt
