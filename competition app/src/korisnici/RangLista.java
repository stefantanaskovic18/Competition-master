package korisnici;

import glavni.DB;

import java.sql.ResultSet;
import java.util.ArrayList;

public class RangLista
{
    // --------- ATTRIBUTES ------------

    private String takmicar;
    private int kvalitetKoda;
    private int tacnostKoda;
    private int opstiUtisak;
    private double srednjaOcena;

    // ---------- METHODS -------------

    private RangLista(String takmicar, int kvalitetKoda, int tacnostKoda, int opstiUtisak, double srednjaOcena)
    {
        this.takmicar = takmicar;
        this.kvalitetKoda = kvalitetKoda;
        this.tacnostKoda = tacnostKoda;
        this.opstiUtisak = opstiUtisak;
        this.srednjaOcena = srednjaOcena;
    }

    private static ArrayList<RangLista>formirajRangListu()
    {
        String sql = "SELECT korisnickoIme, kvalitetKoda, tacnostKoda, opstiUtisak, srednjaOcena FROM korisnici, radovi WHERE korisnici.korisnikID = radovi.korisnikID AND srednjaOcena NOT NULL ORDER BY 5 DESC";
        DB bp = DB.getInstanca();
        ArrayList<RangLista>rangLista = new ArrayList<>();
        try
        {
            ResultSet rs = bp.select(sql);
            while (rs.next())
            {
                String username = rs.getString("korisnickoIme");
                int kvalitet = rs.getInt("kvalitetKoda");
                int tacnost = rs.getInt("tacnostKoda");
                int utisak = rs.getInt("opstiUtisak");
                double ocena = rs.getDouble("srednjaOcena");
                rangLista.add(new RangLista(username, kvalitet, tacnost, utisak, ocena));
            }
            return rangLista;
        }catch (Exception e)
        {
            System.err.println("Greska pri dohvartanju podataka iz baze za rang listu.");
        }
        return null;
    }

    public static void ispisiRangListu()
    {
        ArrayList<RangLista> lista = formirajRangListu();
        if(lista!=null)
        {
            System.out.println("\n\t\t\t\t\t\t\t\t\t*************** RANG LISTA *****************\n");
            System.out.println("\t\t   takmicar\t\t      kvalitet koda\t\t   tacnost koda\t\t  opsti utisak\t\t    srednja ocena");
            System.out.println("\t\t   --------\t\t      -------------\t\t   ------------\t\t  ------------\t\t    -------------");
            for(int i=0; i<lista.size(); i++)
            {
                System.out.println("\t\t"+(i+1)+"\t"+lista.get(i));
            }
            System.out.print("\n");
        }
    }

    public String toString()
    {
        return takmicar+"\t\t || \t\t"+kvalitetKoda+"\t\t || \t\t"+tacnostKoda+"\t\t || \t\t"+opstiUtisak+"\t\t || \t\t"+srednjaOcena;
    }
}
