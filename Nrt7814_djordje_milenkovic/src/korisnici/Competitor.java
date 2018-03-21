package korisnici;

import glavni.DB;
import glavni.FileManager;
import glavni.Regex;
import meniji.Meni;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Competitor extends User implements Meni
{
    // ---------------ATTRIBUTES----------------

    private static Competitor instanca;
    private Scanner scan;

    // ---------------METHODS------------------

    private Competitor(String korisnickoIme, String sifra)
    {
        super(korisnickoIme, sifra);
    }
    @Override
    public void logout()
    {
        instanca = null;
        System.out.println("Uspesno ste se odjavili.");
    }
    @Override
    public void glavniMeni()
    {
        String odluka;
        scan = new Scanner(System.in)
        while(instanca!=null)
        {
            System.out.println("*******************************************");
            System.out.println("| 0. Odjavi se                            |");
            System.out.println("| 1. Predaj rad                           |");
            System.out.println("| 2. Pogledaj informacije o predatom radu |");
            System.out.println("*******************************************");
            do
            {
                System.out.print("Izaberite opciju: ");
                odluka = scan.nextLine();
            }while (!Regex.unosGlavniMeni(odluka));
            switch (odluka)
            {
                case "0":
                {
                    this.logout();
                    break;
                }
                case "1":
                {
                    this.predajRad();
                    break;
                }
                case "2":
                {
                    this.informacijeOradu();
                    break;
                }
            }
        }

    }
    public static Competitor login(String ime, String pass)
    {
        if(instanca == null)
        {
            instanca = new Competitor(ime, pass);
        }
        System.out.println("Uspesno ste se ulogovali kao takmicar: "+instanca);
        return instanca;
    }
    public String toString()
    {
        if(instanca!=null)
            return super.toString();
        return "";
    }

    private boolean informacijeOradu()
    {

        if(!FileManager.daLiPostoji(this.getKorisnickoIme()+".txt", "predatiRadovi"))
        {
            System.out.println("Morate prvo predati rad da bi vam komisija ocenila isti.");
            return false;
        }
        String sql = "SELECT kvalitetKoda, tacnostKoda, opstiUtisak, srednjaOcena, komentar FROM radovi, korisnici WHERE radovi.korisnikID = korisnici.korisnikID AND korisnickoIme = \""+this.getKorisnickoIme()+"\"";
        DB bp = DB.getInstanca();
        try
        {
            ResultSet rs = bp.select(sql);
            int kvalitetKoda = rs.getInt("kvalitetKoda");
            int tacnostKoda = rs.getInt("tacnostKoda");
            int opstiUtisak = rs.getInt("opstiUtisak");
            double ocena = rs.getDouble("srednjaOcena");
            String komentar = rs.getString("komentar");

            if(komentar.equals("U fazi ocenjivanja"))
            {
                System.out.println(komentar);
                return true;
            }
            else
            {
                System.out.println("-------------------------------------------------------");
                System.out.println("Kvalitet koda: "+kvalitetKoda);
                System.out.println("Tacnost koda: "+tacnostKoda);
                System.out.println("Opsti utisak: "+opstiUtisak);
                System.out.println("Vasa konacna ocena je: "+ocena+" - komentar: "+komentar);
                System.out.println("-------------------------------------------------------");
                return true;
            }


        }catch (Exception e)
        {
            System.err.println("Gresk pri preuzimanju komentara iz baze.");
        }
        return false;
    }
    private boolean ocenjen(String username)
    {
        String sql = "SELECT srednjaOcena FROM radovi WHERE korisnikID = (SELECT korisnikID FROM korisnici WHERE korisnickoIme = \""+username+"\")";
        DB bp = DB.getInstanca();
        try
        {
            ResultSet rs = bp.select(sql);
            //String komentar = rs.getString("komentar");
            //if(komentar.equals("U fazi ocenjivanja"))
            double srednjaOcena = rs.getDouble("srednjaOcena");
            if((char)srednjaOcena != '\u0000')
            {
                System.out.println("Vas rad je ocenjen.");
                return true;
            }
        }catch (SQLException e)
        {
            System.err.println("Greska pri proveri da li je rad ocenjen.");
        }
        return false;
    }
    private boolean predajRad()
    {
        if(ocenjen(this.getKorisnickoIme()))
        {
           return false;
        }
        String folder;
        do
        {
            System.out.print("Upisite apsolutnu putanju do vaseg rada: ");
            folder = scan.nextLine();
        }while (!Regex.sifra(folder));
        if(FileManager.upisi(folder, this.getKorisnickoIme()+".txt") && promeniKomentar(this.getKorisnickoIme()))
        {
            System.out.println("Uspesno ste predali vas rad na ocenjivanje.");
            return true;
        }
        return false;
    }
    private boolean promeniKomentar(String username)
    {
        String sql = "UPDATE radovi SET komentar = \"U fazi ocenjivanja\" WHERE korisnikID = (SELECT korisnikID FROm korisnici WHERE korisnickoIme = \""+username+"\")";
        DB bp = DB.getInstanca();
        try
        {
            int x = bp.uidQuery(sql);
            if(x!=0)
                return true;
        }catch (SQLException e)
        {
            System.err.println("Greska pri predaji rada.");
        }
        return false;
    }
}
