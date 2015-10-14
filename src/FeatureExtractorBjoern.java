import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;


public class FeatureExtractorBjoern {

	public static void main(String[] args) throws IOException{
		
	
    				String test_dir ="/Users/Aylin/Desktop/Princeton/BAA/datasets/"
    						+ "c++/100authors_noOptimization_bjoern_snowmanCFG";
		       		
		        	String output_filename = "/Users/Aylin/Desktop/Princeton/"
		        			+ "BAA/arffs/"
		        			+ "100authors_noOptimization_bjoern_snowmanCFG.arff" ;

		        	
		        	

		           	List test_binary_paths = Util.listBinaryFiles(test_dir);
		           	List test_dis_paths = listBjoernNodeFiles(test_dir);
			
		 
		        		

		    	String text = "";
		      	//Writing the test arff
		      	//first specify relation
		    	Util.writeFile("@relation "+"Disassembly_Unigrams_Bigrams"+"\n"+"\n",
		    			output_filename, true);
		    	Util.writeFile("@attribute instanceID_original {", output_filename, true);
		  
		    	


		   	for(int j=0; j < test_binary_paths.size();j++ )
			{
				File sourceFile = new File(test_binary_paths.get(j).toString());
				String fileName = sourceFile.getName();
				Util.writeFile(fileName+",", output_filename, true);
				if ((j+1)==test_binary_paths.size())
					Util.writeFile("}"+"\n", output_filename, true);
			}


		   	//get the Unigrams in the disassembly and write the unigram features
		       String[] disassemblyUnigrams =getBjoernUnigrams(test_dir);
		    	for (int i=0; i<disassemblyUnigrams.length; i++)	   	
		       {  	disassemblyUnigrams[i] = disassemblyUnigrams[i].replace("'", "apostrophesymbol");
		            	Util.writeFile("@attribute 'disassemblyUnigrams "+i+"=["+disassemblyUnigrams[i]+"]' numeric"+"\n", output_filename, true);}
			   
		   	
		    	//get the bigrams in the disassembly and write the bigram features
		    	String[] disassemblyBigrams =getDisBigrams(test_dir);
		     	for (int i=0; i<disassemblyBigrams.length; i++)	   	
			       {  	disassemblyBigrams[i] = disassemblyBigrams[i].replace("'", "apostrophesymbol");
			            	Util.writeFile("@attribute 'disassemblyBigrams "+i+"=["+disassemblyBigrams[i]+"]' numeric"+"\n", output_filename, true);}


		    File authorFileName = null;
			//Writing the classes (authorname)
			Util.writeFile("@attribute 'authorName_original' {",output_filename, true);
			for(int i=0; i< test_binary_paths.size(); i++){
				int testIDlength = test_binary_paths.get(i).toString().length();   
				authorFileName= new File(test_binary_paths.get(i).toString());
				String authorName= authorFileName.getParentFile().getName();

				text = text.concat(authorName + ",");  
				String[] words = text.split( ",");
				  Set<String> uniqueWords = new HashSet<String>();

				   for (String word : words) {
				       uniqueWords.add(word);
				   }
				   words = uniqueWords.toArray(new String[0]);
				   int authorCount = words.length;
				   if (i+1==test_binary_paths.size()){
				   for (int j=0; j< authorCount; j++){
					   {System.out.println(words[j]);
						if(j+1 == authorCount)
						{
					   Util.writeFile(words[j]+"}"+"\n\n",output_filename, true);
						}
						else
						{
						Util.writeFile(words[j]+","+"",output_filename, true);

							}
						}
					   }

				   }
				   
				 }
			
		   	
			Util.writeFile("@data"+"\n", output_filename, true);	
			//Finished defining the attributes
			
			
			//EXTRACT LABELED FEATURES
		   	for(int i=0; i< test_binary_paths.size(); i++){
				String featureText = Util.readFile(test_binary_paths.get(i).toString());
				int testIDlength = test_binary_paths.get(i).toString().length(); 
				authorFileName= new File(test_binary_paths.get(i).toString());
				String authorName= authorFileName.getParentFile().getName();

				System.out.println(test_binary_paths.get(i));
				System.out.println(authorName);
				File fileCPPID = new File(test_binary_paths.get(i).toString());
				String fileNameID = fileCPPID.getName();
				Util.writeFile(fileNameID+",", output_filename, true);
				String disText = Util.readFile(test_binary_paths.get(i).toString());

				
		   
				    
			    //get count of each wordUnigram in disassembly 
			    float[] wordUniCount = getBjoernUnigramTF(disText, disassemblyUnigrams);
			    for (int j=0; j<wordUniCount.length; j++)
				{Util.writeFile(wordUniCount[j] +",", output_filename, true);}	
			    
			    //get count of each bigram in in disassembly	 
			    float[] wordBigramCount = getDisBigramsTF(disText, disassemblyBigrams);
			    for (int j=0; j<wordBigramCount.length; j++)
				{Util.writeFile(wordBigramCount[j] +",", output_filename, true);}
			   	
		    	
				Util.writeFile(authorName+"\n", output_filename, true);

		   	
		   	}}
		       	
		   	
		
		
		
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String [] getBjoernUnigrams(String dirPath) throws IOException{
		
	
		List  test_file_paths = listBjoernNodeFiles(dirPath);
		String[] words = null;
		Set<String> uniGrams = new LinkedHashSet<String>();

		ArrayList<String> ar = new ArrayList<String>();

		String filePath="";
		HashSet<String> uniqueWords = new HashSet<String>();

 	    for(int i=0; i< test_file_paths.size(); i++){
 	    	
 	    	filePath = test_file_paths.get(i).toString();  
			System.out.println(filePath);						   
			   String[] arr;
			   String[] toAdd;

				BufferedReader br = new BufferedReader(new FileReader(filePath));
				String line;
				
				while ((line = br.readLine()) != null)
				{
					arr = line.split("\\\t");
					if (arr.length > 2){
/*					System.out.println("Redundant " + arr[0] 
		                                 + " , needed " + arr[2] 
		                            );*/
						for(int i1=1; i1< arr.length; i1++){

						arr[i1]=	arr[i1].replaceAll("\\\"", " ");	
						arr[i1]=	arr[i1].replaceAll("^[A-Fa-f0-9]+$", "hexadecimal");
						arr[i1]=	arr[i1].replaceAll("\\d+", "number");
					
						toAdd = arr[i1].split("\\s+");
					
					for(int i11 =0; i11< toAdd.length; i11++)
						{
						if(toAdd[i11].contains("0x")){
							toAdd[i11]="hexadecimal";
						}
						uniGrams.add(toAdd[i11]);
							//	System.out.println(toAdd[i1]);
		            	}	
					}}
				}
				
				
 	    }	 	      
 	      
       
 	    		words =   uniGrams.toArray(new String[uniGrams.size()]);
			    System.out.println(words);

			    return words;
 
		
	}
	

 
 //not normalized by the number of ASTTypes in the source code in the source code
    public static float [] getBjoernUnigramTF (String featureText, String[] wordUnigrams  )
    {    
    	
    	
    	
    	String str;
    float symbolCount = wordUnigrams.length;
    float [] counter = new float[(int) symbolCount];
    for (int i =0; i<symbolCount; i++){
 	  str = wordUnigrams[i].toString();
 	
 		featureText=	featureText.replaceAll("\\\"", " ");	
 		featureText=	featureText.replaceAll("^[A-Fa-f0-9]+$", "hexadecimal");
 		featureText=	featureText.replaceAll("\\d+", "number");
	
 	 counter[i] = StringUtils.countMatches(featureText, str); 
 	 }

    
    return counter;
    }
    
    
    
    
    
    
    public static String [] getDisBigrams(String dirPath) throws IOException{

    	
    

    List test_file_paths = Util.listDisFiles(dirPath);
	Set<String> bigrams = new LinkedHashSet<String>();
	String[] uniquebigrams = null;
	ArrayList<String> ar = new ArrayList<String>();
	String filePath="";

	    for(int i=0; i< test_file_paths.size(); i++){
	    	
	    	filePath = test_file_paths.get(i).toString();  
		System.out.println(filePath);						   
		   String[] arr;
		   String[] toAdd;

			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			
			while ((line = br.readLine()) != null)
			{
				arr = line.split("\\\t");
				if (arr.length > 2){
/*					System.out.println("Redundant " + arr[0] 
	                                 + " , needed " + arr[2] 
	                            );*/
					for(int i1=1; i1< arr.length; i1++){

					arr[i1]=	arr[i1].replaceAll("\\\"", " ");	
					arr[i1]=	arr[i1].replaceAll("^[A-Fa-f0-9]+$", "hexadecimal");
					arr[i1]=	arr[i1].replaceAll("\\d+", "number");
				
					toAdd = arr[i1].split("\\s+");
					
				for(int i11 =1; i11< toAdd.length; i11++)
					{
					if(toAdd[i11].contains("0x")){
						toAdd[i11]="hexadecimal";}
					bigrams.add(toAdd[i11-1].trim() + " " +toAdd[i11].trim());
						//	System.out.println(toAdd[i1-1]+ " " +toAdd[i1]);
	            	}	
				}
			}
			
			
	    }	 	      
		       
	    }
	    uniquebigrams = bigrams.toArray(new String[bigrams.size()]);
		

	    return uniquebigrams;
	    
    }
    
    
    public static float [] getDisBigramsTF (String featureText, String[] DisBigrams ) throws IOException
    {    
        float symbolCount = DisBigrams.length;
        float [] counter = new float[(int) symbolCount];
        String str;
        for (int i =0; i<symbolCount; i++){

     	  str = DisBigrams[i].toString();

   		featureText=	featureText.replaceAll("\\\"", " ");	
   		featureText=	featureText.replaceAll("^[A-Fa-f0-9]+$", "hexadecimal");
   		featureText=	featureText.replaceAll("\\d+", "number");
  	
     	 counter[i] = StringUtils.countMatches(featureText, str);  	   

        }
        return counter;
    

}

    
    
    
    
