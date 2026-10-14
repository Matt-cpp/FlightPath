public class Convertisseur {

    public static double KilometreToNautique(double value) {
        return value/1.852;
    }

    public static double nautiqueToKilometre(double value) {
        return value*1.852;
    }

    public static double degresToMinute(double value) {
        return value*60;
    }

    public static double minuteToDegres(double value) {
        return value/60;
    }

    public static double radianToDegres(double value) {
        return value*57.2958;
    }

    public static double degresToRadian(double value) {
        return value/57.2958;
    }
}