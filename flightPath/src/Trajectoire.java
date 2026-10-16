import java.util.List;

public class Trajectoire {

    private List<Coordonnee> points;

    public Trajectoire(List<Coordonnee> points) {
        this.points = points;
    }

    public List<Coordonnee> getPoints() {
        return points;
    }
}