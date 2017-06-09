
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.el.lang.ELArithmetic.LongDelegate;






/** * Servlet implementation class LoginServlet */ 
public class searchservlet extends HttpServlet { 
	
	SimpleCache<String> cache = new SimpleCache<String>();
	SimpleCache<String> syncache = new SimpleCache<String>();
	
	String word;
	String url; String tfidf;
	String[][] tfidfarr;
	 String[][] urlsarr;
	 List[] lists;
	 HashMap<String,String> amap;
	 
	 public static String stemmer(String args)
     {
        char[] w = new char[500000];
        //char ch;
        String temp = new String(args);
        temp = temp.trim();
        
        System.out.println(temp.length());
        System.out.println("In Stemmer");
        String output="";
        	
        Stemmer s = new Stemmer();
        
               //int ch = in.read();
        int t=0;
  					
  			while(true)
  			{		if(t==temp.length())break;
          	       int ch =(int) temp.charAt(t);
          	       t++;
          	        //System.out.println("ch is:"+ch);
                if (Character.isLetter((char) ch))
                {
                   int j = 0;
                   while(true)
                   {  ch = Character.toLowerCase((char) ch);
                      w[j] = (char) ch;
                      if (j < 500000) j++;
                      
                     
                      if(t==temp.length()) {ch = -1;}
                      if(t<=(temp.length()-1))
                      {
                      	//if(t==(temp.length()-1)){ ch = -1; }
                      	ch = (int) temp.charAt(t);
                      	t++;
                      }
                      //System.out.println("ch2 is:"+ch);
                      if (!Character.isLetter((char) ch))
                      {
                         /* to test add(char ch) */
                         for (int c = 0; c < j; c++) s.add(w[c]);

                         /* or, to test add(char[] w, int j) */
                         /* s.add(w, j); */

                         s.stem();
                         {  String u;

                            /* and now, to test toString() : */
                            u = s.toString();

                            System.out.print(u);
                            System.out.print("Before Concatenating"); 
                            output = output.concat(" ").concat(u);
                            
                         }
                         break;
                      }
                   }
                
                }
                if (ch < 0) break;
                System.out.print((char)ch);
             }
  			return output;
     }


	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, java.io.IOException {
		doPost(request, response);
	}
	
public void doPost(HttpServletRequest request, HttpServletResponse response) 
throws ServletException, java.io.IOException {
	//ArrayList synList = new ArrayList<>();
	HashMap<String,String> synmap = new HashMap<String, String>();
			
	String word;
	String url; String tfidf;String urlsyn;String syn;
	String[][] tfidfarr;
	 String[][] urlsarr;
	 List[] lists;
	 List[] slists;
	 HashMap<String,String> amap;
	String uinput = request.getParameter("username");
	StringTokenizer stz = new StringTokenizer(uinput, " ");
	String[] arr= new String[stz.countTokens()];
	int temp=0;
	long timebefore = System.currentTimeMillis();
	 
	System.out.println("CountTokens: "+stz.countTokens());
	int tokens = stz.countTokens();
	if(tokens==0){
		HttpSession session = request.getSession(true); 
		
		//session.setAttribute("noinput","noinput"); 
		
		response.sendRedirect("index.jsp");  
		
	} else{
	while(stz.hasMoreTokens()){
			String before=	stz.nextToken();
			before = stemmer(before);
			before = before.trim();
			
		arr[temp++]= before;
		System.out.println("intialialized array");
	}

	lists = new List[tokens];
	slists = new List[tokens];
	System.out.println("content is: "+arr[0]);
	
	

	//hit the cache
	for(int i=0;i<tokens;i++){
	lists[i]=cache.get(arr[i]);
	slists[i]=syncache.get(arr[i]);
	System.out.println("hitted cache");
	
	
	
	if(lists[i]==null|| slists[i]==null){
		syncache.put(arr[i],null);
		cache.put(arr[i],null);
		ArrayList<Worddata> mylist = new ArrayList<Worddata>();
		ArrayList<Synopsisdata> synlist = new ArrayList<Synopsisdata>(); 
		System.out.println("trying connection to database as not found in cache");
		Connection con=null;
		Statement st=null;
		Statement st2 = null;
		ResultSet rs=null;
		ResultSet rssyn=null;
		
		try
		{

		Class.forName("com.mysql.jdbc.Driver").newInstance ();
		String synquery="";
		String query="";
			String name = arr[i];
		con=DriverManager.getConnection("jdbc:mysql://localhost/Search","root","");
		st=con.createStatement() ;
		st2=con.createStatement();
		char input  = name.charAt(0);
		System.out.println("character is:"+input);
		//name = stemmer(name);
		//name = name.trim();
		
		switch(input){
		case 'a': 
			 query = "SELECT * FROM a where word='appl' LIMIT 100;";
			System.out.println("hitted database with name: "+name);
			break;
		case 'b':
			query = "SELECT * FROM b s where word='"+name+"' and s.tfidf<'1'";
			synquery = "SELECT * FROM bsynopsis where word='"+name+"' ";
			System.out.println("hitted database with name: "+input);
			break;
		case 'c':
			query = "SELECT * FROM c s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'd':
			query = "SELECT * FROM d s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'e':
			query = "SELECT * FROM e s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'f':
			query = "SELECT * FROM f s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'g':
			query = "SELECT * FROM g s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'h':
			query = "SELECT * FROM h s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'i':
			query = "SELECT * FROM i s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'j':
			query = "SELECT * FROM j s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'k':
			query = "SELECT * FROM k s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'l':
			query = "SELECT * FROM l s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'm':
			query = "SELECT * FROM m s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'n':
			query = "SELECT * FROM n s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'o':
			query = "SELECT * FROM o s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'p':
			query = "SELECT * FROM p s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'q':
			query = "SELECT * FROM q s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'r':
			query = "SELECT * FROM r s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 's':
			query = "SELECT * FROM s s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 't':
			query = "SELECT * FROM t s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'u':
			query = "SELECT * FROM u s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'v':
			query = "SELECT * FROM v s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'w':
			query = "SELECT * FROM w s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'x':
			query = "SELECT * FROM x s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'y':
			query = "SELECT * FROM y s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		case 'z':
			query = "SELECT * FROM z s where word='"+name+"' ORDER BY s.tfidf DESC";
			System.out.println("hitted database with name: "+input);
			break;
		
			
			
		}
		
		System.out.println("Query is:"+query);
		
		
		rs=st.executeQuery(query);
		rssyn=st2.executeQuery(synquery);
		System.out.println("Executed query");
		Double ftfidf=.0000;
		Random rd = new Random();
		while(rssyn.next()){
			urlsyn=rssyn.getString(2);
			syn=rssyn.getString(3);
			System.out.println("url is: "+urlsyn+" syn is:"+syn );
			//synmap.put(urlsyn, syn);
			Synopsisdata syndata = new Synopsisdata(urlsyn,syn);
			synlist.add(syndata);
			
			
			
		}
		
		while(rs.next())
		{
		 word=rs.getString(1);
		 url = rs.getString(2);
		 tfidf = rs.getString(3);
		 String somenum = Integer.toString(rd.nextInt(100));
		 String y = tfidf.concat(somenum);
		 tfidf = y;
		 
		 try{
		 if(!(tfidf.equals(null)))
		  ftfidf=Double.parseDouble(tfidf);
		 if(ftfidf>=1.0000||ftfidf<0.00){
			 
			 //ftfidf= 0.00000;
			 
			 String othertemp = ".000000000000";
			 String someothernum = Integer.toString(rd.nextInt(100));
			 String z = othertemp.concat(someothernum);
			 //ftfidf = Double.parseDouble(z);
			 tfidf = z;
		 }
			 
		 }catch(Exception e){
			 //ftfidf = 0.00000;
			 String othertemp = ".000000000000";
			 String someothernum = Integer.toString(rd.nextInt(100));
			 String z = othertemp.concat(someothernum);
			 //ftfidf = Double.parseDouble(z);
			 tfidf=z;
		 }
		 
		//System.out.println("Got the resulset");
		//System.out.println(" "+word+" "+url+" "+tfidf);
		
		
		 Worddata data = new Worddata(url,tfidf);
		mylist.add(data);
		
		//System.out.println("word is:"+word+"url is: "+url+"tfidf is: "+tfidf);
		
		}
		long timeafterexecution = System.currentTimeMillis();
		
		long timeforexecution = timeafterexecution - timebefore;
		
		System.out.println("time taken for executing query combined is: "+timeforexecution);
		cache.put(name, mylist);
		syncache.put(name, synlist);
		
		lists[i] = mylist;
		slists[i]= synlist;
		

		}
		catch(Exception eer)
		{
		System.out.println(eer.toString());
		}
		
	}
	System.out.println("the For Loop");
	
	}
	String[][] urlsynarr = new String[tokens][];
	String[][] synarr = new String[tokens][];
	tfidfarr = new String[tokens][];
	urlsarr = new String[tokens][];
	
	for(int i=0;i<tokens;i++){
		
		tfidfarr[i]= new String[lists[i].size()];
		urlsarr[i] = new String[lists[i].size()];
		System.out.println("The size is: "+slists[i].size());
		urlsynarr[i] = new String[slists[i].size()];
		synarr[i] = new String[slists[i].size()];
	}
	
	
	
	for(int i =0;i<tokens;i++){
		int j=0;
		
		for(Iterator synit = slists[i].iterator(); synit.hasNext();){
			Synopsisdata thesynopsis = (Synopsisdata) synit.next();
			urlsynarr[i][j] = thesynopsis.url;
			synarr[i][j] = thesynopsis.synopsis;
			j = j+1;
		}
		j=0;
		for(Iterator it = lists[i].iterator(); it.hasNext();) {
			
			Worddata theword=(Worddata) it.next();
			tfidfarr[i][j] = theword.tfidf;
			urlsarr[i][j] = theword.url;
			j = j+1;
	       
		}
	        
	    }
	System.out.println("inserted inside tfidf and urls");
	
	int total=0;
	for(int i=0;i<tokens;i++){
		total = total+lists[i].size();
	}
long timeafter = System.currentTimeMillis();
	
	long time = timeafter - timebefore;
	
	System.out.println("time taken is: "+time);
	
	
	
	 amap = new HashMap<String, String>();
	for(int i=0;i<tokens;i++){
		for(int k=0;k<tfidfarr[i].length;k++){
			if(amap.get(urlsarr[i][k])==null){
			amap.put(urlsarr[i][k],tfidfarr[i][k]);
			}else {
				Double other=Double.valueOf(amap.get(urlsarr[i][k]))+ Double.valueOf(tfidfarr[i][k]);
				String finalstring = BigDecimal.valueOf(other).toPlainString();
				String thefinalstring = finalstring.replaceFirst("0", "");
				amap.put(urlsarr[i][k], thefinalstring );
			}
		}
	}
	List<String> somelist = new ArrayList<String>();
	if(tokens>1){
		for(Iterator it = somelist.iterator(); it.hasNext();) {
		for(int i=0;i<tokens;i++){
			for(int k=0;k<synarr[i].length;k++){
				
			
			String synopsis=synmap.get(urlsynarr[i][k]).toString();
			String temptoken =it.next().toString();
			StringTokenizer nooftokens = new StringTokenizer(temptoken);
			if(synopsis.indexOf(temptoken)!=-1){
				Double temptfidf=Double.parseDouble(amap.get(urlsynarr[i][k])) + (nooftokens.countTokens()*5);
				amap.put(urlsynarr[i][k], temptfidf.toString());
			}
			
			}
		}
	}
	}
	for(int i=0;i<tokens;i++){
		for(int k=0; k<synarr[i].length;k++){
			synmap.put(urlsynarr[i][k], synarr[i][k]);
		}
	}
	
	System.out.println("inserted inside hashmap");
	System.out.println("Trying for Wikipedia data");
	String iurl= "http://en.wikipedia.org/wiki/"+uinput;
	URL wikiurl = new URL(iurl);
    URLConnection conn = wikiurl.openConnection();
    conn.setDoOutput(true);
    //OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
    //wr.write(data);
    //wr.flush();

    // Get the response
    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    String line;
  /*  while ((line = rd.readLine()) != null) {
       System.out.println(" "+line);
    } */
   // wr.close();
    rd.close();
	/*Iterator its = amap.entrySet().iterator();
	while(its.hasNext()){
		Map.Entry pairs= (Map.Entry) its.next();
		//System.out.println(pairs.getKey()+"@"+ pairs.getValue());
		
	} */
	//Map reverseOrderedMap = new TreeMap(Collections.reverseOrder());
	TreeMap<String,String> sortedMap = new TreeMap<String,String>(Collections.reverseOrder());
	Iterator itr = amap.keySet().iterator();
	while(itr.hasNext()){
		String next = (String) itr.next();
		sortedMap.put(amap.get(next),next);
	}
	
	
	
long timeaftersorted = System.currentTimeMillis();
	
	long timeforsorting = timeaftersorted - timebefore;
	
	System.out.println("time taken for sorting combined is: "+timeforsorting);
	List<String> antlist = new ArrayList<String>();
    Iterator it = sortedMap.keySet().iterator();
    while(it.hasNext()){
    String key=it.next().toString();
   
    String value = sortedMap.get(key);
    String synopsis = synmap.get(value);
    antlist.add(value);
    antlist.add(synopsis);
    
   
    }
    it = antlist.iterator();
    while(it.hasNext()){
    	String damnit=(String) it.next();
    	//System.out.println("key is:"+damnit);
    }
    
	System.out.println("Sending it to output.jsp");
	request.setAttribute("user", sortedMap);
	ServletContext context = getServletContext();
	RequestDispatcher dispatcher = context.getRequestDispatcher("/output.jsp");
	dispatcher.forward(request, response);
	
	//HttpSession session = request.getSession(true); 
	//session.setAttribute("user",sortedOutputMap); 
	//response.sendRedirect("output.jsp");  
	
	} 
}

} 
/*
 * SimpleCache
 * Copyright (C) 2008 Christian Schenk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 */




