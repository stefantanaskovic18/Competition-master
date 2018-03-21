package glavni;

public final class Regex
{
    public static boolean unosGlavniMeni(String unos)
    {
        return unos.matches("[0-2]");
    }

    public static boolean unosAdminMeni(String unos)
    {
        return unos.matches("[0-3]");
    }

    public static boolean ocena(String unos)
    {
        return unos.matches("[1-9]") || unos.matches("[1-1][0-0]");
    }

    public static boolean korisnickoIme(String unos)
    {
        return unos.matches("\\w{2,20}");
    }

    public static boolean sifra(String unos)
    {
        return unos.matches(".+");
    }
}
