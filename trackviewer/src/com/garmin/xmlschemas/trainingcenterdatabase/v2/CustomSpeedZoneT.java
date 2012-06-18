//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2012.06.18 at 06:00:39 PM MESZ 
//


package com.garmin.xmlschemas.trainingcenterdatabase.v2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomSpeedZone_t complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomSpeedZone_t">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}Zone_t">
 *       &lt;sequence>
 *         &lt;element name="ViewAs" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}SpeedType_t"/>
 *         &lt;element name="LowInMetersPerSecond" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}SpeedInMetersPerSecond_t"/>
 *         &lt;element name="HighInMetersPerSecond" type="{http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2}SpeedInMetersPerSecond_t"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomSpeedZone_t", propOrder = {
    "viewAs",
    "lowInMetersPerSecond",
    "highInMetersPerSecond"
})
public class CustomSpeedZoneT
    extends ZoneT
{

    @XmlElement(name = "ViewAs", required = true)
    protected SpeedTypeT viewAs;
    @XmlElement(name = "LowInMetersPerSecond")
    protected double lowInMetersPerSecond;
    @XmlElement(name = "HighInMetersPerSecond")
    protected double highInMetersPerSecond;

    /**
     * Gets the value of the viewAs property.
     * 
     * @return
     *     possible object is
     *     {@link SpeedTypeT }
     *     
     */
    public SpeedTypeT getViewAs() {
        return viewAs;
    }

    /**
     * Sets the value of the viewAs property.
     * 
     * @param value
     *     allowed object is
     *     {@link SpeedTypeT }
     *     
     */
    public void setViewAs(SpeedTypeT value) {
        this.viewAs = value;
    }

    /**
     * Gets the value of the lowInMetersPerSecond property.
     * 
     */
    public double getLowInMetersPerSecond() {
        return lowInMetersPerSecond;
    }

    /**
     * Sets the value of the lowInMetersPerSecond property.
     * 
     */
    public void setLowInMetersPerSecond(double value) {
        this.lowInMetersPerSecond = value;
    }

    /**
     * Gets the value of the highInMetersPerSecond property.
     * 
     */
    public double getHighInMetersPerSecond() {
        return highInMetersPerSecond;
    }

    /**
     * Sets the value of the highInMetersPerSecond property.
     * 
     */
    public void setHighInMetersPerSecond(double value) {
        this.highInMetersPerSecond = value;
    }

}
