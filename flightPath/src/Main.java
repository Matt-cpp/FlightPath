//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.lang.Math;
public class Main {
    public static void main(String[] args) {
        double teta = 45;

        double longueur = longueurParallele(teta);

        System.out.println("Longueur du parallèle avec pour colatitude teta = " + teta + " : " + longueur);

        double longitudeA = 0;
        double longitudeB = 180;
        double distParallele = distSurMemeParallele(longitudeA, longitudeB, teta);

        System.out.println("Distance entre point A de longitude : " + longitudeA + " et le point B de longitude : " + longitudeB + " avec colatitude teta : " + distParallele );

        double degLatitude = 90;
        double distMeridien = 2*distSurMemeMeridien(teta, degLatitude);

        System.out.println("Distance entre A jusqu'au pole Nord puis jusqu'au méridien de B avec pour degré de latitude = " + degLatitude + " : " + distMeridien);

        double phiA = 30;
        double lambdaA = 10;
        double phiB = 70;
        double lambdaB = 100;

        double distOrtho = ortohodromie(phiA, lambdaA, phiB, lambdaB);


        System.out.println("Distance orthodromique entre point A et point B : " + distOrtho);
    }

    public static double longueurParallele(double teta) {
        double rayonTerre = 6371;
        double r = rayonTerre*Math.cos(teta);
        return 2*Math.PI*r;
    }

    public static double distSurMemeParallele(double longitudeA, double longitudeB, double teta) {
        double rayonTerre = 6371;
        double r = rayonTerre * Math.cos(Math.toRadians(teta));

        double deltaLongitude = Math.abs(longitudeB - longitudeA);
        if (deltaLongitude > 180) {
            deltaLongitude = 360 - deltaLongitude;
        }

        return r * Math.toRadians(deltaLongitude);
    }

    public static double distSurMemeMeridien(double teta, double degLatitude) {
        double distDegLatitude = 111.12;
        return (degLatitude - teta) * distDegLatitude;
    }

    public static double ortohodromie(double phiA, double lambdaA, double phiB, double lambdaB){

        double radPhiA = Math.toRadians(phiA);
        double radLambdaA = Math.toRadians(lambdaA);
        double radPhiB = Math.toRadians(phiB);
        double radLambdaB = Math.toRadians(lambdaB);

        double base = Math.sin(radPhiA)*Math.sin(radPhiB)+ Math.cos(radPhiA)*Math.cos(radPhiB)*Math.cos(radLambdaB-radLambdaA);
        System.out.println("res attendu = 0.469846310393. res obtenu : "+base);

        double base2 = Math.acos(base);
        System.out.println("res attendu = 61.9756793. res obtenu :"+base2);

        double dist = 60*Math.toDegrees(base2);


        return dist;

    }


}