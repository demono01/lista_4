import java.math.BigInteger; 
import java.sql.Savepoint;
import java.util.Random;
import java.io.*;
  
public class Tradycyjne { 
	
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
     
    private Random r; 
     public Tradycyjne() { 
        r = new Random(); 
        /*p = BigInteger.probablePrime(bitlength, r); 
        q = BigInteger.probablePrime(bitlength, r); 
        N = p.multiply(q); 
           
        phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); 
        e = BigInteger.probablePrime(bitlength/2, r); 
         
        while (phi.gcd(e).compareTo(BigInteger.ONE) > 0 && e.compareTo(phi) < 0 ) { 
            e.add(BigInteger.ONE); 
        } */
        BigInteger[] tab =new BigInteger[k];
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
    } 
     
    public Tradycyjne(BigInteger e, BigInteger d, BigInteger N) { 
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
    	Tradycyjne rsa = new Tradycyjne(); 
    	savekeys();
        String teststring ;        
        System.out.println("Enter the plain text:");
        teststring=in.readLine();
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
        return (new BigInteger(message)).modPow(d, N).toByteArray(); 
    }  
    public static void savekeys() throws IOException
    {
    	file = new File("C:\\Users\\Grimm\\git\\lista_4\\Lista4RsaZad1\\src\\public.key");
    	out = new FileOutputStream(file);
    	
    	BigInteger tE = e;
    	BigInteger tN = N;
    	String temp = "e:"+tE+"N:"+tN;
    	byte[] pubkkeycontent = temp.getBytes();
    	out.write(pubkkeycontent);
    	out.flush();
    	out.close();
    	
    	file = new File("C:\\Users\\Grimm\\git\\lista_4\\Lista4RsaZad1\\src\\private.key");
    	out = new FileOutputStream(file);
    	BigInteger tD = d;
    	temp = "d:"+tD+"N:"+tN;
    	pubkkeycontent = temp.getBytes();
    	out.write(pubkkeycontent);
    	out.flush();
    	out.close();
    }
}