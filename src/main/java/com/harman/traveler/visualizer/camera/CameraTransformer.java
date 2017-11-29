/*
 * Project        Traveler
 * (c) copyright  2020
 * Company        HARMAN Automotive Systems GmbH
 *        All rights reserved
 *
 * Secrecy Level  STRICTLY CONFIDENTIAL
 *
 * File           CameraTransformer.java
 * Creation date  29.11.2017
 */
package com.harman.traveler.visualizer.camera;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * Transformer for scene camera.
 * 
 * @author VIvanov
 *
 */
public class CameraTransformer extends Group
{
    public enum RotateOrder
    {
        XYZ, 
        XZY, 
        YXZ, 
        YZX, 
        ZXY, 
        ZYX; 
    }
    
    private Translate translate;
    
    private Translate pivot;
    
    private Translate inversePivot;
    
    private Rotate rotateX;
    
    private Rotate rotateY;
    
    private Rotate rotateZ;
    
    private Scale scale;
    
    public CameraTransformer()
    {
        this(null);
    }
    
    public CameraTransformer(RotateOrder rotateOrder)
    {
        super();
        this.translate = new Translate();
        this.pivot = new Translate();
        this.inversePivot = new Translate();
        this.rotateX = new Rotate();
        rotateX.setAxis(Rotate.X_AXIS);
        this.rotateY = new Rotate();
        rotateY.setAxis(Rotate.Y_AXIS);
        this.rotateZ = new Rotate();
        rotateZ.setAxis(Rotate.Z_AXIS);
        this.scale = new Scale();
        if (rotateOrder == null) 
        {
            super.getTransforms().addAll(translate, rotateZ, rotateY, rotateX, scale);
        }
        else
        {
            //Choose the order of rotations based on the rotateOrder
            switch (rotateOrder)
            {
                case XYZ:
                    super.getTransforms().addAll(translate, pivot, rotateZ, rotateY, rotateX, scale, inversePivot);
                    break;
                    
                case XZY:
                    super.getTransforms().addAll(translate, pivot, rotateY, rotateZ, rotateX, scale, inversePivot);
                    break;
                    
                case YXZ:
                    super.getTransforms().addAll(translate, pivot, rotateZ, rotateX, rotateY, scale, inversePivot);
                    break;
                    
                case YZX:
                    super.getTransforms().addAll(translate, pivot, rotateX, rotateZ, rotateY, scale, inversePivot);
                    break;
                    
                case ZXY:
                    super.getTransforms().addAll(translate, pivot, rotateY, rotateX, rotateZ, scale, inversePivot);
                    break;
                    
                case ZYX:
                    super.getTransforms().addAll(translate, pivot, rotateX, rotateY, rotateZ, scale, inversePivot);
                    break;
                    
                default:
                    super.getTransforms().addAll(translate, rotateZ, rotateY, rotateX, scale);
            }
        }
    }
    
    /**
     * @return the translate
     */
    public Translate getTranslate()
    {
        return translate;
    }

    /**
     * @return the pivot
     */
    public Translate getPivot()
    {
        return pivot;
    }

    /**
     * @return the inversePivot
     */
    public Translate getInversePivot()
    {
        return inversePivot;
    }

    /**
     * @return the rotateX
     */
    public Rotate getRotateX()
    {
        return rotateX;
    }

    /**
     * @return the rotateY
     */
    public Rotate getRotateY()
    {
        return rotateY;
    }

    /**
     * @return the rotateZ
     */
    public Rotate getRotateZ()
    {
        return rotateZ;
    }

    /**
     * @return the scale
     */
    public Scale getScale()
    {
        return scale;
    }

    public void setTranslate(double x, double y, double z)
    {
        translate.setX(x);
        translate.setY(y);
        translate.setZ(z);
    }

    public void setTranslate(double x, double y)
    {
        translate.setX(x);
        translate.setY(y);
    }

    public void setTranslatex(double x)
    {
        translate.setX(x);
    }

    public void setTranslatey(double y)
    {
        translate.setY(y);
    }

    public void setTranslatez(double z)
    {
        translate.setZ(z);
    }

    public void setRotate(double x, double y, double z)
    {
        rotateX.setAngle(x);
        rotateY.setAngle(y);
        rotateZ.setAngle(z);
    }

    public void setRotateX(double x)
    {
        rotateX.setAngle(x);
    }

    public void setRotateY(double y)
    {
        rotateY.setAngle(y);
    }

    public void setRotateZ(double z)
    {
        rotateZ.setAngle(z);
    }

    public void setScale(double scaleFactor)
    {
        scale.setX(scaleFactor);
        scale.setY(scaleFactor);
        scale.setZ(scaleFactor);
    }

    public void setScale(double x, double y, double z)
    {
        scale.setX(x);
        scale.setY(y);
        scale.setZ(z);
    }

    public void setScalex(double x)
    {
        scale.setX(x);
    }

    public void setScaley(double y)
    {
        scale.setY(y);
    }

    public void setScalez(double z)
    {
        scale.setZ(z);
    }

    public void setPivot(double x, double y, double z)
    {
        pivot.setX(x);
        pivot.setY(y);
        pivot.setZ(z);
        inversePivot.setX(-x);
        inversePivot.setY(-y);
        inversePivot.setZ(-z);
    }

    public void reset()
    {
        translate.setX(0.0);
        translate.setY(0.0);
        translate.setZ(0.0);
        rotateX.setAngle(0.0);
        rotateY.setAngle(0.0);
        rotateZ.setAngle(0.0);
        scale.setX(1.0);
        scale.setY(1.0);
        scale.setZ(1.0);
        pivot.setX(0.0);
        pivot.setY(0.0);
        pivot.setZ(0.0);
        inversePivot.setX(0.0);
        inversePivot.setY(0.0);
        inversePivot.setZ(0.0);
    }
}
