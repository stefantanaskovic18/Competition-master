package korisnici;
import glavni.DB;
import glavni.FileManager;
import glavni.Regex;
import meniji.Meni;
import glavni.IzuzetakUnosa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Administrator extends User implements Meni
{
    // --------------ATTRIBUTES-------------------

    private static Administrator instanca = null;
    private Scanner scan;

    // --------------METHODS---------------------

    private Administrator(String korisnickoIme, String sifra)
    {
        super(korisnickoIme, sifra);
    }
    public static Administrator login(String ime, String pass)
    {
        if(instanca == null)
        {
            instanca = new Administrator(ime, pass);
            System.out.println("Uspesno ste se ulogovali kao administrator: "+instanca);
        }
        return instanca;
    }
    public void logout()
    {
        instanca = null;
        System.out.println("Uspesno ste se odjavili.");
    }
    public String toString()
    {
        if(instanca != null)
            return super.toString();
        return "";
    }
    @Override
    public void glavniMeni()
    {
        String odluka;
        scan = new Scanner(System.in)
        while(instanca!=null)
        {
            System.out.println("*****************************");
            System.out.println("| 0. Izlogujte se           |");
            System.out.println("| 1. Dodajte takmicara      |");
            System.out.println("| 2. Dodajte clana komisije |");
            System.out.println("| 3. Obrisite korisnika     |");
            System.out.println("*****************************");
            do
            {
                System.out.print("Izaberite opciju: ");
                odluka = scan.nextLine();
            }while (!Regex.unosAdminMeni(odluka));
            switch (odluka)
            {
                case "0":
                    logout();
                    break;
                case "1":
                    addCompetitior();
                    break;
                case "2":
                    addCommision();
                    break;
                case "3":
                    deleteUser();
                    break;
            }
        }
    }

    // METODE ZA DODAVANJE NOVOG TAKMICARA

    private ArrayList<String> addUsers()
    {
        String sql = "SELECT korisnickoIme FROM korisnici";
        DB bp = DB.getInstanca();
        ArrayList<String> listaTakmicara = new ArrayList<>();
        try
        {
            ResultSet rs = bp.select(sql);
            while (rs.next())
            {
                String username = rs.getString("korisnickoIme");
                listaTakmicara.add(username);
            }
            return listaTakmicara;
        }catch (Exception e)
        {
            System.err.println("Greska pri dohvatanju podataka iz baze.");
        }
        return null;
    }
    private boolean insertCompetitiorIntoBase(String username, String sifra)
    {
        String sql = "INSERT INTO korisnici (korisnickoIme, sifra) VALUES (\""+username+"\", \""+sifra+"\")";
        DB bp = DB.getInstanca();
        try
        {
            int x = bp.uidQuery(sql);
            if(x!=0)
                return true;
        }catch (Exception e)
        {
            System.err.println("Korisnicko ime vec postoji. Pokusajte sa nekim drugim.");
        }
        return false;
    }

    private boolean insertExam()
    {
        String sql = "INSERT INTO radovi (korisnikID, komentar) VALUES ((SELECT MAX(korisnikID) FROM korisnici), \"Nije predat rad\")";
        DB bp = DB.getInstanca();
        try
        {
            int x = bp.uidQuery(sql);
            if(x!=0)
                return true;
        }catch (Exception e)
        {
            return false;
        }
        return false;
    }
    private void addCompetitior()
    {
        String username;
        String sifra;
        try
        {
            do
            {
                System.out.print("Unesite korisnicko ime takmicara (moze imati samo alfa-numericke znakove i ne vise od 20 karaktera): ");
                username = scan.nextLine();
            }while (!Regex.korisnickoIme(username));
            do
            {
                System.out.print("Unesite sifru za novog takmicara: ");
                sifra = scan.nextLine();
            }while (!Regex.sifra(sifra));
            if(username.indexOf('t')!=0)
                throw new IzuzetakUnosa("Greska pri dodavanju novog takmicara. Korisnicko ime takmicara mora imati pocetno slovo t.");
            else
            {
                if(insertCompetitiorIntoBase(username, sifra) && insertExam())
                {
                    System.out.println("Uspesno dodat novi takmicar!");
                }
            }

        }catch (IzuzetakUnosa i)
        {
            System.out.println(i);
        }catch (Exception e)
        {
            System.err.println("Desila se neka katastrofalna greska."+e);
        }
    }

    // METODE ZA DODAVANJE NOVOG CLANA KOMISIJE

    private void addCommision()
    {
        String username;
        String sifra;
        try
        {
            do
            {
                System.out.print("Unesite korisnicko ime za clana komisije: ");
                username = scan.nextLine();
            }while (!Regex.korisnickoIme(username));
            do
            {
                System.out.print("Unesite sifru za novog clana komisije: ");
                sifra = scan.nextLine();
            }while (!Regex.sifra(sifra));
            if(username.indexOf('k')!=0)
                throw new IzuzetakUnosa("Greska pri dodavanju novog clana komisije. Korisnicko ime komisije mora imati pocetno slovo k.");
            else
            {
                insertCommisionIntoBase(username, sifra);
            }
        }catch (IzuzetakUnosa i)
        {
            System.out.println(i);
        }catch (Exception e)
        {
            System.err.println("Desila se neka katastrofalna greska.");
        }

    }
    private boolean insertCommisionIntoBase(String username, String sifra)
    {
        String sql = "INSERT INTO korisnici (korisnickoIme, sifra) VALUES (\""+username+"\", \""+sifra+"\")";
        DB bp = DB.getInstanca();
        try
        {
            int x = bp.uidQuery(sql);
            if(x!=0)
            {
                System.out.println("Uspesno dodat novi clan komisije!");
                return true;
            }
        }catch (SQLException e)
        {
            System.err.println("Greska pri ubacivanju novog clana komisije. Probajte sa novim korisnickim imenom.");
        }
        return false;
    }

    // METODE ZA BRISANJE KORISNIKA APLIKACIJE

    private boolean isInBase(String username) throws IzuzetakUnosa
    {
        ArrayList<String> svi = addUsers();
        for(int i=0; i<svi.size(); i++)
        {
            if(svi.get(i).equals(username))
                return true;
        }
        throw new IzuzetakUnosa("Uneli ste ime koje ne postoji u bazi.");
    }
    private void deleteUser()
    {
        System.out.println(addUsers());
        String izbor;
        do
        {
            System.out.print("Unesite korisnicko ime korisnika kojeg zelite da obrisete: ");
            izbor = scan.nextLine();
        }while (!Regex.korisnickoIme(izbor));
        try
        {
            isInBase(izbor);
        }catch (IzuzetakUnosa i)
        {
            System.out.println(i);
        }
        if(izbor.indexOf('t')==0)
        {
            deleteExam(izbor);
            FileManager.obrisiRadIzDatoteke(izbor+".txt");
        }
        if(izbor.indexOf('a')==0 && !izbor.equals(this.getKorisnickoIme()))
        {
            System.out.println("Ne mozete obrisati drugog administratora!");
        }
        String sql = "DELETE FROM korisnici WHERE korisnickoIme = \""+izbor+"\"";
        DB bp = DB.getInstanca();
        try
        {
            int x = bp.uidQuery(sql);
            if(x!=0)
            {
                // ako admin zeli sebe da obrise
                if(izbor.equals(this.getKorisnickoIme()))
                {
                    this.logout();
                    System.out.println("Deaktivirali ste svoj nalog");
                    logout();
                }
                System.out.println("User je uspesno obrisan.");
            }
        }catch (SQLException e)
        {
            System.err.println("Greska pri brisanju korisnika. Pokusajte ponovo."+e.getMessage());
        }
    }
    private boolean deleteExam(String username)
    {
        String sql = "DELETE FROM radovi WHERE korisnikID = (SELECT korisnikID FROM korisnici WHERE korisnickoIme = \""+username+"\")";
        DB bp = DB.getInstanca();
        try
        {
            int x = bp.uidQuery(sql);
            if(x!=0)
            {
                System.out.println("Korisnikov rad je uspesno obrisan");
                return true;
            }
        }catch (Exception e)
        {
            System.err.println("Greska pri brisanju rada iz baze.");
        }
        return false;
    }
}

































