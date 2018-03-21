package glavni;

public class IzuzetakUnosa extends Exception
{
    private String poruka;

    public IzuzetakUnosa(String poruka)
    {
        this.poruka = poruka;
    }

    public String toString()
    {
        return poruka;
    }

}
