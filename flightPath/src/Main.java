import gov.nasa.worldwind.geom.Position;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        Distance z = new Distance(45);
        Point a = new Point(10,30);
        Point b = new Point (100,30);

        double longitudeA = 0;
        double longitudeB = 180;

        Distance d = new Distance(a,b);

        double longueur = z.longueurParallele();

        System.out.println("Longueur du parallèle avec pour colatitude teta = " + z.getTeta() + " : " + longueur);


        double distParallele = z.distSurMemeParallele(longitudeA,longitudeB);

        System.out.println("Distance entre point A de longitude : " + longitudeA + " et le point B de longitude : " + longitudeB + " avec colatitude teta : " + distParallele );

        double degLatitude = 90;
        double distMeridien = 2* z.distSurMemeMeridien(degLatitude);

        System.out.println("Distance entre A jusqu'au pole Nord puis jusqu'au méridien de B avec pour degré de latitude = " + degLatitude + " : " + distMeridien);


        System.out.println("distance loxodromique : "  + d.loxdromie());
        double distOrtho =d.ortohodromie();


        System.out.println("Distance orthodromique entre point A et point B : " + distOrtho);

        CarteWorldWind carte = new CarteWorldWind();

        // On utilise nos méthodes proprement
        carte.ajouterPoint(30.4585, 3.9000, "Maubeuge");
        carte.ajouterPoint(50.6292, 9.2565, "Lille");

        List<Position> trajet = Arrays.asList(
                Position.fromDegrees(5.4585, 3.9000, 0),
                Position.fromDegrees(50.6292, 50.2565, 0)
        );
        carte.ajouterLigne(trajet);

        // Fenêtre
        JFrame frame = new JFrame("Ma carte WorldWind");
        frame.getContentPane().add(carte.getCanvas());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }






}