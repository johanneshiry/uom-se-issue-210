package com.github.johanneshiry.uomseissue210.quantities;

import com.github.johanneshiry.uomseissue210.quantities.interfaces.SpecificConductance;
import com.github.johanneshiry.uomseissue210.quantities.interfaces.SpecificResistance;
import tec.uom.se.unit.MetricPrefix;
import tec.uom.se.unit.ProductUnit;
import tec.uom.se.unit.Units;

import javax.measure.Unit;
import javax.measure.quantity.Length;


public class PowerSystemUnits extends Units {

    /* ==== Basic non electric units ==== */
    /** Kilometre */
    public static final Unit<Length> KILOMETRE = MetricPrefix.KILO(METRE);

    /** Ohm per kilometre */
    public static final Unit<SpecificResistance> OHM_PER_KILOMETRE =
                    new ProductUnit<>(OHM.divide(KILOMETRE));

    /** Siemens per kilometre */
    public static final Unit<SpecificConductance> SIEMENS_PER_KILOMETRE =
                    new ProductUnit<>(SIEMENS.divide(KILOMETRE));

}
