package com.harman.traveler.visualizer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.harman.learning.Common.Position;
import com.harman.learning.TraceFile.TracePath;
import com.harman.traveler.visualizer.camera.CameraTransformer;
import com.harman.traveler.visualizer.geometry.GridGeometry;
import com.harman.traveler.visualizer.geometry.TracePathContainer;
import com.sun.javafx.geom.Vec3d;
import com.sun.javafx.scene.CameraHelper;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;

import javafx.animation.FillTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.PickResult;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Drag3DObject extends Application {
    private static final boolean RUN_JASON=false; 
    /**
     * @param args the command line arguments 
     */ 
    public static void main(String[] args) { 
        launch(args); 
    } 
 
    /*
     *   
     Add to Root ->-> Run ->-> Enjoy! 
     
     Depends how you want to move your object, but assuming this : 
 
     when the mouse moves up or down, the object moves vertically parallel to the screen. 
 
     when the mouse moves left or right, the object moves horizontally parallel to the screen. 
 
     To do this, you only need a couple of informations. 
 
     1. Relative displacement of the mouse. 
     2. The Up and Right axis of the view. 
 
     When you got that, if your mouse as some horizontal movement you do : 
 
     objPos += mouseMovementx*scale*RightAxis 
 
     scale maps the screen mouse coordinates to your software display units. 
 
     Simply replace mouseMovementX by mouseMovementY and RightAxis by UpAxis to get vertical motion. 
     
     
     scale could simply be the ratio of viewvolume dimension over the screen dimension. This has some problem if you are using a perspective projection, because if your object is far away the ratio will be incorrect because the dimension are bigger and the object won't stay under the mouse. But it work correctly for an ortho projection. 
 
     Some concrete numbers. Say you are using an ortho project with with following values : 
 
     left = -100, right = 100, top = 100, bottom = -100, near and far do not matter. The view volume dimension will always be : 
 
     horizontally abs( left - right ) = 200 in our case 
     vertically abs( top - bottom ) = 200 in our case 
 
     And your screen dimension are 1024 X 768. So our movement scale is horizontally = 200 / 1024 and 200/768. 
 
     Great, now if your mouse moves 300 units to the left and 100 units up. 
     
     objPos += ( cameraRightAxis*300*200/1024 ) + ( cameraUpAxis*100*200/1024 ) 
     
     That is for an ortho projection.  
     
     For a perspective projection, we need to adjust the scale depending on the distance of the object to the near plane. I won't put any number, it should become obvious now. 
     ViewFrustum = ( left, right, bottom, top, znear, zfar ) 
     
     XScale = abs( left - right ) / XScreenDimension * ( distance( object, camera )  / znear ) 
     YScale = abs( top - bottom ) / YScreenDimension * ( distance( object, camera )  / znear ) 
     */ 
    @Override 
    public void start(Stage stage) { 
      this.selectedMaterial = new PhongMaterial(Color.AQUA); 
      this.selectedMaterial.setDiffuseColor(Color.AQUA); 
      this.selectedMaterial.setSpecularColor(Color.AQUA); 
    	
        File[] files = new File("d:/Data/GroundTruth/").listFiles(new FileFilter()
		{

			@Override
			public boolean accept(File f) {
				// TODO Auto-generated method stub
				return f.getName().endsWith("trs");
			}
		});
        
        List<Point3D> points;
        PolyLine3D line;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;
        
        Coordinate anchor = null;
        Point3D a = null;
        Envelope sceneEnvelope = new Envelope();
        for (File f : files)
        {
        	try {
				TracePath traceFile = TracePath.parseFrom(new FileInputStream(f));
				points = new ArrayList<>();
				int start = 0;
				if (anchor == null)
				{
                    anchor = new Coordinate(traceFile.getRecordsList().get(0).getPosition().getLongitude(),
                            traceFile.getRecordsList().get(0).getPosition().getLatitude(),
                            traceFile.getRecordsList().get(0).getPosition().getAltitude());
					a = new Point3D(traceFile.getRecordsList().get(0).getPosition().getLongitude(),
						traceFile.getRecordsList().get(0).getPosition().getLatitude(),
						traceFile.getRecordsList().get(0).getPosition().getAltitude());
					points.add(new Point3D(0, 0, 0));
					start = 1;
				}
                TracePathContainer container = new TracePathContainer(traceFile, 6, Color.BLUE, anchor, 1000000);
                Envelope envelope = container.getEnvelope();
                sceneEnvelope.expandToInclude(envelope);
                        
                root.getChildren().add(container);
                
//				for (int i = start; i < traceFile.getRecordsList().size(); ++i)
//				{
//					Position pos = traceFile.getRecordsList().get(i).getPosition();
//					Point3D p = new Point3D((pos.getLongitude() - a.getX()) * 1000000,
//							(pos.getLatitude() - a.getY()) * 1000000,
//							0);
//					maxX = Math.max(maxX, Math.abs(p.getX()));
//					maxY = Math.max(maxY, Math.abs(p.getY()));
//					points.add(p);
//				}
//				line = new PolyLine3D(points, 6, Color.BLUE, traceFile);
//	            //root.getChildren().add(line);
//	            break;
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        double max = Math.max(sceneEnvelope.getWidth(), sceneEnvelope.getHeight());
        GridGeometry grid = new GridGeometry(new Envelope(-max, max, -max, max), 4, Color.GREEN, 10, -2.f);
        root.getChildren().add(grid);
        
        
        
//        points = new ArrayList<>();
//        points.add(new Point3D(-max, -max, 0));
//        points.add(new Point3D(-max, max, 0));
//        points.add(new Point3D(max, max, 0));
//        points.add(new Point3D(max, -max, 0));
//        points.add(new Point3D(-max, -max, 0));
//        
//        line = new PolyLine3D(points, 6, Color.GREEN);
//        root.getChildren().add(line);
        
//        double x = 0;
//        int cnt = 0;
//        while (x < maxX)
//        {
//            points = new ArrayList<>();
//        	points.add(new Point3D(-maxX + x, -maxY, 0));
//            points.add(new Point3D(-maxX + x, maxY, 0));
//            line = new PolyLine3D(points, 3, Color.GREEN);
//            x += (++cnt) * 500; 
//            root.getChildren().add(line);
//        }
//        int step = (int)Math.round(2 * max / 10.0);
//        for (int i = 1; i < 10; ++i) {
//        	points = new ArrayList<>();
//        	points.add(new Point3D(-max + i * step, -max, 0));
//            points.add(new Point3D(-max + i * step, max, 0));
//            line = new PolyLine3D(points, 6, Color.GREEN);
//            root.getChildren().add(line);
//            points = new ArrayList<>();
//        	points.add(new Point3D(-max, -max + i * step, 0));
//            points.add(new Point3D(max, -max + i * step, 0));
//            line = new PolyLine3D(points, 6, Color.GREEN);
//            root.getChildren().add(line);
//        }
        
//        Sphere s = new Sphere(150); 
//        s.setTranslateX(5); 
//        s.setPickOnBounds(true); 
//        root.getChildren().add(s); 
         
        showStage(stage); 
    } 
 
    /**
     * ************************************************************************* 
     ************************ Stage Default Setup **************************** 
     *///*********************************************************************** 
    private Group root = new Group(); 
    private PerspectiveCamera camera; 
    private final double sceneWidth = 1024; 
    private final double sceneHeight = 768; 
    private final CameraTransformer cameraTransform = new CameraTransformer(); 
 
    private double mousePosX; 
    private double mousePosY; 
    private double mouseOldX; 
    private double mouseOldY; 
    private double mouseDeltaX; 
    private double mouseDeltaY; 
 
    private void showStage(Stage stage) { 
        Scene scene = new Scene(root, sceneWidth, sceneHeight, true, SceneAntialiasing.BALANCED); 
        scene.setFill(Color.web("3d3d3d")); 
 
        loadCamera(scene); 
        loadControls(scene); 
 
        stage.setTitle("F(X)yz Sample: 3D Dragging"); 
        stage.setScene(scene); 
        stage.show(); 
    } 
 
    private void loadCamera(Scene scene) { 
        //initialize camera 
        camera = new PerspectiveCamera(true); 
        camera.setVerticalFieldOfView(RUN_JASON); 
 
        //setup camera transform for rotational support 
        cameraTransform.setTranslate(0, 0, 0); 
        cameraTransform.getChildren().add(camera); 
        camera.setNearClip(0.1); 
        camera.setFarClip(100000.0); 
        camera.setTranslateZ(-2000); 
        cameraTransform.setRotate(-105.0, 0.0, 15.0); 
 
        //add a Point Light for better viewing of the grid coordinate system 
        PointLight light = new PointLight(Color.GAINSBORO); 
        cameraTransform.getChildren().add(light); 
        cameraTransform.getChildren().add(new AmbientLight(Color.WHITE)); 
        light.setTranslateX(camera.getTranslateX()); 
        light.setTranslateY(camera.getTranslateY()); 
        light.setTranslateZ(camera.getTranslateZ()); 
        //attach camera to scene 
        scene.setCamera(camera); 
 
    } 
     
    private volatile boolean isPicking=false; 
    private PhongMaterial selectedMaterial;
    private Vec3d vecIni, vecPos; 
    private double distance; 
    private Sphere s; 
    private SimpleObjectProperty<Shape3D> selected;
    private SimpleObjectProperty<Shape3D> selectedProperty() {
    	if (this.selected == null) {
    		this.selected = new SimpleObjectProperty<Shape3D>();
    		this.selected.addListener(new ChangeListener<Shape3D>() {

	@Override
	public void changed(ObservableValue<? extends Shape3D> observable, Shape3D oldValue, Shape3D newValue) {
		if (oldValue != null) {
			Object oldMaterial = oldValue.getProperties().get("Material");
			if (oldMaterial != null && oldMaterial instanceof Material) {
				oldValue.setMaterial((Material)oldMaterial);
			}
		}
		if (newValue != null) {
			newValue.getProperties().put("Material", newValue.getMaterial());
			newValue.setMaterial(selectedMaterial);
		}
	}
    			
    		});
    	}
    	return this.selected;
    }
    public final Shape3D getSelected() { return this.selectedProperty().get(); }
    public final void setSelected(Shape3D selected) { this.selectedProperty().set(selected); }    
     
    private void loadControls(Scene scene) { 
        //First person shooter keyboard movement  
        scene.setOnKeyPressed(event -> { 
            double change = 10.0; 
            //Add shift modifier to simulate "Running Speed" 
            if (event.isShiftDown()) { 
                change = 50.0; 
            } 
            //What key did the user press? 
            KeyCode keycode = event.getCode(); 
            //Step 2c: Add Zoom controls 
            if (keycode == KeyCode.ADD) { 
                camera.setTranslateZ(camera.getTranslateZ() + change); 
            } 
            else if (keycode == KeyCode.SUBTRACT) { 
                camera.setTranslateZ(camera.getTranslateZ() - change); 
            } 
            //Step 2d:  Add Strafe controls 
            else if (keycode == KeyCode.W || keycode == KeyCode.UP) { 
                camera.setTranslateY(camera.getTranslateY() - change); 
            } 
            else if (keycode == KeyCode.S || keycode == KeyCode.DOWN) { 
                camera.setTranslateY(camera.getTranslateY() + change); 
            } 
            else if (keycode == KeyCode.A || keycode == KeyCode.LEFT) { 
                camera.setTranslateX(camera.getTranslateX() - change); 
            } 
            else if (keycode == KeyCode.D || keycode == KeyCode.RIGHT) { 
                camera.setTranslateX(camera.getTranslateX() + change); 
            } 
            else if (keycode == KeyCode.R) {
            	 camera.setTranslateX(0); 
            	 camera.setTranslateY(0);
            	 camera.setTranslateZ(-2000);
                 cameraTransform.setRotate(-105.0, 0.0, 15.0); 
            }
        }); 
 
        scene.setOnScroll((ScrollEvent se) -> {
        	double change = se.getDeltaY();
        	camera.setTranslateZ(camera.getTranslateZ() + change);
        });
        
        scene.setOnMousePressed((MouseEvent me) -> { 
            mousePosX = me.getSceneX(); 
            mousePosY = me.getSceneY(); 
            mouseOldX = me.getSceneX(); 
            mouseOldY = me.getSceneY(); 
            PickResult pr = me.getPickResult(); 
            if (pr != null && pr.getIntersectedNode() != null && pr.getIntersectedNode().getUserData() != null) {
            	Shape3D shape = (Shape3D)pr.getIntersectedNode();
            	this.setSelected(shape);
            	//System.out.println(p2);
            }
            else {
            	this.setSelected(null);
            }
            if(pr!=null && pr.getIntersectedNode() != null && pr.getIntersectedNode() instanceof Sphere){ 
                distance=pr.getIntersectedDistance(); 
                s = (Sphere) pr.getIntersectedNode(); 
                isPicking=true; 
                vecIni = unProjectDirection(mousePosX, mousePosY, scene.getWidth(),scene.getHeight()); 
            } 
        }); 
        scene.setOnMouseDragged((MouseEvent me) -> { 
            mouseOldX = mousePosX; 
            mouseOldY = mousePosY; 
            mousePosX = me.getSceneX(); 
            mousePosY = me.getSceneY(); 
            mouseDeltaX = (mousePosX - mouseOldX); 
            mouseDeltaY = (mousePosY - mouseOldY); 
            if(RUN_JASON){ 
                //objPos += mouseMovementx*scale*RightAxis 
                if (isPicking) { 
                    if (mousePosX < mouseOldX) { 
                        System.out.println("moving left"); 
                        Point3D pos = CameraHelper.pickProjectPlane(camera, mousePosX, mousePosY); 
                        s.setTranslateX(s.getTranslateX() + (pos.getX() - mouseOldX) * camera.getNearClip()); 
 
                        return; 
                    } else if (mousePosX > mouseOldX) { 
                        System.err.println("moving right"); 
                        Point3D pos = CameraHelper.pickProjectPlane(camera, mousePosX, mousePosY); 
                        s.setTranslateX(s.getTranslateX() - (pos.getX() - mouseOldX) * camera.getNearClip()); 
 
                        return; 
                    } 
 
                    if (mousePosY < mouseOldY) { 
                        System.out.println("moving up"); 
                    } else if (mousePosY > mouseOldY) { 
                        System.err.println("moving down"); 
                    }      
                    return; 
                } 
            } else { 
                if(isPicking){ 
                    double modifier = (me.isControlDown()?0.2:me.isAltDown()?2.0:1)*(30d/camera.getFieldOfView()); 
                    modifier *=(30d/camera.getFieldOfView()); 
                    vecPos = unProjectDirection(mousePosX, mousePosY, scene.getWidth(),scene.getHeight()); 
                    Point3D p=new Point3D(distance*(vecPos.x-vecIni.x), 
                                distance*(vecPos.y-vecIni.y),distance*(vecPos.z-vecIni.z)); 
                    s.getTransforms().add(new Translate(modifier*p.getX(),0,modifier*p.getZ())); 
                    vecIni=vecPos; 
 
                } else { 
                    double modifier = 10.0; 
                    double modifierFactor = 0.1; 
 
                    if (me.isControlDown()) { 
                        modifier = 0.1; 
                    } 
                    if (me.isShiftDown()) { 
                        modifier = 50.0; 
                    } 
                    if (me.isPrimaryButtonDown()) { 
                        cameraTransform.getRotateY().setAngle(((cameraTransform.getRotateY().getAngle() + mouseDeltaX * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180);  // + 
                        cameraTransform.getRotateX().setAngle(((cameraTransform.getRotateX().getAngle() - mouseDeltaY * modifierFactor * modifier * 2.0) % 360 + 540) % 360 - 180);  // - 
                    } else if (me.isSecondaryButtonDown()) { 
                        double z = camera.getTranslateZ(); 
                        double newZ = z + mouseDeltaX * modifierFactor * modifier; 
                        camera.setTranslateZ(newZ); 
                    } else if (me.isMiddleButtonDown()) { 
                        cameraTransform.getTranslate().setX(cameraTransform.getTranslate().getX() + mouseDeltaX * modifierFactor * modifier * 0.3);  // - 
                        cameraTransform.getTranslate().setY(cameraTransform.getTranslate().getY() + mouseDeltaY * modifierFactor * modifier * 0.3);  // - 
                    } 
                } 
            } 
        }); 
        scene.setOnMouseReleased((MouseEvent me)->{ 
            if(isPicking){ 
                isPicking=false; 
            } 
        }); 
 
    }//************************************************************************* 
    //****************************  End Default  ******************************* 
    //************************************************************************** 
 
    /*
     From fx83dfeatures.Camera3D 
     http://hg.openjdk.java.net/openjfx/8u-dev/rt/file/f4e58490d406/apps/toys/FX8-3DFeatures/src/fx83dfeatures/Camera3D.java 
    */ 
    /*
     * returns 3D direction from the Camera position to the mouse 
     * in the Scene space  
     */ 
     
    public Vec3d unProjectDirection(double sceneX, double sceneY, double sWidth, double sHeight) { 
        double tanHFov = Math.tan(Math.toRadians(camera.getFieldOfView()) * 0.5f); 
        Vec3d vMouse = new Vec3d(2*sceneX/sWidth-1, 2*sceneY/sWidth-sHeight/sWidth, 1); 
        vMouse.x *= tanHFov; 
        vMouse.y *= tanHFov; 
 
        Vec3d result = localToSceneDirection(vMouse, new Vec3d()); 
        result.normalize(); 
        return result; 
    } 
     
    public Vec3d localToScene(Vec3d pt, Vec3d result) { 
        Point3D res = camera.localToParentTransformProperty().get().transform(pt.x, pt.y, pt.z); 
        if (camera.getParent() != null) { 
            res = camera.getParent().localToSceneTransformProperty().get().transform(res); 
        } 
        result.set(res.getX(), res.getY(), res.getZ()); 
        return result; 
    } 
     
    public Vec3d localToSceneDirection(Vec3d dir, Vec3d result) { 
        localToScene(dir, result); 
        result.sub(localToScene(new Vec3d(0, 0, 0), new Vec3d())); 
        return result; 
    } 
}
