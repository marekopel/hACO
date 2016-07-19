package mk;

import java.util.Iterator;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class myReducer extends Reducer<FloatWritable, Text, FloatWritable, Text> {

	protected void reduce(Text cargo, Iterable<FloatWritable> arr, Context ctx)
			throws java.io.IOException, InterruptedException {

		System.out.println("arr: "+arr+", cargo: "+cargo);
		Iterator it = arr.iterator();
		float count = 0;
		while (it.hasNext()) {
			FloatWritable i = (FloatWritable) it.next();
			count = count + i.get();
			System.out.println(i.get());
		}
		ctx.write(new FloatWritable(count), cargo);
//		ctx.write(word, new FloatWritable(arr));

	};
}