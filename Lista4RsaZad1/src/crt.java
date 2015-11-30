
import java.math.BigInteger; 
import java.sql.Savepoint;
import java.util.Random;
import java.io.*;
  
public class crt { 
	
    private BigInteger p; 
    private BigInteger q; 
    private static BigInteger N; 
    private BigInteger phi; 
    private static BigInteger e; 
    private static BigInteger d; 
    private static int k; // ilosc liczb prime
    private static int bitlength; 
    private int blocksize = 256; //blocksize in byte 
    static FileOutputStream out;
    static File file;
    static BigInteger[] tab;
    static BigInteger[] dxtab;
    static BigInteger[] qinvert;
    static BigInteger[] massagex;//zbedne
     
    class MyThread extends Thread {

        private int id;
        private Boolean done = false;
        public MyThread(int id) {
            this.id = id;
        }

        @Override
        public void run() {
        	while (done==false) {
        		dxtab[id] = d.mod(tab[id].subtract(BigInteger.ONE));
        		if(id<k-1) qinvert[id] = tab[id].modInverse(tab[id+1]);
        		else qinvert[id] = tab[id].modInverse(tab[0]);
        		System.out.println("Thread"+id+"is done");
        		done = true;
            }        	   
        }
    }


 
    private Random r; 
     public crt() { 
        r = new Random(); 
        /*p = BigInteger.probablePrime(bitlength, r); 
        q = BigInteger.probablePrime(bitlength, r); 
        N = p.multiply(q); 
           
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); 
        e = BigInteger.probablePrime(bitlength/2, r); 
         
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0 ) { 
            e.add(BigInteger.ONE); 
        } */
        tab =new BigInteger[k];
    	for (int i = 0; i < k; i++) //generujemy piczby pierwsze i mnozymy n i tworzymy phi
        {
    		tab[i] = BigInteger.probablePrime(bitlength, r);
    		if(i==0)
    		{
    			N = tab[i] ;
    			phi = tab[i].subtract(BigInteger.ONE) ;
    		}
    		else
    		{
    			N = N.multiply(tab[i]);
    			phi = phi.multiply(tab[i].subtract(BigInteger.ONE));
    		}
    		
		}
    	
    	e = BigInteger.probablePrime(bitlength/2, r);
    	while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0 ) { 
            e.add(BigInteger.ONE); 
        }
    	d = e.modInverse(phi);  
    	dxtab = new BigInteger[k];
    	qinvert = new BigInteger[k];
    	massagex = new BigInteger[k];
    	for (int i = 0; i < k; i++) {
    		new MyThread(i).start();
		}
    	

    	 
    	 
    } 
     
    public crt(BigInteger e, BigInteger d, BigInteger N) { 
        this.e = e; 
        this.d = d; 
        this.N = N; 
    } 
     
    @SuppressWarnings("deprecation")
	public static void main (String[] args) throws IOException
    { 
    	DataInputStream in=new DataInputStream(System.in); 
    	String testk;
        String testd;
        System.out.println("Enter the k:");
        testk=in.readLine();
        System.out.println("Enter the d(bitlength):");
        testd=in.readLine();
        k = Integer.parseInt(testk);
        bitlength = Integer.parseInt(testd);
        String teststring ;        
        System.out.println("Enter the plain text:");
        teststring=in.readLine();
    	crt rsa = new crt(); 
    	savekeys();
        
        System.out.println("Encrypting String: " + teststring); 
        System.out.println("String in Bytes: " + bytesToString(teststring.getBytes())); 

        // encrypt 
        byte[] encrypted = rsa.encrypt(teststring.getBytes());                   
        System.out.println("Encrypted String in Bytes: " + bytesToString(encrypted)); 
         
        // decrypt 
        byte[] decrypted = rsa.decrypt(encrypted);       
        System.out.println("Decrypted String in Bytes: " +  bytesToString(decrypted)); 
         
        System.out.println("Decrypted String: " + new String(decrypted)); 
         
    } 

   private static String bytesToString(byte[] encrypted) { 
        String test = ""; 
        for (byte b : encrypted) { 
            test += Byte.toString(b); 
        } 
        return test; 
    } 
     
 //Encrypt message
     public byte[] encrypt(byte[] message) {      
        return (new BigInteger(message)).modPow(e, N).toByteArray(); 
    } 
       
// Decrypt message
    public byte[] decrypt(byte[] message) { 
    	BigInteger[] massagedec = new BigInteger[k];
    	for (int i = 0; i < massagedec.length; i++) {
    		massagedec[i] = new BigInteger(message).modPow(dxtab[i], tab[i]);
		}
    	BigInteger temp = BigInteger.ZERO;
    	for (int i = 0; i < massagedec.length; i++) {
			temp = temp.add(massagedec[i].multiply(qinvert[k-i-1]).multiply(tab[k-i-1]) );
		}
    	temp.mod(N);
    	
        return (temp.mod(N).toByteArray()); 
    }  
    public static void savekeys() throws IOException
    {
    	file = new File("C:\\Users\\Grimm\\git\\lista_4\\Lista4RsaZad1\\src\\public.key");
    	out = new FileOutputStream(file);
    	
    	BigInteger tE = e;
    	BigInteger tN = N;
    	String temp = "e:"+tE+"N:"+tN;
    	//
    	byte[] pubkkeycontent = temp.getBytes();
    	out.write(pubkkeycontent);
    	out.flush();
    	out.close();
    	
    	file = new File("C:\\Users\\Grimm\\git\\lista_4\\Lista4RsaZad1\\src\\private.key");
    	out = new FileOutputStream(file);
    	//w private jest liczby prime,dxtab i qinvert
    	temp = "";
    	for (int i = 0; i < k; i++) {
			temp += "prime"+i+":"+tab[i];
		}    	
    	for (int i = 0; i < k; i++) {
			temp += "dx"+i+":"+dxtab[i];
		}
    	for (int i = 0; i < k; i++) {
			temp += "qinvert"+i+":"+qinvert[i];
		}
    	pubkkeycontent = temp.getBytes();
    	out.write(pubkkeycontent);
    	out.flush();
    	out.close();
    }
}