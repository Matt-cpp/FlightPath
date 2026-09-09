public class Distance {

    private Point A;
    private Point B;
    private double latA;
    private double latB;
    private double longA;
    private double longB;

    public Distance (Point a,Point b){
        this.A = a;
        B=b;
        longA=A.getLongitude();
        longB=B.getLongitude();
        latA=A.getLatitude();
        latB=B.getLatitude();
    }

    public Distance(double teta){

    }

    public double loxdromie (){
        // si meme latitude -> division par 0 danger
        if (latA == latB) {
            return Math.abs((longA - longB)) * 60 * Math.cos(latA);
        }
        else if (longA==longB){
            System.out.println(latA-latB);
            return Math.abs(latA-latB) * 60;
        }
        else {
            double rv = this.routeVraie(longA,latA,longB,latB);
            double res = (60 * (latA - latB)) / Math.cos(rv);
            return Math.abs(res);
        }
    }

    private double routeVraie (double longA, double latA, double longB, double latB){
        double res;
        double b= Math.log(Math.tan(Math.toRadians(45+ (latB/2))));
        double a= Math.log(Math.tan(Math.toRadians(45+ (latA/2))));


        //resultat en radiant pour eviter des conversions inutiles pour la loxodromie
        return Math.atan((Math.toRadians(longA-longB)) /(b-a));
    }

    public double ortohodromie(){

        double radPhiA = Math.toRadians(latA);
        double radLambdaA = Math.toRadians(longA);
        double radPhiB = Math.toRadians(latB);
        double radLambdaB = Math.toRadians(longB);

        double base = Math.sin(radPhiA)*Math.sin(radPhiB)+ Math.cos(radPhiA)*Math.cos(radPhiB)*Math.cos(radLambdaB-radLambdaA);
        //System.out.println("res attendu = 0.469846310393. res obtenu : "+base);

        double base2 = Math.acos(base);
        //System.out.println("res attendu = 61.9756793. res obtenu :"+base2);

        double dist = 60*Math.toDegrees(base2);


        return dist;

    }
}