	public static List <File> listBjoernEdgeFiles(String dirPath)
    {

        File topDir = new File(dirPath);

        List<File> directories = new ArrayList<>();
        directories.add(topDir);

        List<File> textFiles = new ArrayList<>();

        List<String> filterWildcards = new ArrayList<>();
        filterWildcards.add("*edges.csv");


        FileFilter typeFilter = new WildcardFileFilter(filterWildcards);

        while (directories.isEmpty() == false)
        {
            List<File> subDirectories = new ArrayList<File>();

            for(File f : directories)
            {
                subDirectories.addAll(Arrays.asList(f.listFiles((FileFilter)DirectoryFileFilter.INSTANCE)));
                textFiles.addAll(Arrays.asList(f.listFiles(typeFilter)));
            }

            directories.clear();
            directories.addAll(subDirectories);


        }
        Collections.sort(textFiles);
        return textFiles;

}
    
	public static List <File> listBjoernNodeFiles(String dirPath)
    {

        File topDir = new File(dirPath);

        List<File> directories = new ArrayList<>();
        directories.add(topDir);

        List<File> textFiles = new ArrayList<>();

        List<String> filterWildcards = new ArrayList<>();
        filterWildcards.add("*nodes.csv");


        FileFilter typeFilter = new WildcardFileFilter(filterWildcards);

        while (directories.isEmpty() == false)
        {
            List<File> subDirectories = new ArrayList<File>();

            for(File f : directories)
            {
                subDirectories.addAll(Arrays.asList(f.listFiles((FileFilter)DirectoryFileFilter.INSTANCE)));
                textFiles.addAll(Arrays.asList(f.listFiles(typeFilter)));
            }

            directories.clear();
            directories.addAll(subDirectories);


        }
        Collections.sort(textFiles);
        return textFiles;

}
    
}