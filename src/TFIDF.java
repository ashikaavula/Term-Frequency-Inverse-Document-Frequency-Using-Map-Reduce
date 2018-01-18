//Ashika Avula 800972702

package org.myorg;

import java.io.IOException;
import java.io.File;
//import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
//import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapreduce.lib.input.FileSplit;

public class TFIDF extends Configured implements Tool {

   private static final Logger LOG = Logger .getLogger( TFIDF.class);
	
   private static final String TEMP_PATH = "tfout_as_TempOut"; //here i am creating temporary folder to store intermediate output
   public static void main( String[] args) throws  Exception {

       
      String[] argstemp = {args[0], TEMP_PATH};// here i am creating temporary arguments for passing into TermFrequency while calling it.

      int r=ToolRunner .run( new TermFrequency(), argstemp); //calling TermFrequency using ToolRunner.run method
      int res  = ToolRunner .run(new TFIDF(), args);
      System .exit(res);
   }

   public int run( String[] args) throws  Exception {
	   
      Configuration newconfig_forjob2 = getConf(); //creating new configuration to store the file configuration
      //int filecount = new File(args[0]).listFiles().length;
      //newconfig_forjob2.setInt("total_no_of_files",filecount);
      
  	
	FileSystem f = FileSystem.get(newconfig_forjob2 ); //getting file configuration
	int file_count = f.listStatus(new Path (args[0])).length; //getting the file count and storing in file_count
	 newconfig_forjob2.setInt("total_no_of_files", file_count); //setting the file count to configuration object
      
      Job job2  = Job .getInstance(getConf(), " tfidf ");
      job2.setJarByClass( this .getClass());

      FileInputFormat.addInputPath(job2, new Path (TEMP_PATH) );//setting the new intermediate path as input path for 2nd job inorder to pass to map2
      FileOutputFormat.setOutputPath(job2,  new Path(args[ 1]));
      job2.setMapperClass( Map1 .class);
      job2.setReducerClass( Reduce1 .class);
      job2.setMapOutputKeyClass( Text .class); // Instantiating seperate output classes for map and reduce
      job2.setMapOutputValueClass( Text .class);
      job2.setOutputKeyClass( Text .class);
      job2.setOutputValueClass( DoubleWritable .class);

      return job2.waitForCompletion( true)  ? 0 : 1;
   }
   
   public static class Map1 extends Mapper<LongWritable ,  Text ,  Text ,  Text> {
	    

       public void map( LongWritable offset,  Text lineText,  Context context)
	    throws  IOException,  InterruptedException {

	    	  
    	     String line  = lineText.toString();//converting the text to string 
             
             //splitting the input near the specified delimiter using .split() method and stored in string array.
             String filesplit[] = line.split ("#####");  //Now line is split into 2 parts. First part is stored in filesplit[0] and second part in filesplit[1].             
             Text currentWord  = new Text(filesplit[0]);
             String filename_termfreq_split[] = filesplit[1].split("\t"); //again the second part is split near tab space and stored in string array.
             String temp_value = filename_termfreq_split[0] +"=" + filename_termfreq_split[1]; //the "value" format from map2 is filename=tf_value
             
	        
	     context.write(currentWord,new Text(temp_value));
	         
	      }
	   }
   
   public static class Reduce1 extends Reducer<Text, Text, Text, DoubleWritable> {
		@Override public void reduce(Text word, Iterable<Text> counts, Context context)
				throws IOException, InterruptedException {
	
			Configuration newconfig_forjob2 = context.getConfiguration(); //retreiving the configuation
			long totalfiles = Long.valueOf(newconfig_forjob2 .get("total_no_of_files")); // retreiving the total file count from the configuration 
			long term_occ_count=0;
			Map<String,Double> map = new HashMap<String,Double>();	//hash map is used for storing file values. Here i am storing in hash map so that we don't need to parse the files.		
		       for(Text count :counts){
				term_occ_count++; //counting the occurance of the term in how many files 
				String[] val = count.toString().split("=");  //"count" is value from map2. It is split into 2 parts.  
				String key= word.toString()+"#####"+val[0];//First part is stored in val[0] and second part in val[1]. Appending back the filename to word.
				double value=Double.valueOf(val[1]);
				map.put(key, value); //putting all the key and values in map
			}
					
                       String newkey="";	 
			double tfidf=0;
		        double idf = Math.log10(1+(totalfiles/term_occ_count)); //calculate idf using the formula
			for(Map.Entry<String,Double> entry:map.entrySet()){
			  tfidf=entry.getValue()*idf; //calculating tfidf by getting tf values from hash map
                          newkey = entry.getKey();    // getting key values from hash map
		        context.write(new Text(newkey),new DoubleWritable(tfidf));
			}						
		
	}
  }
}
