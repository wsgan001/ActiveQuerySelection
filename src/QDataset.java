import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class QDataset {
	public int nQ;
	public Query[] listOfQueries;
	String filename;
	public ArrayList<Query> base;
	public ArrayList<Query> subset1;
	public ArrayList<Query> subset2;
	public ArrayList<Query> candidates;
	public ArrayList<Query> test; // never updated yet from RunTestingAlgorithm
	public ArrayList<Query> randomQ;
	public double resultRandom, resultCandidates;
	public HashMap<String, HashMap<String, Float>> queryTerms;
	
	public int batchSize = 5;
	public int limit = 600;

	//public Query[] listOfCandidateQueries;
	public int nCandidateQ;

	public int nBase;

	public QDataset(String filename) throws IOException
	{
		this.filename = filename;
		nQ=0;
		listOfQueries = new Query[10000];
		base = new ArrayList<Query>();
		subset1 = new ArrayList<Query>();
		subset2 = new ArrayList<Query>();
		candidates = new ArrayList<Query>();
		test = new ArrayList<Query>();
		randomQ = new ArrayList<Query>();
		queryTerms = new HashMap<String, HashMap<String, Float>>();

		
		populateQueryTerms();
		populateDataset();
		populateBase();
		populateCandidates();
	}
	
	public void populateQueryTerms() throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader("src/data/LETOR/queryTerms"));
		String line = br.readLine();
		String prevQID = "";
		int justStarted = 1;
		HashMap<String, Float> hm = new HashMap<String, Float>();
		while(line != null)
		{
			String qID = line.substring(6, line.indexOf('.', 6));
			String word = line.substring(line.indexOf('>')+1, line.indexOf('<', line.indexOf('>')));
			System.out.println("Query: "+qID+"_"+word+"_");
			if(prevQID.compareTo(qID) != 0)
			{
				// this means this is a new qID, so create a new HashMap for this qID
				// but first, put this hm into the queryTerms
				if(justStarted == 1) {justStarted++;} else queryTerms.put(prevQID, hm);
				System.out.println("Putting query "+prevQID +" into the hashmap with no of words: "+hm.size()+"\n\n");
				hm = new HashMap<String, Float>();

				//q = new Query();
			}
			if(hm.containsKey(word))
			{
				Float f = hm.get(word);
				f++;
				hm.put(word, f);
			}
			else
			{
				hm.put(word, new Float(1.0));
			}
			prevQID = qID;
			line = br.readLine();
		}
		this.queryTerms.put(prevQID, hm);
		System.out.println("Putting query "+prevQID +" into the hashmap with no of words: "+hm.size()+"\n\n");
		System.out.println("Total final size of queryterms: "+queryTerms.size());
	}
	
	void populateCandidates() {
		for(int i=21;i<limit;i++)
		{
			if(listOfQueries[i].nD > 0) candidates.add(listOfQueries[i]);
		}
		System.out.println("Candidates populated with "+candidates.size());
	}

	void populateDataset()
	{
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			String prevQID = "";
			Query q = new Query();
			int c=0;
			while(line != null)
			{
				
				String qID = line.substring(6, line.indexOf(' ', 6));
				//System.out.println("Dataset qID=_"+qID+"_");
				if(prevQID.compareTo(qID) != 0)
				{
					//System.out.println("new query found:"+prevQID+" "+qID);
					// new query found
					//first add previous query to the list of queries in the dataset
					listOfQueries[nQ++] = q;
					c+=q.nD;
					q = new Query();
				}
				prevQID = qID;
				q.addDoc(line);
				//if(nQ%1000 == 0) System.out.println(qID);
				line = br.readLine();
			}
			listOfQueries[nQ++] = q;
			c+= q.nD;
			System.out.println("nQ= "+nQ);
			System.out.println("Total no of documents = should be total no of lines in the file = c= "+c);
			System.out.println("Database populated with "+this.nQ);
		} catch (FileNotFoundException e) {e.printStackTrace();} catch (IOException e) {e.printStackTrace();}
	}
	
	void populateBase()
	{
		for(int i=0;i<20;i++)
		{
			base.add(listOfQueries[i]);
		}
		this.nBase = 20;
		System.out.println("Base populated with "+base.size());
	}
	
	public ArrayList<Query> getCandidates() {
		return candidates;
	}

	public void setCandidates(ArrayList<Query> candidates) {
		this.candidates = candidates;
	}
	
	public int getnCandidateQ() {
		return nCandidateQ;
	}

	public void setnCandidateQ(int nCandidateQ) {
		this.nCandidateQ = nCandidateQ;
	}
	
	public ArrayList<Query> getTest() {
		return test;
	}

	public void setTest(ArrayList<Query> test) {
		this.test = test;
	}
	
	public ArrayList<Query> getRandomQ() {
		return randomQ;
	}

	public void setRandomQ(ArrayList<Query> randomQ) {
		this.randomQ = randomQ;
	}
	
	/*public Query[] getListOfCandidateQueries() {
		return listOfCandidateQueries;
	}

	public void setListOfCandidateQueries(Query[] listOfTestQueries) {
		this.listOfCandidateQueries = listOfTestQueries;
	}*/
}
