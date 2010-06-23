package com.todotxt.todotxttouch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class TodoUtil {
	
	private final static String TAG = TodoUtil.class.getSimpleName();
	
	private final static File TODOFILE = new File(Environment
			.getExternalStorageDirectory(),
			"data/com.todotxt.todotxttouch/todo.txt");
	
	private final static Pattern prioPattern = Pattern.compile("\\(([A-Z])\\) (.*)");

	private final static Pattern contextPattern = Pattern.compile("@(\\w+)");

	private final static Pattern projectPattern = Pattern.compile("\\+(\\w+)");

	private final static Pattern tagPattern = Pattern.compile("#(\\w+)");

	public static Task createTask(int id, String line){
		//prio and text
		Matcher m = prioPattern.matcher(line);
		int prio = 0;
		String text = null;
		if(m.find()){
			prio = TaskHelper.parsePrio(m.group(1));
			text = m.group(2);
		}else{
			text = line;
		}
		//contexts
		m = contextPattern.matcher(text);
		List<String> contexts = new ArrayList<String>();
		while(m.find()){
			String context = m.group(1);
			contexts.add(context);
		}
		//projects
		m = projectPattern.matcher(text);
		List<String> projects = new ArrayList<String>();
		while(m.find()){
			String project = m.group(1);
			projects.add(project);
		}
		//tags
		m = tagPattern.matcher(text);
		List<String> tags = new ArrayList<String>();
		while(m.find()){
			String tag = m.group(1);
			tags.add(tag);
		}
		return new Task(id, prio, text.trim(), contexts, projects, tags);
	}

	public static ArrayList<Task> loadTasksFromUrl(Context cxt, String url)
			throws IOException {
		ArrayList<Task> items = new ArrayList<Task>();
		BufferedReader in = null;
		InputStream is = Util.getInputStreamFromUrl(url);
		try {
			in = new BufferedReader(new InputStreamReader(is));
			String line;
			int counter = 0;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if(line.length() > 0){
					items.add(createTask(counter, line));
				}
				counter++;
			}
		} finally {
			Util.closeStream(in);
			Util.closeStream(is);
		}
		return items;
	}

	public static ArrayList<Task> loadTasksFromFile()
			throws IOException {
		ArrayList<Task> items = new ArrayList<Task>();
		BufferedReader in = null;
		InputStream is = new FileInputStream(TODOFILE);
		try {
			in = new BufferedReader(new InputStreamReader(is));
			String line;
			int counter = 0;
			while ((line = in.readLine()) != null) {
				line = line.trim();
				if (line.length() > 0) {
					items.add(createTask(counter, line));
				}
				counter++;
			}
		} finally {
			Util.closeStream(in);
			Util.closeStream(is);
		}
		return items;
	}

	public static void writeToFile(List<Task> tasks){
		try{
			if(Util.isDeviceWritable()){
				FileWriter fw = new FileWriter(TODOFILE);
				for(int i = 0; i < tasks.size(); ++i)
				{
					String fileFormat = TaskHelper.toFileFormat(tasks.get(i));
					fw.write(fileFormat);
					fw.write("\n");
				}
				fw.close();
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}