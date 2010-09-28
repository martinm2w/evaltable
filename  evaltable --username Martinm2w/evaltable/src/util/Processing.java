package util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Processing {

	
	public static void main(String[] args) throws IOException{
	
		Processing ps = new Processing();
		
//		ps.parseFile1("D:/m2w cs/evaltable/input/reynard_sessions(2)",
//					"D:/m2w cs/evaltable/output/reynard_sessions(2)_result_1");
//		
//		ps.parseFile2("D:/m2w cs/evaltable/output/reynard_sessions(2)_result_1",
//						"D:/m2w cs/evaltable/output/reynard_sessions(2)_result_2");
		
//		ps.parseFile3("D:/m2w cs/evaltable/output/reynard_sessions(2)_result_2",
//						"D:/m2w cs/evaltable/output/reynard_sessions(2)_result_3");
//			
		for (int i = 1; i < 50; i ++){
			if(i < 10){
				ps.parseSeFile("reynard_sessions(2)_session0" + i);
			}else{
			ps.parseSeFile("reynard_sessions(2)_session" + i);
			}
		}
		ps.parseSeFile("reynard_sessions(2)_session50");
	}
	
	public void parseFile1(String input, String output){
		
		ArrayList<ArrayList<String>> fileList = new ArrayList<ArrayList<String>>();
//		ArrayList<ArrayList<String>> dataList = new ArrayList<ArrayList<String>>();
		
				
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(input));
			PrintWriter pw = new PrintWriter(output);
			String tempStr = null;
			
			
			/*1.1save file into List*/  // ok
			while((tempStr = br.readLine()) != null ) {
				
				
				/*add file name*/
				if(tempStr.contains("processing:")){
					ArrayList<String> templist = new ArrayList<String>();
					templist.add(tempStr);
					fileList.add(templist);
				}
				
				
				/*normal file*/
				if(tempStr.contains("calculate") && !tempStr.contains("calculate Expressive Disagreement")){
					String tempsh = null;
					ArrayList<String> templist = new ArrayList<String>();
					templist.add(tempStr);
			
					do{//add lines 

                        br.mark(1000);
                        tempsh=br.readLine();
                        templist.add(tempsh);
                                              
                   }while(tempsh!=null && ( tempsh.contains("The") || tempsh.contains("calculate") || tempsh.contains("qt_thrs")));

                   br.reset();
                   
                   fileList.add(templist);
					
				}
				
				/*expressive disagreement with -1*/
				
				if(tempStr.contains("calculate Expressive Disagreement")){
					String tempsh = null;
					ArrayList<String> templist = new ArrayList<String>();
					templist.add(tempStr);
			
					do{//add lines 

                        br.readLine();
                        br.mark(1000);
                        tempsh=br.readLine();
                        templist.add(tempsh);
                                              
                   }while(tempsh!=null && ( tempsh.contains("The") || tempsh.contains("calculate") || tempsh.contains("qt_thrs")));

                   br.reset();
                   
                   fileList.add(templist);
					
				}
			}
			
			br.close();
			System.out.println("list size = " + fileList.size());
			
		
			/*add qt_thrs for those haven't*/			
			for(int x = 0; x < fileList.size(); x++){
				if (fileList.get(x).get(0).contains("calculate")){
					if(fileList.get(x).get(1).contains("qt_thrs:")){
						continue;
					}else{
						fileList.get(x).add(1, "qt_thrs	0.0	0.0	0.0	0.0	0.0	0.0	");
					}			
				}
			}
			
			String tempFile = null;
			
			for(int i = 0; i < fileList.size(); i++){
				
				String tempTopic = null;
				String tempCat = null;
				String tempSession = null;
				
				
				for (int j = 0; j < fileList.get(i).size(); j++){
					
					String tempLine = fileList.get(i).get(j);
//					String tempTopic = null;
//					String tempCat = null;
//					String tempFile = null;
					
					/*file name */
					if (tempLine != null && tempLine.contains("processing:")){
						tempFile = tempLine.split("processing: ")[1];
						
						pw.println("||||||||||||||||||||||||||||||||||||");
						pw.println("File:\t" + tempFile);
						
					}
					
					if (tempFile != null){
						tempSession = tempFile.split("_")[0];
					}
					
					/*topic & cat no merge*/
					if (tempLine != null && tempLine.toLowerCase().contains("calculate") && tempLine.contains("-")){
						tempTopic = tempLine.toLowerCase().split("calculate ")[1].split(" -")[0];
						tempCat = tempLine.toLowerCase().split("- ")[1].split(" quintile")[0];
						pw.println("Topic\tCategory");
						pw.println(tempTopic + "%" + tempCat);
						}
					/*topic & cat with merge*/
					if (tempLine != null && tempLine.toLowerCase().contains("merged")){
						tempTopic = "merged";
						tempCat = "merged";
						pw.println("Topic\tCategory");
						pw.println(tempTopic + "%" + tempCat);
						}
					
					/*qt_thrs*/
					if (tempLine != null && tempLine.contains("qt_thrs")){
						String[] qt_thrs1 = tempLine.split("\\s+");
                        String[] qt_thrs_array = new String[qt_thrs1.length-1];
                        
                        pw.print("qt_thrs\t");
                        for(int h = 0; h < qt_thrs_array.length; h++){
                            qt_thrs_array[h] = qt_thrs1[h+1];

                            pw.print(qt_thrs_array[h] + "\t");
                        }
                        pw.print("\n");
                        
					}
					
					
					/*quntile data with "of" && no -1*/
					if (tempLine != null && tempLine.toLowerCase().contains("the quintile score of" ) && tempLine.toLowerCase().contains("actual score:")){
						
						String tempName = (tempLine.toLowerCase().split("the quintile score of ")[1].split(" is")[0]);
						String tempQtScore = tempLine.toLowerCase().split("is: ")[1].split(" ")[0];
						String tempAcScore = tempLine.toLowerCase().split("actual score: ")[1];
						
						pw.print(tempName + "!" + tempQtScore + "!" + tempAcScore + "!" + tempTopic + "!" + tempCat + "!" + tempSession + "\n");
					}
					/*qt data without "of" && no -1*/
					if (tempLine != null && tempLine.toLowerCase().contains("the quintile score" ) && tempLine.toLowerCase().contains("actual score:") && !tempLine.toLowerCase().contains("the quintile score of" )){
						
						String tempName = (tempLine.toLowerCase().split("the quintile score ")[1].split(" is")[0]);
						String tempQtScore = tempLine.toLowerCase().split("is: ")[1].split(" ")[0];
						String tempAcScore = tempLine.toLowerCase().split("actual score: ")[1];
						
						pw.print(tempName + "!" + tempQtScore + "!" + tempAcScore + "!" + tempTopic + "!" + tempCat + "!" + tempSession + "\n");
					}
					/*qt data with -1 no acscore*/
					if (tempLine != null && tempLine.toLowerCase().contains("the quintile score of" ) && !tempLine.toLowerCase().contains("actual score:") && tempLine.contains("-1")){
						
						String tempName = (tempLine.toLowerCase().split("the quintile score of ")[1].split(" is")[0]);
						String tempQtScore = tempLine.toLowerCase().split("is: ")[1].split(" ")[0];
						String tempAcScore = "0.0";
							
						pw.print(tempName + "!" + tempQtScore + "!" + tempAcScore + "!" + tempTopic + "!" + tempCat + "!" + tempSession + "\n");
					}
				} 
				
				pw.println();
				pw.println();
				
			}
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void parseFile2(String input, String output){
		
		HashMap<String, String> nameMap = new HashMap<String, String>();
		ArrayList<String> newfileList = new ArrayList<String>();
		ArrayList<ArrayList<String>> secNameList = new ArrayList<ArrayList<String>>();
		
		try {	
			
			BufferedReader namebr = new BufferedReader(new FileReader(input));
			PrintWriter newpw = new PrintWriter(output);
			String nameStr = null;
			ArrayList<String> nameList = new ArrayList<String>();
			
			/*get name list*/
			while((nameStr = namebr.readLine()) != null ) {
				
				if(nameStr != null && nameStr.contains("!")){
					String tempName = nameStr.split("!")[0];
					nameMap.put(tempName, tempName);
				}
			}
			namebr.close();
			Object[] nameOb = nameMap.keySet().toArray();
			for (int i = 0; i < nameOb.length ; i ++){
				nameList.add(nameOb[i].toString());
				System.out.println(nameList.get(i));
				
			}
			
			/*rebuild file*/
			for (int i = 0; i < nameList.size(); i ++){
				System.out.println("3");
				BufferedReader br = new BufferedReader(new FileReader(input));
				String tempStr = null;
				ArrayList<String> tempList = new ArrayList<String>();
				
				while((tempStr = br.readLine()) != null ) {
					if(tempStr != null && tempStr.contains("!") && !tempStr.contains("expressive")){
						String tempName = tempStr.split("!")[0];
						if (tempName.equals(nameList.get(i))){
							newfileList.add(tempStr);
							tempList.add(tempStr);// strings according to names.
							}
					}
				}
				
				secNameList.add(tempList);
				tempList.clear();				
				br.close();
			}
			for (int i = 0; i < newfileList.size(); i ++){
				System.out.println(newfileList.get(i));
				newpw.println(newfileList.get(i));
			}
			newpw.close();
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void parseFile3(String input, String output){
		HashMap<String, String> seMap = new HashMap<String, String>();
		ArrayList<String> newfileList = new ArrayList<String>();
		ArrayList<ArrayList<String>> secSeList = new ArrayList<ArrayList<String>>();
		
		try {	
			
			BufferedReader sebr = new BufferedReader(new FileReader(input));
			PrintWriter sepw = new PrintWriter(output);
			String seStr = null;
			ArrayList<String> seList = new ArrayList<String>();
			
			/*get se list*/
			while((seStr = sebr.readLine()) != null ) {
				
				if(seStr != null && seStr.contains("!")){
					String tempSe = seStr.split("!")[5];
					seMap.put(tempSe, tempSe);
				}
			}
			sebr.close();
			Object[] seOb = seMap.keySet().toArray();
			for (int i = 0; i < seOb.length ; i ++){
				seList.add(seOb[i].toString());
				System.out.println(seList.get(i));
				
			}
			
			/*rebuild file*/
			for (int i = 0; i < seList.size(); i ++){
//				System.out.println("3");
				BufferedReader br = new BufferedReader(new FileReader(input));
				String tempStr = null;
				ArrayList<String> tempList = new ArrayList<String>();
				
				while((tempStr = br.readLine()) != null ) {
					if(tempStr != null && tempStr.contains("!")){
						String tempSe = tempStr.split("!")[5];
						if (tempSe.equals(seList.get(i))){
							newfileList.add(tempStr);
							tempList.add(tempStr);// strings according to ses.
//							System.out.println(tempStr);
							}
					}
				}
				secSeList.add(tempList);
				br.close();
			}
			for (int i = 0; i < newfileList.size(); i ++){
//				System.out.println(newfileList.get(i));
				sepw.println(newfileList.get(i));
			}
			sepw.close();
			
			System.out.println(secSeList.size());
			
			
			for (int i = 0; i < secSeList.size(); i ++){
				System.out.println(secSeList.get(i).get(0));
				String curSe = secSeList.get(i).get(0).split("!")[5];
				PrintWriter secSepw = new PrintWriter("D:/m2w cs/evaltable/output2/reynard_sessions(2)_" + curSe);
				for (int j = 0; j < secSeList.get(i).size(); j ++){
					secSepw.println(secSeList.get(i).get(j));
				}
				secSepw.close();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void parseSeFile(String se){
		
		String input = "D:/m2w cs/evaltable/output2/" + se;
		String output = "D:/m2w cs/evaltable/output2/table_" + se;
		
		HashMap<String, String> nameMap = new HashMap<String, String>();
				
		try {	
			
			BufferedReader namebr = new BufferedReader(new FileReader(input));
			String nameStr = null;
			ArrayList<String> nameList = new ArrayList<String>();
			ArrayList<ArrayList<String>> fileList = new ArrayList<ArrayList<String>>();
			
			
			/*1.get name list*/
			while((nameStr = namebr.readLine()) != null ) {
				
				if(nameStr != null && nameStr.contains("!")){
					String tempName = nameStr.split("!")[0];
					nameMap.put(tempName, tempName);
				}
			}
			namebr.close();
			Object[] nameOb = nameMap.keySet().toArray();
			
			for (int i = 0; i < nameOb.length ; i ++){
				nameList.add(nameOb[i].toString());
				System.out.println(nameList.get(i));
			}
			
			/*2.output table*/
			PrintWriter pw = new PrintWriter(output);
			
			/*2.1 get fileList with names in sublist*/
			for (int i = 0; i < nameList.size(); i ++){
				BufferedReader br = new BufferedReader(new FileReader(input));
				String tempStr = null;
				ArrayList<String> subList = new ArrayList<String>();
				
				while((tempStr = br.readLine()) != null ) {
					if(tempStr != null && tempStr.contains("!")){
						String tempName = tempStr.split("!")[0];
						
						if (tempName.equals(nameList.get(i))){
							subList.add(tempStr);

						}
					}
				}
				
				fileList.add(subList);
				br.close();
			}
			
			/*2.2 print file*/
			
			System.out.println(fileList.get(0).get(0));
			
			ArrayList<ArrayList<String>> _taList = new ArrayList<ArrayList<String>>();
			ArrayList<ArrayList<String>> _scoreList = new ArrayList<ArrayList<String>>();
			
			for (int i = 0; i < fileList.size(); i ++){
				/*print topic & cat*/
				ArrayList<String> taList = new ArrayList<String>();
				ArrayList<String> scoreList = new ArrayList<String>();
					
				for(int j = 0; j < fileList.get(i).size(); j++){					
					String tempStr = fileList.get(i).get(j);
					taList.add("[" + tempStr.split("!")[3] + "-" + tempStr.split("!")[4] + "]\t");
					scoreList.add(tempStr.split("!")[1] + "\t" + tempStr.split("!")[2]);
				}
				_taList.add(taList);
				_scoreList.add(scoreList);
			}
			
				/*print 1st line*/
			pw.print("name\t"); 
			for (int k = 0; k < _taList.get(0).size(); k++){
					pw.print(_taList.get(0).get(k) + "\t");
				}
			pw.println();
				
				/*print 2nd line*/
			for (int i = 0; i < _scoreList.size(); i++){
				String curName = fileList.get(i).get(0).split("!")[0];	
				pw.print(curName + "\t");
				for (int k = 0; k < _scoreList.get(i).size(); k++){
					pw.print(_scoreList.get(i).get(k) + "\t");
				}
				pw.println();
				
				}
			
			
			pw.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
