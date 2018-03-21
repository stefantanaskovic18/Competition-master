package glavni;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class FileManager
{
    private static ArrayList<String> procitaj(String putanja)
    {
        try
        {
            ArrayList<String> lines = (ArrayList) Files.readAllLines(Paths.get(""+putanja), Charset.forName("UTF-8"));
            return lines;
        }catch (Exception e)
        {
            System.err.println("Greska! Rad ne moze da se procita. Pokusajte da uneste ponovo ime foldera/rada. Ili proverite da li vam se rad nalazi u folderu radovi ako ste prijavljeni kao takmicar."+e.getMessage());
        }
        return null;
    }
    public static boolean upisi(String putanja, String imeDatoteke)
    {
        try
        {
            ArrayList<String> sadrzaj = FileManager.procitaj(putanja);
            if(sadrzaj == null)
                return false;
            FileWriter fw = new FileWriter("Datoteke/predatiRadovi/"+imeDatoteke, false);
            PrintWriter out = new PrintWriter(fw);
            for(int i=0; i<sadrzaj.size(); i++)
            {
                out.print(sadrzaj.get(i)+System.getProperty("line.separator"));
            }
            out.close();
            fw.close();
            return true;
        }catch (Exception e)
        {
            System.err.println("Greska pri predaji rada.");
        }
        return false;
    }
    // metoda za proveru u funkciji informacije koju poziva takmicar. Proverava da li je takmicar predao rad u folder predatiRadovi.
    public static boolean daLiPostoji(String nazivFajla, String folder)
    {
        File f = new File("Datoteke/"+folder);
        if(f.exists())
        {
            String []lista = f.list();
            for(String fajl:lista)
            {
                if(fajl.equals(nazivFajla))
                    return true;
            }
        }
        return false;
    }
    // ova metoda je za clana komisije, da bi video rad takmicara
    public static boolean procitajSadrzajRada(String imeDatoteke)
    {
        ArrayList<String> sadrzaj = procitaj("Datoteke/predatiRadovi/"+imeDatoteke);
        if(sadrzaj != null)
        {
            System.out.println("\n**********Rad takmcara "+imeDatoteke.split(".txt")[0]+"**********");
            for(int i=0; i<sadrzaj.size(); i++)
            {
                System.out.println(sadrzaj.get(i));
            }
            return true;
        }
        return false;
    }
    // kada administrator brise takmicara, da se obrisu i radovi
    public static boolean obrisiRadIzDatoteke(String username)
    {
        File f = new File("Datoteke/radovi/"+username);
        if(f.exists())
        {
            f.delete();
        }
        f = new File("Datoteke/predatiRadovi/"+username);
        if(f.exists())
        {
            f.delete();
            return true;
        }
        return false;
    }
}






















