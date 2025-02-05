package csv2json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CSV2JSONConverter {

	public static void main(String[] args) {
		// read the file
		String inputFileName = "QueryResults.csv";
		File inputFile = new File(inputFileName);
		List<String> attributes = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		File outputFile = new File("QueryResults.json");
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
				if (lineNumber == 0) { 
					attributes = getTokens(line);
//					System.out.println(attributes);
					//fileWriter.write(attributes+"\n");
					noOfAttributes = attributes.size();
				} 
				else {
					buffer += line+"\n";
					//System.out.print(buffer);
					if (countValuesInLine(buffer) % noOfAttributes == 0) {
						values = getTokensUsingSplits(buffer);
						System.out.println(values);
						if (attributes.size() == values.size()) {
							String str = attributes_values_paring(attributes, values);
							//System.out.print(str+"\n");
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
				System.out.print(value+"??");
				value = value.replaceAll("\"|\n", "");
				System.out.println(value);
			}
			else{
				System.out.print(value+"<<<");
				if (value.startsWith("\"") & ! value.endsWith("\"")) {
					value = value.substring(1);
				}
				else if (value.endsWith("\"") & !value.startsWith("\"")) {
					value = value.substring(0,value.length()-2);
				}
				else if(!value.endsWith("\"") & !value.startsWith("\"")){
					value = value.substring(0,value.length()-1);
				}
				else if(value.endsWith("\"") & value.startsWith("\"")) {
					value = value.substring(1,value.length()-2);
				}
				// replacing special characters
				value = value.replace("\\", "\\\\");
				value = value.replace("\n", "\\n");
				value = value.replace("\"", "\\\"");
				value ="\""+value+"\"";
				System.out.println(value);
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
