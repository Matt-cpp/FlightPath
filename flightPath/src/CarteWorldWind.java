import gov.nasa.worldwind.Model;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.awt.WorldWindowGLCanvas;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.globes.EarthFlat;
import gov.nasa.worldwind.globes.FlatGlobe;
import gov.nasa.worldwind.layers.Earth.BMNGOneImage;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.render.PointPlacemark;
import gov.nasa.worldwind.render.PointPlacemarkAttributes;
import gov.nasa.worldwind.render.Path;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;

import java.awt.Color;
import java.awt.Dimension;
import java.util.List;

public class CarteWorldWind {

    private final WorldWindowGLCanvas wwd;
    private final RenderableLayer couche;

    public CarteWorldWind() {
        wwd = new WorldWindowGLCanvas();
        wwd.setPreferredSize(new Dimension(1000, 700));

        Model model = (Model) WorldWind.createConfigurationComponent(AVKey.MODEL_CLASS_NAME);

        // Remplace le globe 3D par un globe plat
        EarthFlat globePlat = new EarthFlat();
        globePlat.setProjection(FlatGlobe.PROJECTION_MERCATOR); // ou PROJECTION_MERCATOR
        model.setGlobe(globePlat);

        wwd.setModel(model);

        model.getLayers().add(new BMNGOneImage());

        couche = new RenderableLayer();
        couche.setName("Mes données");
        model.getLayers().add(couche);
    }

    /** Permet de récupérer le composant graphique pour l'ajouter à une fenêtre */
    public WorldWindowGLCanvas getCanvas() {
        return wwd;
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

    public void ajouterLigne(List<Position> positions) {
        Path chemin = new Path(positions);
        chemin.setAltitudeMode(WorldWind.CLAMP_TO_GROUND);
        chemin.setPathType(String.valueOf(WorldWind.GREAT_CIRCLE));

        ShapeAttributes attrsLigne = new BasicShapeAttributes();
        attrsLigne.setOutlineMaterial(new Material(Color.RED));
        attrsLigne.setOutlineWidth(2.0);
        chemin.setAttributes(attrsLigne);

        couche.addRenderable(chemin);
        wwd.redraw();
    }
}