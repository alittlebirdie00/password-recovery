/*******************************
 *
 * University of Central Florida
 * CIS 3360 - Fall 2017
 * Author: Rolando Murillo
 * 
 *******************************/

import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class RecoverPassword {
    
    // Variable declarations
    private static String hashValue;
    private static int counter = 0;
    private static String encryptedPassword;
    
    public static void main(String[] args) {
        RecoverPassword rp = new RecoverPassword();
        
        // Read command line arguments
        String fileName = args[0];
        RecoverPassword.hashValue = args[1];
        
        rp.printHeaderOutput(fileName, RecoverPassword.hashValue);
        
        // Append ".txt" if not already in the argument
        if(!fileName.endsWith(".txt"))
            fileName = fileName.concat(".txt");
        
        rp.printReport(fileName, hashValue);
    }
    
    // Print header output
    public void printHeaderOutput(String filename, String hashvalue) {
        
        // Print dashes
        for(int i = 0; i < 40; i++) {
            System.out.print("-");
        }
        
        // Header
        System.out.println("\nCIS3360 Password Recovery by Rolando Murillo");
        System.out.println("    Dictionary file name       : " + filename);
        System.out.println("    Salted password hash value : " + hashvalue);
    }
    
    public void printReport(String filename, String hashvalue) {
        String currentS, ascii;
        
        // Open and read file
        try{
            File f = new File(filename);           
            Scanner input = new Scanner(new FileInputStream(f));
            boolean passwordFound = false;
            
            // Print the dictionary contents
            System.out.println("\nIndex    Word     Unsalted ASCII equivalent");
            printDictionary(filename);
            
            // Conduct hash testings
            while(input.hasNextLine() && passwordFound == false) {
                currentS = input.nextLine();
                ascii = convertToAscii(currentS);
                passwordFound = conductHashTestingForAscii(ascii);
            }
            
            // Print success or failure report
            printSFReport(passwordFound);
            
        } catch(FileNotFoundException e) {
            System.out.println("Error \nFilenamed: \"" + filename +"\" not found");
        }
           

    }
    
    public void printDictionary(String filename) {
        try {
            int i = 1;
            
            // Open file
            File f = new File(filename);
            
            // Read the contents of the file
            Scanner input = new Scanner(new FileInputStream(f));
            
            // Format each Word and UnsaltedAscii line
            while(input.hasNextLine()) {
                String s = input.nextLine();
                System.out.printf("%4d", i);
                System.out.println("  :  " + s + "   " + convertToAscii(s));
                i++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error \nFilenamed: \"" + filename +"\" not found");
        }
    }
    
    public void printSFReport(boolean isSuccess) {
        
        // If password was found..
        if (isSuccess) {
            String ascii = RecoverPassword.encryptedPassword.substring(3);
            System.out.println("\nPassword recovered:");
            System.out.println("    Password              :" + convertAsciiToString(ascii));
            System.out.println("    ASCII value           :" + RecoverPassword.encryptedPassword.substring(3));
            System.out.println("    Salt value            :" + RecoverPassword.encryptedPassword.substring(0, 3));
            System.out.println("    Combinations tested   :" + RecoverPassword.counter);
           
        }
        else {
            System.out.println("\nPassword not found in dictionary\n\nCombinations tested: " + RecoverPassword.counter);
        }
        
    }
    
    public String convertAsciiToString(String ascii) {
        String s = "";
        String t;
        int i;
        
        while(ascii.length() > 2) {
            // Get a substring of the ascii string
            t = ascii.substring(0, 2);
            i = Integer.valueOf(t);
            // Convert to an String then concatenate
            s = s + String.valueOf((char)i);
            if(ascii.length() > 2)
                ascii = ascii.substring(2);
        }
        
        t = ascii;
        i = Integer.valueOf(t);
        s = s + String.valueOf((char)i);
        
        return s;
    }
    
    public String convertToAscii(String s) {
        String ascii = "";
        
        // ASCII Value
        int aValue;
        
        for(int i = 0; i < s.length(); i++) {
            // Parse the character from the string
            char c = s.charAt(i);
            // Convert the char to an integer by casting the char to an int, 
            // which will automatically give the ASCII value
            aValue = (int)c;
            ascii = ascii + Integer.toString(aValue);
        }
        
        return ascii;
    }
    

    
    public boolean conductHashTestingForAscii(String ascii) {        
        String salt, testSaltAscii;
        int hashValueForSaltedAscii;
        
        // triple nested for loop for the three digit spaces
        // needed for all possible values of testing
        for(int x = 0; x < 10; x++) {
            for(int y = 0; y < 10; y++) {
                for(int z = 0; z < 10; z++) {
                    salt = String.format("%d%d%d", x,y,z);
                    
                    // Temp saltAscii
                    testSaltAscii = salt + ascii;
                    RecoverPassword.counter++;
                    
                    // Compute hash
                    hashValueForSaltedAscii = computeHashValue(testSaltAscii);
                    
                    // Check if equivalent
                    if(String.valueOf(hashValueForSaltedAscii).equals(RecoverPassword.hashValue)) {
                        RecoverPassword.encryptedPassword = testSaltAscii;
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
    
    public int computeHashValue(String saltedascii) {
        
        // Get the substrings for each side
        String left = saltedascii.substring(0, 7);
        String right = saltedascii.substring(7);
        
        // Convert them to integers
        int l = Integer.valueOf(left);
        int r = Integer.valueOf(right);
        
        // Hash value computation
        int hashValue = (((243 * l) + r) % 85767489);
        
        return hashValue;
    }
    
    
    
}
