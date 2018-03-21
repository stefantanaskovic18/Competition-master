package korisnici;

public abstract class User
{
    // --------------ATTRIBUTES-----------------

    protected String korisnickoIme;
    protected String sifra;

    // --------------METHODS-------------------


    protected String getKorisnickoIme()
    {
        return korisnickoIme;
    }

    protected User(String korisnickoIme, String sifra)
    {
        this.korisnickoIme = korisnickoIme;
        this.sifra = sifra;
    }

    public String toString()
    {
        return korisnickoIme;
    }

    public abstract void logout();
}
