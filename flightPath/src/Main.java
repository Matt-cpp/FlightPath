import gov.nasa.worldwind.geom.Position;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final Color COULEUR_FOND = new Color(245, 247, 250);
    private static final Color COULEUR_PRINCIPALE = new Color(35, 68, 105);

    private final CarteWorldWind carte = new CarteWorldWind();
    private final JTextField latitudeDepart = creerChamp();
    private final JTextField longitudeDepart = creerChamp();
    private final JTextField latitudeArrivee = creerChamp();
    private final JTextField longitudeArrivee = creerChamp();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::creerFenetre);
    }

    private static void creerFenetre() {
        new Main().afficherFenetre();
    }

    private void afficherFenetre() {
        JButton valider = new JButton("Afficher les chemins");
        valider.setFont(new Font("SansSerif", Font.BOLD, 13));
        valider.setBackground(COULEUR_PRINCIPALE);
        valider.setForeground(Color.WHITE);
        valider.setFocusPainted(false);
        valider.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        valider.addActionListener(this::afficherTrajectoires);

        JButton basculerVue = new JButton("Afficher le globe");
        basculerVue.setFont(new Font("SansSerif", Font.BOLD, 12));
        basculerVue.setForeground(Color.WHITE);
        basculerVue.setBackground(COULEUR_PRINCIPALE);
        basculerVue.setFocusPainted(false);
        basculerVue.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        basculerVue.setPreferredSize(new Dimension(172, 34));
        basculerVue.addActionListener(event -> basculerVue(basculerVue));

        JPanel formulaire = new JPanel(new GridLayout(1, 2, 14, 0));
        formulaire.setBackground(Color.WHITE);
        formulaire.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                BorderFactory.createEmptyBorder(14, 18, 14, 18)
        ));
        formulaire.add(creerGroupeCoordonnees("Départ", latitudeDepart, longitudeDepart));
        formulaire.add(creerGroupeCoordonnees("Arrivée", latitudeArrivee, longitudeArrivee));

        JPanel validation = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 20));
        validation.setBackground(Color.WHITE);
        validation.add(valider);
        formulaire.add(validation);

        JLayeredPane carteAvecBouton = new JLayeredPane() {
            @Override
            public void doLayout() {
                carte.getCanvas().setBounds(0, 0, getWidth(), getHeight());
                Dimension tailleBouton = basculerVue.getPreferredSize();
                basculerVue.setBounds(
                        getWidth() - tailleBouton.width - 16,
                        getHeight() - tailleBouton.height - 16,
                        tailleBouton.width,
                        tailleBouton.height
                );
            }
        };
        carteAvecBouton.setPreferredSize(carte.getCanvas().getPreferredSize());
        carteAvecBouton.add(carte.getCanvas(), JLayeredPane.DEFAULT_LAYER);
        carteAvecBouton.add(basculerVue, JLayeredPane.PALETTE_LAYER);

        JPanel contenu = new JPanel(new BorderLayout(0, 12));
        contenu.setBackground(COULEUR_FOND);
        contenu.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        contenu.add(formulaire, BorderLayout.NORTH);
        contenu.add(carteAvecBouton, BorderLayout.CENTER);

        JFrame frame = new JFrame("Chemins aériens");
        frame.setContentPane(contenu);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void basculerVue(JButton bouton) {
        boolean vueGlobe = carte.basculerVue();
        bouton.setText(vueGlobe ? "Afficher le planisphère" : "Afficher le globe");
    }

    private JPanel creerGroupeCoordonnees(
            String titre, JTextField latitude, JTextField longitude) {
        JPanel groupe = new JPanel(new GridLayout(3, 1, 0, 5));
        groupe.setBackground(Color.WHITE);
        JLabel nom = new JLabel(titre);
        nom.setFont(new Font("SansSerif", Font.BOLD, 14));
        nom.setForeground(COULEUR_PRINCIPALE);
        groupe.add(nom);
        groupe.add(creerLigneCoordonnee("Latitude", latitude));
        groupe.add(creerLigneCoordonnee("Longitude", longitude));
        return groupe;
    }

    private JPanel creerLigneCoordonnee(String nom, JTextField champ) {
        JPanel ligne = new JPanel(new BorderLayout(8, 0));
        ligne.setBackground(Color.WHITE);
        JLabel etiquette = new JLabel(nom);
        etiquette.setFont(new Font("SansSerif", Font.PLAIN, 12));
        etiquette.setPreferredSize(new Dimension(62, 24));
        champ.setPreferredSize(new Dimension(90, 28));
        ligne.add(etiquette, BorderLayout.WEST);
        ligne.add(champ, BorderLayout.CENTER);
        return ligne;
    }

    private static JTextField creerChamp() {
        JTextField champ = new JTextField();
        champ.setFont(new Font("SansSerif", Font.PLAIN, 13));
        champ.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 208, 218)),
                BorderFactory.createEmptyBorder(4, 7, 4, 7)
        ));
        return champ;
    }

    private void afficherTrajectoires(java.awt.event.ActionEvent event) {
        try {
            double latA = lireCoordonnee(latitudeDepart, "latitude du départ ", -90, 90);
            double lonA = lireCoordonnee(longitudeDepart, "longitude du départ ", -180, 180);
            double latB = lireCoordonnee(latitudeArrivee, "latitude de l'arrivée ", -90, 90);
            double lonB = lireCoordonnee(longitudeArrivee, "longitude de l'arrivée ", -180, 180);

            Distance distance = new Distance(
                    new Point(lonA, latA),
                    new Point(lonB, latB)
            );
            Trajectoire loxodromie = distance.loxodromie(100);
            Trajectoire orthodromie = distance.orthodromie(100);

            List<Position> positionsLoxodromie = convertirPositions(loxodromie);
            List<Position> positionsOrthodromie = convertirPositions(orthodromie);
            double distanceLoxodromie = distance.distanceLoxodromie();
            double distanceOrthodromie = distance.distanceOrthodromie();

            carte.effacer();
            carte.ajouterPoint(latA, lonA, "Départ");
            carte.ajouterPoint(latB, lonB, "Arrivée");
            carte.ajouterLigne(
                    positionsLoxodromie,
                    String.format("Loxodromie : %.2f km", distanceLoxodromie),
                    Color.RED
            );
            carte.ajouterLigne(
                    positionsOrthodromie,
                    String.format("Orthodromie : %.2f km", distanceOrthodromie),
                    Color.BLUE
            );
        } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(
                    carte.getCanvas(),
                    exception.getMessage(),
                    "Coordonnées invalides",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static double lireCoordonnee(
            JTextField champ, String nom, double minimum, double maximum) {
        double valeur;
        try {
            valeur = Double.parseDouble(champ.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Saisissez une valeur numérique pour la " + nom + ".");
        }

        if (!Double.isFinite(valeur) || valeur < minimum || valeur > maximum) {
            throw new IllegalArgumentException(
                    "La " + nom + " doit être comprise entre " + minimum + " et " + maximum + "."
            );
        }
        return valeur;
    }

    private static List<Position> convertirPositions(Trajectoire trajectoire) {
        List<Position> positions = new ArrayList<>();
        for (Coordonnee coordonnee : trajectoire.getPoints()) {
            positions.add(Position.fromDegrees(
                    coordonnee.getLatitude(),
                    coordonnee.getLongitude()
            ));
        }
        return positions;
    }
}
