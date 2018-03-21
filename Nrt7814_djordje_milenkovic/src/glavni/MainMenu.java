package glavni;
import korisnici.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public final class MainMenu
{
    private static boolean validacija(String username, String password)
    {
        String sql = "SELECT korisnickoIme, sifra FROM korisnici WHERE korisnickoIme = \""+username+"\" AND sifra = \""+password+"\"";
        DB bp = DB.getInstanca();
        try
        {
            ResultSet rs = bp.select(sql);
            String korisnickoIme = rs.getString("korisnickoIme");
            String sifra = rs.getString("sifra");
            if(korisnickoIme.equals(username) && sifra.equals(password))
                return true;
            else
                return false;
        }catch (SQLException e)
        {
            return false;
        }
    }
    public static void glavniMeni()
    {
        String odluka;
        Scanner scan = new Scanner(System.in);
        System.out.println("************************************");
        System.out.println("|  0. Izadjite iz aplikacije       |");
        System.out.println("|  1. Ulogujte se                  |");
        System.out.println("|  2. Vidite rang listu takmicara  |");
        System.out.println("************************************");
        do
        {
            System.out.print("Izaberite opciju: ");
            odluka = scan.nextLine();
        } while (!Regex.unosGlavniMeni(odluka));
        int counter=3;
        switch (odluka)
        {
            case "0":
                MainMenu.izadji();
            case "1":
                while(counter>=1)
                {
                    try
                    {
                        String username;
                        String password;
                        do
                        {
                            System.out.print("Unesite korisnicko ime: ");
                            username = scan.nextLine();
                        }while (!Regex.sifra(username));
                        do
                        {
                            System.out.print("Unesite sifru: ");
                            password = scan.nextLine();
                        }while (!Regex.sifra(password));
                        if(validacija(username, password))
                        {
                            if(username.indexOf('a')==0)
                            {
                                User admin = Administrator.login(username, password);
                                ((Administrator)admin).glavniMeni();
                                break;
                            }
                            else if(username.indexOf('k')==0)
                            {
                                User commision = Commision.login(username, password);
                                ((Commision)commision).glavniMeni();
                                break;
                            }
                            else
                            {
                                User competitor = Competitor.login(username, password);
                                ((Competitor)competitor).glavniMeni();
                                break;
                            }
                        }
                        else
                        {
                            counter--;
                            if(counter==0)
                            {
                                System.out.println("3 puta sta pogresili korisnicko ime ili lozinku. Aplikacija se ovde zaustavlja.");
                                System.exit(0);
                            }
                            throw new IzuzetakUnosa("\nPogresno korisnicko ime ili sifra. Imate jos "+counter+" pokusaja");
                        }
                    }catch (IzuzetakUnosa i)
                    {
                        System.out.println(i);
                    }
                }
                break;
            case "2":
                RangLista.ispisiRangListu();
                break;
        }

    }
    private static void izadji()
    {
        System.out.println("Dovidjenja");
        System.exit(0);
    }
}
