import java.util.ArrayList;
import java.util.List;

public class Distance {

    private Point A;
    private Point B;
    private double latA;
    private double latB;
    private double longA;
    private double longB;
    private double teta;

    public Distance (Point a,Point b){
        this.A = a;
        B=b;
        longA=A.getLongitude();
        longB=B.getLongitude();
        latA=A.getLatitude();
        latB=B.getLatitude();
    }

    public Distance(double teta){
        this.teta=teta;
    }

    public double getTeta() {
        return teta;
    }

    public Trajectoire loxodromie(int nombrePoints) {

        if (nombrePoints < 2) {
            throw new IllegalArgumentException(
                    "Il faut au moins 2 points."
            );
        }

        if (Math.abs(latA) >= 90 || Math.abs(latB) >= 90) {
            throw new IllegalArgumentException(
                    "La loxodromie ne peut pas partir d'un pôle."
            );
        }

        List<Coordonnee> points = new ArrayList<>();

        double phiA = Math.toRadians(latA);
        double phiB = Math.toRadians(latB);

        double lambdaA = Math.toRadians(longA);
        double lambdaB = Math.toRadians(longB);

        // Différence de longitude normalisée
        double deltaLambda = lambdaB - lambdaA;

        while (deltaLambda > Math.PI) {
            deltaLambda -= 2 * Math.PI;
        }

        while (deltaLambda < -Math.PI) {
            deltaLambda += 2 * Math.PI;
        }

        // Calcul de la route vraie
        double rv = routeVraie(
                longA, latA,
                longA + Math.toDegrees(deltaLambda), latB
        );

        // Latitude croissante
        double psiA = Math.log(
                Math.tan(Math.PI / 4 + phiA / 2)
        );

        double psiB = Math.log(
                Math.tan(Math.PI / 4 + phiB / 2)
        );

        for (int i = 0; i < nombrePoints; i++) {

            double t = (double) i / (nombrePoints - 1);

            double latitude;
            double longitude;

            if (Math.abs(phiB - phiA) < 1e-10) {
                // Déplacement sur un parallèle
                latitude = latA;
                longitude = Math.toDegrees(
                        lambdaA + t * deltaLambda
                );

            } else if (Math.abs(deltaLambda) < 1e-10) {
                // Déplacement sur un méridien
                latitude = Math.toDegrees(
                        phiA + t * (phiB - phiA)
                );
                longitude = longA;

            } else {
                // Interpolation de la latitude isométrique
                double psi = psiA + t * (psiB - psiA);

                double phi = 2 * Math.atan(Math.exp(psi))
                        - Math.PI / 2;

                latitude = Math.toDegrees(phi);

                // Longitude calculée à partir de la route vraie
                double lambda = lambdaA
                        + Math.tan(rv) * (psi - psiA);

                longitude = Math.toDegrees(lambda);
            }

            points.add(new Coordonnee(latitude, longitude));
        }

        return new Trajectoire(points);
    }

    private double routeVraie(
            double longA, double latA,
            double longB, double latB) {

        double phiA = Math.toRadians(latA);
        double phiB = Math.toRadians(latB);

        double psiA = Math.log(
                Math.tan(Math.PI / 4 + phiA / 2)
        );

        double psiB = Math.log(
                Math.tan(Math.PI / 4 + phiB / 2)
        );

        double deltaLambda = Math.toRadians(longB - longA);

        while (deltaLambda > Math.PI) {
            deltaLambda -= 2 * Math.PI;
        }

        while (deltaLambda < -Math.PI) {
            deltaLambda += 2 * Math.PI;
        }

        return Math.atan2(
                deltaLambda,
                psiB - psiA
        );
    }
    public double distanceLoxodromie() {
        double phiA = Math.toRadians(latA);
        double phiB = Math.toRadians(latB);

        double deltaPhi = phiB - phiA;

        double deltaLambda = Math.toRadians(longB - longA);

        // Choisir le chemin longitudinal le plus court
        deltaLambda = Math.atan2(
                Math.sin(deltaLambda),
                Math.cos(deltaLambda)
        );

        double psiA = Math.log(
                Math.tan(Math.PI / 4 + phiA / 2)
        );

        double psiB = Math.log(
                Math.tan(Math.PI / 4 + phiB / 2)
        );

        double deltaPsi = psiB - psiA;

        double q;

        if (Math.abs(deltaPsi) > 1e-10) {
            q = deltaPhi / deltaPsi;
        } else {
            // Cas d'un déplacement sur un parallèle
            q = Math.cos(phiA);
        }

        double distanceAngulaire = Math.sqrt(
                deltaPhi * deltaPhi
                        + q * q * deltaLambda * deltaLambda
        );

        double rayonTerre = 6371.0;

        return rayonTerre * distanceAngulaire;
    }

