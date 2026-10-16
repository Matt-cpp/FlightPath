import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.awt.WorldWindowGLJPanel;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.event.SelectEvent;
import gov.nasa.worldwind.globes.Earth;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.layers.Earth.BMNGOneImage;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.geom.Vec4;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;
import gov.nasa.worldwindx.examples.util.ToolTipController;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class CarteWorldWind {

    private final WorldWindowGLJPanel wwd;
    private final RenderableLayer couche;
    private final ToolTipController toolTipController;
    private final Map<Path, LigneSurvol> lignes = new IdentityHashMap<>();
    private boolean vueGlobe;

    public CarteWorldWind() {
        wwd = new WorldWindowGLJPanel();
        wwd.setPreferredSize(new Dimension(1000, 700));

        Model model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);
        wwd.setModel(model);

        wwd.getModel().setGlobe(new EarthFlat());
        wwd.setView(new BasicOrbitView());

        model.getLayers().add(new BMNGOneImage());

        couche = new RenderableLayer();
        couche.setName("Mes données");
        model.getLayers().add(couche);

        toolTipController = new ToolTipController(wwd) {
            private String dernierTexteLigne;

            @Override
            protected String getRolloverText(SelectEvent event) {
                return estUneLigne(event) ? null : super.getRolloverText(event);
            }

            @Override
            protected String getHoverText(SelectEvent event) {
                return estUneLigne(event) ? null : super.getHoverText(event);
            }

            @Override
            public void selected(SelectEvent event) {
                if (!event.isRollover() && !event.isHover()) {
                    super.selected(event);
                    return;
                }

                String texteLigne = trouverLigneSousCurseur(event);
                if (texteLigne != null) {
                    if (!texteLigne.equals(dernierTexteLigne)) {
                        super.selected(event);
                        hideToolTip();
                        showToolTip(event, texteLigne);
                        wwd.redraw();
                        dernierTexteLigne = texteLigne;
                    }
                    return;
                }

                if (dernierTexteLigne != null) {
                    hideToolTip();
                    wwd.redraw();
                }
                dernierTexteLigne = null;
                super.selected(event);
            }

            private boolean estUneLigne(SelectEvent event) {
                return event.getTopObject() instanceof Path
                        && lignes.containsKey((Path) event.getTopObject());
            }

            private String trouverLigneSousCurseur(SelectEvent event) {
                Point curseur = event.getPickPoint();
                if (curseur == null || !event.hasObjects()) {
                    return null;
                }

                double curseurY = CarteWorldWind.this.wwd.getHeight() - curseur.y;
                String textePlusProche = null;
                double distanceMinimum = 10.0 * 10.0;

                for (LigneSurvol ligne : lignes.values()) {
                    double distance = ligne.distanceAuCurseur(curseur.x, curseurY);
                    if (distance < distanceMinimum) {
                        distanceMinimum = distance;
                        textePlusProche = ligne.texte;
                    }
                }

                return textePlusProche;
            }
        };
    }

    /** Permet de récupérer le composant graphique pour l'ajouter à une fenêtre */
    public WorldWindowGLJPanel getCanvas() {
        return wwd;
    }

    public boolean basculerVue() {
        vueGlobe = !vueGlobe;
        wwd.getModel().setGlobe(vueGlobe ? new Earth() : new EarthFlat());
        wwd.redraw();
        return vueGlobe;
    }

    public void ajouterPoint(double lat, double lon, String label) {
        PointPlacemark point = new PointPlacemark(Position.fromDegrees(lat, lon, 0));
        point.setLabelText(label);

        PointPlacemarkAttributes attrs = new PointPlacemarkAttributes();
        attrs.setLineMaterial(new Material(Color.red));
        attrs.setScale(1.0);
        point.setAttributes(attrs);

        couche.addRenderable(point);
        wwd.redraw();
    }

    public void effacer() {
        couche.removeAllRenderables();
        lignes.clear();
        wwd.redraw();
    }


    public void ajouterLigne(List<Position> positions, String libelle, Color couleur) {
        Path chemin = new Path(positions);
        chemin.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        chemin.setPathType(String.valueOf(WorldWind.LINEAR));

        ShapeAttributes attrsLigne = new BasicShapeAttributes();
        attrsLigne.setOutlineMaterial(new Material(couleur));
        attrsLigne.setOutlineWidth(2.0);
        chemin.setAttributes(attrsLigne);
        chemin.setFollowTerrain(true);
        if (libelle != null) {
            chemin.setValue(AVKey.DISPLAY_NAME, libelle);
            chemin.setValue(AVKey.HOVER_TEXT, libelle);
            lignes.put(chemin, new LigneSurvol(positions, libelle));
        }

        couche.addRenderable(chemin);
        wwd.redraw();
    }

    private class LigneSurvol {
        private final List<Position> positions;
        private final String texte;

        private LigneSurvol(List<Position> positions, String texte) {
            this.positions = positions;
            this.texte = texte;
        }

        private double distanceAuCurseur(double curseurX, double curseurY) {
            double distanceMinimum = Double.POSITIVE_INFINITY;
            Vec4 precedent = null;

            for (Position position : positions) {
                Vec4 pointEcran = wwd.getView().project(
                        wwd.getModel().getGlobe().computePointFromPosition(position)
                );
                if (precedent != null) {
                    distanceMinimum = Math.min(
                            distanceMinimum,
                            distancePointSegment(curseurX, curseurY,
                                    precedent.x, precedent.y, pointEcran.x, pointEcran.y)
                    );
                }
                precedent = pointEcran;
            }

            return distanceMinimum;
        }
    }

    private double distancePointSegment(
            double px, double py, double ax, double ay, double bx, double by) {
        double dx = bx - ax;
        double dy = by - ay;
        double longueurCarree = dx * dx + dy * dy;

        if (longueurCarree == 0) {
            dx = px - ax;
            dy = py - ay;
            return dx * dx + dy * dy;
        }

        double proportion = ((px - ax) * dx + (py - ay) * dy) / longueurCarree;
        proportion = Math.max(0, Math.min(1, proportion));
        double procheX = ax + proportion * dx;
        double procheY = ay + proportion * dy;
        dx = px - procheX;
        dy = py - procheY;
        return dx * dx + dy * dy;
    }
}