/**
 * This class provides a very simple implementation of an object cache.
 * 
 * @author Christian Schenk
 */
 class SimpleCache<T> {

	/** Objects are stored here */
	private final Map<String, List> objects;
	/** Holds custom expiration dates */
	private final Map<String, Long> expire;
	/** The default expiration date */
	private final long defaultExpire;
	/** Is used to speed up some operations */
	private final ExecutorService threads;

	/**
	 * Constructs the cache with a default expiration time for the objects of
	 * 100 seconds.
	 */
	public SimpleCache() {
		this(500);
	}

	/**
	 * Construct a cache with a custom expiration date for the objects.
	 * 
	 * @param defaultExpire
	 *            default expiration time in seconds
	 */
	public SimpleCache(final long defaultExpire) {
		this.objects = Collections.synchronizedMap(new HashMap<String, List>());
		this.expire = Collections.synchronizedMap(new HashMap<String, Long>());

		this.defaultExpire = defaultExpire;

		this.threads = Executors.newFixedThreadPool(256);
		Executors.newScheduledThreadPool(2).scheduleWithFixedDelay(this.removeExpired(), this.defaultExpire / 2, this.defaultExpire, TimeUnit.SECONDS);
	}

	/**
	 * This Runnable removes expired objects.
	 */
	private final Runnable removeExpired() {
		return new Runnable() {
			public void run() {
				for (final String name : expire.keySet()) {
					if (System.currentTimeMillis() > expire.get(name)) {
						threads.execute(createRemoveRunnable(name));
					}
				}
			}
		};
	}

	/**
	 * Returns a runnable that removes a specific object from the cache.
	 * 
	 * @param name
	 *            the name of the object
	 */
	private final Runnable createRemoveRunnable(final String name) {
		return new Runnable() {
			public void run() {
				objects.remove(name);
				expire.remove(name);
			}
		};
	}

	/**
	 * Returns the default expiration time for the objects in the cache.
	 * 
	 * @return default expiration time in seconds
	 */
	public long getExpire() {
		return this.defaultExpire;
	}

	/**
	 * Put an object into the cache.
	 * 
	 * @param name
	 *            the object will be referenced with this name in the cache
	 * @param obj
	 *            the object
	 */
	public void put(final String name, final List obj) {
		this.put(name, obj, this.defaultExpire);
	}

	/**
	 * Put an object into the cache with a custom expiration date.
	 * 
	 * @param name
	 *            the object will be referenced with this name in the cache
	 * @param obj
	 *            the object
	 * @param expire
	 *            custom expiration time in seconds
	 */
	public void put(final String name, final List obj, final long expireTime) {
		
		this.objects.put(name, obj);
		this.expire.put(name, System.currentTimeMillis() + expireTime * 1000);
	}

	/**
	 * Returns an object from the cache.
	 * 
	 * @param name
	 *            the name of the object you'd like to get
	 * @param type
	 *            the type of the object you'd like to get
	 * @return the object for the given name and type
	 */
	public List get(final String name) {
		final Long expireTime = this.expire.get(name);
		if (expireTime == null) return null;
		if (System.currentTimeMillis() > expireTime) {
			this.threads.execute(this.createRemoveRunnable(name));
			return null;
		}
		return this.objects.get(name);
	}

	/**
	 * Convenience method.
	 */
	/*@SuppressWarnings("unchecked")
	public <R extends T> R get(final String name, final Class<R> type) {
		return (R) this.get(name);
	} */
}
  class Worddata {
		String url;
		String tfidf;
		
		public Worddata(String a,String b){
			url= a;
			tfidf = b;
		}

	}
  class Synopsisdata {
		String url;
		String synopsis;
		
		public Synopsisdata(String a,String b){
			url= a;
			synopsis = b;
		}

	}
  
   class Stemmer
  {  public char[] b;
     public int i,     /* offset into b */
                 i_end, /* offset to end of stemmed word */
                 j, k;
     public static final int INC = 500000;
                       /* unit of size whereby b is increased */
     public Stemmer()
     {  b = new char[INC];
        i = 0;
        i_end = 0;
     }

     /**
      * Add a character to the word being stemmed.  When you are finished
      * adding characters, you can call stem(void) to stem the word.
      */

     public void add(char ch)
     {  if (i == b.length)
        {  char[] new_b = new char[i+INC];
           for (int c = 0; c < i; c++) new_b[c] = b[c];
           b = new_b;
        }
        b[i++] = ch;
     }


     /** Adds wLen characters to the word being stemmed contained in a portion
      * of a char[] array. This is like repeated calls of add(char ch), but
      * faster.
      */

     public void add(char[] w, int wLen)
     {  if (i+wLen >= b.length)
        {  char[] new_b = new char[i+wLen+INC];
           for (int c = 0; c < i; c++) new_b[c] = b[c];
           b = new_b;
        }
        for (int c = 0; c < wLen; c++) b[i++] = w[c];
     }

     /**
      * After a word has been stemmed, it can be retrieved by toString(),
      * or a reference to the internal buffer can be retrieved by getResultBuffer
      * and getResultLength (which is generally more efficient.)
      */
     public String toString() { return new String(b,0,i_end); }

     /**
      * Returns the length of the word resulting from the stemming process.
      */
     public int getResultLength() { return i_end; }

     /**
      * Returns a reference to a character buffer containing the results of
      * the stemming process.  You also need to consult getResultLength()
      * to determine the length of the result.
      */
     public char[] getResultBuffer() { return b; }

     /* cons(i) is true <=> b[i] is a consonant. */

     public final boolean cons(int i)
     {  switch (b[i])
        {  case 'a': case 'e': case 'i': case 'o': case 'u': return false;
           case 'y': return (i==0) ? true : !cons(i-1);
           default: return true;
        }
     }

     /* m() measures the number of consonant sequences between 0 and j. if c is
        a consonant sequence and v a vowel sequence, and <..> indicates arbitrary
        presence,

           <c><v>       gives 0
           <c>vc<v>     gives 1
           <c>vcvc<v>   gives 2
           <c>vcvcvc<v> gives 3
           ....
     */

     public final int m()
     {  int n = 0;
        int i = 0;
        while(true)
        {  if (i > j) return n;
           if (! cons(i)) break; i++;
        }
        i++;
        while(true)
        {  while(true)
           {  if (i > j) return n;
                 if (cons(i)) break;
                 i++;
           }
           i++;
           n++;
           while(true)
           {  if (i > j) return n;
              if (! cons(i)) break;
              i++;
           }
           i++;
         }
     }

     /* vowelinstem() is true <=> 0,...j contains a vowel */

     public final boolean vowelinstem()
     {  int i; for (i = 0; i <= j; i++) if (! cons(i)) return true;
        return false;
     }

     /* doublec(j) is true <=> j,(j-1) contain a double consonant. */

     public final boolean doublec(int j)
     {  if (j < 1) return false;
        if (b[j] != b[j-1]) return false;
        return cons(j);
     }

     /* cvc(i) is true <=> i-2,i-1,i has the form consonant - vowel - consonant
        and also if the second c is not w,x or y. this is used when trying to
        restore an e at the end of a short word. e.g.

           cav(e), lov(e), hop(e), crim(e), but
           snow, box, tray.

     */

     public final boolean cvc(int i)
     {  if (i < 2 || !cons(i) || cons(i-1) || !cons(i-2)) return false;
        {  int ch = b[i];
           if (ch == 'w' || ch == 'x' || ch == 'y') return false;
        }
        return true;
     }

     public final boolean ends(String s)
     {  int l = s.length();
        int o = k-l+1;
        if (o < 0) return false;
        for (int i = 0; i < l; i++) if (b[o+i] != s.charAt(i)) return false;
        j = k-l;
        return true;
     }

     /* setto(s) sets (j+1),...k to the characters in the string s, readjusting
        k. */

     public final void setto(String s)
     {  int l = s.length();
        int o = j+1;
        for (int i = 0; i < l; i++) b[o+i] = s.charAt(i);
        k = j+l;
     }

     /* r(s) is used further down. */

     public final void r(String s) { if (m() > 0) setto(s); }

     /* step1() gets rid of plurals and -ed or -ing. e.g.

            caresses  ->  caress
            ponies    ->  poni
            ties      ->  ti
            caress    ->  caress
            cats      ->  cat

            feed      ->  feed
            agreed    ->  agree
            disabled  ->  disable

            matting   ->  mat
            mating    ->  mate
            meeting   ->  meet
            milling   ->  mill
            messing   ->  mess

            meetings  ->  meet

     */

     public final void step1()
     {  if (b[k] == 's')
        {  if (ends("sses")) k -= 2; else
           if (ends("ies")) setto("i"); else
           if (b[k-1] != 's') k--;
        }
        if (ends("eed")) { if (m() > 0) k--; } else
        if ((ends("ed") || ends("ing")) && vowelinstem())
        {  k = j;
           if (ends("at")) setto("ate"); else
           if (ends("bl")) setto("ble"); else
           if (ends("iz")) setto("ize"); else
           if (doublec(k))
           {  k--;
              {  int ch = b[k];
                 if (ch == 'l' || ch == 's' || ch == 'z') k++;
              }
           }
           else if (m() == 1 && cvc(k)) setto("e");
       }
     }

     /* step2() turns terminal y to i when there is another vowel in the stem. */

     public final void step2() { if (ends("y") && vowelinstem()) b[k] = 'i'; }

     /* step3() maps double suffices to single ones. so -ization ( = -ize plus
        -ation) maps to -ize etc. note that the string before the suffix must give
        m() > 0. */

     public final void step3() { if (k == 0) return; /* For Bug 1 */ switch (b[k-1])
     {
         case 'a': if (ends("ational")) { r("ate"); break; }
                   if (ends("tional")) { r("tion"); break; }
                   break;
         case 'c': if (ends("enci")) { r("ence"); break; }
                   if (ends("anci")) { r("ance"); break; }
                   break;
         case 'e': if (ends("izer")) { r("ize"); break; }
                   break;
         case 'l': if (ends("bli")) { r("ble"); break; }
                   if (ends("alli")) { r("al"); break; }
                   if (ends("entli")) { r("ent"); break; }
                   if (ends("eli")) { r("e"); break; }
                   if (ends("ousli")) { r("ous"); break; }
                   break;
         case 'o': if (ends("ization")) { r("ize"); break; }
                   if (ends("ation")) { r("ate"); break; }
                   if (ends("ator")) { r("ate"); break; }
                   break;
         case 's': if (ends("alism")) { r("al"); break; }
                   if (ends("iveness")) { r("ive"); break; }
                   if (ends("fulness")) { r("ful"); break; }
                   if (ends("ousness")) { r("ous"); break; }
                   break;
         case 't': if (ends("aliti")) { r("al"); break; }
                   if (ends("iviti")) { r("ive"); break; }
                   if (ends("biliti")) { r("ble"); break; }
                   break;
         case 'g': if (ends("logi")) { r("log"); break; }
     } }

     /* step4() deals with -ic-, -full, -ness etc. similar strategy to step3. */

     public final void step4() { switch (b[k])
     {
         case 'e': if (ends("icate")) { r("ic"); break; }
                   if (ends("ative")) { r(""); break; }
                   if (ends("alize")) { r("al"); break; }
                   break;
         case 'i': if (ends("iciti")) { r("ic"); break; }
                   break;
         case 'l': if (ends("ical")) { r("ic"); break; }
                   if (ends("ful")) { r(""); break; }
                   break;
         case 's': if (ends("ness")) { r(""); break; }
                   break;
     } }

     /* step5() takes off -ant, -ence etc., in context <c>vcvc<v>. */

     public final void step5()
     {   if (k == 0) return; /* for Bug 1 */ switch (b[k-1])
         {  case 'a': if (ends("al")) break; return;
            case 'c': if (ends("ance")) break;
                      if (ends("ence")) break; return;
            case 'e': if (ends("er")) break; return;
            case 'i': if (ends("ic")) break; return;
            case 'l': if (ends("able")) break;
                      if (ends("ible")) break; return;
            case 'n': if (ends("ant")) break;
                      if (ends("ement")) break;
                      if (ends("ment")) break;
                      /* element etc. not stripped before the m */
                      if (ends("ent")) break; return;
            case 'o': if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
                                      /* j >= 0 fixes Bug 2 */
                      if (ends("ou")) break; return;
                      /* takes care of -ous */
            case 's': if (ends("ism")) break; return;
            case 't': if (ends("ate")) break;
                      if (ends("iti")) break; return;
            case 'u': if (ends("ous")) break; return;
            case 'v': if (ends("ive")) break; return;
            case 'z': if (ends("ize")) break; return;
            default: return;
         }
         if (m() > 1) k = j;
     }

     /* step6() removes a final -e if m() > 1. */

     public final void step6()
     {  j = k;
        if (b[k] == 'e')
        {  int a = m();
           if (a > 1 || a == 1 && !cvc(k-1)) k--;
        }
        if (b[k] == 'l' && doublec(k) && m() > 1) k--;
     }

     /** Stem the word placed into the Stemmer buffer through calls to add().
      * Returns true if the stemming process resulted in a word different
      * from the input.  You can retrieve the result with
      * getResultLength()/getResultBuffer() or toString().
      */
     public void stem()
     {  k = i - 1;
        if (k > 1) { step1(); step2(); step3(); step4(); step5(); step6(); }
        i_end = k+1; i = 0;
     }

     /** Test program for demonstrating the Stemmer.  It reads text from a
      * a list of files, stems each word, and writes the result to standard
      * output. Note that the word stemmed is expected to be in lower case:
      * forcing lower case must be done outside the Stemmer class.
      * Usage: Stemmer file-name file-name ...
      */
     
  }
  