package mk;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class myDriver {
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "ACOjob");
		job.setJarByClass(myDriver.class);
		job.setMapperClass(myMapper.class);
		job.setReducerClass(myReducer.class);
		job.setOutputKeyClass(FloatWritable.class);
		job.setOutputValueClass(Text.class);

		Path input_dir = new Path("hdfs://172.17.0.2:9000/user/root/ACO-in");
		FileInputFormat.addInputPath(job, input_dir);
		Path output_dir = new Path("hdfs://172.17.0.2:9000/user/root/ACO-out");
		FileOutputFormat.setOutputPath(job, output_dir);

//		System.exit(job.waitForCompletion(true) ? 0 : 1);
		job.waitForCompletion(true);
	}
}
