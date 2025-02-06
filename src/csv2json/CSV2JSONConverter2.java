package csv2json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
// match regex
// (".*",){3}"(.*\n)*","\d+",("\d{4}-\d{1,2}-\d{1,2} \d{1,2}:\d{1,2}:\d{1,2}",){2}"(\d+","){4}\d+"

public class CSV2JSONConverter2 {

	public static void main(String[] args) {
		// read the file
		String inputFileName = "C:\\Users\\Hp\\Downloads\\QueryResults4.csv";
		File inputFile = new File(inputFileName);
		List<String> attributes = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		File outputFile = new File("C:\\Users\\Hp\\Downloads\\QueryResults4.json");
		String attr_value_pattern = "(\".*\",){3}\"(.*\\n)*\",\"\\d+\",(\"\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\",){2}\"(\\d+\",\"){4}\\d+\"\\n";
		try {
			Scanner inputScanner = new Scanner(inputFile);
			FileWriter fileWriter = new FileWriter(outputFile);
			int lineNumber = 0;
			String buffer = "";
			int noOfAttributes = 0;
			fileWriter.write("["+"\n");
			int jasonObject = 0;
			while (inputScanner.hasNext()) {
				String line = inputScanner.nextLine();
				System.out.println(line);
				if (lineNumber == 0) { 
					attributes = getTokens(line);
//					System.out.println(attributes);
					//fileWriter.write(attributes+"\n");
					noOfAttributes = attributes.size();
					String temp = attributes.get(3);
					attributes.remove(3);
					attributes.add(temp);
				} 
				else {
					buffer += line+"\n";
					if (buffer.matches(attr_value_pattern)) {
						System.out.println("<BUFFER>");
						System.out.println(buffer);		
						System.out.println("</BUFFER>");
						Pattern pattern = Pattern.compile("(\".*\",){3}");
						Matcher matcher = pattern.matcher(buffer);
						int start =0, end =0;
						if (matcher.find())
						{
						    start = matcher.end();
						}
						pattern = Pattern.compile(",\"\\d+\",(\"\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}\",){2}\"(\\d+\",\"){4}\\d+\"\\n");
						matcher = pattern.matcher(buffer);
						if (matcher.find())
						{
						    end = matcher.start();
						}						
						String other = buffer.substring(0, start)+buffer.substring(end+1, buffer.length()-1);
						String body = buffer.substring(start, end);
//						System.out.println("<@@@>");
//						System.out.println(other+","+body);
//						System.out.println("</@@@>");
						if (countValuesInLine(other)+1 == noOfAttributes) {
							values = getTokensUsingSplits(other);
							values.add(body);
//							System.out.println("<VALUES>");
//							System.out.println(values.toString());
//							System.out.println("</VALUES>");
							if (attributes.size() == values.size()) {
								String str = attributes_values_paring(attributes, values);
								System.out.println("<JSON>");
								System.out.println(str);
								System.out.println("</JSON>");
								if (jasonObject == 0) {
									fileWriter.write(str+"\n");
								}
								else {
									fileWriter.write(","+str+"\n");
								}
								jasonObject++;
							}
							else {
								System.out.println("ERRORRRR");
								System.exit(404);
							}
						}
						buffer = "";
					}
				}
				lineNumber++;
			}
			fileWriter.write("]");
			fileWriter.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	static List<String> getTokens(String line){
		List<String> tokens = new ArrayList<String>();
		StringTokenizer stringTokenizer = new StringTokenizer(line, ",");
		while (stringTokenizer.hasMoreTokens()) {
			tokens.add(stringTokenizer.nextToken());
		}
		return tokens;
	} 
	
	static int countValuesInLine(String line) {
		return line.split("\",\"").length;
	}
	
	static List<String> getTokensUsingSplits(String line){
		List<String> tokens = new ArrayList<String>();
		String [] splitTokens = line.split("\",\"");
		for (int i = 0; i < splitTokens.length; i++) {
			tokens.add(splitTokens[i]);
		}
		return tokens;
	}
	static String attributes_values_paring(List<String>attrs, List<String>vals){
		String jsonStr = "{";
		for (int i = 0; i < attrs.size(); i++) {
			String value = vals.get(i);
			if (value.matches("\"?[-0-9]+\"?\n?")) {
				//System.out.print(value+"??");
				value = value.replaceAll("\"|\n", "");
				//System.out.println(value);
			}
			else{
//				System.out.println("<VALUE>"+value+"</VALUE>");
				int l = value.length();
//				System.out.print(value+"<<<");
				if (value.startsWith("\"") & !value.endsWith("\"")) {
					value = value.substring(1);
//					System.out.println("C1"+value);
				}
				else if (value.endsWith("\"") & !value.startsWith("\"")) {
					value = value.substring(0,l-2);
//					System.out.println("C2"+value);
				}
				else if(!value.endsWith("\"") & !value.startsWith("\"") & l > 1){
					value = value.substring(0,l);
//					System.out.println("C3"+value);
				}
				else if(value.endsWith("\"") & value.startsWith("\"") & l > 2) {
					value = value.substring(1,l-2);
//					System.out.println("C4"+value);
				}
				// replacing special characters
				value = value.replace("\\", "\\\\");
				value = value.replace("\n", "\\n");
				value = value.replace("\"", "\\\"");
				value ="\""+value+"\"";
//				System.out.println("<VALUE_P>"+value+"</VALUE_P>");
			}
			jsonStr += "\""+attrs.get(i)+ "\""+ ":"+ value;
			if (i < attrs.size()-1) {
				jsonStr += ",\n";
			}
		}
		jsonStr += "}";
		return jsonStr;
	}
}
