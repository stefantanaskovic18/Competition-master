package korisnici;

import glavni.DB;
import glavni.FileManager;
import glavni.Regex;
import meniji.Meni;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Commision extends User implements Meni
{
    // -------------ATTRIBUTES---------------

    private static Commision instanca;
    private Scanner scan;

    // --------------METHODS-----------------

    private Commision(String korisnickoIme, String sifra)
    {
        super(korisnickoIme, sifra);
    }
    public static Commision login(String ime, String pass)
    {
        if(instanca == null)
        {
            instanca = new Commision(ime, pass);
            System.out.println("Uspesno ste se ulogovali kao clan komisije: "+instanca);
        }
        return instanca;
    }
    public String toString()
    {
        if(instanca!=null)
            return super.toString();
        return "";
    }
    @Override
    public void glavniMeni()
    {
        String odluka;
        scan = new Scanner(System.in)
        while (instanca!=null)
        {
            System.out.println("*****************************");
            System.out.println("| 0. Odjavite se            |");
            System.out.println("| 1. Prikazi spisak radova  |");
            System.out.println("| 2. Oceni rad              |");
            System.out.println("*****************************");
            do
            {
                System.out.print("Izaberite opciju: ");
                odluka = scan.nextLine();
            }while (!Regex.unosGlavniMeni(odluka));
            switch (odluka)
            {
                case "0":
                    logout();
                    break;
                case "1":
                    radovi();
                    break;
                case "2":
                    oceniRad();
                    break;
            }
        }
    }
    @Override
    public void logout()
    {
        instanca = null;
        System.out.println("Uspesno ste se odjavili.");
    }


    private void radovi()
    {
        ArrayList<String> listaRadova = RadovizaOcenjivanje();
        if(listaRadova.size()==0)
        {
            System.out.println("Trenutno ni jedan takmicar nije predao svoj rad.");
        }
        else
        {
            System.out.println(listaRadova);
        }
    }
    private static ArrayList<String> RadovizaOcenjivanje()
    {
        String sql = "SELECT korisnickoIme FROM korisnici, radovi WHERE korisnici.korisnikID = radovi.korisnikID AND komentar = \"U fazi ocenjivanja\"";
        ArrayList<String> predatiRadovi = new ArrayList<>();
        DB bp = DB.getInstanca();
        try
        {
            ResultSet rs = bp.select(sql);
            while (rs.next())
            {
                String username = rs.getString("korisnickoIme");
                predatiRadovi.add(username);
            }
            return predatiRadovi;
        }catch (SQLException e)
        {
            System.err.println("Greska pri dohvatanju podataka o predatim radovima");
        }
        return null;
    }
    private boolean oceniRad()
    {
        if(RadovizaOcenjivanje().size()==0)
        {
            System.out.println("Trenutno ni jedan takmicar nije predao svoj rad.");
            return false;
        }
        else
            System.out.println(RadovizaOcenjivanje());
        String izborRada;
        do
        {
            System.out.print("Izaberite rad koji zelite da ocenite: ");
            izborRada = scan.nextLine();
        }while (!Regex.korisnickoIme(izborRada));

        if(FileManager.procitajSadrzajRada(izborRada+".txt"))
        {
            String ocene[] = new String[3];
            String sveOcene;
            String komentar;
            System.out.println();
            do
            {
                System.out.println("Ocene unosite sledecim redosledom: Kvalitet koda, Tacnost koda, Opsti utisak. Napisati u jednoj liniji u terminalu. Ocene razdvojiti jednim razmakom. ");
                System.out.print("Unesite: ");
                sveOcene = scan.nextLine();
                if(sveOcene.split(" ").length==3)
                {
                    ocene[0] = sveOcene.split(" ")[0];
                    ocene[1] = sveOcene.split(" ")[1];
                    ocene[2] = sveOcene.split(" ")[2];
                }
            }while (sveOcene.split(" ").length!=3 || !Regex.ocena(ocene[0]) || !Regex.ocena(ocene[1]) || !Regex.ocena(ocene[2]));
            do
            {
                System.out.print("Unesite zavrsni komentar za rad: ");
                komentar = scan.nextLine();
            }while (!Regex.sifra(komentar));
            if(unesiOcenuUBazu(izborRada, Integer.parseInt(ocene[0]), Integer.parseInt(ocene[1]), Integer.parseInt(ocene[2]), komentar))
                return true;
        }
        return false;
    }
    private boolean unesiOcenuUBazu(String username, int kvalitetKoda, int tacnostKoda, int opstiUtisak, String komentar)
    {
        DB bp = DB.getInstanca();
        double srednjaOcena = (kvalitetKoda+tacnostKoda+opstiUtisak)/3.0;
        srednjaOcena = Math.round(srednjaOcena*100.0)/100.0;
        String sql = "UPDATE radovi SET kvalitetKoda = "+kvalitetKoda+", tacnostKoda = "+tacnostKoda+", opstiUtisak = "+opstiUtisak+", srednjaOcena = "+srednjaOcena+", komentar = \""+komentar.trim()+"\" WHERE korisnikID = (SELECT korisnikID FROM korisnici WHERE korisnickoIme = \""+username+"\")";
        try
        {
            int x = bp.uidQuery(sql);
            if(x!=0)
            {
                System.out.println("Uspesno ste ocenili takmicara");
                return true;
            }
        }catch (Exception e)
        {
            System.err.println("Greska pri ubacivanju ocene u bazu!"+e.getMessage());
        }
        return false;
    }
}
