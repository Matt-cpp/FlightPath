//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
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
        double latA = 30.0;
        double latB = 70.0;
        double longA = 10.0;
        double longB = 100.0;

        System.out.println(loxodromie(longA,latA,longB,latB));

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

    public static double loxodromie (double longA, double latA,double longB, double latB){
        // si meme latitude -> division par 0 danger
        if (latA == latB){
            return Math.abs((longA-longB))*60*Math.cos(latA);
        }
        else if (longA==longB){
            return Math.abs(latA-latB) * 60;
        }
        else {
            double rv = routeVraie(longA,latA,longB,latB);
            double res = (60 * (latA - latB)) / Math.cos(rv);
            return Math.abs(res);
        }
    }

    public static double routeVraie(double longA, double latA, double longB, double latB ){
        double res;
        double b= Math.log(Math.tan(Math.toRadians(45+ (latB/2))));
        double a= Math.log(Math.tan(Math.toRadians(45+ (latA/2))));


        //resultat en radiant pour eviter des conversions inutiles pour la loxodromie
        return Math.atan((Math.toRadians(longA-longB)) /(b-a));
    }




}