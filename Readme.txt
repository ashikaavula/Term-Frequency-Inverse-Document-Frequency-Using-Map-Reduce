//Ashika Avula 
//aavula@uncc.edu

I have compiled all java files in terminal. The following are the steps which i followed for execution.
------------------------------------------------------------------------------------------------------------------------------------

First we have to create input and output paths in hdfs. 

My input path is:
/home/cloudera/wordcount/input

My output path is:
/home/cloudera/wordcount/output

Now place all the 8 input files in canterbury in HDFS using following command.
Command: hadoop fs -put /home/cloudera/Canterbury/* /user/cloudera/wordcount/input

We can check if the files are placed in our input hdfs location using the following command.
Command: hadoop fs -ls /user/cloudera/wordcount/input

In the same way we can check files in ouput by using following command.
Command: hadoop fs -ls /user/cloudera/wordcount/output
--------------------------------------------------------------------------------------------------------------------------------------

DocWordCount.java execution as follows:

First Compile the java file and build class path	 
1. mkdir -p build
2. Java -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* /home/cloudera/Downloads/DocWordCount.java -d build -Xlint

Next generate JAR file for that particular file.
3. jar -cvf docwordcount.jar -C build/ .

Everytime we execute we have to delete the output Folder.
4. hadoop fs -rm -r /user/cloudera/wordcount/output

Command for execution of JAR file
5. hadoop jar docwordcount.jar org.myorg.DocWordCount /user/cloudera/wordcount/input /user/cloudera/wordcount/output

Generate output
6.  hadoop fs -cat /user/cloudera/wordcount/output/*

To get the output into our local directory
7. hadoop fs -get /user/cloudera/wordcount/output/part-r-00000 /home/cloudera/
part-r-00000 is the output file.

--------------------------------------------------------------------------------------------------------------------------------------
TermFrequency.java execution as follows:

First Compile the java file and build class path	 
1. mkdir -p build
2. Java -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* /home/cloudera/Downloads/TermFrequency.java -d build -Xlint

Next generate JAR file for that particular file.
3. jar -cvf termfrequency.jar -C build/ .

Everytime we execute we have to delete the output Folder.
4. hadoop fs -rm -r /user/cloudera/wordcount/output

Command for execution of JAR file
5. hadoop jar termfrequency.jar org.myorg.TermFrequency /user/cloudera/wordcount/input /user/cloudera/wordcount/output

Generate output
6.  hadoop fs -cat /user/cloudera/wordcount/output/*

To get the output into our local directory
7. hadoop fs -get /user/cloudera/wordcount/output/part-r-00000 /home/cloudera/
part-r-00000 is the output file.

----------------------------------------------------------------------------------------------------------------------------------------		
TFIDF.java execution as follows:

First Compile the java file and build class path. Here i am calling TermFrequency in TFIDF, so i am building class path for both files.	 
1. mkdir -p build
2. Java -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* /home/cloudera/Downloads/TermFrequency.java /home/cloudera/Downloads/TFIDF.java -d build -Xlint

Next generate JAR file for that particular file.
3. jar -cvf tfidf.jar -C build/ .

Everytime we execute we have to delete the output Folder.
4. hadoop fs -rm -r /user/cloudera/wordcount/output

Command for execution of JAR file
5. hadoop jar tfidf.jar org.myorg.TFIDF /user/cloudera/wordcount/input /user/cloudera/wordcount/output

Generate final output(Here i am generating intermediate output into a folder called tfout_as_TempOut which is stored in "/user/cloudera/tfout_as_TempOut" path)
6.  hadoop fs -cat /user/cloudera/wordcount/output/*

To get the output into our local directory
7. hadoop fs -get /user/cloudera/wordcount/output/part-r-00000 /home/cloudera/
part-r-00000 is the output file.

----------------------------------------------------------------------------------------------------------------------------------------

Search.java execution as follows:

First Compile the java file and build class path	 
1. mkdir -p build
2. Java -cp /usr/lib/hadoop/*:/usr/lib/hadoop-mapreduce/* /home/cloudera/Downloads/Search.java -d build -Xlint

Next generate JAR file for that particular file.
3. jar -cvf search.jar -C build/ .

Everytime we execute we have to delete the output Folder.
4. hadoop fs -rm -r /user/cloudera/wordcount/output

Command for execution of JAR file for 2 queries
5. hadoop jar search.jar org.myorg.Search /user/cloudera/wordcount/input /user/cloudera/wordcount/output computer science
5. hadoop jar search.jar org.myorg.Search /user/cloudera/wordcount/input /user/cloudera/wordcount/output data analysis

Generate output
6.  hadoop fs -cat /user/cloudera/wordcount/output/*

To get the output into our local directory
7. hadoop fs -get /user/cloudera/wordcount/output/part-r-00000 /home/cloudera/
part-r-00000 is the output file.