    public Trajectoire orthodromie(
            int nombrePoints) {

        List<Coordonnee> points = new ArrayList<>();

        if (nombrePoints < 2) {
            throw new IllegalArgumentException(
                    "Il faut au moins 2 points."
            );
        }

        double phiA = Math.toRadians(latA);
        double lambdaA = Math.toRadians(longA);
        double phiB = Math.toRadians(latB);
        double lambdaB = Math.toRadians(longB);


        double[] a = {
                Math.cos(phiA) * Math.cos(lambdaA),
                Math.cos(phiA) * Math.sin(lambdaA),
                Math.sin(phiA)
        };

        double[] b = {
                Math.cos(phiB) * Math.cos(lambdaB),
                Math.cos(phiB) * Math.sin(lambdaB),
                Math.sin(phiB)
        };

        double produitScalaire =
                a[0] * b[0]
                        + a[1] * b[1]
                        + a[2] * b[2];


        produitScalaire = Math.max(
                -1.0, Math.min(1.0, produitScalaire)
        );

        double angle = Math.acos(produitScalaire);

        for (int i = 0; i < nombrePoints; i++) {

            double t = (double) i / (nombrePoints - 1);

            double x, y, z;

            if (angle < 1e-10) {
                x = a[0];
                y = a[1];
                z = a[2];

            } else if (Math.PI - angle < 1e-7) {
                throw new IllegalArgumentException(
                        "Points antipodaux : orthodromie non unique."
                );

            } else {
                double poidsA =
                        Math.sin((1 - t) * angle) / Math.sin(angle);

                double poidsB =
                        Math.sin(t * angle) / Math.sin(angle);

                x = poidsA * a[0] + poidsB * b[0];
                y = poidsA * a[1] + poidsB * b[1];
                z = poidsA * a[2] + poidsB * b[2];
            }


            double latitude = Math.toDegrees(
                    Math.atan2(z, Math.sqrt(x * x + y * y))
            );

            double longitude = Math.toDegrees(
                    Math.atan2(y, x)
            );

            points.add(new Coordonnee(latitude, longitude));
        }

        return new Trajectoire(points);
    }

    public double distanceOrthodromie() {
        double phiA = Math.toRadians(latA);
        double phiB = Math.toRadians(latB);

        double deltaPhi = phiB - phiA;
        double deltaLambda = Math.toRadians(longB - longA);

        // Normalisation de la différence de longitude
        deltaLambda = Math.atan2(
                Math.sin(deltaLambda),
                Math.cos(deltaLambda)
        );

        double a = Math.pow(Math.sin(deltaPhi / 2), 2)
                + Math.cos(phiA) * Math.cos(phiB)
                * Math.pow(Math.sin(deltaLambda / 2), 2);

        double angle = 2 * Math.atan2(
                Math.sqrt(a),
                Math.sqrt(1 - a)
        );

        double rayonTerre = 6371.0;

        return rayonTerre * angle;
    }

    public double longueurParallele() {
        double rayonTerre = 6371;
        double r = rayonTerre*Math.cos(this.teta);
        return 2*Math.PI*r;
    }

    public double distSurMemeMeridien(double degLatitude) {
        double distDegLatitude = 111.12;
        return (degLatitude - this.teta) * distDegLatitude;
    }

    public double distSurMemeParallele(double longitudeA, double longitudeB) {
        double rayonTerre = 6371;
        double r = rayonTerre * Math.cos(Math.toRadians(this.teta));

        double deltaLongitude = Math.abs(longitudeB - longitudeA);
        if (deltaLongitude > 180) {
            deltaLongitude = 360 - deltaLongitude;
        }

        return r * Math.toRadians(deltaLongitude);
    }

    public Point getA() {
        return A;
    }

    public void setA(Point a) {
        A = a;
    }

    public Point getB() {
        return B;
    }

    public void setB(Point b) {
        B = b;
    }

    public double getLatA() {
        return latA;
    }

    public void setLatA(double latA) {
        this.latA = latA;
    }

    public double getLatB() {
        return latB;
    }

    public void setLatB(double latB) {
        this.latB = latB;
    }

    public double getLongA() {
        return longA;
    }

    public void setLongA(double longA) {
        this.longA = longA;
    }

    public double getLongB() {
        return longB;
    }

    public void setLongB(double longB) {
        this.longB = longB;
    }

    public void setTeta(double teta) {
        this.teta = teta;
    }
}





