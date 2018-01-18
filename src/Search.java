package org.myorg;

import java.io.IOException;
//import java.io.File;
//import java.util.regex.Pattern;
//import java.util.HashMap;
//import java.util.Map;



import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapreduce.lib.input.FileSplit;


public class Search extends Configured implements Tool {

   private static final Logger LOG = Logger .getLogger( Search.class);
	
 
   public static void main( String[] args) throws  Exception {

      
      int res  = ToolRunner .run(new Search(), args);
      System .exit(res);
   }

   public int run( String[] args) throws  Exception {

	      //Configuration config = new Configuration();
	     // String given_query = args[2];
	      getConf().set("query", args[2]); //creating a new argument for giving query and setting it to configuration object
      
      Job job  = Job .getInstance(getConf(), " wordcount ");
      job.setJarByClass( this .getClass());

      FileInputFormat.addInputPaths(job, args[0]);
      FileOutputFormat.setOutputPath(job,  new Path(args[ 1]));
      
      job.setMapperClass( Map .class);
      job.setReducerClass(Reduce.class);
     
      job.setOutputKeyClass( Text .class);
      job.setOutputValueClass( DoubleWritable .class);

      return job.waitForCompletion( true)  ? 0 : 1;
   }
   
   public static class Map extends Mapper<LongWritable ,  Text ,  Text ,  DoubleWritable> {
	    
           //  private static final Pattern WORD_BOUNDARY = Pattern .compile("\\s*\\b\\s*");
	      public void map( LongWritable offset,  Text lineText,  Context context)
	        throws  IOException,  InterruptedException {

	    	  
    	     String line  = lineText.toString().toLowerCase();  //converting the text to string using .toString() method and into lowercase using .toLowerCase()
             
             
             String filesplit[] = line.split ("#####");     //splitting the input near the specified delimiter using .split() method and stored in string array.
             //Now line is split into 2 parts. First part is stored in filesplit[0] and second part in filesplit[1]. 
             String word = filesplit[0];  //Assigning filsplit[0] to word.
             String filename_termfreq_split[] = filesplit[1].split("\t");  //again the second part is split near tab space and stored in string array.

             //String temp_key = filename_termfreq_split[0]; 
             //String temp_tfidf = filename_termfreq_split[1];
             
             Configuration config = context.getConfiguration(); //getting the file configuration
             String givenquery = config.get("query"); //getting the query using the file configuration
            
            String[] query1 = givenquery.split(" ");//spliting the query near space and storing in string array.

	        for (String s : query1) {               
                if (s.equals(word)) { //if the word matches the given query word then write happens.
                context.write(new Text(filename_termfreq_split[0]) , new DoubleWritable(Double.parseDouble(filename_termfreq_split[1])));// filename is given as key and tfidf as value.
                }
            }
         }
     }
     public static class Reduce extends Reducer<Text ,  DoubleWritable ,  Text ,  DoubleWritable > {
      @Override 
      public void reduce( Text word,  Iterable<DoubleWritable > counts,  Context context)
         throws IOException,  InterruptedException {
         double total_tfidf  = 0.0;
         for ( DoubleWritable count  : counts) {
            total_tfidf  += count.get(); //summing up the count if the filename is same.
         }
         context.write(word,  new DoubleWritable(total_tfidf)); //output the filename and respective sum
        }
      }
    }
    
